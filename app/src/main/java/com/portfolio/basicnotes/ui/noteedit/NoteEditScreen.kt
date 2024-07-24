package com.portfolio.basicnotes.ui.noteedit

import androidx.activity.compose.BackHandler
import androidx.annotation.ColorRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.portfolio.basicnotes.R
import com.portfolio.basicnotes.data.NoteColorPalette
import com.portfolio.basicnotes.ui.util.LoadingAnimation
import com.portfolio.basicnotes.ui.util.NoteEditTopAppBar


@Composable
fun NoteEditScreen(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
    viewModel: NoteEditScreenViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()

    BackHandler {
        onBackLogic(viewModel, onBackPressed)
    }

        Scaffold(
            modifier = modifier,
            topBar = {
                NoteEditTopAppBar(
                    onBackPressed = { onBackLogic(viewModel, onBackPressed) },
                    onDeletePressed = { viewModel.deleteNoteAndGoBack(onBackPressed) }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { onBackLogic(viewModel, onBackPressed) },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = stringResource(id = R.string.content_description_save)
                    )
                }
            }
        ) {
            if (uiState.value.stateLoaded) {
                NoteEditor(
                    onChooseColor = { viewModel.updateColor(it) },
                    chosenColor = uiState.value.noteColor,
                    title = uiState.value.title,
                    content = uiState.value.content,
                    onContentChange = { viewModel.updateContent(it) },
                    onTitleChange = { viewModel.updateTitle(it) },
                    modifier = Modifier
                        .padding(paddingValues = it)
                        .fillMaxSize(),
                    focusOnTitle = viewModel.noteIsNew()
                )
            } else {
                LoadingAnimation(
                    modifier = Modifier
                        .padding(paddingValues = it)
                        .fillMaxSize()
                )
            }
        }
}

@Composable
fun NoteEditor(
    modifier: Modifier = Modifier,
    onChooseColor: (Int) -> Unit,
    @ColorRes chosenColor: Int,
    title: String,
    content: String,
    onContentChange: (String) -> Unit,
    onTitleChange: (String) -> Unit,
    focusOnTitle: Boolean = false
) {
    Column(modifier = modifier) {
        NoteEditorField(
            value = title,
            onValueChange = onTitleChange,
            chosenColor = chosenColor,
            label = stringResource(id = R.string.title_label),
            imeAction = ImeAction.Next,
            requestFocusImmediately = focusOnTitle
        )
        ColorChooser(chosenColor, onChooseColor, modifier = Modifier.fillMaxWidth())
        NoteEditorField(
            value = content,
            onValueChange = onContentChange,
            chosenColor = chosenColor,
            label = stringResource(id = R.string.text_label),
            modifier = Modifier.fillMaxHeight()
        )
    }
}

@Composable
fun ColorChooser(@ColorRes chosenColor: Int, onColorChosen: (Int) -> Unit, modifier: Modifier) {
    LazyRow (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
    ) {
        items(NoteColorPalette.colors) { colorRes ->
            RadioButton(
                selected = colorRes == chosenColor,
                onClick = { onColorChosen(colorRes) },
                colors = RadioButtonColors(
                    colorResource(id = colorRes),
                    colorResource(id = colorRes),
                    colorResource(id = colorRes),
                    colorResource(id = colorRes)
                )
            )
        }
    }
}

@Composable
private fun NoteEditorField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    @ColorRes chosenColor: Int,
    label: String,
    imeAction: ImeAction = ImeAction.Default,
    requestFocusImmediately: Boolean = false
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember {FocusRequester()}

    TextField(
        label = { Text(label) },
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        keyboardOptions = KeyboardOptions(imeAction = imeAction),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        colors = TextFieldDefaults.colors().copy(
            focusedContainerColor = colorResource(id = chosenColor),
            unfocusedContainerColor = colorResource(id = chosenColor),
            focusedTextColor = colorResource(id = R.color.black),
            unfocusedTextColor = colorResource(id = R.color.black),
            focusedLabelColor = colorResource(id = R.color.black),
            unfocusedLabelColor = colorResource(id = R.color.black)
        )
    )
    if (requestFocusImmediately) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}

private fun onBackLogic(
    viewModel: NoteEditScreenViewModel,
    onBackPressed: () -> Unit
) {
    if (viewModel.noteIsSaveable())
        viewModel.saveNoteAndGoBack(onBackPressed)
    else
        onBackPressed()
}

@Preview
@Composable
fun NoteEditorPreview() {
    NoteEditor(
        onChooseColor = {},
        chosenColor = R.color.post_it_orange,
        title = "Title",
        content = "Content",
        onContentChange = {},
        onTitleChange = {}
    )
}