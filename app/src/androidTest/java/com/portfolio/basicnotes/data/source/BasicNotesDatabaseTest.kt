package com.portfolio.basicnotes.data.source

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.portfolio.basicnotes.R
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

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
        assertEntityListsEqual(entities, fetchedEntities)
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
        assertEntityListsEqual(entities, fetchedEntitiesAfterRemoval)
    }

    @Test
    fun noteDao_updates_items() = runTest {
        populateDatabase()

        val fetchedEntities = noteDao.getAllNotesStream().first().toMutableList()
        noteDao.updateNote(changeTitleAndColor(fetchedEntities[0]))
        fetchedEntities[0] = changeTitleAndColor(fetchedEntities[0])

        val fetchedEntitesAfterUpdate = noteDao.getAllNotesStream().first()

        assertEntityListsEqual(fetchedEntities, fetchedEntitesAfterUpdate)
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

    /**
     * Asserts that [a] and [b] have the same LocalNotes, ignoring ids and element order
     */
    private fun assertEntityListsEqual(
        a: List<LocalNote>,
        b: List<LocalNote>
    ) {
        assertEquals(a.size, b.size)

        val sortedA = a.sortedWith(compareBy({ it.title }, { it.content }, { it.color }))
        val sortedB = b.sortedWith(compareBy({ it.title }, { it.content }, { it.color }))

        for (i in sortedA.indices) {
            val expected = sortedA[i]
            val actual = sortedB[i]

            Assert.assertTrue(
                "Note at index $i does not match. Expected: $expected, but got: $actual",
                expected.equalsIgnoreId(actual)
            )
        }
    }

    private fun getMockNotes(): List<LocalNote> {
        return listOf(
            LocalNote(
                title = "Weekly Goals",
                content = "Complete the project report by Wednesday. Start the new marketing campaign. Schedule a team meeting to discuss next quarter's targets. Exercise at least three times this week.",
                color = R.color.post_it_blue,
                date = LocalDate.now()
            ),
            LocalNote(
                title = "Gardening Tips",
                content = "Water plants early in the morning. Use compost for better soil nutrition. Prune the roses and remove weeds regularly. Consider planting new herbs.",
                color = R.color.post_it_green,
                date = LocalDate.now()
            ),
            LocalNote(
                title = "Dream Journal",
                content = "Had a dream about exploring an ancient city.",
                color = R.color.post_it_pink,
                date = LocalDate.now()
            ),
            LocalNote(
                title = "Restaurant Recommendations",
                content = "Try the new Italian place downtown.",
                color = R.color.post_it_orange,
                date = LocalDate.now()
            ),
            LocalNote(
                title = "Creative Writing Ideas",
                content = "Write a short story set in a dystopian future. Explore the theme of time travel.",
                color = R.color.post_it_white,
                date = LocalDate.now()
            )
        )
    }

    private fun LocalNote.equalsIgnoreId(other: LocalNote): Boolean {
        return (
                this.content == other.content
                        && this.title == other.title
                        && this.color == other.color
                        && this.date == other.date
                )
    }
}