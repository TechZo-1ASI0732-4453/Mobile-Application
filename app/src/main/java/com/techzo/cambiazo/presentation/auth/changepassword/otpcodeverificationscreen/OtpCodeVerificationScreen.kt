package com.techzo.cambiazo.presentation.auth.changepassword.otpcodeverificationscreen

import android.util.Log
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.hilt.navigation.compose.hiltViewModel
import com.techzo.cambiazo.common.components.ButtonApp
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.common.components.TextLink
import com.techzo.cambiazo.presentation.auth.changepassword.ChangePasswordViewModel

@Composable
fun OtpCodeVerificationScreen(
    goBack: () -> Unit,
    goNewPassword: (String) -> Unit,
    email: String,
    codeGenerated: String,
    changePasswordViewModel: ChangePasswordViewModel = hiltViewModel()
) {
    var isEmailResent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        changePasswordViewModel.resetEmailState()
    }
    changePasswordViewModel.onEmailChange(email)

    var firstDigit by remember { mutableStateOf("") }
    var secondDigit by remember { mutableStateOf("") }
    var thirdDigit by remember { mutableStateOf("") }
    var fourthDigit by remember { mutableStateOf("") }

    // FocusRequester para cada campo
    val focusRequester1 = remember { FocusRequester() }
    val focusRequester2 = remember { FocusRequester() }
    val focusRequester3 = remember { FocusRequester() }
    val focusRequester4 = remember { FocusRequester() }

    MainScaffoldApp(
        paddingCard = PaddingValues(top = 10.dp),
        contentsHeader = {
            Column(
                modifier = Modifier.padding(bottom = 50.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { goBack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(35.dp)
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Código de\nVerificación",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 40.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 20.dp)
                    )
                }
            }
        }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 50.dp, horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Ingrese el código enviado a su correo electrónico",
                textAlign = TextAlign.Center,
                fontSize = 19.sp,
                letterSpacing = 1.2.sp,
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OtpInputField(
                    value = firstDigit,
                    onValueChange = { newValue, isDeleting ->
                        firstDigit = newValue
                        if (!isDeleting && newValue.isNotEmpty()) {
                            focusRequester2.requestFocus()
                        }
                    },
                    focusRequester = focusRequester1
                )
                OtpInputField(
                    value = secondDigit,
                    onValueChange = { newValue, isDeleting ->
                        secondDigit = newValue
                        if (!isDeleting && newValue.isNotEmpty()) {
                            focusRequester3.requestFocus()
                        } else if (isDeleting && newValue.isEmpty()) {
                            focusRequester1.requestFocus()
                        }
                    },
                    focusRequester = focusRequester2
                )
                OtpInputField(
                    value = thirdDigit,
                    onValueChange = { newValue, isDeleting ->
                        thirdDigit = newValue
                        if (!isDeleting && newValue.isNotEmpty()) {
                            focusRequester4.requestFocus()
                        } else if (isDeleting && newValue.isEmpty()) {
                            focusRequester2.requestFocus()
                        }
                    },
                    focusRequester = focusRequester3
                )
                OtpInputField(
                    value = fourthDigit,
                    onValueChange = { newValue, isDeleting ->
                        fourthDigit = newValue
                        if (isDeleting && newValue.isEmpty()) {
                            focusRequester3.requestFocus()
                        }
                    },
                    focusRequester = focusRequester4
                )
            }

            Text(
                text = "Ingrese el código de 4 dígitos",
                fontSize = 16.sp,
                color = Color(0xFF8C8C8C),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (isEmailResent) {

                TextLink(
                    clickable = {},
                    text1 = "¿No recibiste el código? ",
                    text2 = "Correo Enviado"
                )
            } else {
                TextLink(
                    clickable = {
                        changePasswordViewModel.sendEmail(email)
                        isEmailResent = true
                    },
                    text1 = "¿No recibiste el código? ",
                    text2 = "Reenviar código"
                )
            }

            ButtonApp(
                text = "Verificar",
                onClick = {
                    val inputCode = firstDigit + secondDigit + thirdDigit + fourthDigit
                    if (changePasswordViewModel.validateCode(inputCode,codeGenerated)) {
                        goNewPassword(email)
                    } else {
                        Log.e("CODE_VERIFICATION", "CODIGO INCORRECTO")
                    }
                }
            )
        }
    }
}

@Composable
fun OtpInputField(
    value: String,
    onValueChange: (String, Boolean) -> Unit,
    focusRequester: FocusRequester
) {
    BasicTextField(
        value = value,
        onValueChange = { newValue ->
            val isDeleting = newValue.length < value.length
            if (newValue.length <= 1) {
                onValueChange(newValue, isDeleting)
            }
        },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        singleLine = true,
        textStyle = androidx.compose.ui.text.TextStyle(
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        ),
        modifier = Modifier
            .width(65.dp)
            .height(50.dp)
            .border(
                width = 1.dp,
                color = Color(0xFFC2C2C2),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 12.dp)
            .focusRequester(focusRequester),
        decorationBox = { innerTextField ->
            if (value.isEmpty()) {
                Text(
                    text = "-",
                    fontSize = 24.sp,
                    color = Color(0xFFC2C2C2),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            innerTextField()
        }
    )
}




