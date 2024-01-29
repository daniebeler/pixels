package com.daniebeler.pixelix.domain.model

import com.daniebeler.pixelix.data.remote.dto.RelationshipDto

data class Relationship(
    val id: String,
    val following: Boolean,
    val followedBy: Boolean,
    val muting: Boolean,
    val blocking: Boolean
)