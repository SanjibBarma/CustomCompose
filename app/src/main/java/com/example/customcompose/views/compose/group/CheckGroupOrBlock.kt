package com.example.customcompose.views.compose.group

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.customcompose.model.Block
import com.example.customcompose.views.compose.referring.FileUploadBlock
import com.example.customcompose.views.compose.referring.BrandBlock
import com.example.customcompose.views.compose.referring.CheckboxBlock
import com.example.customcompose.views.compose.referring.DatePickerBlock
import com.example.customcompose.views.compose.referring.DropdownBlock
import com.example.customcompose.views.compose.referring.EditTextBlock
import com.example.customcompose.views.compose.referring.GameBlock
import com.example.customcompose.views.compose.referring.GiveAbleBlock
import com.example.customcompose.views.compose.referring.image_view.ImageBlock
import com.example.customcompose.views.compose.referring.InteractiveAvBlock
import com.example.customcompose.views.compose.referring.InteractiveGalleryBlock
import com.example.customcompose.views.compose.referring.LookupBlock
import com.example.customcompose.views.compose.referring.MultipleChoiceBlock
import com.example.customcompose.views.compose.referring.NumberInputBlock
import com.example.customcompose.views.compose.referring.terms_agreement.TermsAgreementBlock
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
    surveyBlock: Block,
    isActiveGroup: Boolean,
    position: Int?,
    destination: String,
) {
    val context = LocalContext.current

    Column{
        when (surveyBlock.type) {
            "textInput" -> EditTextBlock(surveyBlock, isActiveGroup, destination)
            "terms" -> TermsAgreementBlock(surveyBlock, isActiveGroup, destination)
            "otp" -> OTPBlock(surveyBlock, isActiveGroup, destination)
            "dropdown" -> DropdownBlock(surveyBlock, isActiveGroup, destination)
            "multipleChoice" -> MultipleChoiceBlock(surveyBlock, isActiveGroup, destination)
            "numberInput" -> NumberInputBlock(surveyBlock, isActiveGroup, destination)
            "checkbox" -> CheckboxBlock(surveyBlock, isActiveGroup, destination)
            "emoji_rating" -> EmojiRatingBlock(surveyBlock, isActiveGroup, destination)
            "camera" -> ImageCaptureBlock(surveyBlock, isActiveGroup, destination)
            "url" -> UrlBlock(surveyBlock, isActiveGroup, destination)
            "video" -> VideoBlock(surveyBlock, isActiveGroup, destination)
            "star_rating" -> StarRatingBlock(surveyBlock, isActiveGroup, destination)
            "date" -> DatePickerBlock(surveyBlock, isActiveGroup, destination)
            "checklist" -> CheckListBlock(surveyBlock, isActiveGroup, destination)
            "product" -> BrandBlock(surveyBlock, isActiveGroup, destination)
            "interactive_gallery" -> InteractiveGalleryBlock(surveyBlock, isActiveGroup, destination)
            "image" -> ImageBlock(surveyBlock, isActiveGroup, destination)
            "lookup" -> LookupBlock(surveyBlock, isActiveGroup, destination)
            "game" -> GameBlock(surveyBlock, isActiveGroup, destination)
            "giveable" -> GiveAbleBlock(surveyBlock, isActiveGroup, destination)
            "interactive_av" -> InteractiveAvBlock(surveyBlock, isActiveGroup, destination)
            "non-referring" -> NonReferringGroup(surveyBlock, position, isActiveGroup, destination)
            "numbervalidation" -> NumberValidationGroup(surveyBlock, position, isActiveGroup, destination)
            "file_upload" -> FileUploadBlock(surveyBlock, position, isActiveGroup, destination)

            else -> {
                Text("Unsupported block type: ${surveyBlock.type}", color = Color.Red)
                LaunchedEffect(surveyBlock.type) {
                    Toasty.error(context, "View not found for block type: ${surveyBlock.type}", Toasty.LENGTH_SHORT).show()
                }
            }
        }
    }
}