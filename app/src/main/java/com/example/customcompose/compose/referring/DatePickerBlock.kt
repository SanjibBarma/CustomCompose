package com.example.customcompose.compose.referring

import android.os.Build
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.customcompose.viewmodel.BlockListViewModel
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DatePickerBlock(block: Block, blockListViewModel: BlockListViewModel) {
    val dateDialogState = rememberMaterialDialogState()

    val context = LocalContext.current
    val isSkippable = block.skip?.id != "-1"

    var pickedDate by remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mutableStateOf<LocalDate?>(null)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }

    val formattedDate by remember {
        derivedStateOf {
            pickedDate?.let {
                DateTimeFormatter
                    .ofPattern("dd/MM/yyyy")
                    .format(it)
            } ?: ""
        }
    }

    Column {
        Text(block.question!!.slug)
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                .padding(8.dp)
                .clickable { dateDialogState.show() }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(formattedDate)
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
                    Toast.makeText(
                        context,
                        "Clicked ok",
                        Toast.LENGTH_LONG
                    ).show()
                }
                negativeButton(text = "Cancel")
            }
        ) {
            datepicker(
                initialDate = LocalDate.now(),
                title = "Pick a date",
                //date validator will allow user to custom date selection
//                        allowedDateValidator = {}
            ) {
                pickedDate = it
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row (
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){

            if (isSkippable){
                Button(
                    onClick = {
                        block.skip?.group_no?.let { groupId ->
                            block.skip.id.let { blockId ->
                                blockListViewModel.addBlockToTheList(blockId, groupId)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Blue,
                        contentColor = Color.White
                    )
                ) {
                    Text("Skip")
                }

                Spacer(modifier = Modifier.width(8.dp))
            }

            Button(
                onClick = {
                    block.referTo?.group_no?.let { groupId ->
                        block.referTo.id?.let { blockId ->
                            blockListViewModel.addBlockToTheList(blockId, groupId)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue,
                    contentColor = Color.White
                )
            ) {
                Text("Next")
            }
        }
    }
}

