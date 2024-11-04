package com.techzo.cambiazo.presentation.profile.subscription

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Diamond
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.components.ButtonApp
import com.techzo.cambiazo.common.components.ButtonIconHeaderApp
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.common.components.SubTitleText
import com.techzo.cambiazo.common.components.TextTitleHeaderApp
import com.techzo.cambiazo.domain.Subscription
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun MySubscriptionScreen(
    back: () -> Unit = {},
    openPlans: () -> Unit = {}
) {

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
                TextTitleHeaderApp("Mi Suscripción")
            }
        },
        content = {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)) {
                SubTitleText("Suscripción actual")
                Spacer(modifier = Modifier.height(10.dp))

                Constants.userSubscription?.let { subscription ->
                    SubscriptionPlanCard(subscription = subscription, openPlans = openPlans)
                } ?: run {
                    Text(text = "No subscription available", color = Color.Red)
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    )

}


@Composable
fun SubscriptionPlanCard(
    subscription: Subscription,
    openPlans: () -> Unit = {}
) {
    val plan = subscription.plan
    val backgroundColor = when (plan.id) {
        1 -> Color.Gray
        2 -> Color.Black
        else -> Color(0xFFFFD146)
    }
    val iconColor = if (plan.id == 3) Color.Black else Color.White

    val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
    val outputDateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
    val parsedDate = inputDateFormat.parse(subscription.endDate)
    val formattedEndDate = parsedDate?.let { outputDateFormat.format(it) } ?: subscription.endDate

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(end = 20.dp, start = 20.dp, top = 20.dp, bottom = 10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 5.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(backgroundColor, shape = RoundedCornerShape(5.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Diamond,
                        contentDescription = null,
                        modifier = Modifier.size(25.dp),
                        tint = iconColor
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = plan.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 28.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            if (plan.id != 1) {
                Row(
                    modifier = Modifier.padding(horizontal = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = Color.Black
                    )

                    Spacer(modifier = Modifier.width(3.dp))

                    Text(text = "Tu suscripción se renovará el ", fontSize = 12.5.sp)
                    Text(text = formattedEndDate, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                    Text(text = " por ", fontSize = 12.5.sp)
                    Text(text = "$${plan.price}", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            HorizontalDivider(color = Color(0xFFF2F2F2), thickness = 1.5.dp)

            Spacer(modifier = Modifier.height(15.dp))

            subscription.plan.benefits.forEach { benefits ->
                Text(
                    modifier = Modifier.padding(start = 2.dp, bottom = 2.dp).padding(horizontal = 5.dp),
                    text = "• ${benefits.description}",
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            ButtonApp(
                text = "Ver todos los planes",
                bgColor = Color.Black,
                fColor = Color.White,
                bColor = Color.Black,
                onClick = { openPlans() }
            )
        }
    }
}