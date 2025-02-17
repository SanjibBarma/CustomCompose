package com.example.customcompose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.customcompose.model.BlockList
import com.example.customcompose.model.SurveyDataModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BlockListViewModel(private val surveyDataModelList: List<SurveyDataModel>): ViewModel() {
    private val _blockListItem = MutableStateFlow<List<BlockList>>(emptyList())
    val blockListItem = _blockListItem.asStateFlow()

    private fun addBlockToTheList(blockId: String, groupId: String, position: Int){
        viewModelScope.launch {
            if (surveyDataModelList.isNotEmpty()){
                surveyDataModelList.find { it.group == groupId }?.let { group ->
                    group.blocks.find {it.id == blockId}?.let { block ->
                        val newBlock = BlockList(
                            position = position + 1,
                            surveyBlock = group,
                            surveyHistoryModel = null
                        )

                        _blockListItem.value = _blockListItem.value + newBlock
                    }

                }
            }
        }
    }
}