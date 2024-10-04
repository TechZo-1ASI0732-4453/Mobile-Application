package com.techzo.cambiazo.presentation.filter

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.techzo.cambiazo.common.components.ButtonApp
import com.techzo.cambiazo.common.components.ButtonIconHeaderApp
import com.techzo.cambiazo.common.components.DropdownList
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.common.components.MoneyFieldApp
import com.techzo.cambiazo.common.components.TextTitleHeaderApp
import com.techzo.cambiazo.common.components.TextToggleButton

@Preview(showBackground = true, widthDp = 393, heightDp = 851)
@Composable
fun FilterScreen(
    back: () -> Unit = {}
){
    var countrySelected by remember { mutableStateOf("Seleccione un País") }
    var departmentSelected by remember { mutableStateOf("Seleccione un Departamento") }
    var districtSelected by remember { mutableStateOf("Seleccione un Distrito") }

    var minPrice by remember { mutableStateOf("") }
    var maxPrice by remember { mutableStateOf("") }

    var boostActive by remember { mutableStateOf(false) }

    val itemList = listOf<String>("Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6")
    var selectedIndex by rememberSaveable { mutableStateOf(0) }

    MainScaffoldApp(
        paddingCard = PaddingValues(start=20.dp,end=20.dp,top=25.dp),
        contentsHeader = {
            Column(
                Modifier.padding(bottom = 40.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ){

                ButtonIconHeaderApp(Icons.Filled.Close, onClick = {back()})
                TextTitleHeaderApp("Filtros")

            }
        }
    ) {

        Box( modifier = Modifier.fillMaxWidth()) {
            Text(text = "Ubicacion")
        }

        DropdownList(
            label = countrySelected,
            itemList = itemList,
            onItemClick = {countrySelected = it}
        )
        DropdownList(
            label = departmentSelected,
            itemList = itemList,
            onItemClick = {departmentSelected = it}
        )
        DropdownList(
            label = districtSelected,
            itemList = itemList,
            onItemClick = {districtSelected = it}
        )

        Spacer(modifier = Modifier.height(20.dp))
        Box( modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp, bottom = 10.dp)) {
            Text(text = "Valor Aproximado")
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

            MoneyFieldApp(minPrice,"Min", Modifier.weight(1f)){minPrice=it}
            Text("a",modifier = Modifier.padding(horizontal = 20.dp))
            MoneyFieldApp(maxPrice,"Max", Modifier.weight(1f)){maxPrice=it}

        }
        Spacer(modifier = Modifier.height(20.dp))

        Box( modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp, bottom = 10.dp)) {
            Text(text = "Boost")
        }
        TextToggleButton(
            selected = boostActive,
            onToggle = {  boostActive = !boostActive},
            labelOff = "Activado",
            labelOn = "Desactivado"
        )
        Spacer(modifier = Modifier.height(50.dp))
        ButtonApp(text = "Aplicar filtro", onClick = {})

        ButtonApp(text = "Borrar filtro", bgColor = Color.White, fColor = Color(0xFFFFD146), onClick = {})

    }
}


