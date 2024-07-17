package com.portfolio.basicnotes.ui.notesgrid

import androidx.annotation.ColorRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portfolio.basicnotes.data.Note
import com.portfolio.basicnotes.data.NoteRepository
import com.portfolio.basicnotes.ui.util.NoteSelectionTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesGridviewModel @Inject constructor(private val noteRepository: NoteRepository) : ViewModel(){

    // Note selection tracking
    private val noteSelectionTracker = NoteSelectionTracker()
    private val _noteSelectionStates = noteSelectionTracker.noteSelectionStates
    private val _isSelectionMode = noteSelectionTracker.isSelectionMode

    private val _notesFlow = noteRepository.observeAllNotes()

    // Combine flows into uiState flow
    val uiState: StateFlow<UiState> = combine(
        _notesFlow,
        _isSelectionMode,
        _noteSelectionStates
    ) { notes, isSelectionMode, noteSelectionStates ->
        UiState(
            notes = notes,
            stateLoaded = true,
            isSelectionMode = isSelectionMode,
            noteSelectionStates = noteSelectionStates
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = UiState()
    )

    fun toggleNoteSelected(id: Int) {
        noteSelectionTracker.toggleNoteSelected(id)
    }

    fun deleteSelectedNotes() {
        val selectedNotes: List<Int> = noteSelectionTracker.getAllSelectedNotes()
        if (selectedNotes.isNotEmpty()) {
            viewModelScope.launch {
                noteRepository.deleteNotes(selectedNotes)
            }
        }
    }

    fun changeSelectedNotesColor(@ColorRes color: Int) {
        val selectedNotes: List<Int> = noteSelectionTracker.getAllSelectedNotes()
        if (selectedNotes.isNotEmpty()) {
            viewModelScope.launch {
                noteRepository.updateNotesColor(selectedNotes, color)
            }
        }
    }

    fun deselectAllNotes() {
        noteSelectionTracker.deselectAllNotes()
    }

    fun selectAllNotes() {
        val ids: MutableList<Int> = getAllNoteIds().toMutableList()
        noteSelectionTracker.selectNotes(ids)
    }

    private fun getAllNoteIds(): List<Int> {
        return uiState.value.notes.map { it.id }
    }

}

data class UiState(
    val notes: List<Note> = listOf(),
    val stateLoaded: Boolean = false,
    val isSelectionMode: Boolean = false,
    val noteSelectionStates: Map<Int, Boolean> = emptyMap()
)