package com.techzo.cambiazo.presentation.articles.publish


import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.components.ButtonApp
import com.techzo.cambiazo.common.components.ButtonIconHeaderApp
import com.techzo.cambiazo.common.components.CustomInput
import com.techzo.cambiazo.common.components.DialogApp
import com.techzo.cambiazo.common.components.DropdownList
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.common.components.SubTitleText
import com.techzo.cambiazo.common.components.TextTitleHeaderApp
import com.techzo.cambiazo.domain.Product
import com.techzo.cambiazo.presentation.articles.ArticlesViewModel

@Composable
fun PublishScreen(
    viewModel: PublishViewModel = hiltViewModel(),
    articlesViewModel: ArticlesViewModel = hiltViewModel(),
    back : () -> Unit = {},
    product: Product? = null,
    openMyArticles: () -> Unit = {},
    openSubscription: () -> Unit = {}
) {

    val productToEdit = remember { product }
    val messages = remember { productToEdit?.let { "¡Cambios guardados con éxito!" } ?: "¡Publicación exitosa!" }
    val descriptionMessage = remember { productToEdit?.let { "Tus modificaciones se han guardado correctamente." } ?: "Otros usuarios podrán hacerte ofertas y también podrás ofertar cuando quieras intercambiar algo." }
    val action = remember { productToEdit?.let { "Editar" } ?: "Publicar" }

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
    val messageError = viewModel.messageError.value
    val descriptionError = viewModel.descriptionError.value
    val image = viewModel.image.value

    val selectedImageLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        viewModel.selectImage(uri)
    }
    val buttonEnable = viewModel.buttonEdit.value

    val productState = viewModel.productState.value
    val context = LocalContext.current
    val spaceHeight = 20.dp

    val limitReached  = viewModel.limitReached.value


    if(limitReached){
        DialogApp(
            message = messageError?: "¡Has alcanzado el límite de publicaciones!",
            descriptionError,
            "Regresar",
            "Comprar suscripción",
            onClickButton1 = {
                viewModel.hideDialog()
                back()
            },
            onClickButton2 = {
                viewModel.hideDialog()
                openSubscription()
            })

    }

    val articles =  articlesViewModel.products.value.data?: emptyList()

    LaunchedEffect(Unit) {
        viewModel.productDataToEdit(product)
    }

    LaunchedEffect(articles) {
        if(productToEdit == null){
            viewModel.validateReachingLimit(articles)
        }
    }

    MainScaffoldApp(
        paddingCard = PaddingValues(start = 30.dp, end = 30.dp, top = 25.dp),
        contentsHeader = {
            Column(
                Modifier
                    .padding(bottom = 30.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                ButtonIconHeaderApp(Icons.Filled.Close, onClick = {back()})
                TextTitleHeaderApp(action)
            }
        },
    ) {

        LazyColumn {
            item {

                SubTitleText(subTittle = "Imagen")
                image?.let { uri ->
                    Box(modifier = Modifier
                        .shadow(
                            3.dp,
                            RoundedCornerShape(10.dp),
                            ambientColor = Color(0xFFFFD146),
                            spotColor = Color.Black
                        )) {
                        Image(
                            painter = rememberImagePainter(uri),
                            contentDescription = null,
                            modifier = Modifier
                                .height(350.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = {
                                viewModel.deselectImage()
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(40.dp)
                                .background(Color.Black.copy(alpha = 0.6f), shape = CircleShape)
                                .zIndex(100f),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    }
                } ?: Box(
                    modifier = Modifier
                        .height(150.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { selectedImageLauncher.launch("image/*") }
                ) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        drawRoundRect(
                            color = Color.Gray,
                            style = Stroke(
                                width = 3.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(25f, 25f), 0f)
                            ),
                            cornerRadius = CornerRadius(10.dp.toPx())
                        )
                    }
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ImageSearch,
                            contentDescription = "Upload Icon",
                            modifier = Modifier.size(60.dp),
                            tint = Color.Gray
                        )
                        Text(
                            text = "Sube tu foto",
                            color = Color.Gray,
                            style = TextStyle(fontSize = 16.sp)
                        )
                    }
                }

                if(errorImage){
                    Text(text = "Campo invalido", color = Color.Red)
                }
                Spacer(modifier = Modifier.height(spaceHeight))
            }

            //----------------TITULO------------------
            item{

                SubTitleText(subTittle = "Título")
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
                SubTitleText(subTittle = "Categoría")

                DropdownList(
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
                SubTitleText(subTittle = "Descripción")
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
                SubTitleText(subTittle = "Ubicación")
                DropdownList(
                    selectedOption = countrySelected,
                    label ="Seleccione un País",
                    itemList = countries.data ?: emptyList(),
                    onItemClick ={viewModel.selectCountry(it)},
                    isError = errorCountry,
                    itemToString = {it.name})

                Spacer(modifier = Modifier.height(10.dp))

                DropdownList(
                    selectedOption = departmentSelected,
                    label ="Seleccione un Departamento",
                    itemList = departments.data ?: emptyList(),
                    onItemClick ={ viewModel.selectDepartment(it) },
                    isError = errorDepartment,
                    itemToString = {it.name})

                Spacer(modifier = Modifier.height(10.dp))

                DropdownList(
                    selectedOption = districtSelected,
                    label ="Seleccione un Distrito",
                    itemList = districts.data ?: emptyList(),
                    onItemClick ={ viewModel.selectDistrict(it)},
                    isError = errorDistrict,
                    itemToString = {it.name})

                Spacer(modifier = Modifier.height(spaceHeight))
            }

            //----------------OBJECT TO CHANGE------------------//
            item{


                SubTitleText(subTittle = "¿Que quieres a Cambio?")
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


                SubTitleText(subTittle = "Valor aproximado")
                CustomInput(
                    value = price,
                    placeHolder = "Precio",
                    type = "Number",
                    isError = errorPrice,
                    onValueChange = { viewModel.onChangePrice(it) }
                )
                Spacer(modifier = Modifier.height(spaceHeight))

            }

            //-------------------BOOST------------------------//
            val userPlanId = Constants.userSubscription?.plan?.id
            if ( userPlanId != 1){
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                SubTitleText(subTittle = "Boost de Visibilidad")
                                Text(modifier = Modifier.fillMaxWidth(),
                                    text = "¡Activa tu boost y destaca tu producto un día en la página principal para más ofertas!",
                                    color = Color.Gray)
                            }
                        }

                        Box{
                            Switch(
                                checked = boost,
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
            }


            item {
                if(productState.isLoading){
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFFFFD146))
                    }
                }else{
                    messageError?.let {
                        DialogApp(it,descriptionError,"Entendido",onClickButton1 = {back()})
                    }?:if (productState.data != null) {
                        DialogApp(messages,descriptionMessage,"Entendido",onClickButton1 = { product?.let {openMyArticles()}?:viewModel.clearData() })
                    }else{
                        productToEdit?.let {
                            ButtonApp(text = action, enable = buttonEnable) {
                                viewModel.validateDataToUploadImage(context)
                            }
                        }?:
                        ButtonApp(text = action) {
                            viewModel.validateDataToUploadImage(context)
                        }

                    }

                }
                Spacer(modifier =   Modifier.height(30.dp))

            }
        }
    }
}
