package com.daniebeler.pixelix.domain.usecase

import com.daniebeler.pixelix.domain.repository.StorageRepository
import kotlinx.coroutines.flow.Flow

class GetHideSensitiveContent(
    private val repository: StorageRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return repository.getHideSensitiveContent()
    }
}