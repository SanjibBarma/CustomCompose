package com.example.customcompose.viewmodel

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.customcompose.helper.AudioRecorderService
import com.example.customcompose.helper.SharedPrefHelper
import com.example.customcompose.model.Block
import com.example.customcompose.model.RoutePlanData
import com.example.customcompose.model.RoutePlanListData
import com.example.customcompose.model.SurveyDataModel
import com.example.customcompose.model.SurveyHistoryModel
import com.google.gson.Gson
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
    private val gson = Gson()
    private var jumpMatchCount = 0;


    //survey block filtering with referred-to, skip or jumping logic
    fun addBlockToTheSurveyFlow(blockId: String, groupId: String) {
        println("initial  blockId $blockId groupId: $groupId")
        val curGroup = surveyDataModelList.find { it.group == groupId }

        if (blockId == "submit" && groupId == "submit") {
            println("print_log: 5")
            _isSubmitted.value = true
        } else {
            _isSubmitted.value = false
            if (curGroup?.type == "non-referring" || curGroup?.type == "numbervalidation") {
                sharedPrefHelper.savePreviousGroupId(groupId)
                loadCurrentGroupOrBlock(blockId, groupId)
                println("print_log: 1")
            }else{
                if (sharedPrefHelper.getPreviousGroupId().equals("")){
                    sharedPrefHelper.savePreviousGroupId(groupId)
                    loadCurrentGroupOrBlock(blockId, groupId)
                    println("print_log: 2")
                }else{
                    if (sharedPrefHelper.getPreviousGroupId() != groupId){
                        println("print_log: 3 ${sharedPrefHelper.getPreviousGroupId()}")
                        val previousGroup = surveyDataModelList.find { it.group == sharedPrefHelper.getPreviousGroupId() }

                        //if current block is the last block of the current group
                        //match the jumping logic condition to jump the next group
                        if (previousGroup != null) {
                            for (jumpingLogic in previousGroup.jumping_logic){
                                if (jumpingLogic.conditions != null && jumpingLogic.conditions.size > 0){
                                    jumpMatchCount = 0
                                    for (surveyModel in _surveyBlockListItem.value){
                                        for (surveyHistory in surveyModel.surveyHistoryModel){
                                            if (surveyHistory != null && !surveyHistory.id.isNullOrEmpty()){
                                                for (jumpCondition in jumpingLogic.conditions){
                                                    if (surveyHistory.id == jumpCondition.id && surveyHistory.answer == jumpCondition.answer){
                                                        jumpMatchCount++
                                                        break
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                if (jumpingLogic.conditions.size == jumpMatchCount){
                                    if (jumpingLogic.id == "submit"){
                                        _isSubmitted.value = false
                                        println("show_submit")
                                        //match with conditions
                                        //if survey conditions submit model is matched with the surveyHistory data
                                        //then make the contact "contact_status" FAILED or SUCCESSFUL
                                    }else{
                                        _isSubmitted.value = false
                                        sharedPrefHelper.savePreviousGroupId(jumpingLogic.group_no)
                                        loadCurrentGroupOrBlock(jumpingLogic.id, jumpingLogic.group_no)
                                    }
                                    return
                                }
                            }
                        }


//                        val jumpGroupId = previousGroup?.jumping_logic?.get(0)?.group_no
//                        val jumpBlockId = previousGroup?.jumping_logic?.get(0)?.id
//                        println("checkJumpingLogic    blockId $jumpBlockId groupId: $jumpGroupId")
//
//                        jumpBlockId?.let { blcId ->
//                            jumpGroupId?.let { grpId ->
//                                sharedPrefHelper.savePreviousGroupId(grpId)
//                                loadCurrentGroupOrBlock(blcId, grpId)
//                            }
//                        }
                    }else{
                        println("print_log: 4")
                        sharedPrefHelper.savePreviousGroupId(groupId)
                        loadCurrentGroupOrBlock(blockId, groupId)
                    }
                }
            }
        }
        //loadCurrentGrouporBlock(blockId, groupId)
    }

    //after filtering the block and group update the list for view
    private fun loadCurrentGroupOrBlock(blockId: String, groupId: String) {
        viewModelScope.launch {
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
                } else if (block.type == "lookup" && block.validations?.invisible!!) {
                    lookupApiCall(block.referTo?.id, block.referTo?.group_no)
                } else {
                    if (group != null && block != null) {
                        val existingBlock = _surveyBlockListItem.value.find { it.id == blockId}
                        val comboJson = gson.toJson(existingBlock)
                        Log.d("existingBlock_Data", comboJson)
                        if (existingBlock != null) {
                            existingBlock.surveyHistoryModel = emptyList()
                            val updatedList = _surveyBlockListItem.value.takeWhile { it != existingBlock } /*+ existingBlock*/
                            _surveyBlockListItem.value = updatedList
                            println("Block already exists. Cleared items after position of ${block.id}")
                        } else {
                            val newBlock = block.copy(surveyHistoryModel = emptyList())
                            _surveyBlockListItem.value = _surveyBlockListItem.value.toMutableList().apply { add(newBlock) }
                            println("New Block Added: ${block.id}")
                        }
                    }
                }
            }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }

        if (blockId != null && groupId != null) {
            viewModelScope.launch {
                addBlockToTheSurveyFlow(blockId, groupId)
            }
        }
    }

    private fun stopAudioService(blockId: String?, groupId: String?) {
        val group = surveyDataModelList.find { it.group == groupId }
        val block = group?.blocks?.find { it.id == blockId }

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
                        val existingBlock = _checkListBlockListItem.value.find { it.id == blockId }

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

    fun updateCheckList(value: Boolean) {
        viewModelScope.launch {
            _isCheckList.emit(value)
        }
    }

    private val _routeListItem = MutableStateFlow<List<RoutePlanListData>>(emptyList())
    val routeListItem = _routeListItem.asStateFlow()

    private val _isRoutePlan = MutableStateFlow(false)
    val isRoutePlan = _isRoutePlan.asStateFlow()

    private val _routePlanHistory = MutableStateFlow<List<SurveyHistoryModel>>(emptyList())
    val routePlanHistory = _routePlanHistory.asStateFlow()

    //update the location list for view
    fun addNextRoutePlanData(title: String, locations: List<RoutePlanData>?, position: Int) {
        _routeListItem.update { currentList ->
            //indexOfLast last theke and indexOfFirst first theke index search kore
            val existingIndex = currentList.indexOfLast { it.listPosition == position }

            if (existingIndex != -1) {
                currentList.toMutableList().apply {
                    this[existingIndex] = RoutePlanListData(
                        typeTitle = title,
                        listPosition = existingIndex,
                        locationList = locations
                    )
                }
            } else {
                currentList + RoutePlanListData(
                    typeTitle = title,
                    listPosition = currentList.size,
                    locationList = locations
                )
            }
        }
    }

    fun showRoutePlanView() {
        _isRoutePlan.value = true
    }

    fun hideRoutePlanView() {
        _isRoutePlan.value = false
    }

    fun clearRouteList(){
        _routeListItem.value = emptyList()
    }


}