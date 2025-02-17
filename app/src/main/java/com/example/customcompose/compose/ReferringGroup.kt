//package com.example.customcompose.compose
//
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.imePadding
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.Button
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.mutableStateMapOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import com.example.customcompose.model.Block
//import com.example.customcompose.model.SurveyDataModel
//import com.example.customcompose.views.SubmitButton
//import com.example.customcompose.views.TextInputBlock
//import com.example.customcompose.views.onReferToData
//
//@Composable
//fun ReferringGroup(group: List<Block>, inputData: MutableMap<String, String>, surveyDataModelList: List<SurveyDataModel>, loadedGroups: MutableList<List<Block>>, initialShowSubmit: Boolean, onShowSubmitChange: (Boolean) -> Unit) {
//    val visitedBlocks = remember { mutableStateListOf<Block>() }
//    val allBlocks = group.associateBy { it.id }
//    val scrollState = rememberScrollState()
//
//    var currentBlockId by remember { mutableStateOf(group.firstOrNull()?.id) }
//    var showSubmitButton by remember { mutableStateOf(initialShowSubmit) }
//
//    LaunchedEffect(currentBlockId) {
//        currentBlockId?.let { id ->
//            allBlocks[id]?.let { block ->
//                if (block !in visitedBlocks) visitedBlocks.add(block)
//            } ?: run { onShowSubmitChange(true) } // Block finished, potentially show submit
//        }
//    }
//
//
//    LaunchedEffect(visitedBlocks.size) {
//        scrollState.animateScrollTo(scrollState.maxValue)
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//            .imePadding()
//    ) {
//        visitedBlocks.forEachIndexed { index, block ->
//            val isCurrentBlock = index == visitedBlocks.lastIndex
//
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 8.dp)
//                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
//                elevation = CardDefaults.cardElevation(if (isCurrentBlock) 4.dp else 0.dp),
//                colors = CardDefaults.cardColors(
//                    containerColor = if (isCurrentBlock) Color.White else Color(Color.LightGray.value)
//                )
//            ) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp)
//                ) {
//                    when (block.type) {
//                        "audio_start" -> TextInputBlock(block, inputData, isCurrentBlock, onNext = { blockId, groupId -> onReferToData(blockId, groupId, block, surveyDataModelList, loadedGroups) { onShowSubmitChange(it) } })
//                        "textInput" -> TextInputBlock(block, inputData, isCurrentBlock, onNext = { blockId, groupId -> onReferToData(blockId, groupId, block, surveyDataModelList, loadedGroups) { onShowSubmitChange(it) } })
//                        "terms" -> TermsBlock(block, isCurrentBlock, onNext = { blockId, groupId -> onReferToData(blockId, groupId, block, surveyDataModelList, loadedGroups) { onShowSubmitChange(it) } })
//                        "otp" -> OTPBlock(block, isCurrentBlock, onNext = { blockId, groupId -> onReferToData(blockId, groupId, block, surveyDataModelList, loadedGroups) { onShowSubmitChange(it) } })
//                        "dropdown" -> DropdownBlock(block, inputData, isCurrentBlock, onNext = { blockId, groupId -> onReferToData(blockId, groupId, block, surveyDataModelList, loadedGroups) { onShowSubmitChange(it) } })
//                        "multipleChoice" -> MultipleChoiceBlock(block, inputData, isCurrentBlock, onNext = { blockId, groupId -> onReferToData(blockId, groupId, block, surveyDataModelList, loadedGroups) { onShowSubmitChange(it) } })
//                    }
//                }
//            }
//        }
//
//        if (showSubmitButton) {
//            SubmitButton(inputData, visitedBlocks)
//        }
//    }
//}
//
//
