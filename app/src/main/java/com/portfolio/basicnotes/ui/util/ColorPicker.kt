package com.portfolio.basicnotes.ui.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.portfolio.basicnotes.R
import com.portfolio.basicnotes.data.NoteColorPalette
import com.portfolio.basicnotes.ui.theme.BasicTodoTheme
import kotlinx.coroutines.delay

@Composable
fun ColorPickerDialog(
    onColorPicked: (Int) -> Unit,
    onCancelPressed: () -> Unit
) {
    AlertDialog(
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.outline_palette_24),
                contentDescription = ""
            )
        },
        title = {
            Text(text = "Choose new color for selected notes")
        },
        text = {
            ColorPicker(colors = NoteColorPalette.colors, onColorPicked = onColorPicked)
        },
        onDismissRequest = {},
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = onCancelPressed
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ColorPicker(
    colors: List<Int>,
    onColorPicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val selected = remember {
        mutableStateOf<Int?>(null)
    }

    val buttonsEnabled = remember {
        mutableStateOf(true)
    }

    selected.value?.let {
        LaunchedEffect(key1 = it) {
            delay(500L)
            onColorPicked(it)
        }
    }

    LazyVerticalGrid(
        verticalArrangement = Arrangement.Center,
        horizontalArrangement = Arrangement.Center,
        columns = GridCells.Fixed(3),
        modifier = modifier
    ) {
        items(colors) { colorRes ->
            RadioButton(
                selected = colorRes == selected.value,
                onClick = {
                    buttonsEnabled.value = false
                    selected.value = colorRes
                },
                colors = RadioButtonColors(
                    selectedColor = colorResource(id = colorRes),
                    unselectedColor = colorResource(id = colorRes),
                    disabledSelectedColor = colorResource(id = colorRes),
                    disabledUnselectedColor = colorResource(id = colorRes)
                ),
                enabled = buttonsEnabled.value
            )
        }
    }
}

@Preview
@Composable
fun ColorPickerDialogPreview() {
    BasicTodoTheme {
        ColorPickerDialog(onColorPicked = {}) {}
    }
}