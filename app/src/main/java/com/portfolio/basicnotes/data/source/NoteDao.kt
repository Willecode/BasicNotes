package com.portfolio.basicnotes.data.source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM note")
    fun getAllNotesStream(): Flow<List<LocalNote>>

    @Query("SELECT * FROM note WHERE id = :noteId")
    fun getNoteStream(noteId: Int): Flow<LocalNote>

    @Query("SELECT * FROM note")
    fun getAllNotes(): List<LocalNote>

    @Query("SELECT * FROM note WHERE id = :noteId")
    fun getNote(noteId: Int): LocalNote

    @Delete
    fun deleteNote(note: LocalNote)

    @Delete
    fun deleteNotes(notes: List<LocalNote>)

    @Insert
    fun createNote(note: LocalNote)

    @Update
    fun updateNote(note: LocalNote)

    @Query("UPDATE note SET color = :color WHERE id in (:notes)")
    fun updateNotesColor(notes: List<Int>, color: Int)
}