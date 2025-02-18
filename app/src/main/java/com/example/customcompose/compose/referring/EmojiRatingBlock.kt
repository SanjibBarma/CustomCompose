package com.example.customcompose.compose.referring

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.customcompose.R
import com.example.customcompose.model.Block
import com.example.customcompose.viewmodel.BlockListViewModel

@Composable
fun EmojiRatingBlock(block: Block, blockListViewModel: BlockListViewModel) {
    val isSkippable = block.skip?.id != "-1"

    val emojiImageArray = intArrayOf(
        R.drawable.ic_angry,
        R.drawable.ic_sad,
        R.drawable.ic_confuse,
        R.drawable.ic_happy,
        R.drawable.ic_great
    )

    val emojiTitles = listOf(
        "Terrible",
        "Bad",
        "Okay",
        "Good",
        "Great"
    )

    var selectedEmojiIndex by remember { mutableStateOf<Int?>(null) }

    Column{
        Text(block.question!!.slug)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            emojiImageArray.forEachIndexed { index, emojiResource ->
                EmojiBox(
                    block,
                    emojiImageResource = emojiResource,
                    title = emojiTitles[index],
                    isSelected = selectedEmojiIndex == index,
                    onNext = { blockId, groupId ->
                        selectedEmojiIndex = index // Update selected emoji index
                        blockListViewModel.addBlockToTheList(blockId, groupId) // Call onNext with the updated values
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (isSkippable) {
            Button(
                onClick = {
                    block.skip?.group_no?.let { groupId ->
                        block.skip.id.let { blockId ->
                            blockListViewModel.addBlockToTheList(blockId, groupId)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue,
                    contentColor = Color.White
                )
            ) {
                Text("Skip")
            }
        }
    }
}

@Composable
fun EmojiBox(
    block: Block,
    emojiImageResource: Int,
    title: String,
    isSelected: Boolean,
    onNext: (String, String) -> Unit
) {
    val context = LocalContext.current

    val backgroundColor = if (isSelected) Color.LightGray else Color.White
    val borderColor = if (isSelected) Color.Black else Color.Transparent

    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        shape = RoundedCornerShape(8.dp),
        onClick = {
            block.referTo?.id?.let { blockId ->
                block.referTo.group_no?.let { groupId ->
                    onNext(blockId, groupId)
                }
            }
//            Toast.makeText(context, "$title is Selected", Toast.LENGTH_SHORT).show()
        },
        modifier = Modifier
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .height(70.dp)
                .width(50.dp)
                .background(backgroundColor)
        ) {
            Image(
                painter = painterResource(id = emojiImageResource),
                contentDescription = title,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                color = Color.Black,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


