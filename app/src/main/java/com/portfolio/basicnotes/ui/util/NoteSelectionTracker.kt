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
 * TODO: This should probably use a set instead of a map
 */
class NoteSelectionTracker() {
    private val _noteSelectionStates = MutableStateFlow(mapOf<Int, Boolean>())
    val noteSelectionStates = _noteSelectionStates.asStateFlow()

    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode = _isSelectionMode.asStateFlow()


    fun selectNote(id: Int) {
        if (!_isSelectionMode.value) {
            _isSelectionMode.value = true
        }
        _noteSelectionStates.value = _noteSelectionStates.value.toMutableMap().apply {
            this[id] = true
        }
    }

    fun deselectNote(id: Int) {
        _noteSelectionStates.value = _noteSelectionStates.value.toMutableMap().apply {
            this[id] = false
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
        return _noteSelectionStates.value[id]?.equals(true) ?: false
    }

    fun getAllSelectedNotes():List<Int> {
        return _noteSelectionStates.value.mapNotNull {
            if (it.value) it.key else null
        }
    }

    fun deselectAllNotes() {
        resetSelections()
    }

    private fun countSelections(): Int {
        var ret: Int = 0
        _noteSelectionStates.value.forEach {
            if (it.value)
                ret++
        }
        return ret
    }

    private fun resetSelections() {
        _noteSelectionStates.value = emptyMap()
        _isSelectionMode.value = false
    }

    fun selectNotes(ids: List<Int>) {
        _noteSelectionStates.update { currentMap ->
            val mutable = currentMap.toMutableMap()
            ids.forEach { id ->
                mutable[id] = true
            }
            mutable
        }
    }

}