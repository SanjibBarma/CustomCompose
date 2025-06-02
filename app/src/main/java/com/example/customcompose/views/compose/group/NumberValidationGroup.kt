package com.example.customcompose.views.compose.group

import android.content.Context
import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
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
import com.example.customcompose.MyApplication.Companion.blockListViewModel
import com.example.customcompose.MyApplication.Companion.dashboardViewModel
import com.example.customcompose.MyApplication.Companion.surveyFlowViewModel
import com.example.customcompose.helper.CommonUtils.getTapAnalysisElapsedTime
import com.example.customcompose.helper.CommonUtils.saveTapAnalysisData
import com.example.customcompose.helper.Constants.BTN_NXT
import com.example.customcompose.helper.Constants.fullCampaignData
import com.example.customcompose.helper.Constants.numberValTapResult
import com.example.customcompose.helper.Constants.surveyBasicInfo
import com.example.customcompose.helper.UIState
import com.example.customcompose.model.Block
import com.example.customcompose.model.DynamicInfoConModel
import com.example.customcompose.model.ExtraServiceModel
import com.example.customcompose.model.Result
import com.example.customcompose.model.SurveyHistoryModel
import com.example.customcompose.model.TargetAchievement
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

    appSessionManager.getBrId()?.let {
        dashboardViewModel.fetchTargetAchieveDataByIds(it, appSessionManager.getCampaignId()!!)
    }
    val localTargetAchievementState by dashboardViewModel.localTargetAchieveData.observeAsState()
    var localAchieveList by remember { mutableStateOf<List<TargetAchievement>?>(emptyList()) }

    LaunchedEffect(localTargetAchievementState) {
        numberValTapResult.clear()
        localTargetAchievementState?.target_achievement?.let {
            try {
                localAchieveList = gson.fromJson(it, Array<TargetAchievement>::class.java).toList()
            } catch (e: Exception) {
                localAchieveList = emptyList()
            }
        }
        println("targetAchieveData_compose: $localAchieveList")
    }

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
                    when(block.type){
                        "dropdown" -> NonRefDropdown(block, index, position!!, isActiveGroup){result -> numberValTapData(result)}
                        "date" -> NonRefDate(block, index, position!!, isActiveGroup){result -> numberValTapData(result)}
                        "multipleChoice+icon" -> NonRefMultipleChoice(block, index, position!!, isActiveGroup){result -> numberValTapData(result)}
                        "textInput" -> NonRefTextInput(block, index, position!!, isActiveGroup){result -> numberValTapData(result)}
                        "checkbox" -> NonRefCheckBox(block, index, position!!, isActiveGroup){result -> numberValTapData(result)}
                        "numberInput" -> NonRefNumberInput(block, index, position!!, isActiveGroup){result -> numberValTapData(result)}
                        "multipleChoice" -> NonRefMultipleChoice(block, index, position!!, isActiveGroup){result -> numberValTapData(result)}
                        "dropdown+condition" -> NonRefDropdown(block, index, position!!, isActiveGroup){result -> numberValTapData(result)}
                        "emailInput" -> NonRefEmailInput(block, index, position!!, isActiveGroup){result -> numberValTapData(result)}
                        "contactNo" -> NonRefContactNo(block, index, position!!, isActiveGroup){result -> numberValTapData(result)}
                        "product" -> NonRefProductList(block, index, position!!, isActiveGroup){result -> numberValTapData(result)}
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {

                        val result = Result(
                            option = BTN_NXT,
                            tap_time = (getTapAnalysisElapsedTime()!! / 1000000).toString()
                        )
                        numberValTapResult.add(result)
                        saveTapAnalysisData(currentBlock, numberValTapResult, "number_validation")

                        appSessionManager.setContactNumber("")
                        blockListViewModel.showProgressLoading()
                        surveyFlowViewModel.resetNumberValidationState()

                        if (currentBlock.surveyHistoryModel.isNotEmpty()){
                            //  Log.d("verifyMobile", "basicQuestionList: "+basicQuestionList.get(basicQuestionList.size()-1).toString());
                            val brandBlockList = mutableListOf<Block>()
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

                                        if (nonRefBlocks.type == "product"){
                                            if (nonRefBlocks.question?.alias == "product"
                                                || nonRefBlocks.question?.alias == "previous_brand"
                                                || nonRefBlocks.question?.alias == "secondary_brand"){
                                                brandBlockList.add(nonRefBlocks)
                                            }
                                        }

                                        if (nonRefBlocks.type == "contactNo"){
                                            val phoneNumber = surveyHistory?.answer
                                            val phoneRegex = "^(13|14|15|16|17|18|19)\\d{8}$".toRegex()

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

                                            if (brandBlockList.isNotEmpty() && localAchieveList != null){
                                                if (!checkBrandValidation(context, brandBlockList, localAchieveList!!, currentBlock.surveyHistoryModel)){
                                                    return@Button
                                                }
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
                    goToNextPage (currentBlock, status, position)
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
                    goToNextPage (currentBlock, status, position)
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
                    goToNextPage (currentBlock, status, position)
                }
            )
        }
    }
}

