package com.example.customcompose.views.compose.referring.star_rating

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.customcompose.R
import com.example.customcompose.ui.theme.StatRating

@Composable
fun Star(isSelected: Boolean, onClick: () -> Unit, isActiveGroup: Boolean) {
    val starIcon = if (isSelected) {
        R.drawable.ic_star_on
    } else {
        R.drawable.ic_star_off
    }
    val starColor = if (isSelected) StatRating else Color.Gray

    IconButton(
        onClick = onClick,
        modifier = Modifier.size(48.dp),
        enabled = isActiveGroup
    ) {
        Icon(
            painter = painterResource(id = starIcon),
            contentDescription = "Star",
            tint = starColor
        )
    }
}