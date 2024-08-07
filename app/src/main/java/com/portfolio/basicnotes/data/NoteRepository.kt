package com.portfolio.basicnotes.data

import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    suspend fun getNote(noteId: Int): Note
    suspend fun deleteNote(noteId: Int)
    suspend fun deleteNotes(noteIds: List<Int>)
    suspend fun createNote(title: String, content: String, color: Int)
    suspend fun updateNote(noteId: Int, title: String, content: String, color: Int)
    suspend fun updateNotesColor(noteIds: List<Int>, color: Int)
    fun observeAllNotes(): Flow<List<Note>>
}