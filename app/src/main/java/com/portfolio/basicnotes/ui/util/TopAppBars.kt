package com.portfolio.basicnotes.ui.util

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.portfolio.basicnotes.R
import com.portfolio.basicnotes.ui.theme.BasicTodoTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MainTopBarWithDrawer(
    onDrawerOpen: () -> Unit,
    onDeletePressed: () -> Unit,
    onPalettePressed: () -> Unit,
    onSelectAllPressed: () -> Unit,
    onDeselectAllPressed: () -> Unit,
    actionIconsVisible: Boolean
) {
    TopAppBar(
        colors = topAppBarColors(),
        title = {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.basicnotes_launcher_foreground),
                    contentDescription = stringResource(id = R.string.main_title),
                    modifier = Modifier.size(50.dp),
                    tint = Color.Unspecified
                )
                Text(text = stringResource(id = R.string.main_title))
                }
            },
        navigationIcon = {
            IconButton(
                onClick = onDrawerOpen
            ) {
                Icon(Icons.Filled.Menu, "Open drawer")
            }
        },
        actions = {
            if (actionIconsVisible) {
                ActionIconButtons(
                    onSelectAllPressed = onSelectAllPressed,
                    onDeselectAllPressed = onDeselectAllPressed,
                    onPalettePressed = onPalettePressed,
                    onDeletePressed = onDeletePressed
                )
            }
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MainTopBar(
    onDeletePressed: () -> Unit,
    onPalettePressed: () -> Unit,
    onSelectAllPressed: () -> Unit,
    onDeselectAllPressed: () -> Unit,
    actionIconsVisible: Boolean
) {
    TopAppBar(
        colors = topAppBarColors(),
        title = {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.basicnotes_launcher_foreground),
                    contentDescription = stringResource(id = R.string.main_title),
                    modifier = Modifier.size(50.dp),
                    tint = Color.Unspecified
                )
                Text(text = stringResource(id = R.string.main_title))
            }
        },
        actions = {
            if (actionIconsVisible) {
                ActionIconButtons(
                    onSelectAllPressed = onSelectAllPressed,
                    onDeselectAllPressed = onDeselectAllPressed,
                    onPalettePressed = onPalettePressed,
                    onDeletePressed = onDeletePressed
                )
            }
        }
    )
}

@Composable
private fun ActionIconButtons(
    onSelectAllPressed: () -> Unit,
    onDeselectAllPressed: () -> Unit,
    onPalettePressed: () -> Unit,
    onDeletePressed: () -> Unit
) {
    IconButtonSelectAll(onSelectAllPressed)
    IconButtonDeselectAll(onDeselectAllPressed)
    IconButtonPalette(onPalettePressed)
    IconButtonDelete(onDeletePressed)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun topAppBarColors() = TopAppBarColors(
    containerColor = MaterialTheme.colorScheme.surface,
    scrolledContainerColor = MaterialTheme.colorScheme.surface,
    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
    titleContentColor = MaterialTheme.colorScheme.onSurface,
    actionIconContentColor = MaterialTheme.colorScheme.onSurface
)

@Composable
private fun IconButtonDelete(onDeletePressed: () -> Unit) {
    IconButton(
        onClick = onDeletePressed,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.outline_delete_24),
            "Delete"
        )
    }
}

@Composable
private fun IconButtonPalette(onPalettePressed: () -> Unit) {
    IconButton(
        onClick = onPalettePressed,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.outline_palette_24),
            "Change color"
        )
    }
}

@Composable
private fun IconButtonSelectAll(onSelectAllPressed: () -> Unit) {
    IconButton(
        onClick = onSelectAllPressed,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.outline_select_all_24),
            "Select all"
        )
    }
}

@Composable
private fun IconButtonDeselectAll(onDeselectAllPressed: () -> Unit) {
    IconButton(
        onClick = onDeselectAllPressed,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.outline_deselect_24),
            "Deselect all"
        )
    }
}

@Composable
fun AdaptiveTopBar(
    actionBarType: BasicNoteActionBarType,
    onDeletePressed: () -> Unit = {},
    onPalettePressed: () -> Unit = {},
    onSelectAllPressed: () -> Unit,
    onDeselectAllPressed: () -> Unit,
    actionIconsVisible: Boolean = false,
    onDrawerOpen: () -> Unit = {}
) {
    when (actionBarType){
        BasicNoteActionBarType.RAIL -> {
            MainTopBar(
                onDeletePressed = onDeletePressed,
                actionIconsVisible = actionIconsVisible,
                onPalettePressed = onPalettePressed,
                onSelectAllPressed = onSelectAllPressed,
                onDeselectAllPressed = onDeselectAllPressed
            )
        }
        BasicNoteActionBarType.BOTTOM_BAR -> {
            MainTopBarWithDrawer(
                onDrawerOpen = onDrawerOpen,
                onDeletePressed = onDeletePressed,
                onPalettePressed = onPalettePressed,
                actionIconsVisible = actionIconsVisible,
                onSelectAllPressed = onSelectAllPressed,
                onDeselectAllPressed = onDeselectAllPressed
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditTopAppBar(
    onBackPressed: () -> Unit,
    onDeletePressed: () -> Unit
) {
    TopAppBar(
        colors = topAppBarColors(),
        title = {},
        navigationIcon = {
            IconButtonBack(onBackPressed)
        },
        actions = {
            IconButtonDelete(onDeletePressed)
        }
    )
}

@Composable
private fun IconButtonBack(onBackPressed: () -> Unit) {
    IconButton(
        onClick = onBackPressed
    ) {
        Icon(Icons.Filled.ArrowBack, "Go back")
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TopBarWithDrawerPreview() {
    BasicTodoTheme {
        MainTopBarWithDrawer({}, {}, {}, {}, {}, true)
    }
}

@Preview
@Composable
fun NoteEditTopAppBarPreview() {
    BasicTodoTheme{
        NoteEditTopAppBar({}, {})
    }
}