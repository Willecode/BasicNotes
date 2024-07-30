package com.portfolio.basicnotes.ui.notesgrid

import androidx.lifecycle.viewModelScope
import com.portfolio.basicnotes.data.Note
import com.portfolio.basicnotes.data.NoteRepository
import com.portfolio.basicnotes.ui.util.NoteViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class NotesGridviewModel @Inject constructor(
    noteRepository: NoteRepository
) : NoteViewModel(noteRepository) {

    val uiState: StateFlow<GridScreenUiState> = baseUiStateFlow.map {
        GridScreenUiState(
            notes = it.notes,
            stateLoaded = it.stateLoaded,
            isSelectionMode = it.isSelectionMode,
            selectedNotes = it.selectedNotes,
            userMessage = it.userMessage
        )
    }
        .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = GridScreenUiState()
    )
}

data class GridScreenUiState(
    val notes: List<Note> = listOf(),
    val stateLoaded: Boolean = false,
    val isSelectionMode: Boolean = false,
    val selectedNotes: Set<Int> = emptySet(),
    val userMessage: Int? = null
)