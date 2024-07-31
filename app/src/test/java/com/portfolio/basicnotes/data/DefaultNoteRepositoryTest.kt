package com.portfolio.basicnotes.data

import com.portfolio.basicnotes.data.source.LocalNote
import com.portfolio.basicnotes.data.source.NoteDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakeDao() : NoteDao {
    private val _notes: MutableList<LocalNote> = mutableListOf()
    override fun getAllNotesStream(): Flow<List<LocalNote>> {
        return flow { emit(_notes) }
    }

    override fun getNoteStream(noteId: Int): Flow<LocalNote> {
        return flow { emit(_notes.find { it.id == noteId }!!) }
    }

    override fun getAllNotes(): List<LocalNote> {
        return _notes
    }

    override fun getNote(noteId: Int): LocalNote {
        return _notes.find { it.id == noteId }!!
    }

    override fun deleteNote(note: LocalNote) {
        _notes.remove(_notes.find { it.id == note.id })
    }

    override fun deleteNotes(notes: List<LocalNote>) {
        notes.forEach { paramNote ->
            _notes.remove(
                _notes.find { existingNote ->
                    existingNote.id == paramNote.id
                }
            )
        }
    }

    override fun createNote(note: LocalNote) {
        _notes.add(note)
    }

    override fun updateNote(note: LocalNote) {
        val noteIndex = _notes.indexOf(_notes.find { it.id == note.id })
        _notes[noteIndex] = note
    }

    override fun updateNotesColor(notes: List<Int>, color: Int) {
        TODO("Not yet implemented")
    }

}

class DefaultNoteRepositoryTest {
    private lateinit var fakeDao: FakeDao
    private lateinit var defaultNoteRepository: DefaultNoteRepository

    @Before
    fun initNoteRepository() {
        fakeDao = FakeDao()
        defaultNoteRepository = DefaultNoteRepository(fakeDao)
    }

    @Test
    fun defaultNoteRepository_gets_note() = runTest {
        val notes = populateRepository()
        val note = defaultNoteRepository.getNote(notes[0].id)
        assertTrue(notes[0].equalsIgnoreId(note.toLocalNote()))
    }

    @Test
    fun defaultNoteRepository_deletes_note() = runTest {
        populateRepository()

        val beforeDeleteFetch = defaultNoteRepository.observeAllNotes().first().toMutableList()

        val noteToDelete = beforeDeleteFetch[1]
        defaultNoteRepository.deleteNote(noteToDelete.id)
        beforeDeleteFetch.remove(beforeDeleteFetch.find { it.id == noteToDelete.id })

        val afterDeleteFetch = defaultNoteRepository.observeAllNotes().first()

        assertTrue(noteListsEqual(afterDeleteFetch, beforeDeleteFetch))
    }

    @Test
    fun defaultNoteRepository_deletes_notes() = runTest {
        populateRepository()

        val beforeDeleteFetch = defaultNoteRepository.observeAllNotes().first().toMutableList()

        val notesToDelete = listOf(beforeDeleteFetch[1].id, beforeDeleteFetch[2].id)

        defaultNoteRepository.deleteNotes(notesToDelete)
        notesToDelete.forEach { noteToDeleteId ->
            beforeDeleteFetch.remove(beforeDeleteFetch.find { it.id == noteToDeleteId })
        }
        val afterDeleteFetch = defaultNoteRepository.observeAllNotes().first()

        assertTrue(noteListsEqual(afterDeleteFetch, beforeDeleteFetch))
    }

    @Test
    fun defaultNoteRepository_creates_note() {
        //TODO:
    }

    @Test
    fun defaultNoteRepository_updates_note() {
        //TODO:
    }

    @Test
    fun defaultNoteRepository_updates_notes_color() {
        //TODO:
    }

    @Test
    fun defaultNoteRepository_observes_notes() {
        //TODO:
    }

    /**
     * Adds entries to database and returns the list that should have been added
     */
    private suspend fun populateRepository(): List<LocalNote> {
        val entities = getMockNotes()
        entities.forEach {
            defaultNoteRepository.createNote(
                title = it.title,
                content = it.content,
                color = it.color
            )
        }
        return entities
    }

}