package com.example.customcompose.compose.referring

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.customcompose.compose.CheckGroupOrBlock
import com.example.customcompose.helper.SharedPrefHelper
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.viewmodel.BlockListViewModel
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.launch

@Composable
fun CheckListBlock(block: Block, blockListViewModel: BlockListViewModel, isActiveGroup: Boolean, destination: String) {
    val isSkippable = block.skip?.id != "-1"
    val currentBlockId = block.id ?: ""
    val context = LocalContext.current
    val sharedPrefHelper = remember { SharedPrefHelper(context) }
//    val selectedOptions = remember { mutableStateOf(block.surveyHistoryModel?.firstOrNull()?.answer?.split(",")?.toSet() ?: emptySet()) }
    val selectedOptions = remember { mutableStateOf(sharedPrefHelper.getSet() ?: emptySet()) }

    val showDialog = remember { mutableStateOf(false) }

    var checkListBlockId by remember { mutableStateOf("")  }
    var checkListGroupId by remember { mutableStateOf("")  }

    val answer = selectedOptions.value.joinToString(",")
    val question = block.question?.slug ?: ""
    val selectedSingleOption = remember { mutableStateOf("") }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = if (isActiveGroup) Color.White else Color.LightGray)
    ){
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = question)
            Spacer(modifier = Modifier.height(8.dp))

            block.options?.forEach { option ->
                val isSelected = selectedOptions.value.contains(option.value)

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
//                            .clickable {
//                                selectedOptions.value = if (isSelected) {
//                                    selectedOptions.value + option.value
//                                } else {
//                                    selectedOptions.value - option.value
//                                }
//
//                                if(isSelected){
//                                    showDialog.value = true
//                                }
//                            }
                    ) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { isChecked ->
                                selectedOptions.value = if (isChecked) {
                                    selectedOptions.value + option.value
                                } else {
                                    selectedOptions.value - option.value
                                }

                                if (isChecked) {
                                    checkListBlockId = option.referTo?.id ?: ""
                                    checkListGroupId = option.referTo?.group_no ?: ""
                                    selectedSingleOption.value = option.value

                                    if (!sharedPrefHelper.existsItem(option.value)){
                                        showDialog.value = true
                                        blockListViewModel.clearCheckList()
                                        blockListViewModel.addBlockToTheCheckList(checkListBlockId, checkListGroupId)
                                    }
//                                    sharedPrefHelper.saveItem(option.value)
                                }else{
                                    sharedPrefHelper.removeItem(option.value)
                                }
                            },
                            enabled = isActiveGroup
                        )
                        Text(option.value)
                    }
                }


                if (showDialog.value) {
                    CheckListDialog(
                        selectedOption = selectedSingleOption.value,
                        blockId = checkListBlockId,
                        groupId = checkListGroupId,
                        blockListViewModel = blockListViewModel,
                        onClose = {
                            val surveyHistoryModel = listOf(
                                SurveyHistoryModel(
                                    question = question,
                                    answer = answer,
                                    id = currentBlockId
                                )
                            )
//                block.surveyHistoryModel = surveyHistoryModel
//                if (destination == "mainSurvey") {
//                    blockListViewModel.saveData(currentBlockId, surveyHistoryModel)
//                }
                            /*else{
                                blockListViewModel.saveDataToCheckList(currentBlockId, surveyHistoryModel)
                            }*/
//                            if (sharedPrefHelper.existsItem(option.value)){
                                showDialog.value = false
//                            }
//                showDialog.value = false
                        }
                    )
                }
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
                            block.skip?.group_no?.let { groupId ->
                                block.skip.id.let { blockId ->
                                    if (destination == "mainSurvey") {
                                        block.surveyHistoryModel = listOf(surveyHistoryModel)
                                        blockListViewModel.addBlockToTheSurveyFlow(blockId, groupId)
                                    }else{
                                        blockListViewModel.saveHistoryForChecklist(currentBlockId, surveyHistoryModel)
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
                        block.referTo?.group_no?.let { groupId ->
                            block.referTo.id?.let { nextBlockId ->
                                if (destination == "mainSurvey") {
                                    block.surveyHistoryModel = listOf(surveyHistoryModel)
                                    blockListViewModel.addBlockToTheSurveyFlow(nextBlockId, groupId)
                                }else{
                                    blockListViewModel.saveHistoryForChecklist(currentBlockId, surveyHistoryModel)
                                    blockListViewModel.addBlockToTheCheckList(nextBlockId, groupId)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckListDialog(selectedOption: String, blockId: String, groupId: String, blockListViewModel: BlockListViewModel, onClose: () -> Unit) {

    val surveyViewListItem by blockListViewModel.checkListBlockListItem.collectAsState()
    val listState = remember { LazyListState() }
    val coroutineScope = rememberCoroutineScope()
    val isCheckList by blockListViewModel.isCheckList.collectAsState()
    val context = LocalContext.current
    val sharedPrefHelper = remember { SharedPrefHelper(context) }

//    LaunchedEffect(blockId, groupId) {
//        coroutineScope.launch {
//            //blockListViewModel.clearCheckList()
//            blockListViewModel.addBlockToTheCheckList(blockId, groupId)
//        }
//    }


    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true
        )
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(selectedOption) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .imePadding()
                    .padding(paddingValues)
            ) {
                LaunchedEffect(surveyViewListItem.size) {
                    if (surveyViewListItem.isNotEmpty()) {
                        coroutineScope.launch {
                            listState.scrollToItem(surveyViewListItem.size - 1)
                        }
                    }
                }

                LazyColumn(state = listState) {
                    items(surveyViewListItem) { childView ->
                        val isCurrentGroupActive = !isCheckList && surveyViewListItem.lastOrNull()?.group == childView.group
                        val position = childView.position
                        println("Type Name: ${childView.type}")
                        println("BlockData: $childView")
                        CheckGroupOrBlock(blockListViewModel, childView, isCurrentGroupActive, position, "checkList")
                    }

                    if (isCheckList) {
                        onClose()
                        blockListViewModel.clearCheckList()
                        blockListViewModel.updateCheckList(false)
                        sharedPrefHelper.saveItem(selectedOption)
                    }
                }
            }
        }
    }
}

