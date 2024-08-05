package com.portfolio.basicnotes.ui.notesgrid

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.portfolio.basicnotes.R
import com.portfolio.basicnotes.data.Note
import com.portfolio.basicnotes.ui.theme.BasicTodoTheme
import com.portfolio.basicnotes.ui.util.AdaptiveTopBar
import com.portfolio.basicnotes.ui.util.BasicNoteActionBarType
import com.portfolio.basicnotes.ui.util.BasicNotesSnackBar
import com.portfolio.basicnotes.ui.util.ColorPickerDialog
import com.portfolio.basicnotes.ui.util.DeleteConfirmDialog
import com.portfolio.basicnotes.ui.util.LoadingAnimation
import java.time.LocalDate

@Composable
fun NotesGridScreen(
    modifier: Modifier = Modifier,
    notesGridviewModel: NotesGridviewModel = hiltViewModel(),
    onNoteClicked: (Int) -> Unit,
    actionBarType: BasicNoteActionBarType,
    onDrawerOpen: () -> Unit,
    @StringRes userMessage: Int?,
    onUserMessageDisplayed: () -> Unit
) {
    val uiState = notesGridviewModel.uiState.collectAsState().value

    userMessage?.let {
        LaunchedEffect(key1 = it) {
            notesGridviewModel.updateUserMessage(it)
        }
    }

    if (uiState.stateLoaded) {
        val isSelectionMode = uiState.isSelectionMode
        NotesGridScaffold(
            modifier = modifier,
            onNoteLongClicked = { notesGridviewModel.toggleNoteSelected(it) },
            actionBarType = actionBarType,
            onDrawerOpen = onDrawerOpen,
            onDelete = { notesGridviewModel.deleteSelectedNotes() },
            onColorPicked = { notesGridviewModel.changeSelectedNotesColor(it) },
            onSelectAllPressed = { notesGridviewModel.selectAllNotes() },
            onDeselectAllPressed = { notesGridviewModel.deselectAllNotes() },
            actionIconsVisible = isSelectionMode,
            onUserMessageDisplayed = {
                notesGridviewModel.clearUserMessage()
                onUserMessageDisplayed()
            },
            userMessage = uiState.userMessage,
            selectedNotes = uiState.selectedNotes,
            notes = uiState.notes,
            onNoteClicked =
            if (isSelectionMode) {
                { notesGridviewModel.toggleNoteSelected(it) }
            } else {
                onNoteClicked
            },
        )
    }
    else {
        LoadingAnimation(
            modifier = modifier
                .fillMaxSize()
        )
    }
}

