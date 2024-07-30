package com.portfolio.basicnotes.ui.notelist

import androidx.annotation.ColorRes
import androidx.lifecycle.viewModelScope
import com.portfolio.basicnotes.data.Note
import com.portfolio.basicnotes.data.NoteRepository
import com.portfolio.basicnotes.ui.NoteEditResult
import com.portfolio.basicnotes.ui.util.NoteViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * TODO: should sanitize newlines from the note list item content using:
 *      it.map { note -> note.copy(content = note.content.replace("\n", ""))}
 *
 */
@HiltViewModel
class NoteListViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : NoteViewModel(noteRepository) {

    // Active note is separate from selected notes. It is the one that is shown in NoteEditor.
    private val _activeNote = MutableStateFlow<Note?>(null)

    // Combine own state with parent state
    val uiState: StateFlow<NoteListUiState> = combine(
        baseUiStateFlow,
        _activeNote
    ) { baseUiState, activeNote ->
        NoteListUiState(
            notes = baseUiState.notes,
            stateLoaded = baseUiState.stateLoaded,
            isSelectionMode = baseUiState.isSelectionMode,
            selectedNotes = baseUiState.selectedNotes,
            activeNote = activeNote,
            userMessage = baseUiState.userMessage
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
            if (_activeNote.value != null) {
                noteRepository.updateNote(
                    noteId = _activeNote.value!!.id,
                    title = _activeNote.value!!.title,
                    content = _activeNote.value!!.content,
                    color = _activeNote.value!!.noteColor
                )
                updateUserMessage(NoteEditResult.EditResultOk.userMessage)
            }
        }
    }

    override fun toggleNoteSelected(id: Int) {
        // Active note can't also be selected
        if (_activeNote.value?.id != id)
            super.toggleNoteSelected(id)
    }

    override fun deleteSelectedNotes() {
        val selectedNotes = getSelectedAndActiveNoteIds()
        if (selectedNotes.isNotEmpty()) {
            viewModelScope.launch {
                noteRepository.deleteNotes(selectedNotes)
                updateUserMessage(NoteEditResult.NotesDeletedOk.userMessage)
            }
            _activeNote.value = null
            deselectAllNotes()
        }
    }

    override fun selectAllNotes() {
        val ids: MutableList<Int> = getAllNoteIds().toMutableList()
        _activeNote.value?.let { it -> ids.remove(it.id) } // don't select active note.
        selectNotes(ids)
    }

    private fun getSelectedAndActiveNoteIds(): MutableList<Int> {
        val selectedNotes = uiState.value.selectedNotes.toMutableList()
        _activeNote.value?.let { note ->
            selectedNotes.add(note.id)
        }
        return selectedNotes
    }

    override fun changeSelectedNotesColor(@ColorRes color: Int) {
        val selectedNotes = getSelectedAndActiveNoteIds()
        if (selectedNotes.isNotEmpty()) {
            viewModelScope.launch {
                noteRepository.updateNotesColor(selectedNotes, color)
                updateUserMessage(NoteEditResult.NotesColorChangedOk.userMessage)
            }
        }
    }
}

data class NoteListUiState (
    val notes: List<Note> = listOf(),
    val stateLoaded: Boolean = false,
    val isSelectionMode: Boolean = false,
    val selectedNotes: Set<Int> = emptySet(),
    val activeNote: Note? = null,
    val userMessage: Int? = null

)