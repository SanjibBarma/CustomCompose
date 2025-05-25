package com.example.customcompose.views.compose.group

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.customcompose.MyApplication.Companion.appSessionManager
import com.example.customcompose.MyApplication.Companion.surveyFlowViewModel
import com.example.customcompose.helper.Constants.fullCampaignData
import com.example.customcompose.helper.Constants.surveyBasicInfo
import com.example.customcompose.helper.UIState
import com.example.customcompose.model.Block
import com.example.customcompose.model.ExtraServiceModel
import com.example.customcompose.model.DynamicInfoConModel
import com.example.customcompose.viewmodel.BlockListViewModel
import com.example.customcompose.views.compose.helper_compose.PopupBannedConsumer
import com.example.customcompose.views.compose.helper_compose.PopupFreshConsumer
import com.example.customcompose.views.compose.helper_compose.PopupNonFreshConsumer
import com.example.customcompose.views.compose.number_validation.NonRefCheckBox
import com.example.customcompose.views.compose.number_validation.NonRefContactNo
import com.example.customcompose.views.compose.number_validation.NonRefDate
import com.example.customcompose.views.compose.number_validation.NonRefDropdown
import com.example.customcompose.views.compose.number_validation.NonRefEmailInput
import com.example.customcompose.views.compose.number_validation.NonRefMultipleChoice
import com.example.customcompose.views.compose.number_validation.NonRefNumberInput
import com.example.customcompose.views.compose.number_validation.NonRefProductList
import com.example.customcompose.views.compose.number_validation.NonRefTextInput
import com.google.gson.Gson
import es.dmoral.toasty.Toasty

