package com.techzo.cambiazo.common.components

import android.text.method.PasswordTransformationMethod
import android.text.method.SingleLineTransformationMethod
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.sin

@Composable
fun FieldTextApp(valueText:String,text:String,onValueChange: (String) -> Unit){

    OutlinedTextField(
        value = valueText,
        placeholder = {
            Text(text, color = Color.Gray,
                style= MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.SansSerif))
        },
        onValueChange = { onValueChange(it) },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
    )
}

@Composable
fun MoneyFieldApp(valueText:String,text:String, modifier: Modifier = Modifier,onValueChange: (String) -> Unit){

    OutlinedTextField(
        value = valueText,
        placeholder = {
            Text(text, color = Color.Gray,
                style= MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.SansSerif))
        },
        onValueChange = { newValue ->
            if (newValue.isEmpty() || newValue.matches(Regex("^\\d*(\\.\\d{0,2})?\$"))) {
                onValueChange(newValue)
            }},

        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        )
    )
}

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
    isError: Boolean = false,
    messageError: String? = "Campo Inválido",
    pressEnter: () -> Unit = {},
    hideText: Boolean = false,
    readOnly: Boolean = false,
    onValueChange: (String) -> Unit
){
    val keyboardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }

    val constraints = when(type){
        Constraints.Money.type -> Constraints.Money
        Constraints.Number.type -> Constraints.Number
        Constraints.Text.type -> Constraints.Text
        Constraints.Password.type -> Constraints.Password
        Constraints.TextArea.type -> Constraints.TextArea
        else -> Constraints.Text
    }

    OutlinedTextField(
        modifier = modifier
            .height(constraints.height)
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        value = value,
        placeholder = {
            Text(placeHolder, color = Color.Gray,
                style= MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.SansSerif)
            )
        },
        onValueChange = {
            if (constraints.validate(it)) {
                onValueChange(it)
            }
        },
        singleLine = constraints.singleLine,
        leadingIcon = leadingIcon,//icon start
        trailingIcon = trailingIcon,//icon end
        // Show Enter in the keyboard and active the function onDone when the user press it
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            pressEnter()//function when press enter
            keyboardController?.hide()//hide the keyboard
        }
        ),

        prefix = {prefix?.let { Text(text = prefix)} },
        suffix = {suffix?.let { Text(text = suffix)} },
        readOnly = readOnly,
        isError = isError,
        interactionSource = interactionSource,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,//background color
            unfocusedContainerColor = Color.White,//background color
            focusedIndicatorColor = Color(0xFFFFD146),//border color
            unfocusedIndicatorColor = Color.Gray,//border color
            cursorColor = Color(0xFFFFD146),//cursor color
            //could be omitted
            errorContainerColor = Color.White,//background color
            errorIndicatorColor = Color.Red,//border color
            errorPlaceholderColor = Color.Red,//placeholder color

        )
    )
    if (isError) {
        messageError?.let{
            Text(
                text = "*$messageError",
                color = Color.Red,
            )
        }
    }
}

sealed class Constraints(
    val type: String,
    val validate: (String) ->Boolean,
    val height: Dp = TextFieldDefaults.MinHeight,
    val singleLine: Boolean = true)
{
    object Money : Constraints(
        type = "Money",
        validate = {it.matches(Regex("^\\d*(\\.\\d{0,2})?\$"))},
    )
    object Number : Constraints(
        type = "Number",
        validate = {it.matches(Regex("^\\d*\$"))},
    )
    object Text : Constraints(
        type = "Text",
        validate = {it.length <= 50},
    )
    object Password : Constraints(
        type = "Password",
        validate = {it.length <= 8},
        )
    object TextArea : Constraints(
        type = "TextArea",
        validate = {it.length <= 80},
        singleLine = false,
        height = 100.dp,
        )
}