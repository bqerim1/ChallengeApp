package com.challengeapp.ui.form

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.challengeapp.domain.model.ChoiceQuestion
import com.challengeapp.domain.repository.FormRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class FormUiState(
    val rows: List<FormRow> = emptyList(),
    val isRefreshing: Boolean = false,
)

@HiltViewModel
class FormViewModel @Inject constructor(
    private val repository: FormRepository,
) : ViewModel() {

    private val isRefreshing = MutableStateFlow(false)
    private val selections = MutableStateFlow<Map<Long, Set<Long>>>(emptyMap())

    val uiState: StateFlow<FormUiState> =
        combine(repository.observePages(), selections, isRefreshing) { pages, selected, refreshing ->
            FormUiState(pages.toRows(selected), refreshing)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FormUiState(isRefreshing = true))

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            isRefreshing.value = true
            repository.refresh()
            isRefreshing.value = false
        }
    }

    // Tapping a selected response clears it, so single-select questions can go back to none.
    fun onResponseClick(question: ChoiceQuestion, responseId: Long) {
        selections.update { current ->
            val selected = current[question.id].orEmpty()
            val updated = when {
                responseId in selected -> selected - responseId
                question.multipleSelection -> selected + responseId
                else -> setOf(responseId)
            }
            current + (question.id to updated)
        }
    }
}
