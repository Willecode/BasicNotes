package com.portfolio.basicnotes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.portfolio.basicnotes.ui.BasicNotesNavGraph
import com.portfolio.basicnotes.ui.BasicNotesNavigation
import com.portfolio.basicnotes.ui.BasicNotesRoutes.NOTES_GRID_ROUTE
import com.portfolio.basicnotes.ui.BasicNotesRoutes.NOTES_LIST_ROUTE
import com.portfolio.basicnotes.ui.theme.BasicTodoTheme
import com.portfolio.basicnotes.ui.util.BasicNoteActionBarType
import com.portfolio.basicnotes.ui.util.BasicNotesBottomBar
import com.portfolio.basicnotes.ui.util.BasicNotesSideRail
import com.portfolio.basicnotes.ui.util.NoteListContentType
import com.portfolio.basicnotes.ui.util.createBottomBarFabItem
import com.portfolio.basicnotes.ui.util.createBottomBarItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun BasicNotesApp(
    windowSize: WindowWidthSizeClass
    ) {

    val (actionBarType: BasicNoteActionBarType, contentType: NoteListContentType)
        = adaptToWindowSize(windowSize)

    BasicTodoTheme {
        val navController = rememberNavController()
        val navigation = remember(navController) { BasicNotesNavigation(navController) }
        val bottomBarItems = remember(navigation) { createBottomBarItems(navigation) }
        val bottomBarFabItem = remember(navigation) { createBottomBarFabItem(navigation) }
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val coroutineScope: CoroutineScope = rememberCoroutineScope()

        Surface(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                bottomBar = {
                    if (useBottomBar(currentRoute, actionBarType)) {
                        BasicNotesBottomBar(
                            currentRoute = currentRoute!!,
                            actions = bottomBarItems,
                            fabItem = bottomBarFabItem
                        )
                    }
                },
                contentWindowInsets = WindowInsets.navigationBars
            ) { contentPadding ->
                val showNavRail = useNavRail(currentRoute, actionBarType)
                Box(modifier = Modifier.padding(contentPadding)) {
                    BasicNotesNavGraph(
                        navController = navController,
                        navigation = navigation,
                        modifier = Modifier
                            .padding(start = if (showNavRail) dimensionResource(id = R.dimen.side_rail_width) else 0.dp),
                        drawerState = drawerState,
                        contentType = contentType,
                        actionBarType = actionBarType
                    )
                }
                if (showNavRail) {
                    BasicNotesSideRail(
                        currentRoute = currentRoute!!,
                        actions = bottomBarItems,
                        fabItem = bottomBarFabItem,
                        onDrawerOpen = {coroutineScope.launch { drawerState.open() }}
                    )
                }
            }
        }
    }
}

@Composable
private fun useBottomBar( currentRoute: String?, actionBarType: BasicNoteActionBarType): Boolean =
    (currentRoute in arrayOf(NOTES_GRID_ROUTE, NOTES_LIST_ROUTE)
            && actionBarType == BasicNoteActionBarType.BOTTOM_BAR)

@Composable
private fun useNavRail( currentRoute: String?, actionBarType: BasicNoteActionBarType): Boolean =
    (currentRoute in arrayOf(NOTES_GRID_ROUTE, NOTES_LIST_ROUTE)
            && actionBarType == BasicNoteActionBarType.RAIL)

@Composable
private fun adaptToWindowSize(windowSize: WindowWidthSizeClass): Pair<BasicNoteActionBarType, NoteListContentType> {
    val actionBarType: BasicNoteActionBarType
    val contentType: NoteListContentType
    when (windowSize) {
        WindowWidthSizeClass.Compact -> {
            actionBarType = BasicNoteActionBarType.BOTTOM_BAR
            contentType = NoteListContentType.LIST_ONLY
        }

        WindowWidthSizeClass.Medium -> {
            actionBarType = BasicNoteActionBarType.RAIL
            contentType = NoteListContentType.LIST_ONLY
        }

        WindowWidthSizeClass.Expanded -> {
            actionBarType = BasicNoteActionBarType.RAIL
            contentType = NoteListContentType.LIST_AND_DETAIL
        }

        else -> {
            actionBarType = BasicNoteActionBarType.BOTTOM_BAR
            contentType = NoteListContentType.LIST_ONLY
        }
    }
    return Pair(actionBarType, contentType)
}

