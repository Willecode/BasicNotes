package com.portfolio.basicnotes.data

import com.portfolio.basicnotes.data.source.LocalNote
import com.portfolio.basicnotes.data.source.NoteDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultNoteRepository @Inject constructor(
    private val localDatasource: NoteDao
) : NoteRepository {
    override suspend fun getNote(noteId: Int): Note {
        return withContext(Dispatchers.IO) {
            localDatasource.getNote(noteId).toNote()
        }
    }

    override suspend fun deleteNote(noteId: Int) {
        withContext(Dispatchers.IO)
        { localDatasource.deleteNote(noteId.toLocalNote()) }
    }

    override suspend fun deleteNotes(noteIds: List<Int>) {
        withContext(Dispatchers.IO)
        { localDatasource.deleteNotes(noteIds.map { it.toLocalNote() }) }
    }

    override suspend fun createNote(title: String, content: String, color: Int) {
        withContext(Dispatchers.IO) {
            localDatasource.createNote(LocalNote(title = title, content = content, color = color, date = LocalDate.now()))
        }
    }

    override suspend fun updateNote(noteId: Int, title: String, content: String, color: Int) {
        withContext(Dispatchers.IO) {
            localDatasource.updateNote(LocalNote(id = noteId, title = title, content = content, color = color, date = LocalDate.now()))
        }
    }

    override suspend fun updateNotesColor(noteIds: List<Int>, color: Int) {
        withContext(Dispatchers.IO) {
            localDatasource.updateNotesColor(notes = noteIds, color = color)
        }
    }

    override fun observeAllNotes(): Flow<List<Note>> {
        return localDatasource.getAllNotesStream().map { it.map {it.toNote()} }
    }
}

fun LocalNote.toNote(): Note {
    return Note(id = id, title = title, content = content, date = date, noteColor = color)
}

fun Int.toLocalNote(): LocalNote {
    return LocalNote(this, "", "", 0, date = LocalDate.now())
}