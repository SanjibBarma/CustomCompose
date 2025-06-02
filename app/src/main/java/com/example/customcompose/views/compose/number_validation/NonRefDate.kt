package com.example.customcompose.views.compose.number_validation

import android.widget.DatePicker
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.customcompose.MyApplication.Companion.appSessionManager
import com.example.customcompose.MyApplication.Companion.blockListViewModel
import com.example.customcompose.R
import com.example.customcompose.helper.CommonUtils
import com.example.customcompose.helper.CommonUtils.getTapAnalysisElapsedTime
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.model.NumberCheckData
import com.example.customcompose.model.Result
import com.example.customcompose.viewmodel.BlockListViewModel
import com.google.gson.Gson
import es.dmoral.toasty.Toasty
import java.util.Calendar

@Composable
fun NonRefDate(
    block: Block,
    index: Int,
    position: Int,
    isActiveGroup: Boolean,
    onOptionSelected: (Result) -> Unit
) {
    val gson = Gson()
    val existingData = blockListViewModel.getDataFromIndex(position, index)
    val question = block.question?.alias ?: ""
    val blockId = block.id ?: ""

    val maxAge = block.validations?.max ?: 50
    val minAge = block.validations?.min ?: 18

    val mContext = LocalContext.current
    val mCalendar = Calendar.getInstance()
    val currentYear = mCalendar.get(Calendar.YEAR)
    val currentMonth = mCalendar.get(Calendar.MONTH)
    val currentDay = mCalendar.get(Calendar.DAY_OF_MONTH)

    val minAllowedCalendar = Calendar.getInstance().apply { set(currentYear - maxAge, currentMonth, currentDay) }
    val maxAllowedCalendar = Calendar.getInstance().apply { set(currentYear - minAge, currentMonth, currentDay) }

    var mDate by remember { mutableStateOf(existingData?.answer ?: "") }
    var tempDate by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val nonRefData = appSessionManager.getMobileVerificationData()
        if (!nonRefData.isNullOrEmpty()) {
            val numberCheckData: NumberCheckData? = gson.fromJson(nonRefData, NumberCheckData::class.java)
            numberCheckData?.information?.firstOrNull { it.key == question }?.let {
                mDate = it.value
                blockListViewModel.saveDataAtIndex(
                    position,
                    SurveyHistoryModel(question = question, answer = it.value, id = blockId)
                )
            }
        }

        val result = Result(
            option = question,
            tap_time = (getTapAnalysisElapsedTime()!! / 1000000).toString()
        )
        onOptionSelected(result)
    }

    Column {
        Text(block.question?.slug ?: "")
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                .clickable(enabled = isActiveGroup) { showDialog = true }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = if (mDate.isBlank()) "Select DoB" else mDate,
                    color = if (mDate.isBlank()) Color.Gray else Color.Black
                )
                Icon(
                    painter = painterResource(R.drawable.ic_calendar),
                    contentDescription = "Calendar Icon",
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
                        Text("Date of birth", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        AndroidView(factory = { context ->
                            val initCalendar = if (mDate.isNotBlank()) {
                                val parts = mDate.split("/")
                                Calendar.getInstance().apply {
                                    set(parts[2].toInt(), parts[1].toInt() - 1, parts[0].toInt())
                                }
                            } else {
                                Calendar.getInstance().apply {
                                    set(currentYear - minAge, currentMonth, currentDay)
                                    add(Calendar.DAY_OF_MONTH, -1)
                                }
                            }

                            tempDate = "${initCalendar.get(Calendar.DAY_OF_MONTH)}/${initCalendar.get(Calendar.MONTH) + 1}/${initCalendar.get(Calendar.YEAR)}"

                            DatePicker(context).apply {
                                init(
                                    initCalendar.get(Calendar.YEAR),
                                    initCalendar.get(Calendar.MONTH),
                                    initCalendar.get(Calendar.DAY_OF_MONTH)
                                ) { _, year, month, day ->

                                    val selectedCalendar = Calendar.getInstance().apply {
                                        set(year, month, day)
                                    }

                                    tempDate = if (selectedCalendar.before(minAllowedCalendar)) {
                                        Toasty.warning(context, "Must be less than $maxAge years old", Toasty.LENGTH_SHORT).show()
                                        ""
                                    } else if (selectedCalendar.after(maxAllowedCalendar)) {
                                        Toasty.warning(context, "Must be more than $minAge years old", Toasty.LENGTH_SHORT).show()
                                        ""
                                    } else {
                                        "$day/${month + 1}/$year"
                                    }
                                }
                            }
                        })

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Button(
                                onClick = {
                                    showDialog = false
                                    tempDate = ""
                                },
                                modifier = Modifier.width(120.dp)
                            ) {
                                Text("Cancel")
                            }

                            Button(
                                onClick = {
                                    if (tempDate.isNotBlank()) {
                                        mDate = tempDate
                                        blockListViewModel.saveDataAtIndex(
                                            position,
                                            SurveyHistoryModel(question = question, answer = mDate, id = blockId)
                                        )
                                        showDialog = false
                                    } else {
                                        Toasty.warning(mContext, "Please select a valid date", Toasty.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.width(120.dp)
                            ) {
                                Text("OK")
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}