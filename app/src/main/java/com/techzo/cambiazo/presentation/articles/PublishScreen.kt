package com.techzo.cambiazo.presentation.articles


import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
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
    openMyArticles: () -> Unit = {}
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
    val errorImage = viewModel.errorImage.value

    val image = viewModel.image.value

    val selectedImageLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent(),) { uri ->
        viewModel.selectImage(uri)
    }

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
    ) {

        LazyColumn {
            item {
                Box( modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Imagen")
                }

                image?.let { uri ->
                    Box {

                        IconButton(
                            onClick = {
                                viewModel.deselectImage()
                            },
                            modifier = Modifier
                                .offset(x = 5.dp, y = -5.dp)
                                .size(25.dp)
                                .background(Color(0xFFFFD146), shape = RoundedCornerShape(50.dp))
                                .align(Alignment.TopEnd).zIndex(100f)
                            ,
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color(0xFFFFD146),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Filled.Close,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp).zIndex(100f))
                        }
                        Box(modifier = Modifier
                            .size(100.dp)
                            .border(1.dp, color = Color.Gray, shape = RoundedCornerShape(10.dp))
                            .clip(RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center)
                        {

                            Image(
                                painter = rememberImagePainter(uri),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                }?: Box(
                    modifier = Modifier
                        .size(100.dp)
                        .border(1.dp, color = Color.Gray, shape = RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ){
                    IconButton(
                        onClick = {
                            selectedImageLauncher.launch("image/*")
                        },
                        modifier = Modifier.size(50.dp).background(Color(0xFFFFD146 ), shape = RoundedCornerShape(50))
                    ) {
                            Icon(Icons.Filled.Upload, contentDescription = null)
                    }
                }
                if(errorImage){
                    Text(text = "Campo invalido", color = Color.Red)
                }
                Spacer(modifier = Modifier.height(spaceHeight))
            }

            //----------------TITULO------------------
            item{

                Box( modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Título")
                }
                CustomInput(
                    value = name,
                    placeHolder = "Nombre del objeto",
                    type = "Text",
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
                    type = "Money",
                    isError = errorPrice,
                    onValueChange = { viewModel.onChangePrice(it) }
                )
                Spacer(modifier = Modifier.height(spaceHeight))

            }
            //-------------------BOOST------------------------//
            item {

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Text(text = "Boost de Visibilidad")
                            Text(modifier = Modifier.fillMaxWidth(),
                                text = "¡Activa tu boost y destaca tu producto un día en la página principal para más ofertas!",
                                color = Color.Gray)
                        }
                    }

                    Box{
                        Switch(
                            checked = boost,
                            thumbContent = {
                                if (boost) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = "Active",
                                        tint = Color.Black
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Inactive",
                                        tint = Color.Black
                                    )
                                }
                            },

                            onCheckedChange = { viewModel.onChangeBoost(it)},
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,//circle color when switch is on
                                checkedTrackColor = Color(0xFFFFD146),//background color when switch is on
                                checkedBorderColor = Color(0xFFFFD146), //border color when switch is on
                                checkedIconColor = Color(0xFFFFD146),//icon color when switch is on

                                uncheckedThumbColor = Color.White,//circle color when switch is off
                                uncheckedTrackColor = Color(0xFFD9D9D9),//background color when switch is off
                                uncheckedBorderColor = Color(0xFFD9D9D9),//border color when switch is off
                                uncheckedIconColor = Color.Gray//icon color when switch is off
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }


            item {
                ButtonApp(text = "Publicar", onClick = {viewModel.onPublish(openMyArticles)})
                Spacer(modifier =   Modifier.height(30.dp))
            }
        }
    }
}
