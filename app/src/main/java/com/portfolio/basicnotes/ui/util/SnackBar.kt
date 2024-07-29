package com.portfolio.basicnotes.ui.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.portfolio.basicnotes.ui.theme.BasicTodoTheme

@Composable
fun BasicNotesSnackBar(
    data: SnackbarData,
    modifier: Modifier = Modifier
) {
    Snackbar(
        snackbarData = data,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        actionColor = MaterialTheme.colorScheme.primary
    )
}

// Mock implementation of SnackbarData for preview purposes
class MockSnackbarData(
    private val message: String,
    private val actionLabel: String? = null
) : SnackbarData {
    override val visuals: SnackbarVisuals
        get() = object : SnackbarVisuals {
            override val message: String = this@MockSnackbarData.message
            override val actionLabel: String? = this@MockSnackbarData.actionLabel
            override val duration: SnackbarDuration = SnackbarDuration.Indefinite
            override val withDismissAction: Boolean = false
        }

    override fun dismiss() {}
    override fun performAction() {}
}

@Preview
@Composable
fun BasicNotesSnackBarPreview() {
    BasicTodoTheme {
        BasicNotesSnackBar(
            data = MockSnackbarData(
                message = "This is a mock snackbar message",
                actionLabel = "Action"
            )
        )
    }
}