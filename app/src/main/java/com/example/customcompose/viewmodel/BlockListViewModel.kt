package com.example.customcompose.viewmodel

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.customcompose.MyApplication.Companion.appSessionManager
import com.example.customcompose.helper.AudioRecorderService
import com.example.customcompose.model.Block
import com.example.customcompose.model.RoutePlanData
import com.example.customcompose.model.RoutePlanParentModel
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.views.SurveyDataManager.surveyFlowData
import com.google.gson.Gson
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BlockListViewModel(
    private val context: Context
) : ViewModel() {
    private val _parentSurveyBlockList = MutableStateFlow<List<Block>>(emptyList())
    val parentSurveyBlockList = _parentSurveyBlockList.asStateFlow()

    private val _isSubmitted = MutableStateFlow(false)
    val isSubmitted = _isSubmitted.asStateFlow()

    private val _isProgressLoading = MutableStateFlow(false)
    val isProgressLoading = _isProgressLoading.asStateFlow()

    private val _isTermsShow = MutableStateFlow(false)
    val isTermsShow = _isTermsShow.asStateFlow()

    private val gson = Gson()
    private var jumpMatchCount = 0;


    //survey block filtering with referred-to, skip or jumping logic
    fun addBlockToTheSurveyFlow(blockId: String, groupId: String, position: Int) {
        println("initial  blockId $blockId groupId: $groupId")
        println("initial_Position $position")
        val curGroup = surveyFlowData!!.find { it.group == groupId }

        if (blockId == "submit" && groupId == "submit") {
            println("print_log: 5")
            _isSubmitted.value = true
        } else {
            _isSubmitted.value = false
            if (curGroup?.type == "non-referring" || curGroup?.type == "numbervalidation") {
                appSessionManager.savePreviousGroupId(groupId)
                loadCurrentGroupOrBlock(blockId, groupId, position)
                println("print_log: 1")
            }else{
                if (appSessionManager.getPreviousGroupId().equals("")){
                    appSessionManager.savePreviousGroupId(groupId)
                    loadCurrentGroupOrBlock(blockId, groupId, position)
                    println("print_log: 2")
                }else{
                    if (appSessionManager.getPreviousGroupId() != groupId){
                        println("print_log: 3 ${appSessionManager.getPreviousGroupId()}")
                        val previousGroup = surveyFlowData!!.find { it.group == appSessionManager.getPreviousGroupId() }

                        //if current block is the last block of the current group
                        //match the jumping logic condition to jump the next group
                        if (previousGroup != null) {
                            for (jumpingLogic in previousGroup.jumping_logic){
                                if (jumpingLogic.conditions != null && jumpingLogic.conditions.size > 0){
                                    jumpMatchCount = 0
                                    for (surveyModel in _parentSurveyBlockList.value){
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
                                        appSessionManager.savePreviousGroupId(jumpingLogic.group_no)
                                        loadCurrentGroupOrBlock(jumpingLogic.id, jumpingLogic.group_no, position)
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
                        appSessionManager.savePreviousGroupId(groupId)
                        loadCurrentGroupOrBlock(blockId, groupId, position)
                    }
                }
            }
        }
        //loadCurrentGrouporBlock(blockId, groupId)
    }

    //after filtering the block and group update the list for view
    private fun loadCurrentGroupOrBlock(blockId: String, groupId: String, position: Int) {
        viewModelScope.launch {
            val group = surveyFlowData!!.find { it.group == groupId }
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
                    //try to make empty without mentioned it
                    surveyHistoryModel = emptyList(),
                    jumping_logic = group.jumping_logic,
                    position = _parentSurveyBlockList.value.size
                )

                _parentSurveyBlockList.value = _parentSurveyBlockList.value.toMutableList().apply { add(newBlock) }
            } else {
                if (block!!.type == "audio_start") {
                    startAudioService(block.referTo?.id, block.referTo?.group_no, block.position)
                } else if (block.type == "audio_end") {
                    stopAudioService(block.referTo?.id, block.referTo?.group_no, block.position)
                } else if (block.type == "lookup" && block.validations?.invisible!!) {
                    lookupApiCall(block.referTo?.id, block.referTo?.group_no, block.position)
                } else {

                    viewModelScope.launch(Dispatchers.Main) {
                        val mutableParentBlockList = _parentSurveyBlockList.value.toMutableList()

                        if (position+1 in mutableParentBlockList.indices) {
                            mutableParentBlockList.subList(position+1, mutableParentBlockList.size).clear()

                            _parentSurveyBlockList.value = mutableParentBlockList.toList()
                            println("cleared_data: ${mutableParentBlockList.size}")

                            delay(10)
                        }

                        block.surveyHistoryModel = emptyList()
                        block.position = _parentSurveyBlockList.value.size
                        mutableParentBlockList.add(block)

                        _parentSurveyBlockList.value = mutableParentBlockList.toList()
                    }
//                    if (group != null && block != null) {
//                        val existingBlock = _parentSurveyBlockList.value.find { it.id == blockId}
//                        val comboJson = gson.toJson(existingBlock)
//                        Log.d("existingBlock_Data", comboJson)
//                        if (existingBlock != null) {
//                            val blockPosition = _parentSurveyBlockList.value.indexOf(existingBlock)
//                            existingBlock.surveyHistoryModel = emptyList()
//                            block.position = _parentSurveyBlockList.value.size
//                            val updatedList = _parentSurveyBlockList.value.takeWhile { it != existingBlock } + existingBlock
//                            _parentSurveyBlockList.value = updatedList
//                            println("Block already exists. Cleared items after position of ${block.id}")
//                        } else {
//                            //val newBlock = block.copy(surveyHistoryModel = emptyList())
//                            block.surveyHistoryModel= emptyList()
//                            block.position = _parentSurveyBlockList.value.size
//                            _parentSurveyBlockList.value = _parentSurveyBlockList.value.toMutableList().apply { add(block) }
//                            println("New Block Added: ${block.id}")
//                        }
//                    }
                }
            }
        }
    }

    private fun lookupApiCall(blockId: String?, groupId: String?, position: Int?) {
        Toasty.success(context, "Lookup called", Toasty.LENGTH_SHORT).show()
        if (blockId != null && groupId != null) {
            viewModelScope.launch {
                if (position != null) {
                    addBlockToTheSurveyFlow(blockId, groupId, position)
                }
            }
        }
    }

    private fun startAudioService(blockId: String?, groupId: String?, position: Int?) {
        val intent = Intent(context, AudioRecorderService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }

        if (blockId != null && groupId != null) {
            viewModelScope.launch {
                if (position != null) {
                    addBlockToTheSurveyFlow(blockId, groupId, position)
                }
            }
        }
    }

    private fun stopAudioService(blockId: String?, groupId: String?, position: Int?) {
        val group = surveyFlowData!!.find { it.group == groupId }
        val block = group?.blocks?.find { it.id == blockId }

        val intent = Intent(context, AudioRecorderService::class.java)
        context.stopService(intent)
        if (blockId != null && groupId != null) {
            viewModelScope.launch {
                if (position != null) {
                    addBlockToTheSurveyFlow(blockId, groupId, position)
                }
            }
        }
    }

    //get individual data
    fun getData(blockId: String): List<SurveyHistoryModel?>? {
        return _parentSurveyBlockList.value.find { it.id == blockId }!!.surveyHistoryModel
    }

    //save data for every individual block
    fun saveData(blockId: String, data: List<SurveyHistoryModel?>) {
        viewModelScope.launch {
            val updatedList = _parentSurveyBlockList.value.map {
                if (it.id == blockId) it.copy(surveyHistoryModel = data) else it
            }
            _parentSurveyBlockList.value = updatedList
            println("Data saved at position: $blockId")
        }
    }

    //get the data from SurveyHistoryModel list index
    fun getDataFromIndex(position: Int, index: Int): SurveyHistoryModel? {
        val blockList = _parentSurveyBlockList.value.find { it.position == position }
        return blockList?.surveyHistoryModel?.getOrNull(index)
    }

    fun saveDataAtIndex(position: Int, data: SurveyHistoryModel?) {
        viewModelScope.launch {
            val blockList = _parentSurveyBlockList.value.find { it.position == position }

            blockList?.let { it ->
                val updatedHistoryModel = it.surveyHistoryModel.toMutableList().apply {
                    val existingIndex = indexOfFirst { it?.id == data?.id }
                    if (existingIndex != -1) {
                        this[existingIndex] = data!! // Replace if ID matches
                    } else {
                        this.add(data!!) // Add if ID does not exist
                    }
                }

                val updatedBlockList = _parentSurveyBlockList.value.map {
                    if (it.position == position) it.copy(surveyHistoryModel = updatedHistoryModel) else it
                }
                _parentSurveyBlockList.value = updatedBlockList
                println("Data saved at position: $position, id: ${data?.id}")
            }
        }
    }

    private val _checkListParentBlockList = MutableStateFlow<List<Block>>(emptyList())
    val checkListParentBlockList = _checkListParentBlockList.asStateFlow()

    private val _isCheckList = MutableStateFlow(false)
    val isCheckList = _isCheckList.asStateFlow()

    fun addBlockToTheCheckList(blockId: String, groupId: String) {
        println("blockId $blockId groupId: $groupId")

        viewModelScope.launch {
            if (blockId == "checklist") {
                _isCheckList.value = true
                delay(500)
                _checkListParentBlockList.value = emptyList()
            } else {
                _isCheckList.value = false
                val group = surveyFlowData!!.find { it.group == groupId }
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
                        position = _parentSurveyBlockList.value.size
                    )

                    _parentSurveyBlockList.value = _parentSurveyBlockList.value.toMutableList().apply { add(newBlock) }
                }else{
                    if (group != null && block != null) {
                        val existingBlock = _checkListParentBlockList.value.find { it.id == blockId }

                        if (existingBlock != null) {
                            val updatedList = _checkListParentBlockList.value.takeWhile { it != existingBlock } /*+ existingBlock*/
                            _checkListParentBlockList.value = updatedList
                            println("Block already exists. Cleared items after position of ${block.id}")
                        } else {

                            _checkListParentBlockList.value = _checkListParentBlockList.value.toMutableList().apply { add(block) }
                            println("New Block Added: ${block.id}")
                        }
                    }
                }
            }
        }
    }

    fun clearCheckList() {
        if (_checkListParentBlockList.value.isNotEmpty()) {
            _checkListParentBlockList.value = emptyList()
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

    fun removeChkListHistoryByIndex(index: Int) {
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

    private val _routeParentList = MutableStateFlow<List<RoutePlanParentModel>>(emptyList())
    val routeParentList = _routeParentList.asStateFlow()

    private val _isRoutePlan = MutableStateFlow(false)
    val isRoutePlan = _isRoutePlan.asStateFlow()

    fun addNextRoutePlanData(title: String, locations: List<RoutePlanData>?, position: Int) {
        viewModelScope.launch(Dispatchers.Main) {
            val mutableParentList = _routeParentList.value.toMutableList()

            if (position in mutableParentList.indices) {
                mutableParentList.subList(position, mutableParentList.size).clear()

                _routeParentList.value = mutableParentList.toList()
                println("cleared_data: ${mutableParentList.size}")

                delay(5)
            }

            //render problem
            val newRouteData = RoutePlanParentModel(
                typeTitle = title,
                listPosition = position,
                locationList = locations
            )
            mutableParentList.add(newRouteData)

            _routeParentList.value = mutableParentList.toList()
        }
    }

    fun showRoutePlanView() {
        _isRoutePlan.value = true
    }

    fun hideRoutePlanView() {
        _isRoutePlan.value = false
    }

    fun clearRouteList(){
        _routeParentList.value = emptyList()
    }

    fun clearParentBlockList(){
        _parentSurveyBlockList.value = emptyList()
    }

    private val _isShowOtp = MutableStateFlow(false)
    val isShowOtp = _isShowOtp.asStateFlow()

    fun showOtpPopup() {
        _isShowOtp.value = true
    }

    fun hideOtpPopup() {
        _isShowOtp.value = false
    }

    fun showProgressLoading() {
        _isProgressLoading.value = true
    }

    fun hideProgressLoading() {
        _isProgressLoading.value = false
    }

    fun showTermsPopup() {
        _isTermsShow.value = true
    }

    fun hideTermsPopup() {
        _isTermsShow.value = false
    }

}