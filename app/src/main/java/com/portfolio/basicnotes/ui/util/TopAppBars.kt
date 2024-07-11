package com.portfolio.basicnotes.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.portfolio.basicnotes.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesGridTopAppBar(
    openDrawer: () -> Unit
) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.main_title)) },
        navigationIcon = {
            IconButton(
                onClick = openDrawer
            ) {
                Icon(Icons.Filled.Menu,"")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditTopAppBar(
    openDrawer: () -> Unit,
    onBackPressed: () -> Unit,
    onDeletePressed: () -> Unit
) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.main_title)) },
        navigationIcon = {
            IconButton(
                onClick = onBackPressed
            ) {
                Icon(Icons.Filled.ArrowBack,"Go back")
            }
        },
        actions = {
            IconButton(
                onClick = onDeletePressed,
            ) {
                Icon(Icons.Filled.Delete, "Delete")
            }
        }
    )
}

@Preview
@Composable
fun NotesGridTopAppBarPreview() {
    NotesGridTopAppBar {

    }
}

@Preview
@Composable
fun NoteEditTopAppBarPreview() {
    NoteEditTopAppBar({},{},{})
}