package com.portfolio.basicnotes.ui.notelist

import androidx.annotation.ColorRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portfolio.basicnotes.data.Note
import com.portfolio.basicnotes.data.NoteRepository
import com.portfolio.basicnotes.ui.util.NoteSelectionTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(private val noteRepository: NoteRepository) : ViewModel(){

    // Active note is separate from selected notes. It is the one that is shown in NoteEditor.
    private val _activeNote = MutableStateFlow<Note?>(null)

    // Note selection tracking
    private val noteSelectionTracker = NoteSelectionTracker()
    private val _noteSelectionStates = noteSelectionTracker.noteSelectionStates
    private val _isSelectionMode = noteSelectionTracker.isSelectionMode

    private val _notesFlow = noteRepository.observeAllNotes()
        .map {
            // Sanitize newlines
            it.map { note ->
                note.copy(content = note.content.replace("\n", ""))
            }
        }

    // Combine flows into uiState flow
    val uiState: StateFlow<NoteListUiState> = combine(
        _notesFlow,
        _isSelectionMode,
        _noteSelectionStates,
        _activeNote
    ) { notes, isSelectionMode, noteSelectionStates, activeNote ->
        NoteListUiState(
            notes = notes,
            stateLoaded = true,
            isSelectionMode = isSelectionMode,
            noteSelectionStates = noteSelectionStates,
            activeNote = activeNote
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = NoteListUiState()
    )


    fun updateContent(newContent: String){
        _activeNote.update { _activeNote.value?.copy(content = newContent) }
    }

    fun updateTitle(newTitle: String){
        _activeNote.update { _activeNote.value?.copy(title = newTitle) }
    }

    fun updateColor(@ColorRes newColor: Int){
        _activeNote.update { _activeNote.value?.copy(noteColor = newColor) }

    }

    fun setActiveNote(id: Int?) {
        _activeNote.update { findNote(id) }
    }

    private fun findNote(id: Int?): Note? {
        return uiState.value.notes.find {
            it.id == id
        }
    }

    fun deleteActiveNote() {
        viewModelScope.launch {
            if (_activeNote.value != null) {
                noteRepository.deleteNote(noteId = _activeNote.value!!.id)
                _activeNote.value = null
            }
        }
    }

    fun saveNote() {
        viewModelScope.launch {
            if (_activeNote.value != null)
                noteRepository.updateNote(
                    noteId = _activeNote.value!!.id,
                    title = _activeNote.value!!.title,
                    content = _activeNote.value!!.content,
                    color = _activeNote.value!!.noteColor
                )
        }
    }

    fun toggleNoteSelected(id: Int) {
        // Active note can't also be selected
        if (_activeNote.value?.id != id)
            noteSelectionTracker.toggleNoteSelected(id)
    }

    fun deleteSelectedNotes() {
        val selectedNotes = getSelectedAndActiveNoteIds()
        if (selectedNotes.isNotEmpty()) {
            viewModelScope.launch {
                noteRepository.deleteNotes(selectedNotes)
            }
            _activeNote.value = null
            deselectAllNotes()
        }
    }

    fun deselectAllNotes() {
        noteSelectionTracker.deselectAllNotes()
    }

    fun selectAllNotes() {
        val ids: MutableList<Int> = getAllNoteIds().toMutableList()
        _activeNote.value?.let { it -> ids.remove(it.id) } // don't select active note.
        noteSelectionTracker.selectNotes(ids)
    }

    private fun getAllNoteIds(): List<Int> {
        return uiState.value.notes.map { it.id }
    }

    private fun getSelectedAndActiveNoteIds(): MutableList<Int> {
        val selectedNotes = noteSelectionTracker.getAllSelectedNotes().toMutableList()
        _activeNote.value?.let { note ->
            selectedNotes.add(note.id)
        }
        return selectedNotes
    }

    fun changeSelectedNotesColor(@ColorRes color: Int) {
        val selectedNotes = getSelectedAndActiveNoteIds()
        if (selectedNotes.isNotEmpty()) {
            viewModelScope.launch {
                noteRepository.updateNotesColor(selectedNotes, color)
            }
        }
    }
}

data class NoteListUiState (
    val notes: List<Note> = listOf(),
    val stateLoaded: Boolean = false,
    val isSelectionMode: Boolean = false,
    val noteSelectionStates: Map<Int, Boolean> = emptyMap(),
    val activeNote: Note? = null
)