package com.portfolio.basicnotes.ui

import android.app.Activity
import android.util.Log
import androidx.annotation.StringRes
import androidx.navigation.NavHostController
import com.portfolio.basicnotes.R
import com.portfolio.basicnotes.ui.BasicNotesRouteArgs.NOTE_ID_ARG
import com.portfolio.basicnotes.ui.BasicNotesRoutes.NOTES_GRID_ROUTE
import com.portfolio.basicnotes.ui.BasicNotesRoutes.NOTES_LIST_ROUTE
import com.portfolio.basicnotes.ui.BasicNotesScreens.NOTES_GRID_SCREEN
import com.portfolio.basicnotes.ui.BasicNotesScreens.NOTES_LIST_SCREEN
import com.portfolio.basicnotes.ui.BasicNotesScreens.NOTE_EDIT_SCREEN

private object BasicNotesScreens {
    const val NOTES_GRID_SCREEN = "notesGrid"
    const val NOTE_EDIT_SCREEN = "editNote"
    const val NOTES_LIST_SCREEN = "notesList"
}

object BasicNotesRouteArgs {
    const val NOTE_ID_ARG = "noteId"
    const val USER_MESSAGE_ARG = "userMessage"
}

object BasicNotesRoutes {
    const val NOTES_GRID_ROUTE = NOTES_GRID_SCREEN
    const val NOTE_EDIT_ROUTE = "$NOTE_EDIT_SCREEN?$NOTE_ID_ARG={$NOTE_ID_ARG}"
    const val NOTES_LIST_ROUTE = NOTES_LIST_SCREEN
}

sealed class NoteEditResult(val resultCode: Int, @StringRes val userMessage: Int) {

    object CreateResultOk : NoteEditResult(
        resultCode = Activity.RESULT_FIRST_USER + 1,
        userMessage = R.string.successfully_added_note_message
    )

    object DeleteResultOk : NoteEditResult(
        resultCode = Activity.RESULT_FIRST_USER + 2,
        userMessage = R.string.successfully_deleted_note_message
    )

    object EditResultOk : NoteEditResult(
        resultCode = Activity.RESULT_FIRST_USER + 3,
        userMessage = R.string.successfully_saved_note_message
    )

    object NoteDiscarded : NoteEditResult(
        resultCode = Activity.RESULT_FIRST_USER + 4,
        userMessage = R.string.discarded_note_message
    )

    object NotesColorChangedOk : NoteEditResult(
        resultCode = Activity.RESULT_FIRST_USER + 4,
        userMessage = R.string.successfully_changed_notes_color_message
    )

    object NotesDeletedOk : NoteEditResult(
        resultCode = Activity.RESULT_FIRST_USER + 4,
        userMessage = R.string.successfully_deleted_notes_message
    )
}

class BasicNotesNavigation(private val navHostController: NavHostController) {

    fun navigateToNotesGridScreen() {
        navHostController.navigate(route = NOTES_GRID_ROUTE) {
            popUpTo(NOTES_GRID_ROUTE) {
                inclusive = true
            }
        }
    }

    fun navigateToNoteEditScreen(noteId : Int?) {
        navHostController.navigate(
            NOTE_EDIT_SCREEN.let{
                if (noteId != null)
                    "$it?$NOTE_ID_ARG=$noteId"
                else
                    it
            }
        ) {
            launchSingleTop = true
        }
    }

    fun navigateToNotesListScreen() {
        navHostController.navigate(NOTES_LIST_SCREEN) {
            popUpTo(NOTES_LIST_ROUTE) {
                inclusive = true
            }
        }
    }

    // Debugging
    private fun logNavBackStack() {
        val backStackEntries = navHostController.currentBackStack.value
        Log.d("NavBackStack", "Current Backstack:")
        for (entry in backStackEntries) {
            Log.d("NavBackStack", "Destination: ${entry.destination.route}")
        }
    }
}