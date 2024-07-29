package com.portfolio.basicnotes.ui.notesgrid

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portfolio.basicnotes.data.Note
import com.portfolio.basicnotes.data.NoteRepository
import com.portfolio.basicnotes.ui.NoteEditResult
import com.portfolio.basicnotes.ui.util.NoteSelectionTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesGridviewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Note selection tracking
    private val noteSelectionTracker = NoteSelectionTracker()
    private val _noteSelectionStates = noteSelectionTracker.noteSelectionStates
    private val _isSelectionMode = noteSelectionTracker.isSelectionMode

    /**
     * Message that should be shown to user via snackbar.
     */
    private val _userMessage = MutableStateFlow<Int?>(null)

    private val _notesFlow = noteRepository.observeAllNotes()

    // Combine flows into uiState flow
    val uiState: StateFlow<GridScreenUiState> = combine(
        _notesFlow,
        _isSelectionMode,
        _noteSelectionStates,
        _userMessage
    ) { notes, isSelectionMode, noteSelectionStates, userMessage ->
        GridScreenUiState(
            notes = notes,
            stateLoaded = true,
            isSelectionMode = isSelectionMode,
            noteSelectionStates = noteSelectionStates,
            userMessage = userMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = GridScreenUiState()
    )

    fun toggleNoteSelected(id: Int) {
        noteSelectionTracker.toggleNoteSelected(id)
    }

    fun deleteSelectedNotes() {
        val selectedNotes: List<Int> = noteSelectionTracker.getAllSelectedNotes()
        if (selectedNotes.isNotEmpty()) {
            viewModelScope.launch {
                noteRepository.deleteNotes(selectedNotes)
                updateUserMessage(NoteEditResult.NotesDeletedOk.userMessage)
            }
        }
    }

    fun changeSelectedNotesColor(@ColorRes color: Int) {
        val selectedNotes: List<Int> = noteSelectionTracker.getAllSelectedNotes()
        if (selectedNotes.isNotEmpty()) {
            viewModelScope.launch {
                noteRepository.updateNotesColor(selectedNotes, color)
                updateUserMessage(NoteEditResult.NotesColorChangedOk.userMessage)
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

    fun updateUserMessage(@StringRes msg: Int) {
        _userMessage.value = msg
    }

    fun setUserMessageToNull() {
        _userMessage.value = null
    }
}

data class GridScreenUiState(
    val notes: List<Note> = listOf(),
    val stateLoaded: Boolean = false,
    val isSelectionMode: Boolean = false,
    val noteSelectionStates: Map<Int, Boolean> = emptyMap(),
    val userMessage: Int? = null
)