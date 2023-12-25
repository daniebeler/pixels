package com.daniebeler.pixels.domain.model

import com.daniebeler.pixels.data.remote.dto.NotificationDto

data class Notification(
    val account: Account,
    val id: String,
    val type: String,
    val post: Post?
)

fun NotificationDto.toNotification(): Notification {
    return Notification(
        account = account.toAccount(),
        id = id,
        type = type,
        post = post?.toPost()
    )
}