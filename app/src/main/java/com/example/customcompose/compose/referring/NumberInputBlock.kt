package com.example.customcompose.compose.referring

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.customcompose.model.Block
import com.example.customcompose.viewmodel.BlockListViewModel
import es.dmoral.toasty.Toasty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberInputBlock(block: Block, blockListViewModel: BlockListViewModel) {
    var text by remember { mutableStateOf("") }
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val isSkippable = block.skip?.id != "-1"
    val validationRegex = block.validations?.regex

    fun validateInput(input: String): Boolean {
        return if (validationRegex != null) {
            Regex(validationRegex).matches(input)
        } else {
            true
        }
    }

    Column {
        Text(text = block.question!!.slug)

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
            },
            modifier = Modifier
                .fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.White,
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.Gray
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row (
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            if (isSkippable){
                Button(
                    onClick = {
                        keyboardController?.hide()
                        block.skip?.group_no?.let { groupId ->
                            block.skip.id.let { blockId ->
                                blockListViewModel.addBlockToTheList(blockId, groupId)
                            }
                        }
                    },
                    enabled = (isSkippable || text.isNotBlank()),
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Blue,
                        contentColor = Color.White
                    )
                ) {
                    Text("Skip")
                }

                Spacer(modifier = Modifier.width(8.dp))
            }

            Button(
                onClick = {
                    keyboardController?.hide()
                    if (text.isNotBlank()) {
                        if (validateInput(text)) {
                            block.referTo?.group_no?.let { groupId ->
                                block.referTo.id?.let { blockId ->
                                    blockListViewModel.addBlockToTheList(blockId, groupId)
                                }
                            }
                        } else {
                            Toasty.warning(context, "Input valid ${block.question.slug}", Toasty.LENGTH_SHORT).show()
                        }
                    } else {
                        Toasty.warning(context, "Input valid ${block.question.slug}", Toasty.LENGTH_SHORT).show()
                    }
                },
                enabled = (isSkippable || text.isNotBlank()),
                modifier = Modifier.fillMaxWidth().weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue,
                    contentColor = Color.White
                )
            ) {
                Text("Next")
            }
        }
    }
}
