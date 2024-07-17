package com.portfolio.basicnotes.ui

import android.util.Log
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.portfolio.basicnotes.ui.BasicNotesRouteArgs.NOTE_ID_ARG
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
}

object BasicNotesRoutes {
    const val NOTES_GRID_ROUTE = NOTES_GRID_SCREEN
    const val NOTE_EDIT_ROUTE = "$NOTE_EDIT_SCREEN?$NOTE_ID_ARG={$NOTE_ID_ARG}"
    const val NOTES_LIST_ROUTE = NOTES_LIST_SCREEN
}


class BasicNotesNavigation(private val navHostController: NavHostController) {

    fun navigateToNotesGridScreen() {
        navHostController.navigate(NOTES_GRID_SCREEN) {
            popUpTo(NOTES_LIST_SCREEN) {
                inclusive = true
            }
            launchSingleTop = true
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
            popUpTo(NOTES_GRID_SCREEN) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    // Debugging
    private fun logNavBackStack(navController: NavHostController) {
        val backStackEntries = navController.currentBackStack.value
        Log.d("NavBackStack", "Current Backstack:")
        for (entry in backStackEntries) {
            Log.d("NavBackStack", "Destination: ${entry.destination.route}")
        }
    }
}