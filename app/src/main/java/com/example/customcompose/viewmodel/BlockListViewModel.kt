package com.example.customcompose.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.customcompose.helper.AudioRecorderService
import com.example.customcompose.helper.SharedPrefHelper
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyDataModel
import com.example.customcompose.model.SurveyHistoryModel
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BlockListViewModel(
    private val context: Context,
    private val surveyDataModelList: List<SurveyDataModel>
) : ViewModel() {

    private val sharedPrefHelper =  SharedPrefHelper(context)

    private val _surveyBlockListItem = MutableStateFlow<List<Block>>(emptyList())
    val surveyBlockListItem = _surveyBlockListItem.asStateFlow()

    private val _isSubmitted = MutableStateFlow(false)
    val isSubmitted = _isSubmitted.asStateFlow()

    fun addBlockToTheSurveyFlow(blockId: String, groupId: String) {

        val group = surveyDataModelList.find { it.group == groupId }
        val block = group?.blocks?.find { it.id == blockId }

        if (group?.type == "non-referring" || group?.type == "numbervalidation") {
            loadCurrentGrouporBlock(blockId, groupId)
        }else{
            if (groupId != sharedPrefHelper.getPreviousGroupId()){
                //            println("checkJumpingLogic    blockId $blockId groupId: $groupId")
                val group = surveyDataModelList.find { it.group == sharedPrefHelper.getPreviousGroupId() }

                val currentGroupId = group?.jumping_logic?.get(0)?.group_no
                val currentBlockId = group?.jumping_logic?.get(0)?.id

                currentBlockId?.let { currentGroupId?.let { it1 -> loadCurrentGrouporBlock(it, it1) } }
                //sharedPrefHelper.savePreviousGroupId(groupId)
            }else{
                loadCurrentGrouporBlock(blockId, groupId)
                println("checkJumpingLogic    blockId $blockId groupId: $groupId")
                println("checkJumpingLogicGroupId    ${sharedPrefHelper.getPreviousGroupId()}")
            }
        }
    }

    private fun loadCurrentGrouporBlock(blockId: String, groupId: String) {
        viewModelScope.launch {
            if (blockId == "submit" && groupId == "submit") {
                _isSubmitted.value = true
            } else {
                _isSubmitted.value = false
                val group = surveyDataModelList.find { it.group == groupId }
                val block = group?.blocks?.find { it.id == blockId }

                if (group?.type == "non-referring" || group?.type == "numbervalidation") {
                    val newBlock = Block(
                        id = block?.id,
                        skip = null,
                        type = group.type,
                        options = null,
                        referTo = null,
                        question = null,
                        required = null,
                        validations = null,
                        group = group.group,
                        blocks = group.blocks,
                        surveyHistoryModel = emptyList(),
                        jumping_logic = group.jumping_logic,
                        position = _surveyBlockListItem.value.size
                    )

                    _surveyBlockListItem.value = _surveyBlockListItem.value.toMutableList().apply { add(newBlock) }
                } else {
                    if (block!!.type == "audio_start") {
                        startAudioService(block.referTo?.id, block.referTo?.group_no)
                    } else if (block.type == "audio_end") {
                        stopAudioService(block.referTo?.id, block.referTo?.group_no)
                    } else if (block.type == "lookup") {
                        lookupApiCall(block.referTo?.id, block.referTo?.group_no)
                    } else {
                        if (group != null && block != null) {
                            val existingBlock = _surveyBlockListItem.value.find { it.id == blockId}

                            if (existingBlock != null) {
                                val updatedList = _surveyBlockListItem.value.takeWhile { it != existingBlock } /*+ existingBlock*/
                                _surveyBlockListItem.value = updatedList
                                println("Block already exists. Cleared items after position of ${block.id}")
                            } else {
                                _surveyBlockListItem.value = _surveyBlockListItem.value.toMutableList().apply { add(block) }
                                println("New Block Added: ${block.id}")
                            }
                        }
                    }
                }


            }
        }
        if (groupId != "submit"){
            sharedPrefHelper.savePreviousGroupId(groupId)
        }
    }

    private fun lookupApiCall(blockId: String?, groupId: String?) {
        Toasty.success(context, "Lookup called", Toasty.LENGTH_SHORT).show()
        if (blockId != null && groupId != null) {
            viewModelScope.launch {
                addBlockToTheSurveyFlow(blockId, groupId)
            }
        }
    }

    private fun startAudioService(blockId: String?, groupId: String?) {
        val intent = Intent(context, AudioRecorderService::class.java)
        context.startService(intent)
        if (blockId != null && groupId != null) {
            viewModelScope.launch {
                addBlockToTheSurveyFlow(blockId, groupId)
            }
        }
    }

    private fun stopAudioService(blockId: String?, groupId: String?) {
        val intent = Intent(context, AudioRecorderService::class.java)
        context.stopService(intent)
        if (blockId != null && groupId != null) {
            viewModelScope.launch {
                addBlockToTheSurveyFlow(blockId, groupId)
            }
        }
    }


    //get individual data
    fun getData(blockId: String): List<SurveyHistoryModel?>? {
        return _surveyBlockListItem.value.find { it.id == blockId }!!.surveyHistoryModel
    }


    //save data for every individual block
    fun saveData(blockId: String, data: List<SurveyHistoryModel?>) {
        viewModelScope.launch {
            val updatedList = _surveyBlockListItem.value.map {
                if (it.id == blockId) it.copy(surveyHistoryModel = data) else it
            }
            _surveyBlockListItem.value = updatedList
            println("Data saved at position: $blockId")
        }
    }


    //get the data from SurveyHistoryModel list index
    fun getDataFromIndex(position: Int, index: Int): SurveyHistoryModel? {
        val blockList = _surveyBlockListItem.value.find { it.position == position }
        return blockList?.surveyHistoryModel?.getOrNull(index)
    }

    fun saveDataAtIndex(position: Int, data: SurveyHistoryModel?) {
        viewModelScope.launch {
            val blockList = _surveyBlockListItem.value.find { it.position == position }

            blockList?.let { it ->
                val updatedHistoryModel = it.surveyHistoryModel.toMutableList().apply {
                    val existingIndex = indexOfFirst { it?.id == data?.id }
                    if (existingIndex != -1) {
                        this[existingIndex] = data!! // Replace if ID matches
                    } else {
                        this.add(data!!) // Add if ID does not exist
                    }
                }

                val updatedBlockList = _surveyBlockListItem.value.map {
                    if (it.position == position) it.copy(surveyHistoryModel = updatedHistoryModel) else it
                }
                _surveyBlockListItem.value = updatedBlockList
                println("Data saved at position: $position, id: ${data?.id}")
            }
        }
    }




    private val _checkListBlockListItem = MutableStateFlow<List<Block>>(emptyList())
    val checkListBlockListItem = _checkListBlockListItem.asStateFlow()

    private val _isCheckList = MutableStateFlow(false)
    val isCheckList = _isCheckList.asStateFlow()

    fun addBlockToTheCheckList(blockId: String, groupId: String) {
        println("blockId $blockId groupId: $groupId")

        viewModelScope.launch {
            if (blockId == "checklist") {
                _isCheckList.value = true
                delay(500)
                _checkListBlockListItem.value = emptyList()
            } else {
                _isCheckList.value = false
                val group = surveyDataModelList.find { it.group == groupId }
                val block = group?.blocks?.find { it.id == blockId }

                if (group?.type == "non-referring" || group?.type == "numbervalidation") {
                    val newBlock = Block(
                        id = block?.id,
                        skip = block?.skip,
                        type = group.type,
                        options = block?.options,
                        referTo = block?.referTo,
                        question = block?.question,
                        required = block?.required,
                        validations = block?.validations,
                        group = group.group,
                        blocks = group.blocks,
                        surveyHistoryModel = emptyList(),
                        jumping_logic = group.jumping_logic,
                        position = _surveyBlockListItem.value.size
                    )

                    _surveyBlockListItem.value = _surveyBlockListItem.value.toMutableList().apply { add(newBlock) }
                }else{
                    if (group != null && block != null) {
                        val existingBlock =
                            _checkListBlockListItem.value.find { it.id == blockId }

                        if (existingBlock != null) {
                            val updatedList = _checkListBlockListItem.value.takeWhile { it != existingBlock } /*+ existingBlock*/
                            _checkListBlockListItem.value = updatedList
                            println("Block already exists. Cleared items after position of ${block.id}")
                        } else {

                            _checkListBlockListItem.value = _checkListBlockListItem.value.toMutableList().apply { add(block) }
                            println("New Block Added: ${block.id}")
                        }
                    }
                }
            }
        }
    }

    //get individual _checkListBlockListItem
    fun getDataFromCheckList(blockId: String): List<SurveyHistoryModel?>? {
        return _checkListBlockListItem.value.find { it.id == blockId }!!.surveyHistoryModel
    }

    fun clearCheckList() {
        if (_checkListBlockListItem.value.isNotEmpty()) {
            _checkListBlockListItem.value = emptyList()
            //_isCheckList.value = false
        }
    }


    private val _checkListHistory = MutableStateFlow<List<List<SurveyHistoryModel>>>(emptyList())
    val checkListHistory = _checkListHistory.asStateFlow()

    fun addCheckListHistory(historyList: List<SurveyHistoryModel>){
        viewModelScope.launch {
            _checkListHistory.value = _checkListHistory.value.toMutableList().apply { add(historyList) }
        }
    }

    fun removeHistoryByIndex(index: Int) {
        if (index >= 0 && index < checkListHistory.value.size) {
            val updatedHistory = checkListHistory.value.toMutableList()
            updatedHistory.removeAt(index)
            _checkListHistory.value = updatedHistory
        }
    }

//    fun saveHistoryForChecklist(blockId: String, history: SurveyHistoryModel) {
//        viewModelScope.launch {
//            _checkListHistory.update { currentList ->
//                val updatedList = currentList.map {
//                    if (it.id == blockId) history else it // Replace if id matches
//                }
//                if (updatedList.any { it.id == blockId }) {
//                    updatedList // If blockId exists, return updated list
//                } else {
//                    updatedList + history // Otherwise, add new entry
//                }
//            }
//        }
//    }

    fun updateCheckList(value: Boolean) {
        viewModelScope.launch {
            _isCheckList.emit(value)
        }
    }


}