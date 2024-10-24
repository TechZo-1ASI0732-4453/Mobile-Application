package com.techzo.cambiazo.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import androidx.core.app.NotificationCompat.MessagingStyle.Message

@Composable
fun  <T> DropdownList(
    selectedOption: T?,
    label: String,
    itemList: List<T>,
    onItemClick: (T?) -> Unit,
    itemToString: (T) -> String
) {
    var textFilledSize by remember { mutableStateOf(Size.Zero) }
    var showDropdown by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = selectedOption?.let(itemToString) ?: "",
            label = { Text(text = label) },
            trailingIcon = {
                IconButton(
                    onClick = { showDropdown = !showDropdown },
                ) {
                    Icon(
                        imageVector = if (showDropdown) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        tint = Color.Black
                    )
                }
            },
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = Color.Gray,
                disabledTextColor = Color.Gray
            ),
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFilledSize = coordinates.size.toSize()
                },
            shape = RoundedCornerShape(12.dp)
        )

        Box {
            if (showDropdown) {
                Popup(
                    alignment = Alignment.TopCenter,
                    properties = PopupProperties(excludeFromSystemGesture = true),
                    onDismissRequest = {  }
                ) {
                    Column(
                        modifier = Modifier
                            .width(with(LocalDensity.current) { textFilledSize.width.toDp() })
                            .heightIn(max = 170.dp)
                            .shadow(
                                elevation = 5.dp,
                                shape = RoundedCornerShape(5.dp),
                                clip = true
                            )
                            .verticalScroll(state = scrollState),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .clickable {
                                    showDropdown = false
                                    onItemClick(null)
                                },
                        ) {
                            Text(
                                text = "Ninguno",
                                color = Color.Gray,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp)
                            )
                        }
                        itemList.forEach { item ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White)
                                    .clickable {
                                        showDropdown = false
                                        onItemClick(item)
                                    },
                            ) {
                                Text(
                                    text = item.let(itemToString),
                                    color = Color.Gray,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp)
                                )
                            }
                                }
                        }
                }
            }
        }
    }
}


@Composable
fun  <T> CustomDropDownSelect(
    selectedOption: T?,
    label: String,
    itemList: List<T>,
    isError: Boolean = false,
    messageError: String = "Campo Inválido",
    onItemClick: (T?) -> Unit,
    itemToString: (T) -> String
) {
    var textFilledSize by remember { mutableStateOf(Size.Zero) }
    var showDropdown by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CustomInput(
            value = selectedOption?.let(itemToString) ?: "",
            placeHolder = label,
            type = "Text",
            trailingIcon = {
                IconButton( onClick = { showDropdown = !showDropdown }) {
                    Icon(
                        imageVector = if (showDropdown) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        tint = Color.Black
                    )
                }
            },
            modifier = Modifier.onGloballyPositioned { coordinates ->
                textFilledSize = coordinates.size.toSize()
            },
            readOnly = true,
            isError = isError,
            messageError = null,
            onValueChange = { },
        )
        Box {
            if (isError){
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "*$messageError",
                    color = Color.Red,
                    textAlign = TextAlign.Start
                )
            }
            if (showDropdown) {
                CustomDropDownMenu(
                    hideMenu = { showDropdown = false },
                    itemList = itemList,
                    onItemClick = { onItemClick(it) },
                    itemToString = itemToString,
                    width = with(LocalDensity.current) { textFilledSize.width.toDp() })
            }
        }

    }
}

@Composable
fun <T> CustomDropDownMenu(
    hideMenu:() -> Unit,
    itemList:List<T>,
    onItemClick: (T?) -> Unit,
    width: Dp,
    itemToString: (T) -> String)
{

            Popup(
                alignment = Alignment.TopCenter,
                properties = PopupProperties(excludeFromSystemGesture = true),
                onDismissRequest = {  },
            ) {
                LazyColumn (
                    modifier = Modifier
                        .width(width)
                        .heightIn(max = 170.dp)
                        .shadow(
                            elevation = 5.dp,
                            shape = RoundedCornerShape(5.dp),
                            clip = true
                        )
                ) {
                    item {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .clickable {
                                hideMenu()
                                onItemClick(null)
                            },
                    ) {
                        Text(
                            text = "Ninguno",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp)
                        )
                    }
                    }
                    items(itemList) { item ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .clickable {
                                    hideMenu()
                                    onItemClick(item)
                                },
                        ) {
                            Text(
                                text = item.let(itemToString),
                                color = Color.Gray,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp)
                            )
                        }
                    }
                }
            }
}

