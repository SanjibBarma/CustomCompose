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
import com.example.customcompose.compose.referring.TermsBlock
import com.example.customcompose.compose.referring.UrlBlock
import com.example.customcompose.compose.referring.VideoBlock
import es.dmoral.toasty.Toasty

@Composable
fun ReferringGroup(
    blockListViewModel: BlockListViewModel, block: Block?, survey: SurveyDataModel
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier
            .padding(16.dp)
        ) {

            when (block?.type) {
                "audio_start" -> EditTextBlock(block, blockListViewModel)
                "textInput" -> EditTextBlock(block, blockListViewModel)
                "terms" -> TermsBlock(block, blockListViewModel)
                "otp" -> OTPBlock(block, blockListViewModel)
                "dropdown" -> DropdownBlock(block, blockListViewModel)
                "multipleChoice" -> MultipleChoiceBlock(block, blockListViewModel)
                "numberInput" -> NumberInputBlock(block, blockListViewModel)
                "checkbox" -> CheckboxBlock(block, blockListViewModel)
                "emoji_rating" -> EmojiRatingBlock(block, blockListViewModel)
                "camera" -> ImageCaptureBlock(block, blockListViewModel)
                "url" -> UrlBlock(block, blockListViewModel)
                "video" -> VideoBlock(block, blockListViewModel)
                "star_rating" -> StarRatingBlock(block, blockListViewModel)
                "date" -> DatePickerBlock(block, blockListViewModel)

                else -> {
                    Text("Unsupported block type: ${block?.type}", color = Color.Red)

                    LaunchedEffect(block?.type) {
                        Toasty.error(
                            context,
                            "View not found for block type: ${block?.type}",
                            Toasty.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}

