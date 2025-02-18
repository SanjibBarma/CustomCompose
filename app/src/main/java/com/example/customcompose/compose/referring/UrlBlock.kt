package com.example.customcompose.compose.referring

import android.app.Activity
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.customcompose.model.Block
import com.example.customcompose.viewmodel.BlockListViewModel

@Composable
fun UrlBlock(block: Block, blockListViewModel: BlockListViewModel) {
    val stringUrl = block.options?.get(0)!!.value
    val isSkippable = block.skip?.id != "-1"
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            //.clickable (enabled = isActiveGroup){  }
    ) {
        Text(block.question!!.slug)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                .padding(16.dp)
                .clickable {
                    showDialog = true

                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringUrl,
                style = TextStyle(color = Color.Blue),
                modifier = Modifier.clickable {
                    showDialog = true
                }
            )
        }

        if (isSkippable) {
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    block.skip?.id?.let { blockId ->
                        block.skip.group_no.let { groupId ->
                            blockListViewModel.addBlockToTheList(blockId, groupId)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Skip")
            }
        }

        // Show the dialog for WebViewBlock when triggered
        if (showDialog) {
            WebViewDialog(
                block = block,
                url = stringUrl,
                blockListViewModel,
                onClose = {
                    showDialog = false // Close the dialog when the close button is clicked
                }
            )
        }
    }
}

@Composable
fun WebViewDialog(block: Block, url: String,  blockListViewModel: BlockListViewModel, onClose: () -> Unit) {
    val context = LocalContext.current
    val activity = context as? Activity // Cast to Activity to access window

    Dialog(
        onDismissRequest = {
            onClose
            block.options?.get(0)?.referTo?.id?.let { blockId ->
                block.options[0].referTo?.group_no?.let { groupId ->
                    blockListViewModel.addBlockToTheList(blockId, groupId)
                }
            }
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false, // Important: Prevent default width
            dismissOnBackPress = true
        )
    ) {
        // Set flags to occupy the entire screen, including behind system bars
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        // Use a Layout to handle insets correctly
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.White) // Or your desired background
                .windowInsetsPadding(
                    WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Vertical) // Handle system bar insets
                )
        ) {

            AndroidView(factory = {
                WebView(it).apply {
                    webViewClient = WebViewClient()
                    loadUrl(url)
                }
            }, modifier = Modifier.fillMaxSize())

            IconButton(
                onClick = {
                    // Clear flags when the dialog is dismissed
                    activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                    onClose()

                    block.options?.get(0)?.referTo?.id?.let { blockId ->
                        block.options[0].referTo?.group_no?.let { groupId ->
                            blockListViewModel.addBlockToTheList(blockId, groupId)
                        }
                    }
                },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopEnd)
                    .size(32.dp)
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                    contentDescription = "Close",
                    tint = Color.Red
                )
            }
        }
    }
}