package com.portfolio.basicnotes.ui.notelist

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.portfolio.basicnotes.R
import com.portfolio.basicnotes.data.Note
import com.portfolio.basicnotes.ui.noteedit.NoteEditor
import com.portfolio.basicnotes.ui.theme.BasicTodoTheme
import com.portfolio.basicnotes.ui.util.AdaptiveTopBar
import com.portfolio.basicnotes.ui.util.BasicNoteActionBarType
import com.portfolio.basicnotes.ui.util.ColorPickerDialog
import com.portfolio.basicnotes.ui.util.LoadingAnimation
import com.portfolio.basicnotes.ui.util.NoteListContentType
import java.time.LocalDate

@Composable
fun NoteListScreen(
    modifier: Modifier = Modifier,
    viewModel: NoteListViewModel = hiltViewModel(),
    onNoteClicked: (Int) -> Unit,
    contentType: NoteListContentType,
    actionBarType: BasicNoteActionBarType,
    onDrawerOpen: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState()
    var showPaletteDialog by remember { mutableStateOf(false) }
    if (uiState.value.stateLoaded) {
        Scaffold (
            modifier = modifier,
            topBar = {
                AdaptiveTopBar(
                    actionBarType = actionBarType,
                    onDrawerOpen = onDrawerOpen,
                    onDeletePressed = { viewModel.deleteSelectedNotes() },
                    actionIconsVisible = shouldActionIconsBeVisible(uiState),
                    onPalettePressed = { showPaletteDialog = true },
                    onSelectAllPressed = { viewModel.selectAllNotes() },
                    onDeselectAllPressed = { viewModel.deselectAllNotes() }
                )
            }
        ) { paddingValues ->
            when (contentType) {
                NoteListContentType.LIST_ONLY -> {
                    NoteList(
                        modifier = Modifier.padding(paddingValues),
                        uiState = uiState.value,
                        onNoteClicked =
                        if (uiState.value.isSelectionMode) {
                            { viewModel.toggleNoteSelected(it) }
                        } else
                            onNoteClicked,
                        onNoteLongClicked = { viewModel.toggleNoteSelected(it) },
                        noteSelectionStates = uiState.value.noteSelectionStates
                    )
                }
                NoteListContentType.LIST_AND_DETAIL -> {
                    ListDetailLayout(
                        modifier = Modifier.padding(paddingValues),
                        uiState = uiState.value,
                        onChooseColor = { viewModel.updateColor(it) },
                        onTitleChange = { viewModel.updateTitle(it) },
                        onContentChange = { viewModel.updateContent(it) },
                        activeNote = uiState.value.activeNote,
                        onNoteSave = { viewModel.saveNote() },
                        onNoteClicked =
                        if (uiState.value.isSelectionMode) {
                            { viewModel.toggleNoteSelected(it) }
                        } else {
                            { viewModel.setActiveNote(it) }
                        },
                        onNoteLongClicked = { viewModel.toggleNoteSelected(it) },
                        noteSelectionStates = uiState.value.noteSelectionStates
                    )
                }
            }
            if (showPaletteDialog) {
                ColorPickerDialog(
                    onColorPicked = {
                        viewModel.changeSelectedNotesColor(it)
                        showPaletteDialog = false
                    },
                    onCancelPressed = { showPaletteDialog = false }
                )
            }
        }
    }
    else {
        LoadingAnimation(
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

@Composable
private fun shouldActionIconsBeVisible(uiState: State<NoteListUiState>) =
    (uiState.value.isSelectionMode || uiState.value.activeNote != null)

@Composable
fun NoteList(
    modifier: Modifier = Modifier,
    uiState: NoteListUiState,
    onNoteClicked: (Int) -> Unit,
    onNoteLongClicked: (Int) -> Unit,
    noteSelectionStates: Map<Int, Boolean>,
    activeNote: Note? = null
) {
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        items(uiState.notes) {
            NoteListEntry(
                note = it,
                onNoteClicked = onNoteClicked,
                onNoteLongClicked = onNoteLongClicked,
                isSelected = isNoteSelected(
                    noteSelectionStates = noteSelectionStates,
                    note = it
                ),
                isActive = activeNote?.id == it.id
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
        }
    }
}

private fun isNoteSelected(
    noteSelectionStates: Map<Int, Boolean>,
    note: Note
) = noteSelectionStates[note.id] ?: false

@Composable
private fun ListDetailLayout(
    modifier: Modifier = Modifier,
    uiState: NoteListUiState,
    onChooseColor: (Int) -> Unit,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    activeNote: Note?,
    onNoteClicked: (Int) -> Unit,
    onNoteLongClicked: (Int) -> Unit,
    onNoteSave: () -> Unit,
    noteSelectionStates: Map<Int, Boolean>
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        NoteList(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            uiState = uiState,
            onNoteClicked = onNoteClicked,
            onNoteLongClicked = onNoteLongClicked,
            noteSelectionStates = noteSelectionStates,
            activeNote = activeNote
        )
        Spacer(modifier = Modifier.weight(0.01f))
        if (activeNote != null) {
            Scaffold (
                modifier = Modifier.weight(1f),
                contentWindowInsets = WindowInsets(0),
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = onNoteSave,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    ) {
                        Icon(painterResource(R.drawable.outline_save_24), "Save note")
                    }
                },
                content = {paddingValues ->
                    NoteEditor(
                        modifier = Modifier.padding(paddingValues = paddingValues),
                        onChooseColor = onChooseColor,
                        chosenColor = activeNote.noteColor,
                        title = activeNote.title,
                        content = activeNote.content,
                        onContentChange = onContentChange,
                        onTitleChange = onTitleChange
                    )
                }
            )
        }
        else {
            Text(
                text = "Select a note to view",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
                )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteListEntry(
    modifier: Modifier = Modifier,
    note: Note,
    onNoteClicked: (Int) -> Unit,
    onNoteLongClicked: (Int) -> Unit,
    isActive: Boolean,
    isSelected: Boolean
) {
    val bgColor = getEntryBgColor(isActive, isSelected)
    val contentColor = MaterialTheme.colorScheme.contentColorFor(bgColor)
    Row(
        modifier = modifier
            .background(bgColor)
            .fillMaxWidth()
            .height(70.dp)
            .combinedClickable(
                onClick = { onNoteClicked(note.id) },
                onLongClick = { onNoteLongClicked(note.id) }
            )
//            .then(
//                if (isSelected) {
//                    Modifier.border(
//                        BorderStroke(
//                            dimensionResource(id = R.dimen.selection_border_width),
//                            colorResource(id = R.color.selected_note_border)
//                        )
//                    )
//                } else {
//                    Modifier
//                }
//            )
            .padding(10.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Square(modifier = Modifier.size(30.dp), color = colorResource(id = note.noteColor))
        Spacer(modifier = Modifier.weight(0.1f, fill = true))
        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.weight(1f)
        ) {
            Spacer(modifier = Modifier.weight(0.2f, fill = true))
            Text(
                text = note.title,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                maxLines = 1,
                modifier = Modifier.weight(0.8f),
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = note.date.toString(),
                fontWeight = FontWeight.Light,
                color = contentColor,
                maxLines = 1,
                modifier = Modifier.weight(0.8f)
            )
            Spacer(modifier = Modifier.weight(0.2f))

        }
        Spacer(modifier = Modifier.weight(0.1f, fill = true))
        Text(
            text = note.content,
            color = contentColor,
            maxLines = 1,
            modifier = Modifier.weight(1.8f, fill = true),
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun getEntryBgColor(isActive: Boolean, isSelected: Boolean): Color {
    val bgColor = if (isActive) {
        MaterialTheme.colorScheme.surfaceContainerHighest
    } else if (isSelected) {
        MaterialTheme.colorScheme.surfaceContainerHigh
    } else {
        MaterialTheme.colorScheme.surfaceContainerLow
    }
    return bgColor
}

@Composable
fun Square(
    modifier: Modifier = Modifier,
    color: Color = Color.Magenta
) {
    Box (
        modifier = modifier
            .background(
                color = color,
                shape = RoundedCornerShape(8.dp)
            )
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NoteListEntrySelectedPreview() {
    BasicTodoTheme {
        NoteListEntry(
            note = Note(0, "Title", "Sample text", LocalDate.now(), R.color.post_it_blue),
            onNoteClicked = {},
            onNoteLongClicked = {},
            isSelected = true,
            isActive = false
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NoteListEntryActivePreview() {
    BasicTodoTheme {
        NoteListEntry(
            note = Note(0, "Title", "Sample text", LocalDate.now(), R.color.post_it_blue),
            onNoteClicked = {},
            onNoteLongClicked = {},
            isSelected = false,
            isActive = true
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NoteListPreview() {
    BasicTodoTheme {
        NoteList(
            uiState = buildMockUiState(),
            onNoteClicked = {},
            onNoteLongClicked = {},
            noteSelectionStates = emptyMap()
        )
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    widthDp = 840
)
@Composable
private fun ListDetailLayoutPreview() {
    BasicTodoTheme {
        val uiState = buildMockUiState()
        ListDetailLayout(
            uiState = buildMockUiState(),
            onChooseColor = {},
            onTitleChange = {},
            onContentChange = {},
            activeNote = uiState.notes[0],
            onNoteClicked = {},
            onNoteLongClicked = {},
            noteSelectionStates = emptyMap(),
            onNoteSave = {}
        )
    }
}

private fun buildMockUiState(): NoteListUiState {
    return NoteListUiState(
        stateLoaded = true,
        notes = listOf(
            Note(0,"Title", "Sample text", LocalDate.now(), R.color.post_it_gray),
            Note(1,"Title", "Sample text", LocalDate.now(), R.color.post_it_blue),
            Note(2,"Title", "Sample text", LocalDate.now(), R.color.post_it_purple)
        )
    )
}
