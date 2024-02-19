package com.daniebeler.pfpixelix.ui.composables.timelines.home_timeline

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.ui.composables.InfinitePostsList
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState

@Composable
fun HomeTimelineComposable(
    navController: NavController, viewModel: HomeTimelineViewModel = hiltViewModel()
) {
    Box(modifier = Modifier.fillMaxSize()) {
        InfinitePostsList(items = viewModel.homeTimelineState.homeTimeline,
            isLoading = viewModel.homeTimelineState.isLoading,
            isRefreshing = viewModel.homeTimelineState.refreshing,
            error = viewModel.homeTimelineState.error,
            endReached = false,
            navController = navController,
            emptyMessage = EmptyState(
                icon = Icons.Outlined.PhotoLibrary,
                heading = "No posts",
                message = "Follow accounts or hashtags to fill your home timeline"
            ),
            getItemsPaginated = {
                viewModel.getItemsPaginated()
            },
            onRefresh = {
                viewModel.refresh()
            },
            itemGetsDeleted = { postId -> viewModel.postGetsDeleted(postId) })
    }
}