@Composable
fun NotesGridScaffold(
    modifier: Modifier = Modifier,
    onNoteClicked: (Int) -> Unit,
    onNoteLongClicked: (Int) -> Unit,
    actionBarType: BasicNoteActionBarType,
    onDrawerOpen: () -> Unit,
    onDelete: () -> Unit,
    onColorPicked: (Int) -> Unit,
    onSelectAllPressed: () -> Unit,
    onDeselectAllPressed: () -> Unit,
    actionIconsVisible: Boolean,
    notes: List<Note>,
    selectedNotes: Set<Int>,
    @StringRes userMessage: Int,
    onUserMessageDisplayed: () -> Unit
) {
    var showPaletteDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    BasicNotesSnackBar(data = data)
                }
            )
        },
        topBar = {
            AdaptiveTopBar(
                actionBarType = actionBarType,
                onDrawerOpen = onDrawerOpen,
                onDeletePressed = { showDeleteConfirmDialog = true },
                actionIconsVisible = actionIconsVisible,
                onPalettePressed = { showPaletteDialog = true },
                onSelectAllPressed = onSelectAllPressed,
                onDeselectAllPressed = onDeselectAllPressed
            )
        }
    ) { paddingValues ->
        NotesGrid(
            onNoteClicked = onNoteClicked,
            onNoteLongClicked = onNoteLongClicked,
            modifier = modifier.padding(paddingValues),
            notes = notes,
            selectedNotes = selectedNotes
        )

        // Display user message if there is one
        userMessage.let {
            if (it != 0) {
                val snackbarText = stringResource(it)
                LaunchedEffect(it) {
                    snackbarHostState.showSnackbar(snackbarText)
                    onUserMessageDisplayed()
                }
            }
        }
    }
    if (showPaletteDialog) {
        ColorPickerDialog(
            onColorPicked = {
                onColorPicked(it)
                showPaletteDialog = false
            },
            onCancelPressed = { showPaletteDialog = false }
        )
    }
    if (showDeleteConfirmDialog) {
        DeleteConfirmDialog(
            text = "Selected notes are going to be permanently deleted. Are you sure?",
            confirmButtonText = stringResource(id = R.string.dialog_yes),
            onCancel = { showDeleteConfirmDialog = false },
            onConfirm = {
                onDelete()
                showDeleteConfirmDialog = false
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NotesGrid(
    onNoteClicked: (Int) -> Unit,
    onNoteLongClicked: (Int) -> Unit,
    modifier: Modifier = Modifier,
    notes: List<Note>,
    selectedNotes: Set<Int>
) {
    LazyVerticalStaggeredGrid(
        modifier = modifier,
        columns = StaggeredGridCells.Adaptive(200.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalItemSpacing = 5.dp
    ) {
        items(notes) { entry ->
            EntryCard(
                note = entry,
                isSelected = selectedNotes.contains(entry.id),
                modifier = Modifier.combinedClickable(
                    onClick = { onNoteClicked(entry.id) },
                    onLongClick = { onNoteLongClicked(entry.id) }
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryCard(
    modifier: Modifier = Modifier,
    note: Note,
    isSelected: Boolean
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        colors = CardDefaults.cardColors(
            containerColor = Color(colorResource(id = note.noteColor).hashCode()),
            contentColor = Color(colorResource(id = R.color.black).hashCode())
        ),
        border =
        if (isSelected)
            BorderStroke(
                dimensionResource(id = R.dimen.selection_border_width),
                colorResource(id = R.color.selected_note_border)
            )
        else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 15.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = note.title,
                fontWeight = FontWeight.Bold,
                fontSize = integerResource(id = R.integer.large_text).sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = note.content,
                fontSize = integerResource(id = R.integer.small_text).sp,
                lineHeight = integerResource(id = R.integer.small_text_line_spacing).sp
            )
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    widthDp = 500
)
@Composable
private fun PreviewEntryCard() {
    BasicTodoTheme {
        NotesGrid(
            onNoteClicked = {},
            onNoteLongClicked = {},
            notes = generateMockNotes(),
            selectedNotes = setOf(0, 1, 2, 3, 4, 5, 6, 7, 8)
        )
    }
}

private fun generateMockNotes(): List<Note> {
    return listOf(
        Note(
            id = 0,
            title = "Title",
            content = "Sample text for the preview.Sample text for the preview.",
            date = LocalDate.now(),
            noteColor = R.color.post_it_pink
        ),
        Note(
            id = 1,
            title = "Title",
            content = "Sample text for the preview.",
            date = LocalDate.now(),
            noteColor = R.color.post_it_gray
        ),
        Note(
            id = 2,
            title = "Title",
            content = "Sample text for the preview.",
            date = LocalDate.now(),
            noteColor = R.color.post_it_purple
        ),
        Note(
            id = 3,
            title = "Title",
            content = "Sample text for the preview.",
            date = LocalDate.now(),
            noteColor = R.color.post_it_blue
        ),
        Note(
            id = 4,
            title = "Title",
            content = "Sample text for the preview.",
            date = LocalDate.now(),
            noteColor = R.color.post_it_green
        ),
        Note(
            id = 5,
            title = "Title",
            content = "Sample text for the preview.Sample text for the preview.Sample text for the preview.",
            date = LocalDate.now(),
            noteColor = R.color.post_it_orange
        ),
        Note(
            id = 6,
            title = "Title",
            content = "Sample text for the preview.",
            date = LocalDate.now(),
            noteColor = R.color.post_it_white
        ),
        Note(
            id = 7,
            title = "Title",
            content = "Sample text for the preview.",
            date = LocalDate.now(),
            noteColor = R.color.post_it_yellow
        )
    )
}