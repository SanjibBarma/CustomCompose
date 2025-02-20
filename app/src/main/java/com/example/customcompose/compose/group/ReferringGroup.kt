package com.example.customcompose.compose.group

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.customcompose.model.Block
import com.example.customcompose.model.SurveyDataModel
import com.example.customcompose.viewmodel.BlockListViewModel
import com.example.customcompose.compose.referring.CheckboxBlock
import com.example.customcompose.compose.referring.DatePickerBlock
import com.example.customcompose.compose.referring.DropdownBlock
import com.example.customcompose.compose.referring.EditTextBlock
import com.example.customcompose.compose.referring.EmojiRatingBlock
import com.example.customcompose.compose.referring.ImageCaptureBlock
import com.example.customcompose.compose.referring.MultipleChoiceBlock
import com.example.customcompose.compose.referring.NumberInputBlock
import com.example.customcompose.compose.referring.OTPBlock
import com.example.customcompose.compose.referring.StarRatingBlock
import com.example.customcompose.compose.referring.TermsAgreementBlock
import com.example.customcompose.compose.referring.UrlBlock
import com.example.customcompose.compose.referring.VideoBlock
import es.dmoral.toasty.Toasty

@Composable
fun ReferringGroup(
    blockListViewModel: BlockListViewModel,
    block: Block?,
    survey: SurveyDataModel,
    isActiveGroup: Boolean,
    position: Int
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = if (isActiveGroup) Color.White else Color.LightGray)
    ) {
        Column(modifier = Modifier
            .padding(16.dp)
        ) {

            when (block?.type) {
                "audio_start" -> EditTextBlock(block, blockListViewModel, position, isActiveGroup)
                "textInput" -> EditTextBlock(block, blockListViewModel, position, isActiveGroup)
                "terms" -> TermsAgreementBlock(block, blockListViewModel, position, isActiveGroup)
                "otp" -> OTPBlock(block, blockListViewModel, position, isActiveGroup)
                "dropdown" -> DropdownBlock(block, blockListViewModel, position, isActiveGroup)
                "multipleChoice" -> MultipleChoiceBlock(block, blockListViewModel, position, isActiveGroup)
                "numberInput" -> NumberInputBlock(block, blockListViewModel, position, isActiveGroup)
                "checkbox" -> CheckboxBlock(block, blockListViewModel, position, isActiveGroup)
                "emoji_rating" -> EmojiRatingBlock(block, blockListViewModel, position, isActiveGroup)
                "camera" -> ImageCaptureBlock(block, blockListViewModel, position, isActiveGroup)
                "url" -> UrlBlock(block, blockListViewModel, position, isActiveGroup)
                "video" -> VideoBlock(block, blockListViewModel, position, isActiveGroup)
                "star_rating" -> StarRatingBlock(block, blockListViewModel, position, isActiveGroup)
                "date" -> DatePickerBlock(block, blockListViewModel, position, isActiveGroup)

                else -> {
                    Text("Unsupported block type: ${block?.type}", color = Color.Red)

                    LaunchedEffect(block?.type) {
                        Toasty.error(context, "View not found for block type: ${block?.type}", Toasty.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

