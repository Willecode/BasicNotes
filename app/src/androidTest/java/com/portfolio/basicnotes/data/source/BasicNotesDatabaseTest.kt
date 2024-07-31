package com.portfolio.basicnotes.data.source

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.portfolio.basicnotes.R
import com.portfolio.basicnotes.data.entityListsEqual
import com.portfolio.basicnotes.data.getMockNotes
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BasicNotesDatabaseTest {

    private lateinit var noteDao: NoteDao
    private lateinit var db: BasicNotesDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            BasicNotesDatabase::class.java
        ).build()
        noteDao = db.noteDao()
    }

    @After
    fun closeDb() = db.close()

    @Test
    fun noteDao_fetches_items() = runTest {
        val entities = populateDatabase()
        val fetchedEntities = noteDao.getAllNotesStream().first()
        assertTrue(entityListsEqual(entities, fetchedEntities))
    }

    @Test
    fun noteDao_deletes_items() = runTest {
        populateDatabase()

        val fetchedEntities = noteDao.getAllNotesStream().first()

        noteDao.deleteNotes(
            listOf(
                fetchedEntities[0],
                fetchedEntities[1],
                fetchedEntities[3]
            )
        )
        val entities = fetchedEntities.toMutableList()
        entities.remove(fetchedEntities[0])
        entities.remove(fetchedEntities[1])
        entities.remove(fetchedEntities[3])

        val fetchedEntitiesAfterRemoval = noteDao.getAllNotesStream().first()
        assertTrue(entityListsEqual(entities, fetchedEntitiesAfterRemoval))
    }

    @Test
    fun noteDao_updates_items() = runTest {
        populateDatabase()

        val fetchedEntities = noteDao.getAllNotesStream().first().toMutableList()
        noteDao.updateNote(changeTitleAndColor(fetchedEntities[0]))
        fetchedEntities[0] = changeTitleAndColor(fetchedEntities[0])

        val fetchedEntitesAfterUpdate = noteDao.getAllNotesStream().first()

        assertTrue(entityListsEqual(fetchedEntities, fetchedEntitesAfterUpdate))
    }

    /**
     * Adds entries to database and returns the list that should have been added
     */
    private fun populateDatabase(): List<LocalNote> {
        val entities = getMockNotes()
        entities.forEach {
            noteDao.createNote(it)
        }
        return entities
    }

    private fun changeTitleAndColor(note: LocalNote): LocalNote {
        return note.copy(title = "New title", color = R.color.post_it_purple)
    }


}