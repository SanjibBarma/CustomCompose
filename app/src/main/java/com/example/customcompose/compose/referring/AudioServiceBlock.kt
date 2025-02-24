package com.example.customcompose.compose.referring

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewModelScope
import com.example.customcompose.helper.AudioRecorderService
import com.example.customcompose.model.Block
import com.example.customcompose.viewmodel.BlockListViewModel
import kotlinx.coroutines.launch

@Composable
fun AudioServiceBlock(
    block: Block,
    blockListViewModel: BlockListViewModel,
    isActiveGroup: Boolean,
    destination: String
) {
    val context = LocalContext.current
    val currentGroupId = block.group
    val currentBlockId = block.id
    val coroutineScope = rememberCoroutineScope()

    if (block.type == "audio_start") {
        val intent = Intent(context, AudioRecorderService::class.java)
        context.startService(intent)
        if (currentBlockId != null && currentGroupId != null) {
            blockListViewModel.addBlockToTheSurveyFlow(currentBlockId, currentGroupId)
        }
    }

    if (block.type == "audio_end") {
        val intent = Intent(context, AudioRecorderService::class.java)
        context.stopService(intent)
        if (currentBlockId != null && currentGroupId != null) {
            blockListViewModel.addBlockToTheSurveyFlow(currentBlockId, currentGroupId)
        }
    }
}