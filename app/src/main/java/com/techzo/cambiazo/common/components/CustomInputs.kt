package com.techzo.cambiazo.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly

@Composable
fun CustomInput(
    modifier: Modifier = Modifier,
    value:String,
    placeHolder:String = "",
    type : String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: String? = null,
    suffix: String? = null,
    supportingText: String = "",
    isError: Boolean = false,
    messageError: String? = "Campo Inválido",
    pressEnter: () -> Unit = {},
    readOnly: Boolean = false,
    enabled: Boolean = true,
    onValueChange: (String) -> Unit = {},
){
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val color = remember { mutableStateOf(Color.Gray) }

    val borderThickness = if(!isFocused) 1.dp else 2.dp
    if (isError) {
        color.value = Color.Red
    } else {
        if (isFocused) {
            color.value = Color(0xFFFFD146)
        } else {
            color.value = Color.Gray
        }
    }

    val constraints = when(type){
        Constraints.Money.type -> Constraints.Money
        Constraints.Number.type -> Constraints.Number
        Constraints.Text.type -> Constraints.Text
        Constraints.Password.type -> Constraints.Password
        Constraints.TextArea.type -> Constraints.TextArea
        Constraints.Email.type -> Constraints.Email
        Constraints.Phone.type -> Constraints.Phone
        else -> Constraints.Text
    }

    val borderColor = if(enabled) color.value else Color(0xFFDBDBDB)
    val backgroundColor = if(enabled) Color.White else Color(0xFFECECEC)
    val textColor = if(enabled) Color.Black else Color(0xFF9F9F9F)

    val hidePassword = remember { mutableStateOf(constraints.type == Constraints.Password.type) }
    Column{
        BasicTextField(
            value = value,
            onValueChange = {
                if (constraints.validate(it)) onValueChange(it)
            },
            modifier = modifier
                .height(constraints.height)
                .background(backgroundColor, RoundedCornerShape(8.dp))
                .border(borderThickness, borderColor, RoundedCornerShape(8.dp)),
            singleLine = constraints.singleLine,
            maxLines = if(constraints.singleLine) 1 else 3,
            keyboardOptions = KeyboardOptions(
                keyboardType = constraints.keyboardType,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                pressEnter()//function when press enter
                focusManager.clearFocus()//clear the focus
            }),
            readOnly = readOnly,
            enabled = enabled,
            interactionSource = interactionSource,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = textColor
            ),
            visualTransformation =  if(hidePassword.value) PasswordVisualTransformation() else VisualTransformation.None,
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = if(constraints.singleLine) Alignment.CenterVertically else Alignment.Top
                ) {

                    leadingIcon?.let { leadingIcon()}
                    prefix?.let { Text(text = prefix)}

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 10.dp, vertical = 10.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (value.isEmpty()) {
                            Text(
                                placeHolder,
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp
                                )
                            )
                        }
                        innerTextField()
                    }
                    suffix?.let { Text(text = suffix)}
                    trailingIcon?.let {trailingIcon()}
                    if(constraints.type == Constraints.Password.type) {
                        IconButton(onClick = { hidePassword.value = !hidePassword.value }) {
                            Icon(
                                imageVector = Icons.Default.RemoveRedEye,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }
                    }

                }
            }
        )
        if (isError) {
            messageError?.let{
                Text(
                    text = " *$messageError",
                    color = Color.Red,
                    modifier = Modifier.height(18.dp),
                    fontSize = 12.sp,
                )
            }
        }else{
            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}


sealed class Constraints(
    val type: String,
    val validate: (String) -> Boolean,
    val keyboardType: KeyboardType,
    val height: Dp,
    val singleLine: Boolean = true,
)
{
    object Money : Constraints(
        type = "Money",
        validate = {it.matches(Regex("^\\d*(\\.\\d{0,2})?\$"))},
        keyboardType = KeyboardType.Number,
        height = 50.dp,
    )
    object Number : Constraints(
        type = "Number",
        validate = {it.matches(Regex("^\\d*\$"))},
        keyboardType = KeyboardType.Number,
        height = 50.dp,
    )
    object Text : Constraints(
        type = "Text",
        validate = {it.length <= 50},
        keyboardType = KeyboardType.Text,
        height = 50.dp,
    )
    object Password : Constraints(
        type = "Password",
        validate = {it.length <= 16},
        keyboardType = KeyboardType.Password,
        height = 50.dp,
    )
    object TextArea : Constraints(
        type = "TextArea",
        validate = {it.length <= 120 && it.matches(Regex("^[^\\n\\r]*\$"))},
        singleLine = false,
        keyboardType = KeyboardType.Text,
        height = 100.dp,
    )
    object Email : Constraints(
        type = "Email",
        validate = {it.length <= 254},
        keyboardType = KeyboardType.Email,
        height = 50.dp,
    )
    object Phone : Constraints(
        type = "Phone",
        validate = {it.isDigitsOnly() && it.length <= 9},
        keyboardType = KeyboardType.Phone,
        height = 50.dp,
    )
}