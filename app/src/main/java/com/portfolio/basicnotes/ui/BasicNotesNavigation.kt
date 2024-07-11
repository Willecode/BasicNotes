package com.portfolio.basicnotes.ui

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.portfolio.basicnotes.ui.BasicNotesRouteArgs.NOTE_ID_ARG
import com.portfolio.basicnotes.ui.BasicNotesScreens.NOTES_GRID_SCREEN
import com.portfolio.basicnotes.ui.BasicNotesScreens.NOTE_EDIT_SCREEN

private object BasicNotesScreens {
    const val NOTES_GRID_SCREEN = "notes"
    const val NOTE_EDIT_SCREEN = "editNote"
}

object BasicNotesRouteArgs {
    const val NOTE_ID_ARG = "noteId"
}

object BasicNotesRoutes {
    const val NOTES_GRID_ROUTE = NOTES_GRID_SCREEN
    const val NOTE_EDIT_ROUTE = "$NOTE_EDIT_SCREEN?$NOTE_ID_ARG={$NOTE_ID_ARG}"
}


class BasicNotesNavigation(private val navHostController: NavHostController) {

    fun navigateToNotesGridScreen() {
        navHostController.navigate(NOTES_GRID_SCREEN) {
            popUpTo(navHostController.graph.findStartDestination().id) {
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
        )

    }
}