@Composable
fun NumberValidationGroup(
    blockListViewModel: BlockListViewModel,
    currentBlock: Block?,
    position: Int?,
    isActiveGroup: Boolean,
    destination: String,
) {
    val context = LocalContext.current
    val numberValidationState = surveyFlowViewModel.checkNumberData.observeAsState(initial = UIState.Loading)
    val givableDataState = surveyFlowViewModel.achievementData.observeAsState(initial = UIState.Loading)
    var isFreshConsumer by remember { mutableStateOf(false) }
    var isNonFreshConsumer by remember { mutableStateOf(false) }
    var isBannedConsumer by remember { mutableStateOf(false) }
    var isMaterialGiveable by remember { mutableStateOf(false) }
    var status by remember { mutableIntStateOf(100) }
    var isLoaded by remember { mutableStateOf(false) }
    var dynmcInfoConModelList by remember { mutableStateOf(emptyList<DynamicInfoConModel>()) }
    var messages by remember { mutableStateOf(emptyList<String>()) }
    val gson = Gson()

    Box(
        modifier = Modifier
            .wrapContentHeight()
    ){
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(containerColor = if (isActiveGroup) Color.White else Color.LightGray)
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                println("Nonref Block Size: ${currentBlock?.blocks?.size}")

                for ((index, block) in currentBlock?.blocks!!.withIndex()){
//                println("Nonref Block Id is: ${block.id}")
                    when(block.type){
                        "dropdown" -> NonRefDropdown(block, blockListViewModel, index, position!!, isActiveGroup)
                        "date" -> NonRefDate(block, blockListViewModel, index, position!!, isActiveGroup)
                        "multipleChoice+icon" -> NonRefMultipleChoice(block, blockListViewModel, index, position!!, isActiveGroup)
                        "textInput" -> NonRefTextInput(block, blockListViewModel, index, position!!, isActiveGroup)
                        "checkbox" -> NonRefCheckBox(block, blockListViewModel, index, position!!, isActiveGroup)
                        "numberInput" -> NonRefNumberInput(block, blockListViewModel, index, position!!, isActiveGroup)
                        "multipleChoice" -> NonRefMultipleChoice(block, blockListViewModel, index, position!!, isActiveGroup)
                        "dropdown+condition" -> NonRefDropdown(block, blockListViewModel, index, position!!, isActiveGroup)
                        "emailInput" -> NonRefEmailInput(block, blockListViewModel, index, position!!, isActiveGroup)
                        "contactNo" -> NonRefContactNo(block, blockListViewModel, index, position!!, isActiveGroup)
                        "product" -> NonRefProductList(block, blockListViewModel, index, position!!, isActiveGroup)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        appSessionManager.setContactNumber("")
                        blockListViewModel.showProgressLoading()
                        surveyFlowViewModel.resetNumberValidationState()

                        if (currentBlock.surveyHistoryModel.isNotEmpty()){
                            println("history list size: ${currentBlock.surveyHistoryModel}")
                            for (nonRefBlocks in currentBlock.blocks){
                                for (surveyHistory in currentBlock.surveyHistoryModel){
//                                println("history Id is: ${surveyHistory?.id}")
                                    if (nonRefBlocks.id == surveyHistory?.id){
                                        if (surveyHistory?.answer.isNullOrEmpty()){
                                            blockListViewModel.hideProgressLoading()
                                            Toasty.warning(context, "Provide a valid ${nonRefBlocks.question?.slug}", Toasty.LENGTH_SHORT).show()
                                            return@Button
                                        }
                                        if (nonRefBlocks.type == "contactNo"){
                                            val phoneNumber = surveyHistory?.answer
                                            val phoneRegex = "^(13|14|15|16|17|18|19)\\d{8}$".toRegex()

//                                            if (surveyHistory?.answer.isNullOrEmpty()){
//                                                blockListViewModel.hideProgressLoading()
//                                                Toasty.warning(context, "Provide a valid ${surveyHistory?.question}", Toasty.LENGTH_SHORT).show()
//                                                return@Button
//                                            }
                                            if (phoneNumber?.length != 10) {
                                                blockListViewModel.hideProgressLoading()
                                                Toasty.warning(context, "Contact number must be 10 digits.", Toasty.LENGTH_SHORT).show()
                                                return@Button
                                            }
                                            if (!phoneNumber.matches(phoneRegex)!!) {
                                                blockListViewModel.hideProgressLoading()
                                                Toasty.warning(context, "Contact number is not valid.", Toasty.LENGTH_SHORT).show()
                                                return@Button
                                            }
                                            appSessionManager.setContactNumber(phoneNumber)
                                            surveyBasicInfo["contact_no"] = phoneNumber

                                        }
                                    }
                                }
                            }
                        }

                        val surveyDataMap = mutableMapOf<String, HashMap<String, String>>()
                        val sourceLocation = blockListViewModel.routeParentList.value[0].selectedId
                        val locationId = blockListViewModel.routeParentList.value[blockListViewModel.routeParentList.value.size-1].selectedId

                        surveyBasicInfo["source_location"] = sourceLocation.toString()
                        surveyBasicInfo["location_id"] = locationId.toString()

                        val numberValidationMap = HashMap<String, Any>()

                        if (currentBlock.surveyHistoryModel.isNotEmpty()) {
                            for (surveyHistory in currentBlock.surveyHistoryModel) {
                                if (surveyHistory != null) {
                                    surveyDataMap[surveyHistory.id] = hashMapOf(
                                        "answer" to surveyHistory.answer,
                                        "question" to surveyHistory.question
                                    )
                                }
                            }

                            numberValidationMap.apply {
                                put("numberValidation", surveyDataMap)
                                appSessionManager.getCampaignId()?.toInt()
                                    ?.let { put("campaign_id", it) }
                                if (sourceLocation != null) {
                                    put("source_location", sourceLocation.toString())
                                }
                                if (locationId != null) {
                                    put("location_id", locationId.toString())
                                }
                            }
                        }


                        val jsonString = gson.toJson(numberValidationMap)
                        println("number_validation_map $jsonString")

                        val extraService = appSessionManager.getExtraServiceCamInfo()
                        if (!extraService.isNullOrEmpty()) {
                            val extraServiceData: ExtraServiceModel? = gson.fromJson(extraService, ExtraServiceModel::class.java)

                            for (assignedMaterial in extraServiceData?.data?.campaign_info!!){
                                if (assignedMaterial.id == appSessionManager.getCampaignId()?.toInt()){
                                    isMaterialGiveable = assignedMaterial.material_assigned
                                }
                            }
                        }

//                        val extraServiceValidation = false
                        if (isMaterialGiveable){
                            isLoaded = true
                            surveyFlowViewModel.getAchievementData("bearer ${appSessionManager.getSessionToken()}", appSessionManager.getCampaignId().toString(), numberValidationMap)
                        }else{
                            isLoaded = true
                            surveyFlowViewModel.checkNumber("bearer ${appSessionManager.getSessionToken()}", numberValidationMap)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    enabled = isActiveGroup
                ) {
                    Text("Next")
                }
            }
        }

        if (isLoaded){
            when (val state = numberValidationState.value) {
                is UIState.Error -> {
                    blockListViewModel.hideProgressLoading()
                    Toasty.error(context, state.exception.message ?: "Number validation failed!", Toasty.LENGTH_SHORT).show()
                }
                is UIState.Loading -> {}
                is UIState.Success -> {
                    blockListViewModel.hideProgressLoading()
                    val isExist = state.data.data[0].exist
                    val isEligible = state.data.data[0].eligible
                    status = state.data.data[0].status
                    surveyBasicInfo["seg_status"] = state.data.data[0].status
                    surveyBasicInfo["seg_stts"] = state.data.data[0].status


                    dynmcInfoConModelList = state.data.data[0].information
                    messages = state.data.data[0].message

                    println("numberValidationState: recompose")

                    if (!isExist && isEligible){
                        isFreshConsumer = true
                    }else if (isExist && isEligible) {
                        isNonFreshConsumer = true
                    } else if (isExist && !isEligible) {
                        isBannedConsumer = true
                    }else if (!isExist && !isEligible){
                        isBannedConsumer = true
                    }

                }
            }

        }

        when (val state = givableDataState.value) {
            is UIState.Error -> {
                blockListViewModel.hideProgressLoading()
                Toasty.error(context, state.exception.message ?: "Something went wrong!", Toasty.LENGTH_SHORT).show()
            }
            is UIState.Loading -> {}
            is UIState.Success -> {
            }
        }


        if (isFreshConsumer && isLoaded) {
            PopupFreshConsumer(
                onDismiss = {
                    isFreshConsumer = false
                    isLoaded = false
                },
                goToNextPage = {
                    goToNextPage (blockListViewModel, currentBlock, status, position)
                }
            )
        }

        if (isNonFreshConsumer && isLoaded) {
            PopupNonFreshConsumer(
                dynmcInfoConModelList,
                messages,
                onDismiss = {
                    isNonFreshConsumer = false
                    isLoaded = false
                },

                goToNextPage = {
                    goToNextPage (blockListViewModel, currentBlock, status, position)
                }
            )
        }


        if (isBannedConsumer && isLoaded) {
            PopupBannedConsumer (
                dynmcInfoConModelList,
                messages,
                onDismiss = {
                    isBannedConsumer = false
                    isLoaded = false
                },

                goToNextPage = {
                    goToNextPage (blockListViewModel, currentBlock, status, position)
                }
            )
        }
    }
}

fun goToNextPage(
    blockListViewModel: BlockListViewModel,
    currentBlock: Block?,
    status: Int,
    position: Int?
) {
    if (!fullCampaignData?.conditions?.segments.isNullOrEmpty() && fullCampaignData?.conditions?.segments?.size!! > 0){
        for (segment in fullCampaignData?.conditions?.segments!!){
            if (status == segment.status){
                segment.referTo.id?.let { blockId ->
                    segment.referTo.group_no?.let { groupId ->
                    if (position != null) {
                        blockListViewModel.addBlockToTheSurveyFlow(blockId, groupId, position)
                        return
                    }
                } }
            }
        }
    }

    currentBlock?.position?.let { position ->
        blockListViewModel.addBlockToTheSurveyFlow(
            currentBlock.jumping_logic?.get(0)!!.id,
            currentBlock.jumping_logic[0].group_no,
            position
        )
    }
}

