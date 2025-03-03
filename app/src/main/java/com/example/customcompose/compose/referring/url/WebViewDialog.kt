package com.example.customcompose.compose.referring.url

import android.app.Activity
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.viewmodel.BlockListViewModel

@Composable
fun WebViewDialog(
    block: Block,
    url: String,
    blockListViewModel: BlockListViewModel,
    destination: String,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val question = block.question?.slug ?: ""
    val currentBlockId = block.id ?: ""

    Dialog(
        onDismissRequest = {
            val surveyHistoryModel = SurveyHistoryModel(
                question = question,
                answer = "Yes",
                id = currentBlockId
            )
            block.surveyHistoryModel = listOf(surveyHistoryModel)

            block.options?.get(0)?.referTo?.id?.let { blockId ->
                block.options[0].referTo?.group_no?.let { groupId ->
                    if (destination == "mainSurvey") {
                        blockListViewModel.addBlockToTheSurveyFlow(blockId, groupId)
                    }else{
                        blockListViewModel.addBlockToTheCheckList(blockId, groupId)
                    }
                }
            }
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true
        )
    ) {
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        Box(
            Modifier
                .fillMaxSize()
                .background(Color.White)
                .windowInsetsPadding(
                    WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Vertical)
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

                    val surveyHistoryModel = SurveyHistoryModel(
                        question = question,
                        answer = "Yes",
                        id = currentBlockId
                    )
                    block.surveyHistoryModel = listOf(surveyHistoryModel)

                    block.options?.get(0)?.referTo?.id?.let { blockId ->
                        block.options[0].referTo?.group_no?.let { groupId ->
                            if (destination == "mainSurvey") {
                                blockListViewModel.addBlockToTheSurveyFlow(blockId, groupId)
                            }else{
                                blockListViewModel.addBlockToTheCheckList(blockId, groupId)
                            }
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