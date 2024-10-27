package com.techzo.cambiazo.presentation.explorer.filter

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techzo.cambiazo.common.components.ButtonApp
import com.techzo.cambiazo.common.components.ButtonIconHeaderApp
import com.techzo.cambiazo.common.components.DropdownList
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.common.components.MoneyFieldApp
import com.techzo.cambiazo.common.components.SubTitleText
import com.techzo.cambiazo.common.components.TextTitleHeaderApp
import dagger.hilt.android.scopes.ViewModelScoped

@Preview(showBackground = true, widthDp = 393, heightDp = 851)
@Composable
fun FilterScreen(
    viewModel: FilterViewModel = hiltViewModel(),
    back: () -> Unit = {},
    openExplorer: () -> Unit = {}
){

    LaunchedEffect(Unit) {
        viewModel.getLocations()
    }

    val countries = viewModel.stateCountries.value
    val departments = viewModel.stateDepartments.value
    val districts = viewModel.stateDistricts.value

    val minPrice = viewModel.minPriceText.value.data?:""
    val maxPrice = viewModel.maxPriceText.value.data?:""

    val countrySelected = viewModel.countrySelected.value.data
    val departmentSelected = viewModel.departmentSelected.value.data
    val districtSelected = viewModel.districtSelected.value.data




    MainScaffoldApp(
        paddingCard = PaddingValues(start=30.dp,end=30.dp,top=25.dp),
        contentsHeader = {
            Column(
                Modifier
                    .padding(bottom = 30.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ){

                ButtonIconHeaderApp(Icons.Filled.Close, onClick = {back()})
                TextTitleHeaderApp("Filtros")

            }
        }
    ) {

        Spacer(modifier = Modifier.height(10.dp))


        SubTitleText("Ubicacion")
        DropdownList(
            label = "Selecciona un Pais",
            selectedOption = countrySelected ,
            itemList = countries.data?: emptyList(),
            itemToString = { it.name},
            onItemClick = { viewModel.onChangeCountry(it)}
        )
        Spacer(modifier = Modifier.height(10.dp))

        DropdownList(
            label = "Selecciona un Departamento",
            selectedOption = departmentSelected,
            itemList = departments.data?: emptyList(),
            itemToString = { it.name},
            onItemClick = {viewModel.onChangeDepartment(it)}
        )
        Spacer(modifier = Modifier.height(10.dp))

        DropdownList(
            label = "Selecciona un Distrito",
            selectedOption = districtSelected,
            itemList = districts.data?: emptyList(),
            itemToString = { it.name},
            onItemClick = {viewModel.onChangeDistrict(it)}
        )

        Spacer(modifier = Modifier.height(20.dp))

        SubTitleText(subTittle = "Valor aproximado")
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

            MoneyFieldApp(minPrice,"Min", Modifier.weight(1f)){
                viewModel.onChangeMinPrice(it)
            }
            Text("a",modifier = Modifier.padding(horizontal = 20.dp))
            MoneyFieldApp(maxPrice,"Max", Modifier.weight(1f)){
                viewModel.onChangeMaxPrice(it)
            }

        }

        Spacer(modifier = Modifier.height(20.dp))

        ButtonApp(text = "Aplicar filtro"){
            viewModel.applyFilter()
            openExplorer()
        }

        ButtonApp(text = "Borrar filtro", bgColor = Color.White, fColor = Color(0xFFFFD146)){
            viewModel.clearFilters()
            openExplorer()
        }
    }
}


