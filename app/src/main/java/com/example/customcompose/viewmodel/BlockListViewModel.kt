package com.example.customcompose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.customcompose.model.BlockList
import com.example.customcompose.model.SurveyDataModel
import com.example.customcompose.model.SurveyHistoryModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BlockListViewModel(private val surveyDataModelList: List<SurveyDataModel>) : ViewModel() {
    private val _blockListItem = MutableStateFlow<List<BlockList>>(emptyList())
    val blockListItem = _blockListItem.asStateFlow()

    private val _isSubmitted = MutableStateFlow(false)
    val isSubmitted = _isSubmitted.asStateFlow()

    fun addBlockToTheList(blockId: String, groupId: String) {
        println("blockId $blockId groupId: $groupId")

        viewModelScope.launch {
            if (blockId == "submit" && groupId == "submit") {
                _isSubmitted.value = true
            } else {
                _isSubmitted.value = false
                val group = surveyDataModelList.find { it.group == groupId }
                val block = group?.blocks?.find { it.id == blockId }

                if (group != null && block != null) {
                    val existingBlock = _blockListItem.value.find { it.block.id == blockId && it.group.group == groupId}

                    if (existingBlock != null) {
                        val updatedList = _blockListItem.value.takeWhile { it != existingBlock} /*+ existingBlock*/
                        _blockListItem.value = updatedList
                        println("Block already exists. Cleared items after position of ${block.id}")
                    } else {
                        val newBlock = BlockList(
                            position = _blockListItem.value.size,
                            group = group,
                            block = block,
                            surveyHistoryModel = emptyList()
                        )

                        _blockListItem.value = _blockListItem.value.toMutableList().apply { add(newBlock) }
                        println("New Block Added: ${block.id}")
                    }
                    //println("Updated List: ${_blockListItem.value.map { it.block.id }}")
                }
            }
        }
    }

    //get individual data
    fun getData(position: Int): List<SurveyHistoryModel?> {
        return _blockListItem.value.find { it.position == position }!!.surveyHistoryModel
    }

    //save data for every individual block
    fun saveData(position: Int, data: List<SurveyHistoryModel?>) {
        viewModelScope.launch {
            val updatedList = _blockListItem.value.map {
                if (it.position == position) it.copy(surveyHistoryModel = data) else it
            }
            _blockListItem.value = updatedList
            println("Data saved at position: $position")
        }
    }

    //get the data from SurveyHistoryModel list index
    fun getDataFromIndex(position: Int, index: Int): SurveyHistoryModel? {
        val blockList = _blockListItem.value.find { it.position == position }
        return blockList?.surveyHistoryModel?.getOrNull(index)
    }

    fun saveDataAtIndex(position: Int, index: Int, data: SurveyHistoryModel?) {
        viewModelScope.launch {
            val blockList = _blockListItem.value.find { it.position == position }

            blockList?.let {
                val updatedHistoryModel = it.surveyHistoryModel.toMutableList().apply {
                    if (index >= 0 && index < size) {
                        this[index] = data
                    } else {
                        this.add(data)
                    }
                }

                val updatedBlockList = _blockListItem.value.map {
                    if (it.position == position) it.copy(surveyHistoryModel = updatedHistoryModel) else it
                }
                _blockListItem.value = updatedBlockList
                println("Data saved at position: $position, index: $index")
            }
        }
    }
}

