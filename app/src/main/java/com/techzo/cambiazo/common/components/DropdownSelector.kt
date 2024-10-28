package com.techzo.cambiazo.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties


@Composable
fun  <T> DropdownList(
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
    val color = remember { mutableStateOf(Color.Gray) }
    val borderThickness = if(!showDropdown) 1.dp else 2.dp
    if (isError) {
        color.value = Color.Red
    } else {
        if (showDropdown) {
            color.value = Color(0xFFFFD146)
        } else {
            color.value = Color.Gray
        }
    }

    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        BasicTextField(
            value = selectedOption?.let(itemToString) ?: "",
            readOnly = true,
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFilledSize = coordinates.size.toSize()
                }
                .height(45.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .border(borderThickness, color.value, RoundedCornerShape(8.dp)),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                color = Color.Black
            ),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 10.dp, vertical = 10.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (selectedOption==null) {
                            Text(
                                label,
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 15.sp
                                )
                            )
                        }
                        innerTextField()
                    }
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                }
            }
        )
        Box {
            if (isError){
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "*$messageError",
                    color = Color.Red,
                    textAlign = TextAlign.Start,
                    fontSize = 12.sp
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
                    Icon(
                        imageVector = if (showDropdown) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        tint = Color.Black,
                    )
            },
            modifier = Modifier.onGloballyPositioned { coordinates ->
                textFilledSize = coordinates.size.toSize()
            }.clickable { showDropdown = !showDropdown },
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
                    textAlign = TextAlign.Start,
                    fontSize = 12.sp
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
                onDismissRequest = {hideMenu()  },
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

