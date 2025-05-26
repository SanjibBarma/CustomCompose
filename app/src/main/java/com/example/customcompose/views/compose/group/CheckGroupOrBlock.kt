package com.example.customcompose.views.compose.group

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.customcompose.model.Block
import com.example.customcompose.viewmodel.BlockListViewModel
import com.example.customcompose.views.compose.referring.BrandBlock
import com.example.customcompose.views.compose.referring.CheckboxBlock
import com.example.customcompose.views.compose.referring.DatePickerBlock
import com.example.customcompose.views.compose.referring.DropdownBlock
import com.example.customcompose.views.compose.referring.EditTextBlock
import com.example.customcompose.views.compose.referring.GameBlock
import com.example.customcompose.views.compose.referring.GiveAbleBlock
import com.example.customcompose.views.compose.referring.ImageBlock
import com.example.customcompose.views.compose.referring.InteractiveAvBlock
import com.example.customcompose.views.compose.referring.InteractiveGalleryBlock
import com.example.customcompose.views.compose.referring.LookupBlock
import com.example.customcompose.views.compose.referring.MultipleChoiceBlock
import com.example.customcompose.views.compose.referring.NumberInputBlock
import com.example.customcompose.views.compose.referring.TermsAgreementBlock
import com.example.customcompose.views.compose.referring.video.VideoBlock
import com.example.customcompose.views.compose.referring.check_list.CheckListBlock
import com.example.customcompose.views.compose.referring.emoji_rating.EmojiRatingBlock
import com.example.customcompose.views.compose.referring.image_capture.ImageCaptureBlock
import com.example.customcompose.views.compose.referring.otp.OTPBlock
import com.example.customcompose.views.compose.referring.star_rating.StarRatingBlock
import com.example.customcompose.views.compose.referring.url.UrlBlock
import es.dmoral.toasty.Toasty

@Composable
fun CheckGroupOrBlock(
    blockListViewModel: BlockListViewModel,
    surveyBlock: Block,
    isActiveGroup: Boolean,
    position: Int?,
    destination: String,
) {
    val context = LocalContext.current

    Column{
        when (surveyBlock.type) {
//            "audio_start" -> AudioServiceBlock(surveyBlock, blockListViewModel, isActiveGroup, destination)
//            "audio_end" -> AudioServiceBlock(surveyBlock, blockListViewModel, isActiveGroup, destination)
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
            "lookup" -> LookupBlock(surveyBlock, blockListViewModel, isActiveGroup, destination)
            "game" -> GameBlock(surveyBlock, blockListViewModel, isActiveGroup, destination)
            "giveable" -> GiveAbleBlock(surveyBlock, blockListViewModel, isActiveGroup, destination)
            "interactive_av" -> InteractiveAvBlock(surveyBlock, blockListViewModel, isActiveGroup, destination)
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