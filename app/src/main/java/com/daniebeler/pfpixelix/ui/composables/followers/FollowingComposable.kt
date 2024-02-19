package com.daniebeler.pfpixelix.ui.composables.followers

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.ui.composables.CustomPullRefreshIndicator
import com.daniebeler.pfpixelix.ui.composables.InfiniteListHandler
import com.daniebeler.pfpixelix.ui.composables.custom_account.CustomAccount
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.states.EndOfListComposable
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.FullscreenEmptyStateComposable
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import com.daniebeler.pfpixelix.utils.Navigate

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FollowingComposable(
    navController: NavController, viewModel: FollowersViewModel = hiltViewModel()
) {

    val pullRefreshState =
        rememberPullRefreshState(refreshing = viewModel.followingState.isRefreshing,
            onRefresh = { viewModel.getFollowingFirstLoad(true) })

    val lazyListState = rememberLazyListState()

    LazyColumn(state = lazyListState, content = {
        items(viewModel.followingState.following, key = {
            it.id
        }) {
            CustomAccount(account = it, null, navController)
        }

        if (viewModel.followingState.following.isNotEmpty() && viewModel.followingState.isLoading && !viewModel.followingState.isRefreshing) {
            item {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .wrapContentSize(Alignment.Center)
                )
            }
        }

        if (viewModel.followingState.endReached && viewModel.followingState.following.size > 10) {
            item {
                EndOfListComposable()
            }
        }
    })

    if (!viewModel.followingState.isLoading && viewModel.followingState.error.isEmpty() && viewModel.followingState.following.isEmpty()) {
        FullscreenEmptyStateComposable(
            emptyState = EmptyState(
                icon = Icons.Outlined.Groups,
                heading = "Empty",
                message = "The profiles you follow will appear here",
                buttonText = "Explore trending profiles",
                onClick = {
                    Navigate().navigate("trending_screen/accounts", navController)
                }
            )
        )
    }

    InfiniteListHandler(lazyListState = lazyListState) {
        //viewModel.getFollowingPaginated()
    }

    CustomPullRefreshIndicator(
        viewModel.followingState.isRefreshing, pullRefreshState
    )

    LoadingComposable(isLoading = viewModel.followingState.isLoading && viewModel.followingState.following.isEmpty())
    ErrorComposable(message = viewModel.followingState.error)
}