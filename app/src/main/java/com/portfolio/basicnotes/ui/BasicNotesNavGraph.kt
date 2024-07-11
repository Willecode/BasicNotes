package com.portfolio.basicnotes.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.portfolio.basicnotes.ui.BasicNotesRouteArgs.NOTE_ID_ARG
import com.portfolio.basicnotes.ui.BasicNotesRoutes.NOTES_GRID_ROUTE
import com.portfolio.basicnotes.ui.BasicNotesRoutes.NOTE_EDIT_ROUTE
import com.portfolio.basicnotes.ui.noteedit.NoteEditScreen
import com.portfolio.basicnotes.ui.notesgrid.NotesGridScreen
import com.portfolio.basicnotes.ui.util.BasicNotesModalDrawer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicNotesNavGraph(
    modifier : Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navigation: BasicNotesNavigation = remember(navController) {
        BasicNotesNavigation(navController)
    }
) {
    NavHost(
        navController = navController,
        startDestination = NOTES_GRID_ROUTE,
        modifier = Modifier
            .fillMaxSize()
    ) {
        composable(route = NOTES_GRID_ROUTE) {
            BasicNotesModalDrawer(drawerState = drawerState) {
                NotesGridScreen(
                    onAddNewNote = {navigation.navigateToNoteEditScreen(null)},
                    onOpenDrawer = {coroutineScope.launch { drawerState.open() }},
                    onNoteClicked = {noteId ->
                        navigation.navigateToNoteEditScreen(noteId)
                    }
                )
            }
        }
        composable(
            route = NOTE_EDIT_ROUTE,
            arguments = listOf(
                navArgument(NOTE_ID_ARG) {type = NavType.StringType; nullable = true}
            )
        ) {
            NoteEditScreen(
                onOpenDrawer = {coroutineScope.launch { drawerState.open() }},
                onBackPressed = {navigation.navigateToNotesGridScreen()}
            )
        }
    }
}
