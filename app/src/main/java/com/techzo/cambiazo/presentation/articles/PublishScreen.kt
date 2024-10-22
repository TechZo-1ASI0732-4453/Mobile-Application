package com.techzo.cambiazo.presentation.articles

import android.widget.ToggleButton
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techzo.cambiazo.common.components.ButtonApp
import com.techzo.cambiazo.common.components.ButtonIconHeaderApp
import com.techzo.cambiazo.common.components.DropdownList
import com.techzo.cambiazo.common.components.FieldTextApp
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.common.components.MoneyFieldApp
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

    val title = viewModel.name.value
    val description = viewModel.description.value
    val categorySelected = viewModel.categorySelected.value
    val countrySelected = viewModel.countrySelected.value
    val departmentSelected = viewModel.departmentSelected.value
    val districtSelected = viewModel.districtSelected.value
    val objectToChange = viewModel.objectChange.value
    val price = viewModel.price.value
    val boost = viewModel.boost.value

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
                FieldTextApp(valueText = title, text = "Nombre del objeto") {
                    viewModel.onChangeName(it)
                }
            Spacer(modifier = Modifier.height(spaceHeight))
            }

            //----------------CATEGORY------------------//

            item {
                Box( modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Categoría")
                }

                DropdownList(
                    selectedOption = categorySelected,
                    label ="Seleccione una Categoria",
                    itemList = categories.data ?: emptyList(),
                    onItemClick ={ viewModel.selectCategory(it)},
                    itemToString = {it.name}
                )
            Spacer(modifier = Modifier.height(spaceHeight))
            }


            //----------------DESCRIPTION ------------------//
            item{
                Box( modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Descripción")
                }

                OutlinedTextField(
                    value = description,
                    placeholder = {
                        Text("Descripción del objeto", color = Color.Gray,
                            style= MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Normal,
                                fontFamily = FontFamily.SansSerif))
                    },
                    onValueChange = { viewModel.onChangeDescription(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .padding(vertical = 6.dp)
                        .border(1.dp, Color.Gray, RoundedCornerShape(10.dp)),
                    shape = RoundedCornerShape(10.dp),
                )
                Spacer(modifier = Modifier.height(spaceHeight))

            }
            //----------------LOCATION------------------//
            item {
                Box( modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Ubicación")
                }

                DropdownList(
                    selectedOption = countrySelected,
                    label ="Seeleccione un País",
                    itemList = countries.data ?: emptyList(),
                    onItemClick ={viewModel.selectCountry(it)},
                    itemToString = {it.name})

                Spacer(modifier = Modifier.height(10.dp))

                DropdownList(
                    selectedOption = departmentSelected,
                    label ="Seeleccione un Departamento",
                    itemList = departments.data ?: emptyList(),
                    onItemClick ={ viewModel.selectDepartment(it) },
                    itemToString = {it.name})

                Spacer(modifier = Modifier.height(10.dp))

                DropdownList(
                    selectedOption = districtSelected,
                    label ="Seeleccione un Distrito",
                    itemList = districts.data ?: emptyList(),
                    onItemClick ={ viewModel.selectDistrict(it)},
                    itemToString = {it.name})

                Spacer(modifier = Modifier.height(spaceHeight))
            }

            //----------------OBJECT TO CHANGE------------------//
            item{

                Box( modifier = Modifier.fillMaxWidth()) {
                    Text(text = "¿Que quieres a Cambio?")
                }

                 OutlinedTextField(
                    value = objectToChange,
                    placeholder = {
                        Text("Objetos...", color = Color.Gray,
                            style= MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Normal,
                                fontFamily = FontFamily.SansSerif))
                    },
                    onValueChange = { viewModel.onChangeObjectChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .padding(vertical = 6.dp)
                        .border(1.dp, Color.Gray, RoundedCornerShape(10.dp)),
                    shape = RoundedCornerShape(10.dp),
                )
                Spacer(modifier = Modifier.height(spaceHeight))
            }
            //------------------PRICE--------------------------//
            item{
                Box( modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Valor Apróximado")
                }
                MoneyFieldApp(valueText = price, text = "") {
                    viewModel.onChangePrice(it)
                }
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
                ButtonApp(text = "Publicar"){
                }
                Spacer(modifier =   Modifier.height(30.dp))
            }
        }
    }
}
