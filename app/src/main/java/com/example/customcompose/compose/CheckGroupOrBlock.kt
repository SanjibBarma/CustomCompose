package com.example.customcompose.compose

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
import com.example.customcompose.compose.group.NonReferringGroup
import com.example.customcompose.compose.group.NumberValidationGroup
import com.example.customcompose.compose.referring.BrandBlock
import com.example.customcompose.compose.referring.CheckListBlock
import com.example.customcompose.compose.referring.CheckboxBlock
import com.example.customcompose.compose.referring.DatePickerBlock
import com.example.customcompose.compose.referring.DropdownBlock
import com.example.customcompose.compose.referring.EditTextBlock
import com.example.customcompose.compose.referring.EmojiRatingBlock
import com.example.customcompose.compose.referring.ImageBlock
import com.example.customcompose.compose.referring.ImageCaptureBlock
import com.example.customcompose.compose.referring.InteractiveGalleryBlock
import com.example.customcompose.compose.referring.MultipleChoiceBlock
import com.example.customcompose.compose.referring.NumberInputBlock
import com.example.customcompose.compose.referring.OTPBlock
import com.example.customcompose.compose.referring.StarRatingBlock
import com.example.customcompose.compose.referring.TermsAgreementBlock
import com.example.customcompose.compose.referring.UrlBlock
import com.example.customcompose.compose.referring.VideoBlock
import com.example.customcompose.model.Block
import com.example.customcompose.viewmodel.BlockListViewModel
import es.dmoral.toasty.Toasty

@Composable
fun CheckGroupOrBlock(
    blockListViewModel: BlockListViewModel,
    surveyBlock: Block,
    isActiveGroup: Boolean,
    position: Int?,
    destination: String
) {
    val context = LocalContext.current

    Column{
        when (surveyBlock.type) {
//                "audio_start" -> EditTextBlock(surveyBlock.block, blockListViewModel, isActiveGroup, destination)
            "textInput" -> EditTextBlock(surveyBlock, blockListViewModel, isActiveGroup, destination)
            "terms" -> TermsAgreementBlock(surveyBlock, blockListViewModel, isActiveGroup, destination)
            "otp" -> OTPBlock(surveyBlock, blockListViewModel, isActiveGroup, destination)
            "dropdown" -> DropdownBlock(surveyBlock, blockListViewModel, isActiveGroup, destination)
            "multipleChoice" -> MultipleChoiceBlock(surveyBlock, blockListViewModel, isActiveGroup, destination)
            "numberInput" -> NumberInputBlock(surveyBlock, blockListViewModel, isActiveGroup, destination)
            "checkbox" -> CheckboxBlock(surveyBlock, blockListViewModel, isActiveGroup, destination)
            "emoji_rating" -> EmojiRatingBlock(surveyBlock, blockListViewModel, isActiveGroup, destination)
            "camera" -> ImageCaptureBlock(surveyBlock, blockListViewModel, isActiveGroup, destination)
            "url" -> UrlBlock(surveyBlock, blockListViewModel, isActiveGroup, destination)
            "video" -> VideoBlock(surveyBlock, blockListViewModel, isActiveGroup, destination)
            "star_rating" -> StarRatingBlock(surveyBlock, blockListViewModel, isActiveGroup, destination)
            "date" -> DatePickerBlock(surveyBlock, blockListViewModel, isActiveGroup, destination)
            "checklist" -> CheckListBlock(surveyBlock, blockListViewModel, isActiveGroup, destination)
            "product" -> BrandBlock(surveyBlock, blockListViewModel, isActiveGroup, destination)
            "interactive_gallery" -> InteractiveGalleryBlock(surveyBlock, blockListViewModel, isActiveGroup, destination)
            "image" -> ImageBlock(surveyBlock, blockListViewModel, isActiveGroup, destination)
            "non-referring" -> NonReferringGroup(blockListViewModel, surveyBlock, position, isActiveGroup, destination)
            "numbervalidation" -> NumberValidationGroup(blockListViewModel, surveyBlock, position, isActiveGroup, destination)

            else -> {
                Text("Unsupported block type: ${surveyBlock.type}", color = Color.Red)

                LaunchedEffect(surveyBlock.type) {
                    Toasty.error(context, "View not found for block type: ${surveyBlock.type}", Toasty.LENGTH_SHORT).show()
                }
            }
        }

    }

}