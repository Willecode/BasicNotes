package com.portfolio.basicnotes.ui.noteedit

import androidx.annotation.ColorRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portfolio.basicnotes.R
import com.portfolio.basicnotes.data.NoteRepository
import com.portfolio.basicnotes.ui.BasicNotesRouteArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteEditScreenViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val savedStateHandle: SavedStateHandle) : ViewModel() {

    // If note is new, then this will be null. If an existing note, then it has the id.
    private val _noteId : String? = savedStateHandle[BasicNotesRouteArgs.NOTE_ID_ARG]

    private val _uiState = MutableStateFlow<EditScreenUiState>(EditScreenUiState())
    val uiState : StateFlow<EditScreenUiState> = _uiState

    private var _noteIsNew = false

    init {
        if (!_noteId.isNullOrBlank()) {
            viewModelScope.launch {
                val loadedNote = noteRepository.getNote(_noteId.toInt())
                _uiState.update {
                    it.copy(
                        stateLoaded = true,
                        title = loadedNote.title,
                        content = loadedNote.content,
                        noteColor = loadedNote.noteColor
                    )
                }
            }
        }
        else {
            _noteIsNew = true
            _uiState.update { it.copy(stateLoaded = true) }
        }
    }

    fun saveNoteAndGoBack(goBackFunction: () -> Unit) {
        viewModelScope.launch {
            saveNote()
            goBackFunction()
        }
    }

    private suspend fun saveNote() {
        if (_noteIsNew) {
            noteRepository.createNote(
                uiState.value.title,
                uiState.value.content,
                uiState.value.noteColor
            )
        }
        else {
            noteRepository.updateNote(
                noteId = _noteId?.toInt()!!,
                uiState.value.title,
                uiState.value.content,
                uiState.value.noteColor
            )
        }

    }

    fun updateContent(newContent: String){
        _uiState.update { it.copy(content = newContent) }
    }

    fun updateTitle(newTitle: String){
        _uiState.update { it.copy(title = newTitle) }
    }

    fun updateColor(@ColorRes newColor: Int){
        _uiState.update { it.copy(noteColor = newColor) }
    }

    private fun noteContentSaveable(): Boolean{
        return _uiState.value.title.isNotBlank()
    }

    fun noteIsSaveable(): Boolean {
        return(_uiState.value.stateLoaded && noteContentSaveable())
    }

    fun deleteNoteAndGoBack( goBackFunction: () -> Unit ) {
        viewModelScope.launch{
            deleteNote()
            goBackFunction()
        }
    }

    fun noteIsNew(): Boolean {
        return _noteIsNew
    }

    private suspend fun deleteNote() {
        if (!_noteIsNew) {
            noteRepository.deleteNote(_noteId!!.toInt())
        }
    }


}

data class EditScreenUiState(
    val stateLoaded: Boolean = false,
    val title: String = "",
    val content: String = "",
    @ColorRes val noteColor : Int = R.color.post_it_gray
)