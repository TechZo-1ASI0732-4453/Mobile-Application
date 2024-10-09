package com.techzo.cambiazo.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

@Composable
fun DropdownList(label: String,itemList: List<String>, onItemClick: (String) -> Unit) {

    var selectedOption by remember { mutableStateOf("") }
    var textFilledSize by remember { mutableStateOf(Size.Zero) }
    var showDropdown by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {

        OutlinedTextField(

            value = selectedOption,
            label = {
                Text(text = label)
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                          showDropdown= !showDropdown
                    },
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
                    properties = PopupProperties(
                        excludeFromSystemGesture = true,
                    ),
                    onDismissRequest = { },
                    ) {

                    Column(
                        modifier = Modifier
                            .width(with(LocalDensity.current) { textFilledSize.width.toDp() })
                            .heightIn(max = 170.dp)
                            .shadow( elevation = 5.dp,
                                shape = RoundedCornerShape(5.dp),
                                clip = true,
                                )
                            .verticalScroll(state = scrollState),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {

                        itemList.forEach{ item ->

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White)
                                    .clickable {
                                        selectedOption = item
                                        showDropdown = false
                                    },
                            ) {
                                Text(text = item,
                                    color = Color.Gray,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp))
                            }
                        }

                    }
                }
            }
        }
    }

}