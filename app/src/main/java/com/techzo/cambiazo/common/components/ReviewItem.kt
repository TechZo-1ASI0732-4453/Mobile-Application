package com.techzo.cambiazo.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skydoves.landscapist.glide.GlideImage
import com.techzo.cambiazo.domain.Review

@Composable
fun ReviewItem(review: Review, OnUserClick: (Int) -> Unit, dividerUp: Boolean = true) {
    val interactionSource = remember { MutableInteractionSource() }

    if (dividerUp) HorizontalDivider(color = Color(0xFFDCDCDC), thickness = 1.dp)

    Spacer(modifier = Modifier.height(20.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            GlideImage(
                imageModel = { review.userAuthor.profilePicture },
                modifier = Modifier
                    .size(65.dp)
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { OnUserClick(review.userAuthor.id) }
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = review.userAuthor.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(start = 2.dp)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) { OnUserClick(review.userAuthor.id) }
                )
                StarRating(rating = review.rating.toDouble(), size = 22.dp)
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = review.message,
            fontSize = 16.sp,
            textAlign = TextAlign.Justify,
            lineHeight = 20.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
    }


}