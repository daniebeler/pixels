package com.daniebeler.pfpixelix.ui.composables.explore

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.daniebeler.pfpixelix.R
import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.domain.model.SavedSearchItem
import com.daniebeler.pfpixelix.domain.model.SavedSearchType
import com.daniebeler.pfpixelix.ui.composables.CustomHashtag
import com.daniebeler.pfpixelix.ui.composables.custom_account.CustomAccount
import com.daniebeler.pfpixelix.ui.composables.explore.trending.TrendingComposable
import com.daniebeler.pfpixelix.ui.composables.states.FullscreenLoadingComposable
import com.daniebeler.pfpixelix.utils.Navigate
import com.daniebeler.pfpixelix.utils.imeAwareInsets
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreComposable(
    navController: NavController,
    initialPage: Int = 0,
    viewModel: ExploreViewModel = hiltViewModel(key = "search-viewmodel-key")
) {
    val context: Context = LocalContext.current

    val textFieldState = rememberTextFieldState()
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(Modifier
        .fillMaxSize()
        .semantics { isTraversalGroup = true }) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = 0f },
            inputField = {
                SearchBarDefaults.InputField(state = textFieldState,
                    onSearch = {
                        expanded = false
                        viewModel.onSearch(it)
                        viewModel.saveSearch(it)
                    },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = { Text(stringResource(R.string.explore)) },
                    leadingIcon = {
                        if (!expanded) {
                            Icon(ImageVector.vectorResource(R.drawable.search_outline), contentDescription = null)
                        } else {
                            Icon(Icons.Outlined.ArrowBackIosNew,
                                contentDescription = null,
                                modifier = Modifier.clickable {
                                    expanded = false
                                    textFieldState.clearText()
                                    viewModel.searchState = SearchState()
                                })
                        }
                    },
                    trailingIcon = {
                        AnimatedVisibility(
                            visible = textFieldState.text.isNotBlank(),
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Icon(Icons.Outlined.Clear,
                                contentDescription = "clear search query",
                                modifier = Modifier.clickable {
                                    textFieldState.clearText()
                                    viewModel.searchState = SearchState()
                                })
                        }
                    })
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {

            LaunchedEffect(textFieldState.text) {
                viewModel.textInputChange(textFieldState.text.toString())
            }

            if (textFieldState.text.isBlank() && viewModel.savedSearches.pastSearches.isNotEmpty()) {
                LazyColumn(modifier = Modifier.imeAwareInsets(context, 90.dp)) {
                    items(viewModel.savedSearches.pastSearches.reversed()) {
                        if (it.savedSearchType == SavedSearchType.Account) {
                            Row {
                                CustomAccount(account = it.account!!,
                                    relationship = null,
                                    navController = navController,
                                    { viewModel.deleteSavedSearch(it) })
                            }
                        } else {
                            PastSearchItem(item = it, navController, { text ->
                                expanded = false
                                textFieldState.setTextAndPlaceCursorAtEnd(text)
                                viewModel.onSearch(text)
                            }, { viewModel.deleteSavedSearch(it) })
                        }
                    }
                }
            }
            viewModel.searchState.searchResult?.let { searchResult ->
                LazyColumn(modifier = Modifier.imeAwareInsets(context, 90.dp), content = {
                    items(searchResult.accounts) {
                        CustomAccount(
                            account = it,
                            relationship = null,
                            onClick = { viewModel.saveAccount(it.username, it) },
                            navController = navController
                        )
                    }
                    item { HorizontalDivider(Modifier.padding(12.dp)) }
                    items(searchResult.tags) {
                        CustomHashtag(
                            hashtag = it,
                            onClick = { viewModel.saveHashtag(it.name) },
                            navController = navController
                        )
                    }
                })
            }

            if (viewModel.searchState.isLoading) {
                FullscreenLoadingComposable()
            }
        }
        Box(
            Modifier
                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
                .semantics { traversalIndex = 1f }
                .padding(top = 80.dp),
        ) {
            if (textFieldState.text.isNotBlank() && viewModel.searchState.searchResult != null) {
                SearchResultComposable(
                    searchState = viewModel.searchState,
                    saveAccount = {username, account -> viewModel.saveAccount(username, account)},
                    saveHashtag = {hashtag -> viewModel.saveHashtag(hashtag)},
                    navController = navController
                )
            } else {
                TrendingComposable(navController, initialPage = initialPage)
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchResultComposable(searchState: SearchState, saveAccount: (String, Account) -> Unit, saveHashtag: (String) -> Unit, navController: NavController) {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
    val scope = rememberCoroutineScope()
    Column {

        PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
            Tab(text = { Text(stringResource(id = R.string.accounts)) },
                selected = pagerState.currentPage == 0,
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onBackground,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(0)
                    }

                })

            Tab(text = { Text(stringResource(R.string.hashtags)) },
                selected = pagerState.currentPage == 1,
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onBackground,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                })
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .background(MaterialTheme.colorScheme.background)
        ) { tabIndex ->
            when (tabIndex) {
                0 -> Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(content = {
                        if (searchState.searchResult != null) {
                            items(searchState.searchResult.accounts) {
                                CustomAccount(
                                    account = it,
                                    relationship = null,
                                    onClick = { saveAccount(it.username, it) },
                                    navController = navController
                                )
                            }
                        }
                    })
                }

                1 -> Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(content = {
                        if (searchState.searchResult != null) {
                            items(searchState.searchResult.tags) {
                                CustomHashtag(
                                    hashtag = it,
                                    onClick = { saveHashtag(it.name) },
                                    navController = navController
                                )
                            }
                        }
                    })
                }
            }
        }
    }
}

@Composable
private fun PastSearchItem(
    item: SavedSearchItem,
    navController: NavController,
    setSearchText: (text: String) -> Unit,
    deleteSavedSearch: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickable {
                when (item.savedSearchType) {
                    SavedSearchType.Account -> Navigate.navigate(
                        "profile_screen/" + item.account!!.id, navController
                    )

                    SavedSearchType.Hashtag -> Navigate.navigate(
                        "hashtag_timeline_screen/${item.value}", navController
                    )

                    SavedSearchType.Search -> setSearchText(item.value)
                }
            }, verticalAlignment = Alignment.CenterVertically
    ) {
        if (item.savedSearchType == SavedSearchType.Account) {
            AsyncImage(
                model = item.account!!.avatar,
                error = painterResource(id = R.drawable.default_avatar),
                contentDescription = "",
                modifier = Modifier
                    .height(46.dp)
                    .width(46.dp)
                    .clip(CircleShape)
            )
        } else {
            Box(
                modifier = Modifier
                    .height(46.dp)
                    .width(46.dp)
                    .background(MaterialTheme.colorScheme.surfaceBright, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (item.savedSearchType == SavedSearchType.Hashtag) {
                        Icons.Outlined.Tag
                    } else {
                        Icons.Outlined.Search
                    }, contentDescription = null, tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))

        val text = when (item.savedSearchType) {
            SavedSearchType.Account -> {
                "@${item.value}"
            }

            SavedSearchType.Hashtag -> {
                "#${item.value}"
            }

            else -> {
                item.value
            }
        }
        Text(text = text)
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .height(22.dp)
                .width(22.dp)
                .clickable { deleteSavedSearch() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}