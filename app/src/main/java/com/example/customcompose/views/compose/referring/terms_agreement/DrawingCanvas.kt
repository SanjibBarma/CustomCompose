package com.example.customcompose.views.compose.referring.terms_agreement

import android.content.Context
import android.graphics.Bitmap
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.customcompose.MyApplication.Companion.blockListViewModel
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.viewmodel.BlockListViewModel
import java.io.File
import java.io.FileOutputStream

@Composable
fun DrawingCanvas(block: Block, destination: String, onDismiss: () -> Unit) {
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
        var isChecked by remember { mutableStateOf(false) }
        val question = block.question?.slug ?: ""
        val currentBlockId = block.id ?: ""
        val context = LocalContext.current
        val signatureName = "signature.jpg"

        Scaffold (
            modifier = Modifier
                .fillMaxSize()
        ){ innerPadding ->
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color.White)
                    .padding(bottom = 32.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = question, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(
                    Alignment.CenterHorizontally))
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

                Box(modifier = Modifier.fillMaxWidth()) {
                                       Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .clickable { isChecked = !isChecked },
                            verticalAlignment = Alignment.Top
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = null,
                                modifier = Modifier.align(Alignment.Top).padding(start = 4.dp)
                            )
                            Column(
                                modifier = Modifier
                                    .padding(end = 8.dp, start = 8.dp)
                                    .weight(1f)
                            ) {
                                block.validations?.terms?.forEach { term ->
                                    Text(
                                        text = term,
                                        fontSize = 12.sp,
                                        lineHeight = 14.sp,
                                    )
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .padding(end = 8.dp)
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                            .clickable { paths.clear() }
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            "Clear sign",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            fontSize = 12.sp
                        )
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
                            if (drawingPercentage < (10.0 / maxThreshold) * 100) {
                                Toast.makeText(context, "Sign needed", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            if (drawingPercentage < (50.0 / maxThreshold) * 100) {
                                Toast.makeText(context, "Sign too short", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            // Save the drawing as image
                            val bitmap = createBitmapFromPaths(paths.toList(), paint, canvasSize)
                            saveSignatureToCache(context, bitmap, signatureName)

                            val surveyHistoryModel = SurveyHistoryModel(
                                question = question,
                                answer = signatureName,
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
                            blockListViewModel.showOtpBox()
                        },
                        enabled = isChecked,
                        modifier = Modifier.weight(1f).height(48.dp).width(120.dp)
                    ) {
                        Text("Agree")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    OutlinedButton(
                        onClick = {
                            onDismiss()
                            blockListViewModel.showOtpBox()
                        },
                        modifier = Modifier.weight(1f).height(48.dp).width(120.dp)
                    ) {
                        Text("Disagree", color = Color.Red)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

fun saveSignatureToCache(context: Context, bitmap: Bitmap, filename: String): File {
    val file = File(context.cacheDir, filename)
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }
    return file
}

fun createBitmapFromPaths(paths: List<Path>, paint: Paint, size: Size): Bitmap {
    val bitmap = Bitmap.createBitmap(size.width.toInt(), size.height.toInt(), Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    canvas.drawColor(android.graphics.Color.WHITE) // Background color
    paths.forEach { path ->
        canvas.drawPath(path, paint)
    }
    return bitmap
}

fun Path.approximateLength(): Float {
    val pathMeasure = PathMeasure(this, false)
    var length = 0f
    do {
        length += pathMeasure.length
    } while (pathMeasure.nextContour())
    return length
}