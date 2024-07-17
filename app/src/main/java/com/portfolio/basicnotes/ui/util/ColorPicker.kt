package com.portfolio.basicnotes.ui.util

import android.app.Dialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import com.portfolio.basicnotes.R
import com.portfolio.basicnotes.data.NoteColorPalette
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.TextButton
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.portfolio.basicnotes.ui.theme.BasicTodoTheme

@Composable
fun ColorPickerDialog(
    onColorPicked: (Int) -> Unit,
    onCancelPressed: () -> Unit
) {
    AlertDialog(
        icon = {
            Icon(painter = painterResource(id = R.drawable.outline_palette_24), contentDescription = "")
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
    colors: List< Int>,
    onColorPicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid (
        verticalArrangement = Arrangement.Center,
        horizontalArrangement = Arrangement.Center,
        columns = GridCells.Fixed(3),
        modifier = modifier
    ) {
        items(colors) { colorRes ->
            Card(
                onClick = {onColorPicked(colorRes)},
                colors = CardColors(
                    colorResource(id = colorRes),
                    colorResource(id = colorRes),
                    colorResource(id = colorRes),
                    colorResource(id = colorRes)
                ),
                modifier = Modifier
                    .width(30.dp)
                    .height(30.dp)
                    .fillMaxSize()

            ) {}
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