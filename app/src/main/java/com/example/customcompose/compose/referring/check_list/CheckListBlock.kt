package com.example.customcompose.compose.referring.check_list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.customcompose.MyApplication.Companion.appSessionManager
import com.example.customcompose.R
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.viewmodel.BlockListViewModel
import com.example.customcompose.viewmodel.SurveyFlowViewModel
import es.dmoral.toasty.Toasty

@Composable
fun CheckListBlock(
    block: Block,
    blockListViewModel: BlockListViewModel,
    isActiveGroup: Boolean,
    destination: String,
    numberValidationViewModel: SurveyFlowViewModel
) {
    val isSkippable = block.skip?.id != "-1"
    val currentBlockId = block.id ?: ""
    val context = LocalContext.current
//    val selectedOptions = remember { mutableStateOf(block.surveyHistoryModel?.firstOrNull()?.answer?.split(",")?.toSet() ?: emptySet()) }
    val selectedOptions = remember { mutableStateOf(appSessionManager.getCheckListSet() ?: emptySet()) }

    val showDialog = remember { mutableStateOf(false) }

    var checkListBlockId by remember { mutableStateOf("")  }
    var checkListGroupId by remember { mutableStateOf("")  }

    val answer = selectedOptions.value.joinToString(",")
    val question = block.question?.slug ?: ""
    val selectedSingleOption = remember { mutableStateOf("") }
    val isSelectionLocked = selectedOptions.value.size == block.options?.size
    val checkListHistory = blockListViewModel.checkListHistory.collectAsState()

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
            Text(text = question)
            Spacer(modifier = Modifier.height(8.dp))

            block.options?.forEach { option ->
                val isSelected = selectedOptions.value.map { it.trim() }.contains(option.value.trim())

                Surface(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, Color.Gray),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.background(if (isActiveGroup) Color.White else Color.LightGray)
                            .clickable (enabled = isActiveGroup){
                                if (isSelectionLocked) {
                                    Toasty.warning(context, "You have completed maximum number of checklist!", Toasty.LENGTH_SHORT).show()
                                } else {
                                    val isChecked = !isSelected
                                    selectedOptions.value = if (isChecked) {
                                        selectedOptions.value + option.value
                                    } else {
                                        selectedOptions.value - option.value
                                    }

                                    if (isChecked) {
                                        checkListBlockId = option.referTo?.id ?: ""
                                        checkListGroupId = option.referTo?.group_no ?: ""
                                        selectedSingleOption.value = option.value

                                        if (!appSessionManager.existsItem(option.value)) {
                                            showDialog.value = true
                                            blockListViewModel.clearCheckList()
                                            blockListViewModel.addBlockToTheCheckList(checkListBlockId, checkListGroupId)
                                        }
                                    } else {
                                        appSessionManager.removeItem(option.value)

                                        val index = block.options.indexOf(option) ?: -1
                                        if (index != -1) {
                                            blockListViewModel.removeChkListHistoryByIndex(index)
                                        }
                                    }
                                }
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .clickable (enabled = isActiveGroup){
                                    if (isSelectionLocked) {
                                        Toasty.warning(context, "You have completed maximum number of checklist!", Toasty.LENGTH_SHORT).show()
                                    } else {
                                        val isChecked = !isSelected
                                        selectedOptions.value = if (isChecked) {
                                            selectedOptions.value + option.value
                                        } else {
                                            selectedOptions.value - option.value
                                        }

                                        if (isChecked) {
                                            checkListBlockId = option.referTo?.id ?: ""
                                            checkListGroupId = option.referTo?.group_no ?: ""
                                            selectedSingleOption.value = option.value

                                            if (!appSessionManager.existsItem(option.value)) {
                                                showDialog.value = true
                                                blockListViewModel.clearCheckList()
                                                blockListViewModel.addBlockToTheCheckList(checkListBlockId, checkListGroupId)
                                            }
                                        } else {
                                            appSessionManager.removeItem(option.value)

                                            val index = block.options?.indexOf(option) ?: -1
                                            if (index != -1) {
                                                blockListViewModel.removeChkListHistoryByIndex(index)
                                            }
                                        }
                                    }
                                }
                                .padding(16.dp)
                        ) {
                            val drawableResource = if (isSelected) {
                                R.drawable.ic_checked
                            } else {
                                R.drawable.ic_non_checked
                            }

                            Icon(
                                painter = painterResource(id = drawableResource),
                                contentDescription = "Custom Check",
                                modifier = Modifier
                                    .size(24.dp),
                                tint = if (isSelected) Color.Green else Color.Gray
                            )
                        }

                        Text(option.value)
                    }
                }
            }

            if (showDialog.value) {
                CheckListDialog(
                    selectedOption = selectedSingleOption.value,
                    blockListViewModel = blockListViewModel,
                    onClose = {
                        showDialog.value = false
                    },
                    onDismiss = {
                        showDialog.value = false
                        selectedOptions.value = selectedOptions.value - selectedSingleOption.value
                    },
                    numberValidationViewModel
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                if (isSkippable){
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
                                    }else{
                                        blockListViewModel.addBlockToTheCheckList(blockId, groupId)
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
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

                        val surveyHistoryModel = SurveyHistoryModel(
                            question = question,
                            answer = answer,
                            id = currentBlockId
                        )

                        if (block.validations != null && block.validations.partial){
                            if (selectedOptions.value.size == block.validations.min!!){
                                block.surveyHistoryModel = listOf(surveyHistoryModel)
                                if (checkListHistory.value.isNotEmpty()){
                                    for (indexHistoryList in checkListHistory.value){
                                        for (indexHistory in indexHistoryList){
                                            val checkListHistory = SurveyHistoryModel(
                                                question = indexHistory.question,
                                                answer = indexHistory.answer,
                                                id = indexHistory.id
                                            )

                                            block.surveyHistoryModel = block.surveyHistoryModel + checkListHistory
                                        }
                                    }
                                }

                                block.referTo?.group_no?.let { groupId ->
                                    block.referTo.id?.let { nextBlockId ->
                                        if (destination == "mainSurvey") {
                                            blockListViewModel.addBlockToTheSurveyFlow(nextBlockId, groupId, block.position)
                                        }else{
                                            blockListViewModel.addBlockToTheCheckList(nextBlockId, groupId)
                                        }
                                    }
                                }
                            }else{
                                Toasty.warning(context, "Minimum ${block.validations.min} fields are required!", Toasty.LENGTH_SHORT).show()
                            }
                        }else if (selectedOptions.value.size != block.options?.size){
                            Toasty.warning(context,"All task fields are required", Toasty.LENGTH_SHORT).show()
                        }else{
                            block.surveyHistoryModel = listOf(surveyHistoryModel)

                            block.referTo?.group_no?.let { groupId ->
                                block.referTo.id?.let { nextBlockId ->
                                    if (checkListHistory.value.isNotEmpty()){
                                        for (indexHistoryList in checkListHistory.value){
                                            for (indexHistory in indexHistoryList){
                                                val checkListHistory = SurveyHistoryModel(
                                                    question = indexHistory.question,
                                                    answer = indexHistory.answer,
                                                    id = indexHistory.id
                                                )

                                                block.surveyHistoryModel = block.surveyHistoryModel + checkListHistory
                                            }
                                        }
                                    }

                                    if (destination == "mainSurvey") {
                                        blockListViewModel.addBlockToTheSurveyFlow(nextBlockId, groupId, block.position)
                                    }else{
                                        blockListViewModel.addBlockToTheCheckList(nextBlockId, groupId)
                                    }
                                }
                            }
                        }

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
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
