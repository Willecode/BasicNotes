package com.portfolio.basicnotes.ui.util

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.portfolio.basicnotes.R

@Composable
fun BasicNotesModalDrawer(
    modifier: Modifier = Modifier,
    drawerState: DrawerState,
    content: @Composable () -> Unit
){
    var showAboutDialog by remember{mutableStateOf(false)}
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                //Text("Basic Notes", modifier = Modifier.padding(16.dp))
                //Divider()
                NavigationDrawerItem(
                    label = { Text(text = stringResource(id = R.string.drawer_item_about)) },
                    selected = false,
                    onClick = { showAboutDialog = true }
                )
            }
        }
    ) {
        content()
    }
    if (showAboutDialog) {
        AboutDialog { showAboutDialog = false }
    }
}

@Composable
fun AboutDialog(
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        icon = {
            Icon(Icons.Filled.Info, contentDescription = stringResource(id = R.string.drawer_item_about))
        },
        title = {
            Text(text = stringResource(id = R.string.about_app_title))
        },
        text = {
            Text(
                text = stringResource(id = R.string.about_app_content),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(stringResource(id = R.string.about_app_ok))
            }
        }
    )
}

@Preview
@Composable
fun AboutDialogPreview() {
    AboutDialog {
    }
}
