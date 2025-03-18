package com.example.customcompose.views.compose.group//package com.example.customcompose.compose.group
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import com.example.customcompose.compose.referring.BrandBlock
//import com.example.customcompose.compose.referring.CheckListBlock
//import com.example.customcompose.model.Block
//import com.example.customcompose.compose.referring.CheckboxBlock
//import com.example.customcompose.compose.referring.DatePickerBlock
//import com.example.customcompose.compose.referring.DropdownBlock
//import com.example.customcompose.compose.referring.EditTextBlock
//import com.example.customcompose.compose.referring.EmojiRatingBlock
//import com.example.customcompose.compose.referring.ImageBlock
//import com.example.customcompose.compose.referring.ImageCaptureBlock
//import com.example.customcompose.compose.referring.InteractiveGalleryBlock
//import com.example.customcompose.compose.referring.MultipleChoiceBlock
//import com.example.customcompose.compose.referring.NumberInputBlock
//import com.example.customcompose.compose.referring.OTPBlock
//import com.example.customcompose.compose.referring.StarRatingBlock
//import com.example.customcompose.compose.referring.TermsAgreementBlock
//import com.example.customcompose.compose.referring.UrlBlock
//import com.example.customcompose.compose.referring.VideoBlock
//import com.example.customcompose.viewmodel.BlockListViewModel
//import es.dmoral.toasty.Toasty
//
//@Composable
//fun ReferringGroup(
//    blockListViewModel: BlockListViewModel,
//    surveyBlock: Block,
//    isActiveGroup: Boolean,
//    destination: String
//) {
//    val context = LocalContext.current
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp)
//            .background(Color.White),
//        elevation = CardDefaults.cardElevation(4.dp),
//        colors = CardDefaults.cardColors(containerColor = if (isActiveGroup) Color.White else Color.LightGray)
//    ) {
//        Column(modifier = Modifier
//            .padding(16.dp)
//        ) {
//            when (surveyBlock.block?.type) {
////                "audio_start" -> EditTextBlock(surveyBlock.block, blockListViewModel, isActiveGroup, destination)
//                "textInput" -> EditTextBlock(surveyBlock.block, blockListViewModel, isActiveGroup, destination)
//                "terms" -> TermsAgreementBlock(surveyBlock.block, blockListViewModel, isActiveGroup, destination)
//                "otp" -> OTPBlock(surveyBlock.block, blockListViewModel, isActiveGroup, destination)
//                "dropdown" -> DropdownBlock(surveyBlock.block, blockListViewModel, isActiveGroup, destination)
//                "multipleChoice" -> MultipleChoiceBlock(surveyBlock.block, blockListViewModel, isActiveGroup, destination)
//                "numberInput" -> NumberInputBlock(surveyBlock.block, blockListViewModel, isActiveGroup, destination)
//                "checkbox" -> CheckboxBlock(surveyBlock.block, blockListViewModel, isActiveGroup, destination)
//                "emoji_rating" -> EmojiRatingBlock(surveyBlock.block, blockListViewModel, isActiveGroup, destination)
//                "camera" -> ImageCaptureBlock(surveyBlock.block, blockListViewModel, isActiveGroup, destination)
//                "url" -> UrlBlock(surveyBlock.block, blockListViewModel, isActiveGroup, destination)
//                "video" -> VideoBlock(surveyBlock.block, blockListViewModel, isActiveGroup, destination)
//                "star_rating" -> StarRatingBlock(surveyBlock.block, blockListViewModel, isActiveGroup, destination)
//                "date" -> DatePickerBlock(surveyBlock.block, blockListViewModel, isActiveGroup, destination)
//                "checklist" -> CheckListBlock(surveyBlock.block, blockListViewModel, isActiveGroup, destination)
//                "product" -> BrandBlock(surveyBlock.block, blockListViewModel, isActiveGroup, destination)
//                "interactive_gallery" -> InteractiveGalleryBlock(surveyBlock.block, blockListViewModel, isActiveGroup, destination)
//                "image" -> ImageBlock(surveyBlock.block, blockListViewModel, isActiveGroup, destination)
//                "non-referring" -> NonReferringGroup(blockListViewModel, childView, position, isCurrentGroupActive, "mainSurvey")
//                "numbervalidation" -> NumberValidationGroup(blockListViewModel, childView, position, isCurrentGroupActive, "mainSurvey")
//
//                else -> {
//                    Text("Unsupported block type: ${surveyBlock.block?.type}", color = Color.Red)
//
//                    LaunchedEffect(surveyBlock.block?.type) {
//                        Toasty.error(context, "View not found for block type: ${surveyBlock.block?.type}", Toasty.LENGTH_SHORT).show()
//                    }
//                }
//            }
//
//        }
//    }
//}
//
