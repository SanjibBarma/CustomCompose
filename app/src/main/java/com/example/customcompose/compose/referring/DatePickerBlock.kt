package com.example.customcompose.compose.referring

import android.widget.DatePicker
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.example.customcompose.R
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.viewmodel.BlockListViewModel
import es.dmoral.toasty.Toasty
import java.util.Calendar

@Composable
fun DatePickerBlock(
    block: Block,
    blockListViewModel: BlockListViewModel,
    isActiveGroup: Boolean,
    destination: String
) {
    val currentBlockId = block.id ?: ""
    val context = LocalContext.current
    val isSkippable = block.skip?.id != "-1"

    var mDate = remember { mutableStateOf(block.surveyHistoryModel?.firstOrNull()?.answer ?: "") }
    val question = block.question?.alias ?: ""
    val blockId = block.id ?: ""

    val maxAge = block.validations?.max ?: 0
    val minAge = block.validations?.min ?: 0

    val mContext = LocalContext.current
    val mCalendar = Calendar.getInstance()

    // Get current date
    val currentYear = mCalendar.get(Calendar.YEAR)
    val currentMonth = mCalendar.get(Calendar.MONTH)
    val currentDay = mCalendar.get(Calendar.DAY_OF_MONTH)

    var showDialog by remember { mutableStateOf(false) }
    val tempDate = remember { mutableStateOf("") }

    val minAllowedCalendar = Calendar.getInstance().apply { set(currentYear - maxAge, currentMonth, currentDay) }
    val maxAllowedCalendar = Calendar.getInstance().apply { set(currentYear - minAge, currentMonth, currentDay) }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = if (isActiveGroup) Color.White else Color.LightGray)
    ){
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
        Text(block.question!!.slug)
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                .padding(8.dp)
                .clickable(enabled = isActiveGroup) { showDialog = true }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(mDate.value)
                Icon(
                    painter = painterResource(R.drawable.ic_calendar),
                    contentDescription = "Dropdown Arrow",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        if (showDialog) {
                Dialog(onDismissRequest = { showDialog = false }) {
                    Surface(
                        modifier = Modifier.wrapContentSize(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                question,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            AndroidView(
                                factory = { context ->
                                    DatePicker(context).apply {
                                        init(currentYear - minAge, currentMonth, currentDay) { _, year, month, day ->
                                            val selectedCalendar = Calendar.getInstance().apply {
                                                set(year, month, day)
                                            }

                                            if (selectedCalendar.before(minAllowedCalendar)) {
                                                Toasty.warning(context, "Must be more than $minAge years old", Toasty.LENGTH_SHORT).show()
                                                tempDate.value = ""
                                            }else if (selectedCalendar.after(maxAllowedCalendar)){
                                                Toasty.warning(context, "Must be less than $maxAge years old", Toasty.LENGTH_SHORT).show()
                                                tempDate.value = ""
                                            } else {
                                                tempDate.value = "$day/${month + 1}/$year"
                                            }
                                        }
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.wrapContentWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Button(
                                    onClick = {
                                        showDialog = false
                                        mDate.value = ""
                                    },
                                    modifier = Modifier.weight(1f, fill = false).width(120.dp)
                                ) {
                                    Text("Cancel")
                                }

                                Button(
                                    onClick = {
                                        if (tempDate.value.isNotEmpty()) {
                                            mDate.value = tempDate.value
                                            showDialog = false
                                        } else {
                                            Toasty.warning(mContext, "Please select a valid date", Toasty.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.weight(1f, fill = false).width(120.dp)
                                ) {
                                    Text("OK")
                                }
                            }

                        }
                    }
                }
            }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            if (isSkippable) {
                Button(
                    onClick = {

                        val surveyHistoryModel = SurveyHistoryModel(
                            question = "",
                            answer = "",
                            id = currentBlockId
                        )
                        block.surveyHistoryModel = listOf(surveyHistoryModel)

                        block.skip?.group_no?.let { groupId ->
                            block.skip.id.let { blockId ->
                                if (destination == "mainSurvey") {
                                    blockListViewModel.addBlockToTheSurveyFlow(blockId, groupId, block.position)
                                } else {
                                    blockListViewModel.addBlockToTheCheckList(blockId, groupId)
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Blue,
                        contentColor = Color.White
                    ),
                    enabled = isActiveGroup
                ) {
                    Text("Skip")
                }

                Spacer(modifier = Modifier.width(8.dp))
            }

            Button(
                onClick = {
                    if (mDate.value.isEmpty()) {
                        Toasty.warning(
                            context,
                            "Please enter a valid ${block.question.slug}",
                            Toasty.LENGTH_SHORT
                        ).show()
                    } else {
                        val surveyHistoryModel = SurveyHistoryModel(
                            question = question,
                            answer = mDate.value,
                            id = currentBlockId
                        )
                        block.surveyHistoryModel = listOf(surveyHistoryModel)

                        block.referTo?.group_no?.let { groupId ->
                            block.referTo.id?.let { blockId ->
                                if (destination == "mainSurvey") {
                                    blockListViewModel.addBlockToTheSurveyFlow(blockId, groupId, block.position)
                                } else {
                                    blockListViewModel.addBlockToTheCheckList(blockId, groupId)
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue,
                    contentColor = Color.White
                ),
                enabled = isActiveGroup
            ) {
                Text("Next")
            }
        }
    }
    }
}

