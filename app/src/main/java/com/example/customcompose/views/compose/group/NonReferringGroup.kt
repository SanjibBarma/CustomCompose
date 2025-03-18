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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.customcompose.model.Block
import com.example.customcompose.viewmodel.BlockListViewModel
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
    blockListViewModel: BlockListViewModel,
    currentBlock: Block,
    position: Int?,
    isActiveGroup: Boolean,
    destination: String
) {
    val context = LocalContext.current

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
