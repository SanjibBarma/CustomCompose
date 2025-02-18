package com.example.customcompose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.customcompose.model.Block
import com.example.customcompose.model.BlockList
import com.example.customcompose.model.SurveyDataModel
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BlockListViewModel(private val surveyDataModelList: List<SurveyDataModel>) : ViewModel() {
    private val _blockListItem = MutableStateFlow<List<BlockList>>(emptyList())
    val blockListItem = _blockListItem.asStateFlow()

    private val _isSubmitted = MutableStateFlow(false)
    val isSubmitted = _isSubmitted.asStateFlow()

    fun addBlockToTheList(blockId: String, groupId: String) {
        viewModelScope.launch {
            if (blockId == "submit" && groupId == "submit") {
                _isSubmitted.value = true
            } else {
                val group = surveyDataModelList.find { it.group == groupId }
                val block = group?.blocks?.find { it.id == blockId }

                if (group != null && block != null) {
                    val existingBlock = _blockListItem.value.find { it.block.id == blockId && it.group.group == groupId}

                    if (existingBlock != null) {
                        val updatedList = _blockListItem.value.takeWhile { it != existingBlock} + existingBlock
                        _blockListItem.value = updatedList
                        println("Block already exists. Cleared items after position of ${block.id}")
                    } else {
                        val newBlock = BlockList(
                            position = _blockListItem.value.size + 1,
                            group = group,
                            block = block,
                            surveyHistoryModel = null
                        )

                        _blockListItem.value = _blockListItem.value.toMutableList().apply { add(newBlock) }
                        println("New Block Added: ${block.id}")
                    }
                    //println("Updated List: ${_blockListItem.value.map { it.block.id }}")
                }
            }
        }
    }
}

