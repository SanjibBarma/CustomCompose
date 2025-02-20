package com.example.customcompose.compose.number_validation

import android.os.Build
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.customcompose.R
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.viewmodel.BlockListViewModel
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun NonRefDate(
    block: Block,
    blockListViewModel: BlockListViewModel,
    index: Int,
    position: Int,
    isActiveGroup: Boolean
) {
    val dateDialogState = rememberMaterialDialogState()

    val context = LocalContext.current
    val isRequired = block.required

    val existingData = blockListViewModel.getDataFromIndex(position, index)

    var answer by remember { mutableStateOf(existingData?.answer ?: "") }
    val question = block.question?.slug ?: ""
    val blockId = block.id ?: ""

    var pickedDate by remember {
        mutableStateOf<LocalDate?>(existingData?.answer?.let { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.parse(it, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        })
    }

    val formattedDate by remember {
        derivedStateOf {
            pickedDate?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    DateTimeFormatter
                        .ofPattern("dd/MM/yyyy")
                        .format(it)
                } else {
                    TODO("VERSION.SDK_INT < O")
                }
            } ?: ""
        }
    }

    // Update answer and save data when formattedDate changes
    LaunchedEffect(formattedDate) {
        answer = formattedDate

        val surveyHistoryModel = SurveyHistoryModel(
            question = question,
            answer = formattedDate,
            id = blockId
        )

        blockListViewModel.saveDataAtIndex(position, index, surveyHistoryModel)
    }

    Column {
        Text(block.question!!.slug)
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                .padding(8.dp)
                .clickable(enabled = isActiveGroup) { dateDialogState.show() }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(answer)
                Icon(
                    painter = painterResource(R.drawable.ic_calendar),
                    contentDescription = "Dropdown Arrow",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        MaterialDialog(
            dialogState = dateDialogState,
            buttons = {
                positiveButton(text = "Ok") {
                    // Toast.makeText(context, "Clicked ok", Toast.LENGTH_LONG).show()
                }
                negativeButton(text = "Cancel")
            }
        ) {
            datepicker(
                initialDate = pickedDate ?: LocalDate.now(),
                title = "Pick a date",
            ) {
                pickedDate = it
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
