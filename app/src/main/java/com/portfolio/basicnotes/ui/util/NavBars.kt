package com.portfolio.basicnotes.ui.util

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.portfolio.basicnotes.R
import com.portfolio.basicnotes.ui.BasicNotesNavigation
import com.portfolio.basicnotes.ui.BasicNotesRoutes
import com.portfolio.basicnotes.ui.theme.BasicTodoTheme

@Composable
fun BasicNotesBottomBar(
    modifier: Modifier = Modifier,
    currentRoute: String,
    actions: Array<BottomBarItem>,
    fabItem: BottomBarItem
) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surface,
        actions = {
            NavigationActions(actions, currentRoute)
        },
        floatingActionButton = {
            AddNoteFab(fabItem)
        }
    )
}

@Composable
private fun NavigationActions(
    actions: Array<BottomBarItem>,
    currentRoute: String,
) {
    actions.forEach {
        val isSelected = currentRoute == it.route
        IconButton(
            onClick = {
                if (!isSelected)
                    it.navigationFunction()
            }
        ) {
            Icon(
                painter = painterResource(id = it.icon),
                contentDescription = stringResource(it.title),
                tint = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AddNoteFab(
    fabItem: BottomBarItem,
) {
    FloatingActionButton(
        onClick = { fabItem.navigationFunction() },
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
    ) {
        Icon(painterResource(fabItem.icon), "Add")
    }
}

@Composable
private fun DrawerItem( onDrawerOpen: () -> Unit) {
    IconButton(onClick = onDrawerOpen) {
        Icon(painter = painterResource(id = R.drawable.baseline_menu_24), contentDescription = stringResource(R.string.drawer_label))

    }
}

@Composable
fun BasicNotesSideRail(
    modifier: Modifier = Modifier,
    currentRoute: String,
    actions: Array<BottomBarItem>,
    fabItem: BottomBarItem,
    onDrawerOpen: () -> Unit
) {
    NavigationRail(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        header = {
            DrawerItem(onDrawerOpen = onDrawerOpen)
            AddNoteFab(fabItem)
        }
    ) {
        NavigationActions(actions = actions, currentRoute = currentRoute)
    }
}

data class BottomBarItem(
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    val route: String,
    val navigationFunction: () -> Unit
)

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BasicNotesBottomBarPreview() {
    val items = arrayOf(
        BottomBarItem(R.string.notes_grid_bottom_bar_label, R.drawable.baseline_grid_view_24, BasicNotesRoutes.NOTES_GRID_ROUTE) {  },
        BottomBarItem(R.string.notes_list_bottom_bar_label, R.drawable.outline_view_list_24, BasicNotesRoutes.NOTES_LIST_ROUTE) {  }
    )
    val fabItem =  BottomBarItem(R.string.notes_grid_bottom_bar_label, R.drawable.baseline_add_24, BasicNotesRoutes.NOTE_EDIT_ROUTE) {  }
    BasicTodoTheme {
        BasicNotesBottomBar(currentRoute = "asdf", actions = items, fabItem = fabItem)
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BasicNotesSideRailPreview() {
    val items = arrayOf(
        BottomBarItem(R.string.notes_grid_bottom_bar_label, R.drawable.baseline_grid_view_24, BasicNotesRoutes.NOTES_GRID_ROUTE) {  },
        BottomBarItem(R.string.notes_list_bottom_bar_label, R.drawable.outline_view_list_24, BasicNotesRoutes.NOTES_LIST_ROUTE) {  }
    )
    val fabItem =  BottomBarItem(R.string.notes_grid_bottom_bar_label, R.drawable.baseline_add_24, BasicNotesRoutes.NOTE_EDIT_ROUTE) {  }
    BasicTodoTheme {
        BasicNotesSideRail(currentRoute = "asdf", actions = items, fabItem = fabItem,
            onDrawerOpen = {})
    }
}

fun createBottomBarItems(navigation: BasicNotesNavigation): Array<BottomBarItem> {
    return arrayOf(
        BottomBarItem(R.string.notes_grid_bottom_bar_label, R.drawable.baseline_grid_view_24, BasicNotesRoutes.NOTES_GRID_ROUTE) { navigation.navigateToNotesGridScreen() },
        BottomBarItem(R.string.notes_list_bottom_bar_label, R.drawable.outline_view_list_24, BasicNotesRoutes.NOTES_LIST_ROUTE) { navigation.navigateToNotesListScreen() }
    )
}

fun createBottomBarFabItem(navigation: BasicNotesNavigation): BottomBarItem {
    return BottomBarItem(R.string.notes_grid_bottom_bar_label, R.drawable.baseline_add_24, BasicNotesRoutes.NOTE_EDIT_ROUTE) { navigation.navigateToNoteEditScreen(null) }
}