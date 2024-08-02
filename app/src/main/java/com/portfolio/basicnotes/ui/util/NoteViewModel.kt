package com.portfolio.basicnotes.ui.util

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portfolio.basicnotes.data.Note
import com.portfolio.basicnotes.data.NoteRepository
import com.portfolio.basicnotes.ui.NoteEditResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

abstract class NoteViewModel(
    private val noteRepository: NoteRepository
) : ViewModel() {

    /**
     * Note selection tracking for multinote operations
     */
    private val noteSelectionTracker = NoteSelectionTracker()
    protected val _selectedNotes = noteSelectionTracker.noteSelectionStates
    protected val _isSelectionMode = noteSelectionTracker.isSelectionMode

    /**
     * Message that should be shown to user via snackbar.
     */
    protected val _userMessage = MutableStateFlow<Int?>(null)

    protected val _notesFlow = noteRepository.observeAllNotes()

    /**
     * Combined flow
     */
    protected val baseUiStateFlow: StateFlow<BaseUiState> = combine(
        _notesFlow,
        _isSelectionMode,
        _selectedNotes,
        _userMessage
    ) { notes, isSelectionMode, selectedNotes, userMessage ->
        BaseUiState(
            notes = notes,
            stateLoaded = true,
            isSelectionMode = isSelectionMode,
            selectedNotes = selectedNotes,
            userMessage = userMessage ?: 0
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = BaseUiState()
    )

    fun getAllNoteIds(): List<Int> {
        return baseUiStateFlow.value.notes.map { it.id }
    }

    fun updateUserMessage(@StringRes msg: Int) {
        _userMessage.value = msg
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    open fun toggleNoteSelected(id: Int) {
        noteSelectionTracker.toggleNoteSelected(id)
    }

    open fun deleteSelectedNotes() {
        val selectedNotes: List<Int> = noteSelectionTracker.getAllSelectedNotes()
        if (selectedNotes.isNotEmpty()) {
            viewModelScope.launch {
                noteRepository.deleteNotes(selectedNotes)
                updateUserMessage(NoteEditResult.NotesDeletedOk.userMessage)
            }
        }
    }

    open fun changeSelectedNotesColor(@ColorRes color: Int) {
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

    open fun selectAllNotes() {
        val ids: MutableList<Int> = getAllNoteIds().toMutableList()
        noteSelectionTracker.selectNotes(ids)
    }

    fun selectNotes(ids: List<Int>) {
        noteSelectionTracker.selectNotes(ids)
    }

    data class BaseUiState(
        val notes: List<Note> = listOf(),
        val stateLoaded: Boolean = false,
        val isSelectionMode: Boolean = false,
        val selectedNotes: Set<Int> = emptySet(),
        val userMessage: Int = 0
    )
}