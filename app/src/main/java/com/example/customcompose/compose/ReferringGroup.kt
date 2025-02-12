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
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
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
//
//@Composable
//fun ReferringGroup(group: List<Block>, surveyDataModel: SurveyDataModel, onNext: (List<Block>) -> Unit) {
//    val inputData = remember { mutableStateMapOf<String, String>() }
//    val visitedBlocks = remember { mutableStateListOf<Block>() }
//    val allBlocks = surveyDataModel.blocks.associateBy { it.id }
//    val scrollState = rememberScrollState()
//    var currentBlockId by remember { mutableStateOf(group.firstOrNull()?.id) }
//    var showSubmitButton by remember { mutableStateOf(false) }
//
//    LaunchedEffect(currentBlockId) {
//        currentBlockId?.let { id ->
//            allBlocks[id]?.let { block ->
//                if (block !in visitedBlocks) visitedBlocks.add(block)
//            } ?: run { showSubmitButton = true }
//        }
//    }
//
//    LaunchedEffect(visitedBlocks.size) {
//        scrollState.animateScrollTo(scrollState.maxValue)
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//            .verticalScroll(scrollState)
//            .imePadding()
//    ) {
//        // Loop over all the blocks in the current group
//        group.forEachIndexed { index, block ->
//            val isCurrentBlock = index == group.lastIndex
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
//                        "textInput" -> TextInputBlock(block, inputData, isCurrentBlock, onNext = { nextId -> navigateToNextBlock(nextId, { currentBlockId = it }) })
//                        "terms" -> TermsBlock(block, isCurrentBlock, onNext = { nextId -> navigateToNextBlock(nextId, { currentBlockId = it }) })
//                        "otp" -> OTPBlock(block, isCurrentBlock, onNext = { nextId -> navigateToNextBlock(nextId, { currentBlockId = it }) })
//                        "dropdown" -> DropdownBlock(block, inputData, isCurrentBlock, onNext = { nextId -> navigateToNextBlock(nextId, { currentBlockId = it }) })
//                        "multipleChoice" -> MultipleChoiceBlock(block, inputData, isCurrentBlock, onNext = { nextId -> navigateToNextBlock(nextId, { currentBlockId = it }) })
//                        "camera" -> CameraBlock(block, inputData, isCurrentBlock, onNext = { nextId -> navigateToNextBlock(nextId, { currentBlockId = it }) })
//                    }
//                }
//            }
//        }
//
//        if (showSubmitButton) {
//            SubmitButton(inputData, visitedBlocks)
//        }
//    }
//
//    // Trigger navigation to the next group after processing the current one
//    onNext(group)
//}
//
