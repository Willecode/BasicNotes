package com.portfolio.basicnotes.ui.notesgrid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.portfolio.basicnotes.R
import com.portfolio.basicnotes.data.Note
import com.portfolio.basicnotes.ui.util.LoadingAnimation
import com.portfolio.basicnotes.ui.util.NotesGridTopAppBar

@Composable
fun NotesGridScreen(
    modifier: Modifier = Modifier,
    notesGridviewModel: NotesGridviewModel = hiltViewModel(),
    onAddNewNote: () -> Unit,
    onOpenDrawer: () -> Unit,
    onNoteClicked: (Int) -> Unit
) {
    val uiState = notesGridviewModel.uiState.collectAsState()
    Scaffold (
        topBar = { NotesGridTopAppBar(onOpenDrawer)},
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNewNote) {
                Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.content_description_add))
            }
        },
        modifier = modifier
    ) { paddingValues ->
        if (uiState.value.stateLoaded) {
            NotesGrid(paddingValues, uiState, onNoteClicked)
        }
        else {
            LoadingAnimation(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            )
        }
    }
}

@Composable
private fun NotesGrid(
    paddingValues: PaddingValues,
    uiState: State<UiState>,
    onNoteClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalStaggeredGrid(
        modifier = modifier.padding(paddingValues),
        columns = StaggeredGridCells.Adaptive(200.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalItemSpacing = 5.dp
    ) {
        items(uiState.value.notes) { entry ->
            EntryCard(note = entry, onNoteClicked = { onNoteClicked(entry.id) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryCard(modifier: Modifier = Modifier, note: Note, onNoteClicked: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        colors = CardDefaults.cardColors(
            containerColor = Color(colorResource(id = note.noteColor).hashCode()),
            contentColor = Color(colorResource(id = R.color.black).hashCode())
        ),
        onClick = {onNoteClicked()}
    ) {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 15.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = note.title, fontWeight = FontWeight.Bold, fontSize = integerResource(id = R.integer.large_text).sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = note.content,
                fontSize = integerResource(id = R.integer.small_text).sp,
                lineHeight = integerResource(id = R.integer.small_text_line_spacing).sp
            )
        }
    }
}