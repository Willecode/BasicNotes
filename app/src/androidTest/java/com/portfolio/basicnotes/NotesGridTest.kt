package com.portfolio.basicnotes

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NotesGridTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    lateinit private var _fabDesc: String
    lateinit private var _titleDesc: String
    lateinit private var _contentDesc: String
    lateinit private var _saveDesc: String
    lateinit private var _backDesc: String
    lateinit private var _deleteSelectedDesc: String
    lateinit private var _selectAllDesc: String

    lateinit private var _titleStr: String
    lateinit private var _snackbarAddedNoteMsg: String
    lateinit private var _snackbarDiscardedNoteMsg: String
    lateinit private var _dialogYes: String

    @Before
    fun initVars() {
        _fabDesc = composeTestRule.activity.getString(R.string.content_description_add)
        _titleDesc = composeTestRule.activity.getString(R.string.title_label)
        _contentDesc = composeTestRule.activity.getString(R.string.text_label)
        _saveDesc = composeTestRule.activity.getString(R.string.content_description_save)
        _backDesc = composeTestRule.activity.getString(R.string.content_description_back)
        _deleteSelectedDesc =
            composeTestRule.activity.getString(R.string.content_description_delete_selected)
        _selectAllDesc = composeTestRule.activity.getString(R.string.content_description_select_all)

        _titleStr = composeTestRule.activity.getString(R.string.main_title)
        _snackbarAddedNoteMsg =
            composeTestRule.activity.getString(R.string.successfully_added_note_message)
        _snackbarDiscardedNoteMsg =
            composeTestRule.activity.getString(R.string.discarded_note_message)
        _dialogYes = composeTestRule.activity.getString(R.string.dialog_yes)

    }

    @Test
    fun grid_screen_creates_note() {
        // Add note and save
        composeTestRule.onNodeWithContentDescription(_fabDesc).performClick()
        composeTestRule.onNodeWithText(_titleDesc).performTextInput("My note")
        composeTestRule.onNodeWithText(_contentDesc).performTextInput("Hello World!")
        composeTestRule.onNodeWithContentDescription(_saveDesc).performClick()

        // Verify that on grid screen and message shown
        composeTestRule.onNodeWithText(_titleStr).assertExists()
        composeTestRule.onNodeWithText(_snackbarAddedNoteMsg).assertExists()

        // Verify that note is created
        composeTestRule.onNodeWithText("My note").assertExists()
        composeTestRule.onNodeWithText("Hello World!").assertExists()
    }

    @Test
    fun grid_screen_discards_note() {
        // Add note and discard
        composeTestRule.onNodeWithContentDescription(_fabDesc).performClick()
        composeTestRule.onNodeWithContentDescription(_backDesc).performClick()

        // Verify that on grid screen and message shown
        composeTestRule.onNodeWithText(_titleStr).assertExists()
        composeTestRule.onNodeWithText(_snackbarDiscardedNoteMsg).assertExists()
    }

    @Test
    fun grid_screen_selects_notes_and_multideletes() {
        val noteTitleA = "My note A"
        val noteTitleB = "My note B"

        // Add note and save
        composeTestRule.onNodeWithContentDescription(_fabDesc).performClick()
        composeTestRule.onNodeWithText(_titleDesc).performTextInput(noteTitleA)
        composeTestRule.onNodeWithText(_contentDesc).performTextInput("Hello World!")
        composeTestRule.onNodeWithContentDescription(_saveDesc).performClick()

        // Add another note and save
        composeTestRule.onNodeWithContentDescription(_fabDesc).performClick()
        composeTestRule.onNodeWithText(_titleDesc).performTextInput(noteTitleB)
        composeTestRule.onNodeWithText(_contentDesc).performTextInput("Hello World!")
        composeTestRule.onNodeWithContentDescription(_saveDesc).performClick()

        // Select all notes
        composeTestRule.onNodeWithText(noteTitleA).performTouchInput { longClick() }
        composeTestRule.onNodeWithContentDescription(_selectAllDesc).performClick()

        // Delete selected notes
        composeTestRule.onNodeWithContentDescription(_deleteSelectedDesc).performClick()
        composeTestRule.onNodeWithText(_dialogYes).performClick()
        composeTestRule.onNodeWithText(noteTitleA).assertDoesNotExist()
        composeTestRule.onNodeWithText(noteTitleB).assertDoesNotExist()
    }
}