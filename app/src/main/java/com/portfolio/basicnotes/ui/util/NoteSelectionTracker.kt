package com.portfolio.basicnotes.ui.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Can be used to keep track of selected notes by id.
 * Instantiate class and use the public functions to
 * select/deselect notes and query with [getAllSelectedNotes].
 *
 * Optionally observe the [noteSelectionStates]
 * and [isSelectionMode] flows. Used by view models.
 *
 */
class NoteSelectionTracker() {
    private val _selectedNotes = MutableStateFlow(setOf<Int>())
    val noteSelectionStates = _selectedNotes.asStateFlow()

    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode = _isSelectionMode.asStateFlow()


    fun selectNote(id: Int) {
        enableSelectionMode()
        _selectedNotes.value = _selectedNotes.value.toMutableSet().apply {
            this.add(id)
        }
    }

    private fun enableSelectionMode() {
        if (!_isSelectionMode.value) {
            _isSelectionMode.value = true
        }
    }

    fun deselectNote(id: Int) {
        _selectedNotes.value = _selectedNotes.value.toMutableSet().apply {
            this.remove(id)
        }
        if (countSelections() <= 0) {
            resetSelections()
        }
    }

    fun toggleNoteSelected(id: Int) {
        if (isNoteSelected(id))
            deselectNote(id)
        else
            selectNote(id)
    }

    fun isNoteSelected(id: Int): Boolean {
        return _selectedNotes.value.contains(id)
    }

    fun getAllSelectedNotes():List<Int> {
        return _selectedNotes.value.toList()
    }

    fun deselectAllNotes() {
        resetSelections()
    }

    private fun countSelections(): Int {
        return _selectedNotes.value.size
    }

    private fun resetSelections() {
        _selectedNotes.value = emptySet()
        disableSelectionMode()
    }

    private fun disableSelectionMode() {
        if (_isSelectionMode.value)
            _isSelectionMode.value = false
    }

    fun selectNotes(ids: List<Int>) {
        _selectedNotes.update { current ->
            val mutable = current.toMutableSet()
            ids.forEach { id ->
                mutable.add(id)
            }
            mutable
        }
    }

}