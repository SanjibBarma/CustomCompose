package com.example.customcompose.views.compose.referring.terms_agreement

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.customcompose.model.Block
import com.example.customcompose.viewmodel.BlockListViewModel
import java.io.File

@Composable
fun TermsAgreementBlock(
    block: Block,
    blockListViewModel: BlockListViewModel,
    isActiveGroup: Boolean,
    destination: String
) {
    var previousAns by remember { mutableStateOf(block.surveyHistoryModel.firstOrNull()?.answer ?: "") }
    var showDialog by remember { mutableStateOf(true) }
    val isTermsShow by blockListViewModel.isTermsShow.collectAsState()
    val context = LocalContext.current

    val imageFile = File(context.cacheDir, previousAns)
    val imageBitmap = remember(previousAns) {
        if (imageFile.exists()) {
            BitmapFactory.decodeFile(imageFile.absolutePath)?.asImageBitmap()
        } else null
    }

    LaunchedEffect (Unit){
        if (previousAns == ""){
            showDialog = true
            blockListViewModel.showTermsPopup()
        }
    }

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

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        //show the canvas saved image here if saved el
                        if (imageBitmap != null) {
                            Image(
                                bitmap = imageBitmap,
                                contentDescription = previousAns,
                                modifier = Modifier
                                    .height(250.dp)
                                    .fillMaxWidth()
                                    .background(Color.White)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                block.validations?.terms?.forEach { term ->
                    Text(
                        text = term,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
                    )
                }
            }
        }
    }
}


