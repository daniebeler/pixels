package com.daniebeler.pixelix.ui.composables.hashtagMentionText

import androidx.lifecycle.ViewModel
import com.daniebeler.pixelix.domain.usecase.GetOwnAccountIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class TextWithClickableHashtagsAndMentionsViewModel @Inject constructor(
    private val getOwnAccountIdUseCase: GetOwnAccountIdUseCase
) : ViewModel() {
    suspend fun getMyAccountId(): String {
        return getOwnAccountIdUseCase().first()
    }
}