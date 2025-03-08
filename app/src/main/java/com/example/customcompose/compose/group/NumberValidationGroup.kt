package com.example.customcompose.compose.group

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.customcompose.R
import com.example.customcompose.compose.number_validation.NonRefCheckBox
import com.example.customcompose.compose.number_validation.NonRefContactNo
import com.example.customcompose.compose.number_validation.NonRefDate
import com.example.customcompose.compose.number_validation.NonRefDropdown
import com.example.customcompose.compose.number_validation.NonRefEmailInput
import com.example.customcompose.compose.number_validation.NonRefMultipleChoice
import com.example.customcompose.compose.number_validation.NonRefNumberInput
import com.example.customcompose.compose.number_validation.NonRefProductList
import com.example.customcompose.compose.number_validation.NonRefTextInput
import com.example.customcompose.helper.AppSessionManager
import com.example.customcompose.helper.UIState
import com.example.customcompose.model.Block
import com.example.customcompose.viewmodel.BlockListViewModel
import com.example.customcompose.viewmodel.NumberValidationViewModel
import com.google.gson.Gson
import es.dmoral.toasty.Toasty

@Composable
fun NumberValidationGroup(
    blockListViewModel: BlockListViewModel,
    currentBlock: Block?,
    position: Int?,
    isActiveGroup: Boolean,
    destination: String,
    numberValidationViewModel: NumberValidationViewModel
) {
    val context = LocalContext.current
    val sharedPrefHelper = remember { AppSessionManager(context) }
    val numberValidationState = numberValidationViewModel.checkNumberData.observeAsState(initial = UIState.Loading)
    val givableDataState = numberValidationViewModel.achievementData.observeAsState(initial = UIState.Loading)
    var isLoading by remember { mutableStateOf(false) }
    var isFreshConsumer by remember { mutableStateOf(false) }
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
                        //isLoading = true
                        if (currentBlock.surveyHistoryModel.isNotEmpty()){
                            println("history list size: ${currentBlock.surveyHistoryModel}")
                            for (nonRefBlocks in currentBlock.blocks){
                                for (surveyHistory in currentBlock.surveyHistoryModel){
//                                println("history Id is: ${surveyHistory?.id}")
                                    if (nonRefBlocks.id == surveyHistory?.id){
                                        if (surveyHistory?.answer.isNullOrEmpty()){
                                            Toasty.warning(context, "Provide a valid ${surveyHistory?.question}", Toasty.LENGTH_SHORT).show()
                                            return@Button
                                        }
                                        if (nonRefBlocks.type == "contactNo"){
                                            if (surveyHistory?.answer.isNullOrEmpty()){
                                                Toasty.warning(context, "Provide a valid ${surveyHistory?.question}", Toasty.LENGTH_SHORT).show()
                                                return@Button
                                            }
                                            if (nonRefBlocks.type == "contactNo"){
                                                val phoneNumber = surveyHistory?.answer
                                                val phoneRegex = "^(13|14|15|16|17|18|19)\\d{8}$".toRegex()

                                                if (phoneNumber?.length != 10) {
                                                    Toasty.warning(context, "Contact number must be 10 digits.", Toasty.LENGTH_SHORT).show()
                                                    return@Button
                                                }
                                                if (!phoneNumber.matches(phoneRegex)!!) {
                                                    Toasty.warning(context, "Contact number in not valid.", Toasty.LENGTH_SHORT).show()
                                                    return@Button
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        val surveyDataMap = mutableMapOf<String, HashMap<String, String>>()
                        val sourceLocation = blockListViewModel.routeParentList.value[0].selectedId
                        val locationId = blockListViewModel.routeParentList.value[blockListViewModel.routeParentList.value.size-1].selectedId

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
                                if (sourceLocation != null) {
                                    put("source_location", sourceLocation)
                                }
                                if (locationId != null) {
                                    put("location_id", locationId)
                                }
                            }
                        }


                        val jsonString = gson.toJson(numberValidationMap)
                        println("number_validation_map $jsonString")

//                        val extraService = true
//                        if (extraService){
//                            numberValidationViewModel.getAchievementData("bearer ${sharedPrefHelper.getSessionToken()}", "125", numberValidationMap)
//                        }else{
//                            numberValidationViewModel.checkNumber("bearer ${sharedPrefHelper.getSessionToken()}", numberValidationMap)
//                        }


                        //this will add in popup
                        blockListViewModel.addBlockToTheSurveyFlow(currentBlock.jumping_logic?.get(0)!!.id, currentBlock.jumping_logic[0].group_no)
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    enabled = isActiveGroup
                ) {
                    Text("Next")
                }
            }
        }

        if (isFreshConsumer){
            PopupFreshConsumer(currentBlock, blockListViewModel, onDismiss = {isFreshConsumer = false})
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        when (val state = numberValidationState.value) {
            is UIState.Error -> {
                Toasty.error(context, state.exception.message ?: "Number validation failed!", Toasty.LENGTH_SHORT).show()
                isLoading = false
            }
            is UIState.Loading -> {
            }
            is UIState.Success -> {
                isLoading = false

                val isExist = state.data.data[0].exist
                val isEligible = state.data.data[0].eligible
                val dynmcInfoConModelList = state.data.data[0].information

                if (!isExist && isEligible){
                    //fresh consumer
//                    PopupFreshConsumer(currentBlock, blockListViewModel, onDismiss = {isLoading = false})
                    isFreshConsumer = true
                }else if (isExist && isEligible) {
                    //non fresh consumer
//                    PopupFreshConsumer(currentBlock, blockListViewModel, onDismiss = {isLoading = false})
                    isFreshConsumer = true
                } else if (isExist && !isEligible) {
                    // non fresh consumer
//                    PopupFreshConsumer(currentBlock, blockListViewModel, onDismiss = {isLoading = false})
                    isFreshConsumer = true
                }else if (!isExist && !isEligible){
                    //banned consumer
//                    PopupFreshConsumer(currentBlock, blockListViewModel, onDismiss = {isLoading = false})
                    isFreshConsumer = true
                }
            }
        }

        when (val state = givableDataState.value) {
            is UIState.Error -> {
                Toasty.error(context, state.exception.message ?: "Something went wrong!", Toasty.LENGTH_SHORT).show()
                isLoading = false
            }
            is UIState.Loading -> {
            }
            is UIState.Success -> {
                isLoading = false
            }
        }


    }
}

@Composable
fun PopupFreshConsumer(
    currentBlock: Block?,
    blockListViewModel: BlockListViewModel,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.Transparent)
        ) {

            Column {
                Spacer(modifier = Modifier.height(35.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = CardDefaults.cardColors(Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Consumer is eligible for this contact",
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp, top = 40.dp),
                            fontFamily = FontFamily.SansSerif,
                            color = Color.Black,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = {
                                    blockListViewModel.addBlockToTheSurveyFlow(currentBlock?.jumping_logic?.get(0)!!.id, currentBlock.jumping_logic[0].group_no)
                                },
                                modifier = Modifier
                                    .width(120.dp),
                                colors = ButtonDefaults.buttonColors(Color(0xFF6200EE))
                            ) {
                                Text(text = "Yes", color = Color.White)
                            }

                            OutlinedButton (
                                onClick = { onDismiss() },
                                modifier = Modifier
                                    .width(120.dp)
                            ) {
                                Text(text = "Cancel", color = Color.Red)
                            }
                        }
                    }
                }
            }

            Image(
                painter = painterResource(id = R.drawable.popup_icon_new_contact),
                contentDescription = "Popup Icon",
                modifier = Modifier
                    .height(90.dp)
                    .width(90.dp)
                    .align(Alignment.TopCenter)
            )
        }
    }
}

