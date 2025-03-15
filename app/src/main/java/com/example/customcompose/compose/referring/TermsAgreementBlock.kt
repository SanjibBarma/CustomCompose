package com.example.customcompose.compose.referring

import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
    val isTermsShow by blockListViewModel.isTermsShow.collectAsState()


    if (showDialog && isTermsShow) {
        DrawingCanvas(block, blockListViewModel, destination, onDismiss = { showDialog = false; blockListViewModel.hideTermsPopup() })
    }else{
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable (enabled = isActiveGroup){
                    showDialog = true
                    blockListViewModel.showTermsPopup()
                },
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(containerColor = if (isActiveGroup) Color.White else Color.LightGray)
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = block.question!!.slug)

                Spacer(modifier = Modifier.height(8.dp))

                //show the canvas saved image here if saved el

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
                                fontSize = 12.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DrawingCanvas(block: Block, blockListViewModel: BlockListViewModel, destination: String, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = {
            onDismiss()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = false
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
        var isChecked by remember { mutableStateOf(/*block.surveyHistoryModel?.firstOrNull()?.answer == "Yes"*/false) }
        val question = block.question?.slug ?: ""
        val currentBlockId = block.id ?: ""
        val context = LocalContext.current

        Column (
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = question, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.height(8.dp))
            Card (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(start = 16.dp, end = 16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(4.dp),
            ) {
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
            Spacer(modifier = Modifier.height(8.dp))

            Column (modifier = Modifier.fillMaxWidth().padding(8.dp)){
                Box(
                    modifier = Modifier.border(1.dp, Color.LightGray).align(Alignment.End)
                        .clickable {
                            paths.clear()
                        }
                ) {
                    Text("Clear sign", modifier = Modifier.align(Alignment.Center).padding(start = 32.dp, end = 32.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { isChecked = it },
                        modifier = Modifier.align(Alignment.Top)
                    )
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        block.validations?.terms?.forEach { term ->
                            Text(
                                text = term,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val drawingThreshold = paths.sumOf { path -> path.approximateLength().toDouble() }
            val maxThreshold = 300.0
            val drawingPercentage = (drawingThreshold / maxThreshold) * 100

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, end = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {

                        if (drawingPercentage < (10.0 / maxThreshold) * 100) { // 10 units as percentage
                            Toast.makeText(context, "Sign needed", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (drawingPercentage < (50.0 / maxThreshold) * 100) { // 50 units as percentage
                            Toast.makeText(context, "Sign too short", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val surveyHistoryModel = SurveyHistoryModel(
                            question = question,
                            answer = "Yes",
                            id = currentBlockId
                        )
                        block.surveyHistoryModel = listOf(surveyHistoryModel)
                        if (destination == "mainSurvey") {
                            blockListViewModel.addBlockToTheSurveyFlow(
                                block.referTo?.id!!,
                                block.referTo.group_no!!,
                                block.position
                            )
                        } else {
                            blockListViewModel.addBlockToTheCheckList(
                                block.referTo?.id!!,
                                block.referTo.group_no!!
                            )
                        }
                        onDismiss()
                    },
                    enabled = isChecked,
                    modifier = Modifier.weight(1f).height(56.dp).width(120.dp)
                ) {
                    Text("Agree")
                }

                Spacer(modifier = Modifier.width(16.dp))

                OutlinedButton(
                    onClick = {
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f).height(56.dp).width(120.dp)
                ) {
                    Text("Disagree", color = Color.Red)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Helper function to approximate the path length
fun Path.approximateLength(): Float {
    val pathMeasure = PathMeasure(this, false)
    var length = 0f
    do {
        length += pathMeasure.length
    } while (pathMeasure.nextContour())
    return length
}
