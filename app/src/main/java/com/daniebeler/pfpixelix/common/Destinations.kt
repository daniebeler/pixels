package com.daniebeler.pfpixelix.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.daniebeler.pfpixelix.R

sealed class Destinations(
    val route: String,
    @DrawableRes
    val icon: Int = R.drawable.bookmark_outline,
    @DrawableRes
    val activeIcon: Int = R.drawable.bookmark_outline,
    @StringRes val label: Int = R.string.home
) {
    data object HomeScreen : Destinations(
        route = "home_screen",
        icon = R.drawable.house,
        activeIcon = R.drawable.house_fill,
        label = R.string.home
    )

    data object NotificationsScreen : Destinations(
        route = "notifications_screen",
        icon = R.drawable.notifications_outline,
        activeIcon = R.drawable.notifications,
        label = R.string.alerts
    )

    data object OwnProfile : Destinations(
        route = "own_profile_screen",
        icon = R.drawable.bookmark_outline,
        activeIcon = R.drawable.bookmark_outline,
        label = R.string.profile
    )

    data object NewPost : Destinations(
        route = "new_post_screen", icon = R.drawable.add_circle_outline,
        activeIcon = R.drawable.add_circle
    )

    data object Search : Destinations(
        route = "search_screen/{initialPage}",
        icon = R.drawable.search_outline,
        activeIcon = R.drawable.search,
        label = R.string.search
    )

    data object Profile : Destinations(
        route = "profile_screen/{userid}"
    )

    data object ProfileByUsername : Destinations(
        route = "profile_screen/byUsername/{username}"
    )

    data object EditProfile : Destinations(
        route = "edit_profile_screen"
    )

    data object Preferences : Destinations(
        route = "preferences_screen"
    )

    data object IconSelection : Destinations(
        route = "icon_selection_screen"
    )

    data object MutedAccounts : Destinations(
        route = "muted_accounts_screen"
    )

    data object BlockedAccounts : Destinations(
        route = "blocked_accounts_screen"
    )

    data object LikedPosts : Destinations(
        route = "liked_posts_screen"
    )

    data object BookmarkedPosts : Destinations(
        route = "bookmarked_posts_screen"
    )

    data object FollowedHashtags : Destinations(
        route = "followed_hashtags_screen"
    )

    data object AboutInstance : Destinations(
        route = "about_instance_screen"
    )

    data object AboutPixelix : Destinations(
        route = "about_pixelix_screen"
    )

    data object EditPost : Destinations(
        route = "edit_post_screen/{postId}"
    )

    data object Hashtag : Destinations(
        route = "hashtag_timeline_screen/{hashtag}"
    )

    data object SinglePost : Destinations(
        route = "single_post_screen/{postid}"
    )

    data object Collection : Destinations(
        route = "collection_screen/{collectionid}"
    )

    data object Followers : Destinations(
        route = "followers_screen/{page}/{userid}"
    )



    data object Conversation : Destinations(
        route = "conversations"
    )

    data object Chat : Destinations(
        route = "chat/{userid}"
    )

    data object Mention : Destinations(
        route = "mention/{mentionid}"
    )
}