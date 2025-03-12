package com.example.customcompose.compose.referring

import android.graphics.Paint
import android.graphics.Path
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.viewmodel.BlockListViewModel

@Composable
fun TermsAgreementBlock(
    block: Block,
    blockListViewModel: BlockListViewModel,
    isActiveGroup: Boolean,
    destination: String
) {
    val currentBlockId = block.id ?: ""

    var isChecked by remember { mutableStateOf(block.surveyHistoryModel?.firstOrNull()?.answer == "Yes") }
    var showDialog by remember { mutableStateOf(true) }
    val question = block.question?.slug ?: ""

    if (showDialog) {
        DrawingCanvas(block, blockListViewModel, destination, onDismiss = { showDialog = false })
    }else{
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(containerColor = if (isActiveGroup) Color.White else Color.LightGray)
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = block.question!!.slug)

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        block.validations?.terms?.forEach { term ->
                            Text(
                                text = term,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                    }
                }

//                Spacer(modifier = Modifier.height(12.dp))
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Checkbox(
//                        checked = isChecked,
//                        onCheckedChange = { isChecked = it },
//                        enabled = isActiveGroup
//                    )
//                    Text(text = "I agree to the terms and conditions", fontSize = 14.sp)
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                Column (
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.Center
//                ){
//                    Button(
//                        onClick = {
//                            //isEnabled = false;
//                            val surveyHistoryModel = SurveyHistoryModel(
//                                question = question,
//                                answer = "Yes",
//                                id = currentBlockId
//                            )
//                            block.surveyHistoryModel = listOf(surveyHistoryModel)
//
//                            if (destination == "mainSurvey") {
//                                blockListViewModel.addBlockToTheSurveyFlow(block.referTo?.id!!, block.referTo.group_no!!, block.position)
//                            } else {
//                                blockListViewModel.addBlockToTheCheckList(block.referTo?.id!!, block.referTo.group_no!!)
//                            }
//
//                        },
//                        enabled = isChecked && isActiveGroup
//                    ) {
//                        Text("I Agree")
//                    }
//                }
            }
        }
    }
}

@Composable
fun DrawingCanvas(
    block: Block,
    blockListViewModel: BlockListViewModel,
    destination: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = {
            onDismiss()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true
        )
    ) {
        val paths = remember { mutableStateListOf<Path>() }
        val paint = remember {
            Paint().apply {
                color = android.graphics.Color.BLACK
                strokeWidth = 3f
                style = Paint.Style.STROKE
                strokeJoin = Paint.Join.ROUND
                strokeCap = Paint.Cap.ROUND
            }
        }
        val pathForDrawing = remember { mutableStateOf(Path()) }
        var canvasSize by remember { mutableStateOf(Size.Zero) }

        var isChecked by remember { mutableStateOf(block.surveyHistoryModel?.firstOrNull()?.answer == "Yes") }
        var showDialog by remember { mutableStateOf(false) }
        val question = block.question?.slug ?: ""
        val currentBlockId = block.id ?: ""

        Column (
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            Card (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(4.dp),
            ){
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    if (offset.x in 0f..canvasSize.width && offset.y in 0f..canvasSize.height) {
                                        pathForDrawing.value = Path().apply {
                                            moveTo(offset.x, offset.y)
                                        }
                                    }
                                },
                                onDrag = { change, _ ->
                                    if (change.position.x in 0f..canvasSize.width && change.position.y in 0f..canvasSize.height) {
                                        pathForDrawing.value = Path(pathForDrawing.value).apply {
                                            lineTo(change.position.x, change.position.y)
                                        }
                                    }
                                },
                                onDragEnd = {
                                    paths.add(Path(pathForDrawing.value))
                                    pathForDrawing.value = Path()
                                }
                            )
                        }
                ) {
                    canvasSize = size
                    paths.forEach { path ->
                        drawContext.canvas.nativeCanvas.drawPath(path, paint)
                    }
                    drawContext.canvas.nativeCanvas.drawPath(pathForDrawing.value, paint)
                }

            }

            OutlinedButton(
                onClick = {
                    paths.clear()
                },
                modifier = Modifier
                    .width(120.dp)
                    .height(40.dp)
            ) {
                Text("Clear")
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
                    .padding(8.dp)
            ) {
                Column {
                    block.validations?.terms?.forEach { term ->
                        Text(
                            text = term,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { isChecked = it },
                )
                Text(text = "I agree to the terms and conditions", fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column (
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Button(
                    onClick = {
                        //isEnabled = false;
                        val surveyHistoryModel = SurveyHistoryModel(
                            question = question,
                            answer = "Yes",
                            id = currentBlockId
                        )
                        block.surveyHistoryModel = listOf(surveyHistoryModel)

                        if (destination == "mainSurvey") {
                            blockListViewModel.addBlockToTheSurveyFlow(block.referTo?.id!!, block.referTo.group_no!!, block.position)
                        } else {
                            blockListViewModel.addBlockToTheCheckList(block.referTo?.id!!, block.referTo.group_no!!)
                        }

                    },
                    enabled = isChecked
                ) {
                    Text("I Agree")
                }
            }
        }
    }
}