fun numberValTapData(result: Result) {
    numberValTapResult.add(result)
}

fun checkBrandValidation(
    context: Context,
    brandBlockList: List<Block>,
    localAchieveList: List<TargetAchievement>,
    surveyHistoryModel: List<SurveyHistoryModel?>
): Boolean {
    var matched = 0
    var currentAchievement: TargetAchievement? = null
    val sourceLocation = blockListViewModel.routeParentList.value[0].selectedId

    outerLoop@ for (achievementModel in localAchieveList){
        for (locationTarget in achievementModel.locations){
            if (sourceLocation == locationTarget.id){
                matched = 0
                currentAchievement = achievementModel

                for (brandBlock in brandBlockList){
                    val ansModel: SurveyHistoryModel? = brandBlock.id?.let { surveyHistoryModel[it.toInt()] }
                    println("ansModel: $ansModel")
                    if (brandBlock.question!!.alias == "product"){

                        if (achievementModel.primary_brands.isNotEmpty()){
                            for (productTarget in achievementModel.primary_brands){
                                if (ansModel?.answer == productTarget.id.toString()){
                                    matched++
                                    break
                                }
                            }
                        }else{
                            matched++
                        }

                    }else if (brandBlock.question.alias == "secondary_brand"){

                        if (achievementModel.secondary_brands.isNotEmpty()){
                            for (productTarget in achievementModel.secondary_brands){
                                if (ansModel?.answer == productTarget.id.toString()){
                                    matched++
                                    break
                                }
                            }
                        }else{
                            matched++
                        }

                    }else if (brandBlock.question.alias == "previous_brand"){

                        if (achievementModel.previous_brands.isNotEmpty()){
                            for (productTarget in achievementModel.previous_brands){
                                if (ansModel?.answer == productTarget.id.toString()){
                                    matched++
                                    break
                                }
                            }
                        }else{
                            matched++
                        }

                    }
                }

                if (matched == brandBlockList.size){
                    break@outerLoop
                }
            }
        }
    }

    if (matched == brandBlockList.size){
        if (currentAchievement?.daily_achievement!! >= currentAchievement.daily_target && !currentAchievement.over_achivement){
            Toasty.warning(context, "No more target for this location and products combination", Toasty.LENGTH_SHORT).show()
            blockListViewModel.hideProgressLoading()
            return false
        }
        appSessionManager.insertCurrentTargetAchievementId(Gson().toJson(currentAchievement))
        return true
    }else{
        Toasty.warning(context, "This products and location combination is not allowed.", Toast.LENGTH_SHORT).show()
        blockListViewModel.hideProgressLoading()
        return false
    }
}

fun goToNextPage(
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

