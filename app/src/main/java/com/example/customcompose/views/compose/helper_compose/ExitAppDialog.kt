package com.example.customcompose.views.compose.helper_compose

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.activity.compose.LocalActivity

@Composable
fun ExitAppDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit
) {
    val activity = LocalActivity.current

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = {
                Text(text = "Exit App?")
            },
            text = {
                Text("Do you want to exit the app?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        activity?.finishAffinity() // Closes the whole app
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onDismiss() }
                ) {
                    Text("No")
                }
            }
        )
    }
}
