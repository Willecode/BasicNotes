package com.portfolio.basicnotes.ui.notesgrid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portfolio.basicnotes.data.Note
import com.portfolio.basicnotes.data.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class NotesGridviewModel @Inject constructor(noteRepository: NoteRepository) : ViewModel(){
    val uiState : StateFlow<UiState> = noteRepository.observeAllNotes()
        .map {
            UiState(notes = it, stateLoaded = true)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = UiState()
        )
}

data class UiState(
    val notes: List<Note> = listOf(),
    val stateLoaded: Boolean = false
)

