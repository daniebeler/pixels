package com.daniebeler.pfpixelix.widget.notifications.work_manager

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.pm.PackageManager
import androidx.core.content.FileProvider.getUriForFile
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import coil.imageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import com.daniebeler.pfpixelix.common.Resource
import com.daniebeler.pfpixelix.widget.WidgetRepositoryProvider
import com.daniebeler.pfpixelix.widget.latest_image.updateLatestImageWidget
import com.daniebeler.pfpixelix.widget.latest_image.updateLatestImageWidgetRefreshing
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class LatestImageTask @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val dataStore: DataStore<Preferences>
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        try {
            updateLatestImageWidgetRefreshing(context)
            val repository = WidgetRepositoryProvider(dataStore).invoke()
            if (repository == null) {
                updateLatestImageWidget("", "", context, "you have to be logged in to an account")
                return Result.failure()
            }
            val res = repository.getLatestImage()
            if (res is Resource.Success && res.data != null) {

                val imageUri = getImageUri(res.data.mediaAttachments.first().previewUrl)

                updateLatestImageWidget(imageUri, res.data.id, context)
            } else {
                throw Exception()
            }
        } catch (e: Exception) {
            if (runAttemptCount < 4) {
                updateLatestImageWidget("", "", context, "an error occurred, retrying in ${NotificationWorkManagerRetrySeonds * (runAttemptCount + 1)} seconds")
                return Result.retry()
            }
            updateLatestImageWidget("", "", context, "an unexpected error occurred")
            return Result.failure()
        }
        return Result.success()
    }

    private suspend fun getImageUri(url: String): String {
        val request = ImageRequest.Builder(context).data(url).build()

        // Request the image to be loaded and throw error if it failed
        with(context.imageLoader) {
            val result = execute(request)
            if (result is ErrorResult) {
                throw result.throwable
            }
        }

        // Get the path of the loaded image from DiskCache.
        val path = context.imageLoader.diskCache?.get(url)?.use { snapshot ->
            val imageFile = snapshot.data.toFile()

            // Use the FileProvider to create a content URI
            val contentUri = getUriForFile(
                context, "com.example.android.appwidget.fileprovider", imageFile
            )

            // Find the current launcher everytime to ensure it has read permissions
            val resolveInfo = context.packageManager.resolveActivity(
                Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_HOME) },
                PackageManager.MATCH_DEFAULT_ONLY
            )
            val launcherName = resolveInfo?.activityInfo?.packageName
            if (launcherName != null) {
                context.grantUriPermission(
                    launcherName,
                    contentUri,
                    FLAG_GRANT_READ_URI_PERMISSION or FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                )
            }

            // return the path
            contentUri.toString()
        }
        return requireNotNull(path) {
            "Couldn't find cached file"
        }
    }

}