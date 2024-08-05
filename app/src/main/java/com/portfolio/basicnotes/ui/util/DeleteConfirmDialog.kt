package com.portfolio.basicnotes.ui.util

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.portfolio.basicnotes.R
import com.portfolio.basicnotes.ui.theme.BasicTodoTheme

@Composable
fun DeleteConfirmDialog(
    modifier: Modifier = Modifier,
    text: String,
    confirmButtonText: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.outline_delete_24),
                contentDescription = ""
            )
        },
        text = {
            Text(text = text)
        },
        onDismissRequest = { onCancel() },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                }
            ) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onCancel()
                }
            ) {
                Text(stringResource(id = R.string.dialog_cancel))
            }
        }
    )
}

@Preview
@Composable
private fun DeleteConfirmDialogPreview() {
    BasicTodoTheme {
        DeleteConfirmDialog(
            text = "Are you sure you want to delete selected notes?",
            onConfirm = {},
            confirmButtonText = stringResource(id = R.string.dialog_confirm),
            onCancel = {}
        )
    }
}