package com.portfolio.basicnotes.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
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
import com.portfolio.basicnotes.ui.BasicNotesRoutes.NOTES_LIST_ROUTE
import com.portfolio.basicnotes.ui.BasicNotesRoutes.NOTE_EDIT_ROUTE
import com.portfolio.basicnotes.ui.noteedit.NoteEditScreen
import com.portfolio.basicnotes.ui.notelist.NoteListScreen
import com.portfolio.basicnotes.ui.notesgrid.NotesGridScreen
import com.portfolio.basicnotes.ui.util.BasicNoteActionBarType
import com.portfolio.basicnotes.ui.util.BasicNotesModalDrawer
import com.portfolio.basicnotes.ui.util.NoteListContentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun BasicNotesNavGraph(
    modifier : Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    navigation: BasicNotesNavigation = remember(navController) {
        BasicNotesNavigation(navController)
    },
    contentType: NoteListContentType,
    actionBarType: BasicNoteActionBarType
) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    NavHost(
        navController = navController,
        startDestination = NOTES_GRID_ROUTE,
        enterTransition = { EnterTransition.None},
        exitTransition = { ExitTransition.None },
        modifier = modifier
            .fillMaxSize()
    ) {
        composable(route = NOTES_GRID_ROUTE) {
            BasicNotesModalDrawer(
                drawerState = drawerState
            ) {
                NotesGridScreen(
                    onNoteClicked = { navigation.navigateToNoteEditScreen(it) },
                    actionBarType = actionBarType,
                    onDrawerOpen = { coroutineScope.launch { drawerState.open() } }
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
                onBackPressed = {navController.navigateUp()},
                modifier = Modifier.fillMaxSize()
            )
        }

        composable(route = NOTES_LIST_ROUTE) {
            BasicNotesModalDrawer(
                drawerState = drawerState
            ) {
                NoteListScreen(
                    onNoteClicked = { navigation.navigateToNoteEditScreen(it) },
                    contentType = contentType,
                    actionBarType = actionBarType,
                    onDrawerOpen = { coroutineScope.launch { drawerState.open() } }
                )
            }
        }

    }
}
