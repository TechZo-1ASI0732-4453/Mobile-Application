package com.techzo.cambiazo.presentation.profile.subscription

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Diamond
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techzo.cambiazo.R
import com.techzo.cambiazo.common.components.ButtonApp
import com.techzo.cambiazo.common.components.ButtonIconHeaderApp
import com.techzo.cambiazo.common.components.DialogApp
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.common.components.PaymentText
import com.techzo.cambiazo.common.components.SubTitleText
import com.techzo.cambiazo.common.components.TextTitleHeaderApp
import java.util.Locale

@Composable
fun PaymentScreen(
    back: () -> Unit = {},
    goToMySubscription: () -> Unit = {},
    subscriptionViewModel: SubscriptionViewModel = hiltViewModel()
) {
    val selectedPlan = subscriptionViewModel.selectedPlan.value
    val plan = subscriptionViewModel.state.value.data?.find { it.id == selectedPlan }

    var cardHolderName by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var showBack by remember { mutableStateOf(false) }
    var cardType by remember { mutableStateOf("Unknown") }
    var paymentCardScreen by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }

    val rotationAngle by animateFloatAsState(if (showBack) 180f else 0f, label = "")

    fun determineCardType(number: String): String {
        return when {
            number.startsWith("4") -> "Visa"
            number.startsWith("51") || number.startsWith("52") || number.startsWith("53") || number.startsWith("54") || number.startsWith("55") || (number.take(4).toIntOrNull() in 2221..2720) -> "MasterCard"
            number.startsWith("34") || number.startsWith("37") -> "American Express"
            number.startsWith("36") || number.startsWith("38") || number.startsWith("39") -> "Diners Club International"
            else -> "Unknown"
        }
    }

    val isCardTypeValid = cardType != "Unknown"
    val isExpiryDateValid = expiryDate.length == 5 && expiryDate.substring(0, 2)
        .toIntOrNull() in 1..12 && (expiryDate.substring(3, 5).toIntOrNull() ?: 0) > 24
    val isCvvValid = cvv.length == 3
    val isCardNumberValid = when (cardType) {
        "Visa", "MasterCard", "Diners Club International" -> cardNumber.replace(" ", "").length == 16
        "American Express" -> cardNumber.replace(" ", "").length == 15
        else -> false
    }
    val isFormValid = cardHolderName.isNotEmpty() && isCardNumberValid && isExpiryDateValid && isCvvValid && isCardTypeValid


    LaunchedEffect(cardNumber) {
        cardType = determineCardType(cardNumber)
    }

    MainScaffoldApp(
        paddingCard = PaddingValues(top = 10.dp),
        contentsHeader = {
            Column(
                Modifier
                    .padding(bottom = 30.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ButtonIconHeaderApp(Icons.Filled.ArrowBack, onClick = { if (paymentCardScreen) paymentCardScreen = false else back() })
                TextTitleHeaderApp("Pago")
            }
        },
        content = {
            Column(modifier = Modifier.padding(horizontal = 30.dp)) {

                if (plan != null) {
                    if (!paymentCardScreen){

                        val iconColor = if (plan.id == 3) Color.Black else Color.White
                        val backgroundColor = when (plan.id) {
                            1 -> Color.Gray
                            2 -> Color.Black
                            else -> Color(0xFFFFD146)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        SubTitleText(subTittle = "Subscripción seleccionada")

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                            modifier = Modifier
                                .padding(horizontal = 10.dp, vertical = 20.dp)
                                .fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(65.dp)
                                    .background(backgroundColor, shape = RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Diamond,
                                    contentDescription = null,
                                    modifier = Modifier.size(35.dp),
                                    tint = iconColor
                                )
                            }

                            Spacer(modifier = Modifier.width(20.dp))

                            Column(verticalArrangement = Arrangement.Center) {
                                Text(
                                    text = "Plan ${plan.name}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 28.sp,
                                    color = Color.Black
                                )
                                Text(
                                    text = "$${plan.price} c/mes",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp,
                                    color = Color.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(15.dp))

                        SubTitleText(subTittle = "Método de Pago")

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 5.dp)
                                .clickable(onClick = { paymentCardScreen = true })
                                .background(Color.White),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 2.dp
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .background(Color.White)
                                    .padding(vertical = 10.dp, horizontal = 15.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(0.dp, 10.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(55.dp)
                                            .background(Color(0xFF353535), shape = CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.CreditCard,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Text(
                                        text = "Tarjeta de Crédito o Débito",
                                        fontSize = 16.sp,
                                        color = Color.Black,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Icon(
                                        imageVector = Icons.Outlined.ChevronRight,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(28.dp)
                                    )

                                }

                                HorizontalDivider(color = Color(0xFFF2F2F2), thickness = 1.5.dp)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 0.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.End)
                                ) {
                                    val cardTypes = listOf("Visa", "MasterCard", "American Express", "Diners Club International")
                                    val cardTypeIcons = mapOf(
                                        "Visa" to R.drawable.visa,
                                        "MasterCard" to R.drawable.mastercad,
                                        "American Express" to R.drawable.american_express,
                                        "Diners Club International" to R.drawable.diners_club
                                    )
                                    val backgroundColors = mapOf(
                                        "Visa" to Color(0xFF25359E),
                                        "MasterCard" to Color.White,
                                        "American Express" to Color(0xFF1B7DD4),
                                        "Diners Club International" to Color.White
                                    )

                                    cardTypes.forEach { type ->
                                        val icon = cardTypeIcons[type]
                                        val backgroundColors = backgroundColors[type] ?: Color.Transparent

                                        Box(
                                            modifier = Modifier
                                                .width(40.dp)
                                                .height(25.dp)
                                                .border(
                                                    1.dp,
                                                    Color(0xFFF2F2F2),
                                                    RoundedCornerShape(5.dp)
                                                )
                                                .background(
                                                    backgroundColors,
                                                    shape = RoundedCornerShape(5.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            icon?.let {
                                                Image(
                                                    painter = painterResource(id = it),
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .width(30.dp)
                                                        .height(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                        }

                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 30.dp)
                                .aspectRatio(1.7f)
                                .graphicsLayer {
                                    rotationY = rotationAngle
                                    cameraDistance = 8 * density
                                }
                                .background(
                                    brush = if (cardNumber.length >= 4) {
                                        when (cardType) {
                                            "Visa" -> Brush.verticalGradient(
                                                listOf(
                                                    Color(0xFF2A3DBD),
                                                    Color(0xFF538AE5)
                                                )
                                            )

                                            "MasterCard" -> Brush.verticalGradient(
                                                listOf(
                                                    Color(
                                                        0xFF161616
                                                    ), Color(0xFF343434)
                                                )
                                            )

                                            "American Express" -> Brush.verticalGradient(
                                                listOf(
                                                    Color(0xFF0471D0),
                                                    Color(0xFF1B7DD4)
                                                )
                                            )

                                            "Diners Club International" -> Brush.verticalGradient(
                                                listOf(Color(0xFF1D5892), Color(0xFF2F7DC7))
                                            )

                                            else -> Brush.verticalGradient(
                                                listOf(
                                                    Color(0xFF161616),
                                                    Color(0xFF343434)
                                                )
                                            )
                                        }
                                    } else {
                                        Brush.verticalGradient(
                                            listOf(
                                                Color(0xFF161616),
                                                Color(0xFF363636)
                                            )
                                        )
                                    },
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!showBack) {
                                Column(Modifier.padding(20.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.chip),
                                            contentDescription = null,
                                            modifier = Modifier.size(40.dp)
                                        )

                                        if (cardNumber.length >= 4) {
                                            val cardTypeIcon = when (cardType) {
                                                "Visa" -> R.drawable.visa
                                                "MasterCard" -> R.drawable.mastercad
                                                "American Express" -> R.drawable.american_express
                                                "Diners Club International" -> R.drawable.diners_club
                                                else -> null
                                            }
                                            val backgroundColor = when (cardType) {
                                                "Visa" -> Color(0xFF25359E)
                                                "MasterCard" -> Color.White
                                                "American Express" -> Color(0xFF1B7DD4)
                                                "Diners Club International" -> Color.White
                                                else -> Color.Transparent
                                            }
                                            cardTypeIcon?.let {
                                                Box(
                                                    modifier = Modifier
                                                        .width(60.dp)
                                                        .height(40.dp)
                                                        .background(
                                                            backgroundColor,
                                                            shape = RoundedCornerShape(8.dp)
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Image(
                                                        painter = painterResource(id = it),
                                                        contentDescription = null,
                                                        modifier = Modifier
                                                            .width(50.dp)
                                                            .height(35.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(30.dp))

                                    Text(
                                        text = cardNumber.ifEmpty { "XXXX XXXX XXXX XXXX" },
                                        color = if (cardNumber.isEmpty()) Color(0xFFA8A8A8) else Color.White,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Medium
                                    )

                                    Spacer(modifier = Modifier.height(20.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = cardHolderName.toUpperCase(Locale.ROOT).ifEmpty { "NOMBRE Y APELLIDO" },
                                            color = if (cardHolderName.isEmpty()) Color(0xFFA8A8A8) else Color.White,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = expiryDate.ifEmpty { "MM/YY" },
                                            color = if (expiryDate.isEmpty()) Color(0xFFA8A8A8) else Color.White,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            } else {
                                Column {
                                    Box(
                                        Modifier
                                            .fillMaxWidth()
                                            .background(Color.Black)
                                            .height(50.dp))

                                    Spacer(modifier = Modifier.height(30.dp))

                                    Box(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 30.dp)) {
                                        Text(
                                            text = cvv.ifEmpty { "XXX" },
                                            color = if (cvv.isEmpty()) Color.Gray else Color.Black,
                                            fontSize = 18.sp,
                                            textAlign = TextAlign.End,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color.White)
                                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                                .graphicsLayer {
                                                    rotationY = 180f
                                                }
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(30.dp))

                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        PaymentText(subTittle = "Nombre y Apellido")
                        BasicTextField(
                            value = cardHolderName.toUpperCase(Locale.ROOT),
                            onValueChange = { cardHolderName = it },
                            textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(8.dp)),
                            decorationBox = { innerTextField ->
                                Box( modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 10.dp, vertical = 10.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (cardHolderName.isEmpty()) { Text("Nombre y Apellido", color = Color.Gray) }
                                    innerTextField()
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        PaymentText(subTittle = "Número de la tarjeta")
                        BasicTextField(
                            value = TextFieldValue(cardNumber, TextRange(cardNumber.length)),
                            onValueChange = { newText ->
                                val digits = newText.text.filter { it.isDigit() }
                                var formattedText = digits

                                when (cardType) {
                                    "Visa", "MasterCard", "Unknown" -> {
                                        formattedText = digits.chunked(4).joinToString(" ")
                                    }
                                    "American Express" -> {
                                        formattedText = when {
                                            digits.length > 10 -> digits.take(4) + " " + digits.drop(4).take(6) + " " + digits.drop(10)
                                            digits.length > 4 -> digits.take(4) + " " + digits.drop(4).take(6)
                                            else -> digits
                                        }
                                    }
                                    "Diners Club International" -> {
                                        formattedText = when {
                                            digits.length > 12 -> digits.take(4) + " " + digits.drop(4).take(4) + " " + digits.drop(8).take(4) + " " + digits.drop(12).take(2)
                                            digits.length > 8 -> digits.take(4) + " " + digits.drop(4).take(4) + " " + digits.drop(8).take(4)
                                            digits.length > 4 -> digits.take(4) + " " + digits.drop(4).take(4)
                                            else -> digits
                                        }
                                    }
                                }

                                val maxLength = when (cardType) {
                                    "Visa", "MasterCard", "Unknown" -> 19
                                    "American Express" -> 17
                                    "Diners Club International" -> 17
                                    else -> 19
                                }

                                if (formattedText.length > maxLength) { formattedText = formattedText.take(maxLength) }

                                cardNumber = TextFieldValue(text = formattedText, selection = newText.selection).text

                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
                            visualTransformation = VisualTransformation.None,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(8.dp)),
                            decorationBox = { innerTextField ->
                                Box( modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 10.dp, vertical = 10.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (cardNumber.isEmpty()) { Text("XXXX XXXX XXXX XXXX", color = Color.Gray) }
                                    innerTextField()
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(15.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Column(Modifier.weight(1f)) {
                                PaymentText(subTittle = "Fecha de expiración")
                                BasicTextField(
                                    value = TextFieldValue(expiryDate, TextRange(expiryDate.length)),
                                    onValueChange = { newText ->
                                        val digits = newText.text.filter { it.isDigit() }
                                        var formattedText = digits

                                        if (digits.length >= 3) { formattedText = digits.take(2) + "/" + digits.drop(2) }

                                        if (formattedText.length > 5) { formattedText = formattedText.take(5) }

                                        val newCursorPosition = if (formattedText.length == 3) 4 else formattedText.length

                                        expiryDate = TextFieldValue(text = formattedText, selection = TextRange(newCursorPosition)).text
                                    },
                                    decorationBox = { innerTextField ->
                                        Box( modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 10.dp, vertical = 10.dp),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            if (expiryDate.isEmpty()) { Text("MM/YY", color = Color.Gray) }

                                            innerTextField()
                                        }
                                    },
                                    textStyle = TextStyle(color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Normal),
                                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .background(
                                            Color(0xFFF0F0F0),
                                            shape = RoundedCornerShape(8.dp)
                                        ),
                                    visualTransformation = VisualTransformation.None
                                )
                            }

                            Column(Modifier.weight(1f)) {
                                PaymentText(subTittle = "CVV")
                                BasicTextField(
                                    value = cvv,
                                    onValueChange = {
                                        if (it.length <= 3 && it.all { char -> char.isDigit() }) { cvv = it }
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .background(
                                            Color(0xFFF0F0F0),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .onFocusChanged { showBack = it.hasFocus },
                                    decorationBox = { innerTextField ->
                                        Box( modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 10.dp, vertical = 10.dp),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            if (cvv.isEmpty()) { Text("CVV", color = Color.Gray, fontSize = 16.sp) }
                                            innerTextField()
                                        }
                                    }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(30.dp))
                        ButtonApp(
                            text = "Pagar",
                            enable = isFormValid,
                            onClick = {
                                showDialog = true
                                subscriptionViewModel.createSubscription(selectedPlan!!)
                            })
                    }
                }
            }
        }
    )
    if (showDialog) {
        DialogApp(
            message = "¡Pago exitoso!",
            description = "Muchas gracias por ser ahora miembro del plan ${plan?.name} de CambiaZo, disfruta de los beneficios.",
            labelButton1 = "Confirmar",
            onDismissRequest = { showDialog = false },
            onClickButton1 = {
                showDialog = false
                goToMySubscription()
            }
        )
    }
}