package com.example.customcompose.compose.referring.emoji_rating

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.customcompose.ui.theme.ProductSelected

@Composable
fun EmojiBox(
    emojiImageResource: Int,
    title: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    isActiveGroup: Boolean
) {
    val backgroundColor = if (isSelected) ProductSelected else Color.White
    val borderColor = if (isSelected) Color.Black else Color.Transparent

    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        shape = RoundedCornerShape(4.dp),
        onClick = {
            onSelect()
        },
        modifier = Modifier
            .border(1.dp, borderColor, RoundedCornerShape(4.dp)),
        enabled = isActiveGroup
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