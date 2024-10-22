package com.techzo.cambiazo.presentation.articles


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techzo.cambiazo.common.components.ButtonApp
import com.techzo.cambiazo.common.components.ButtonIconHeaderApp
import com.techzo.cambiazo.common.components.CustomDropDownSelect
import com.techzo.cambiazo.common.components.CustomInput
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.common.components.TextTitleHeaderApp

@Composable
fun PublishScreen(
    viewModel: PublishViewModel = hiltViewModel(),
    back : () -> Unit = {},
) {

    val countries = viewModel.countries.value
    val departments = viewModel.departments.value
    val districts = viewModel.districts.value
    val categories = viewModel.categories.value

    val name = viewModel.name.value
    val description = viewModel.description.value
    val categorySelected = viewModel.categorySelected.value
    val countrySelected = viewModel.countrySelected.value
    val departmentSelected = viewModel.departmentSelected.value
    val districtSelected = viewModel.districtSelected.value
    val objectToChange = viewModel.objectChange.value
    val price = viewModel.price.value
    val boost = viewModel.boost.value

    val errorPrice = viewModel.errorPrice.value
    val errorName = viewModel.errorName.value
    val errorDescription = viewModel.errorDescription.value
    val errorCategory = viewModel.errorCategory.value
    val errorCountry = viewModel.errorCountry.value
    val errorDepartment = viewModel.errorDepartment.value
    val errorDistrict = viewModel.errorDistrict.value
    val errorObjectChange = viewModel.errorObjectChange.value

    val spaceHeight = 20.dp
    MainScaffoldApp(
        paddingCard = PaddingValues(start = 30.dp, end = 30.dp, top = 25.dp),
        contentsHeader = {
            Column(
                Modifier
                    .padding(bottom = 40.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ){

                ButtonIconHeaderApp(Icons.Filled.Close, onClick = {back()})
                TextTitleHeaderApp("Publicar")
            }
        },
    ){

        LazyColumn {

            //----------------TITULO------------------//
            item{

                Box( modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Título")
                }
                CustomInput(
                    value = name,
                    placeHolder = "Nombre del objeto",
                    type = "Password",
                    isError = errorName,
                    onValueChange = { viewModel.onChangeName(it) }
                )
                Spacer(modifier = Modifier.height(spaceHeight))
            }

            //----------------CATEGORY------------------//

            item {
                Box( modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Categoría")
                }

                CustomDropDownSelect(
                    selectedOption = categorySelected,
                    label ="Seleccione una Categoria",
                    itemList = categories.data ?: emptyList(),
                    onItemClick ={ viewModel.selectCategory(it)},
                    isError = errorCategory,
                    itemToString = {it.name}
                )
            Spacer(modifier = Modifier.height(spaceHeight))
            }


            //----------------DESCRIPTION ------------------//
            item{
                Box( modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Descripción")
                }
                CustomInput(value = description,
                    placeHolder = "Descripción del objeto" ,
                    type = "TextArea",
                    isError = errorDescription,
                    onValueChange = { viewModel.onChangeDescription(it) }
                )
                Spacer(modifier = Modifier.height(spaceHeight))

            }
            //----------------LOCATION------------------//
            item {
                Box( modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Ubicación")
                }

                CustomDropDownSelect(
                    selectedOption = countrySelected,
                    label ="Seeleccione un País",
                    itemList = countries.data ?: emptyList(),
                    onItemClick ={viewModel.selectCountry(it)},
                    isError = errorCountry,
                    itemToString = {it.name})

                Spacer(modifier = Modifier.height(10.dp))

                CustomDropDownSelect(
                    selectedOption = departmentSelected,
                    label ="Seeleccione un Departamento",
                    itemList = departments.data ?: emptyList(),
                    onItemClick ={ viewModel.selectDepartment(it) },
                    isError = errorDepartment,
                    itemToString = {it.name})

                Spacer(modifier = Modifier.height(10.dp))

                CustomDropDownSelect(
                    selectedOption = districtSelected,
                    label ="Seeleccione un Distrito",
                    itemList = districts.data ?: emptyList(),
                    onItemClick ={ viewModel.selectDistrict(it)},
                    isError = errorDistrict,
                    itemToString = {it.name})

                Spacer(modifier = Modifier.height(spaceHeight))
            }

            //----------------OBJECT TO CHANGE------------------//
            item{

                Box( modifier = Modifier.fillMaxWidth()) {
                    Text(text = "¿Que quieres a Cambio?")
                }

                CustomInput(
                    value = objectToChange,
                    type = "TextArea",
                    placeHolder = "Objetos...",
                    isError = errorObjectChange,
                    onValueChange = { viewModel.onChangeObjectChange(it) }

                )
                Spacer(modifier = Modifier.height(spaceHeight))
            }
            //------------------PRICE--------------------------//
            item{
                Box( modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Valor Apróximado")
                }

                CustomInput(
                    value = price,
                    placeHolder = "Precio",
                    type = "Password",
                    isError = errorPrice,
                    onValueChange = { viewModel.onChangePrice(it) }
                )
                Spacer(modifier = Modifier.height(spaceHeight))

            }
            //-------------------BOOST------------------------//
            item {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Text(text = "Boost de Visibilidad")
                            Text(text = "¡Activa tu boost y destaca tu producto un día en la página principal para más ofertas!")
                        }
                    }

                    Box(
                        modifier = Modifier
                            .padding(start = 16.dp)
                    ) {
                        Switch(
                            checked = boost,
                            onCheckedChange = { viewModel.onChangeBoost(it)}
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }


            item {
                ButtonApp(text = "Publicar", onClick = {viewModel.createProduct()})
                Spacer(modifier =   Modifier.height(30.dp))
            }
        }
    }
}
