package com.example.customcompose.views.compose.group

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.customcompose.MyApplication.Companion.blockListViewModel
import com.example.customcompose.helper.CommonUtils.getTapAnalysisElapsedTime
import com.example.customcompose.helper.CommonUtils.saveTapAnalysisData
import com.example.customcompose.helper.Constants.BTN_NXT
import com.example.customcompose.helper.Constants.numberValTapResult
import com.example.customcompose.model.Block
import com.example.customcompose.model.Result
import com.example.customcompose.views.compose.number_validation.NonRefCheckBox
import com.example.customcompose.views.compose.number_validation.NonRefContactNo
import com.example.customcompose.views.compose.number_validation.NonRefDate
import com.example.customcompose.views.compose.number_validation.NonRefDropdown
import com.example.customcompose.views.compose.number_validation.NonRefEmailInput
import com.example.customcompose.views.compose.number_validation.NonRefMultipleChoice
import com.example.customcompose.views.compose.number_validation.NonRefNumberInput
import com.example.customcompose.views.compose.number_validation.NonRefProductList
import com.example.customcompose.views.compose.number_validation.NonRefTextInput
import es.dmoral.toasty.Toasty

@Composable
fun NonReferringGroup(
    currentBlock: Block,
    position: Int?,
    isActiveGroup: Boolean,
    destination: String
) {
    val context = LocalContext.current

    LaunchedEffect (Unit){
        numberValTapResult.clear()
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
                    .imePadding()
            ) {
                println("Nonref Block Size: ${currentBlock?.blocks?.size}")

                for ((index, block) in currentBlock?.blocks!!.withIndex()){
//                println("Nonref Block Id is: ${block.id}")
                    when(block.type){
                        "dropdown" -> NonRefDropdown(block, index, position!!, isActiveGroup){result -> tapData(result)}
                        "date" -> NonRefDate(block, index, position!!, isActiveGroup){result -> tapData(result)}
                        "multipleChoice+icon" -> NonRefMultipleChoice(block, index, position!!, isActiveGroup){result -> tapData(result)}
                        "textInput" -> NonRefTextInput(block, index, position!!, isActiveGroup){result -> tapData(result)}
                        "checkbox" -> NonRefCheckBox(block, index, position!!, isActiveGroup){result -> tapData(result)}
                        "numberInput" -> NonRefNumberInput(block, index, position!!, isActiveGroup){result -> tapData(result)}
                        "multipleChoice" -> NonRefMultipleChoice(block, index, position!!, isActiveGroup){result -> tapData(result)}
                        "dropdown+condition" -> NonRefDropdown(block, index, position!!, isActiveGroup){result -> tapData(result)}
                        "emailInput" -> NonRefEmailInput(block, index, position!!, isActiveGroup){result -> tapData(result)}
                        "contactNo" -> NonRefContactNo(block, index, position!!, isActiveGroup){result -> tapData(result)}
                        "product" -> NonRefProductList(block, index, position!!, isActiveGroup){result -> tapData(result)}
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        blockListViewModel.showProgressLoading()

                        if (currentBlock.surveyHistoryModel.isNotEmpty()){
                            println("history list size: ${currentBlock.surveyHistoryModel}")
                            for (nonRefBlocks in currentBlock.blocks){
                                for (surveyHistory in currentBlock.surveyHistoryModel){
//                                println("history Id is: ${surveyHistory?.id}")
                                    if (nonRefBlocks.id == surveyHistory?.id){
                                        if (surveyHistory?.answer.isNullOrEmpty()){
                                            blockListViewModel.hideProgressLoading()
                                            Toasty.warning(context, "Provide a valid ${surveyHistory?.question}", Toasty.LENGTH_SHORT).show()
                                            return@Button
                                        }
                                        if (nonRefBlocks.type == "contactNo"){
                                            if (surveyHistory?.answer.isNullOrEmpty()){
                                                blockListViewModel.hideProgressLoading()
                                                Toasty.warning(context, "Provide a valid ${surveyHistory?.question}", Toasty.LENGTH_SHORT).show()
                                                return@Button
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
                                                    Toasty.warning(context, "Contact number in not valid.", Toasty.LENGTH_SHORT).show()
                                                    return@Button
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        val result = Result(
                            option = BTN_NXT,
                            tap_time = (getTapAnalysisElapsedTime()!! / 1000000).toString()
                        )
                        numberValTapResult.add(result)

                        saveTapAnalysisData(currentBlock, numberValTapResult, "non_referring")

                        currentBlock.position?.let {position ->
                            blockListViewModel.hideProgressLoading()
                            blockListViewModel.addBlockToTheSurveyFlow(currentBlock.jumping_logic?.get(0)!!.id, currentBlock.jumping_logic[0].group_no, position)
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
    }
}

fun tapData(result: Result) {
    numberValTapResult.add(result)
}