package com.techzo.cambiazo.presentation.profile.myreviews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.skydoves.landscapist.glide.GlideImage
import com.techzo.cambiazo.common.components.ButtonIconHeaderApp
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.common.components.StarRating
import com.techzo.cambiazo.common.components.TextTitleHeaderApp
import com.techzo.cambiazo.domain.ReviewWithAuthorDetails
import com.techzo.cambiazo.presentation.profile.ProfileViewModel

@Composable
fun MyReviewsScreen(
    back: () -> Unit = {},
    profileViewModel: ProfileViewModel = hiltViewModel(),
    myReviewsViewModel: MyReviewsViewModel = hiltViewModel()
) {

    val averageRating = profileViewModel.averageRating.value
    val countReviews = profileViewModel.countReviews.value
    val reviewsState = myReviewsViewModel.state.value

    MainScaffoldApp(
        paddingCard = PaddingValues(top = 20.dp),
        contentsHeader = {
            Column(
                Modifier
                    .padding(bottom = 30.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ButtonIconHeaderApp(Icons.Filled.ArrowBack, onClick = { back() })
                TextTitleHeaderApp("Mis Reseñas")
            }
        },
        content = {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = averageRating?.let { String.format("%.1f", it) } ?: "0",
                        color = Color(0xFFFFD146),
                        modifier = Modifier.padding(top = 5.dp),
                        fontSize = 70.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(15.dp))

                    Column {
                        StarRating(rating = averageRating ?: 0.0, size = 35.dp)
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = (countReviews?.toString() ?: "0") + " reseñas",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(start = 5.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                LazyColumn {
                    items(reviewsState.data ?: emptyList()) { review ->
                        ReviewItem(review)
                    }
                }

            }
        }
    )

}

@Composable
fun ReviewItem(review: ReviewWithAuthorDetails){

    HorizontalDivider(color = Color(0xFFDCDCDC), thickness = 1.dp)

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
                    .size(60.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = review.userAuthor.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 2.dp))
                StarRating(rating = review.rating.toDouble(), size = 20.dp)
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = review.message,
            fontSize = 16.sp,
            textAlign = TextAlign.Justify
        )

        Spacer(modifier = Modifier.height(20.dp))
    }



}