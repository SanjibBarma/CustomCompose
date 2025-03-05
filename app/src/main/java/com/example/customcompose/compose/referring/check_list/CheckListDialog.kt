package com.example.customcompose.compose.referring.check_list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.customcompose.helper.SharedPrefHelper
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.viewmodel.BlockListViewModel
import com.example.customcompose.compose.group.CheckGroupOrBlock
import com.example.customcompose.viewmodel.NumberValidationViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckListDialog(
    selectedOption: String,
    blockListViewModel: BlockListViewModel,
    onClose: () -> Unit,
    onDismiss: () -> Unit,
    numberValidationViewModel: NumberValidationViewModel
) {

    val surveyViewListItem by blockListViewModel.checkListParentBlockList.collectAsState()
    val listState = remember { LazyListState() }
    val coroutineScope = rememberCoroutineScope()
    val isCheckList by blockListViewModel.isCheckList.collectAsState()
    val context = LocalContext.current
    val sharedPrefHelper = remember { SharedPrefHelper(context) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Dialog(
        onDismissRequest = {
            onDismiss()
            keyboardController?.hide()
        },
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
                    .padding(paddingValues)
                    .imePadding()
            ) {
                LaunchedEffect(surveyViewListItem.size) {
                    if (surveyViewListItem.isNotEmpty()) {
                        coroutineScope.launch {
                            listState.scrollToItem(surveyViewListItem.size - 1)
                        }
                    }
                }

                LazyColumn(state = listState, modifier = Modifier.imePadding().fillMaxSize().padding(bottom = 64.dp)) {
                    items(surveyViewListItem) { childView ->
                        val isCurrentGroupActive = !isCheckList && surveyViewListItem.lastOrNull()?.group == childView.group
                        val position = childView.position
                        println("Type Name: ${childView.type}")
                        println("BlockData: $childView")
                        CheckGroupOrBlock(
                            blockListViewModel,
                            childView,
                            isCurrentGroupActive,
                            position,
                            "checkList",
                            numberValidationViewModel
                        )
                    }

                    if (isCheckList) {

                        val comboHistory = mutableListOf<SurveyHistoryModel>()
                        for (surveyBlockHistory in blockListViewModel.checkListParentBlockList.value) {
                            for (surveyHistory in surveyBlockHistory.surveyHistoryModel) {
                                if (surveyHistory != null) {
                                    comboHistory.add(
                                        SurveyHistoryModel(
                                            question = surveyHistory.question,
                                            answer = surveyHistory.answer,
                                            id = surveyHistory.id
                                        )
                                    )
                                }
                            }
                        }
                        blockListViewModel.addCheckListHistory(comboHistory)

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
