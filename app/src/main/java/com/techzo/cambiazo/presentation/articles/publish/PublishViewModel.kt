package com.techzo.cambiazo.presentation.articles.publish

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.common.UIState
import com.techzo.cambiazo.common.deleteImageFromFirebase
import com.techzo.cambiazo.common.uploadImageToFirebase
import com.techzo.cambiazo.data.remote.products.CreateProductDto
import com.techzo.cambiazo.data.repository.LocationRepository
import com.techzo.cambiazo.data.repository.ProductCategoryRepository
import com.techzo.cambiazo.data.repository.ProductRepository
import com.techzo.cambiazo.domain.Country
import com.techzo.cambiazo.domain.Department
import com.techzo.cambiazo.domain.District
import com.techzo.cambiazo.domain.Product
import com.techzo.cambiazo.domain.ProductCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class PublishViewModel @Inject constructor(
    private val productCategoryRepository: ProductCategoryRepository,
    private val locationRepository: LocationRepository,
    private val productRepository: ProductRepository
):ViewModel() {

    private val productToEdit = mutableStateOf<Product?>(null)
     val limitReached = mutableStateOf(false)


    private val _allCountries = mutableStateOf<List<Country>>(emptyList())
    private val _allDepartments = mutableStateOf<List<Department>>(emptyList())
    private val _allDistricts = mutableStateOf<List<District>>(emptyList())

    private val _categories = mutableStateOf(UIState<List<ProductCategory>>())
    val categories: State<UIState<List<ProductCategory>>> = _categories
    private val _countries = mutableStateOf(UIState<List<Country>>())
    val countries: State<UIState<List<Country>>> = _countries
    private val _districts = mutableStateOf(UIState<List<District>>())
    val districts: State<UIState<List<District>>> = _districts
    private val _departments = mutableStateOf(UIState<List<Department>>())
    val departments: State<UIState<List<Department>>> = _departments


    private val _name = mutableStateOf("")
    val name: State<String> get() = _name

    private val _description = mutableStateOf("")
    val description: State<String> get() = _description

    private val _price = mutableStateOf("")
    val price: State<String> get() = _price

    private val _objectChange = mutableStateOf("")
    val objectChange: State<String> get() = _objectChange

    private val _categorySelected = mutableStateOf<ProductCategory?>(null)
    val categorySelected: State<ProductCategory?> get() = _categorySelected

    private val _countrySelected = mutableStateOf<Country?>(null)
    val countrySelected: State<Country?> get() = _countrySelected

    private val _departmentSelected = mutableStateOf<Department?>(null)
    val departmentSelected: State<Department?> get() = _departmentSelected

    private val _districtSelected = mutableStateOf<District?>(null)
    val districtSelected: State<District?> get() = _districtSelected

    private val _image = mutableStateOf<Uri?>(null)
    val image: State<Uri?> get() = _image

    private val _boost = mutableStateOf(false)
    val boost: State<Boolean> get() = _boost

    //errors
    private val _errorName = mutableStateOf(false)
    val errorName: State<Boolean> get() = _errorName

    private val _errorDescription = mutableStateOf(false)
    val errorDescription: State<Boolean> get() = _errorDescription

    private val _errorPrice = mutableStateOf(false)
    val errorPrice: State<Boolean> get() = _errorPrice

    private val _errorObjectChange = mutableStateOf(false)
    val errorObjectChange: State<Boolean> get() = _errorObjectChange

    private val _errorCategory = mutableStateOf(false)
    val errorCategory: State<Boolean> get() = _errorCategory

    private val _errorCountry = mutableStateOf(false)
    val errorCountry: State<Boolean> get() = _errorCountry

    private val _errorDepartment = mutableStateOf(false)
    val errorDepartment: State<Boolean> get() = _errorDepartment

    private val _errorDistrict = mutableStateOf(false)
    val errorDistrict: State<Boolean> get() = _errorDistrict

    private val _errorImage = mutableStateOf(false)
    val errorImage: State<Boolean> get() = _errorImage

    private val _productState = mutableStateOf(UIState<Any>())
    val productState: State<UIState<Any>> get() = _productState

    val buttonEdit = derivedStateOf {
        !(_name.value == productToEdit.value?.name &&
                _description.value == productToEdit.value?.description &&
                _price.value == productToEdit.value?.price.toString() &&
                _objectChange.value == productToEdit.value?.desiredObject &&
                _categorySelected.value?.id == productToEdit.value?.productCategory?.id &&
                _countrySelected.value?.id == productToEdit.value?.location?.countryId &&
                _departmentSelected.value?.id == productToEdit.value?.location?.departmentId &&
                _districtSelected.value?.id == productToEdit.value?.location?.districtId &&
                _image.value.toString() == productToEdit.value?.image &&
                _boost.value == productToEdit.value?.boost
                )
    }

    private val _messageError = mutableStateOf<String?>(null)
    val messageError: State<String?> get() = _messageError
    private val _descriptionError = mutableStateOf<String?>(null)
    val descriptionError: State<String?> get() = _descriptionError


    fun validateReachingLimit(list: List<Product>) {
        Log.d("PublishViewModel", "print: ${list}")
        val limit = when (Constants.userSubscription!!.plan.id) {
            1 -> 3
            2 -> 15
            else -> 35
        }
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startOfMonth = calendar.time

        calendar.add(Calendar.MONTH, 1)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        val endOfMonth = calendar.time

        val productsAllowed = list.count {
            ((it.createdAt.after(startOfMonth) && it.createdAt.before(endOfMonth)) ||
                    it.createdAt == startOfMonth ||
                    it.createdAt == endOfMonth)
        }

        Log.d("PublishViewModel", "tamanio es: ${productsAllowed}")

        if (productsAllowed >= limit) {
            limitReached.value = true
            _messageError.value = "Límite de publicaciones alcanzado"
            _descriptionError.value = "Si quieres publicar más artículos, cambia tu suscripción."
        }

    }



    fun clearError(){
        _messageError.value = null
        _descriptionError.value = null
    }

    fun hideDialog(){
        limitReached.value = false
    }

    fun productDataToEdit(product: Product?){
        product?.let {
            productToEdit.value = product
            _name.value = product.name
            _description.value = product.description
            _price.value = product.price.toString()
            _objectChange.value = product.desiredObject
            _categorySelected.value = product.productCategory
            _countrySelected.value = Country(
                id = product.location.countryId,
                name = product.location.countryName
            )
            _departmentSelected.value = Department(
                id = product.location.departmentId,
                name = product.location.departmentName,
                countryId = product.location.countryId
            )
            _districtSelected.value = District(
                id = product.location.districtId,
                name = product.location.districtName,
                departmentId = product.location.departmentId
            )
        _image.value = Uri.parse(product.image)
        }
    }

    init {
        getCategories()
        getLocations()
    }

    fun onChangeName(name: String) {
        _errorName.value = false
        _name.value = name
    }

    fun onChangeDescription(description: String) {
        _errorDescription.value = false
        _description.value = description
    }

    fun onChangePrice(price: String) {
        _errorPrice.value = false
        _price.value = price
    }

    fun onChangeObjectChange(objectChange: String) {
        _errorObjectChange.value = false
        _objectChange.value = objectChange
    }

    fun onChangeBoost(boost: Boolean) {
        _errorObjectChange.value = false
        _boost.value = boost
    }

    fun selectCategory(category: ProductCategory?) {
        if (category != null) _errorCategory.value = false
        _categorySelected.value = category
    }

    fun selectCountry(country: Country?) {
        if (country != null) _errorCountry.value = false
        _countrySelected.value = country
        _departmentSelected.value = null
        _districtSelected.value = null

        _departments.value =  UIState(data = _allDepartments.value.filter { it.countryId == country?.id })
    }

    fun selectDepartment(department: Department?) {
        if (department != null) _errorDepartment.value = false
        _departmentSelected.value = department
        _districtSelected.value = null

        _districts.value =
            UIState(data = _allDistricts.value.filter { it.departmentId == department?.id })
    }

    fun selectDistrict(district: District?) {
        if (district != null) _errorDistrict.value = false
        _districtSelected.value = district
    }

    fun selectImage(image: Uri?) {
        if (image != null) _errorImage.value = false
        _image.value = image
    }

    fun deselectImage() {
        _image.value = null
    }


    private fun getLocations() {
        viewModelScope.launch {
            val countryResult = locationRepository.getCountries()

            if (countryResult is Resource.Success) {
                _allCountries.value = countryResult.data ?: emptyList()
                _countries.value = UIState(data = countryResult.data)
            } else {
                _countries.value = UIState(message = countryResult.message ?: "Ocurrió un error")
            }


            val departmentResult = locationRepository.getDepartments()
            if (departmentResult is Resource.Success) {
                _allDepartments.value = departmentResult.data ?: emptyList()
            }
            val districtResult = locationRepository.getDistricts()
            if (districtResult is Resource.Success) {
                _allDistricts.value = districtResult.data ?: emptyList()
            }

             productToEdit.value?.let{product->
                 _departments.value =  UIState(data = _allDepartments.value.filter { it.countryId == product.location.countryId })
                 _districts.value = UIState(data = _allDistricts.value.filter { it.departmentId == product.location.departmentId })
             }
        }

    }

    private fun getCategories() {
        viewModelScope.launch {
            val result = productCategoryRepository.getProductCategories()
            if (result is Resource.Success) {
                _categories.value = UIState(data = result.data)
            } else {
                _categories.value = UIState(message = result.message ?: "Ocurrió un error")
            }
        }
    }



    fun validateDataToUploadImage(context: Context) {
        _productState.value = UIState(isLoading = true)

        viewModelScope.launch {
            if (isEmptyData()) return@launch

            productToEdit.value?.let { product ->

                if (_image.value.toString() == product.image) {
                    editProduct(product.id, product.image)
                    return@launch
                }
            }

            productToEdit.value?.let {
                deleteProductFromFirebase(context)
            } ?: uploadImageToFirebase(context)

        }

    }

    private suspend fun uploadImageToFirebase(context: Context){
        uploadImageToFirebase(
            context = context,
            fileUri = _image.value!!,
            onSuccess = { imageUrl ->
                productToEdit.value?.let { editProduct(it.id, imageUrl) } ?: createProduct(
                    imageUrl
                )
            },
            onFailure = {
                _messageError.value = "Ocurrió un error al subir la imagen"
                _descriptionError.value = "Hubo un error al subir la imagen, porfavor intenta de nuevo"
            },
            onUploadStateChange = { },
            path = "products"
        )
    }

    private suspend fun deleteProductFromFirebase(context: Context){
        deleteImageFromFirebase(
            imageUrl = productToEdit.value?.image.toString(),
            onSuccess = {
                uploadImageToFirebase(context)
            },
            onFailure = {
                _productState.value = UIState(isLoading = false)
                return@deleteImageFromFirebase
            }
        )
    }

    private  suspend fun createProduct(urlImage: String) {

            val product = CreateProductDto(
                available = true,
                boost = _boost.value,
                description = _description.value,
                desiredObject = _objectChange.value,
                districtId = _districtSelected.value!!.id,
                image = urlImage,
                name = _name.value,
                price = _price.value.toInt(),
                productCategoryId = _categorySelected.value!!.id,
                userId = Constants.user!!.id
            )
            val result = productRepository.createProduct(product)
            if (result is Resource.Success) {
                _productState.value = UIState(isLoading = false)
                _productState.value = UIState(data = result.data)

            }else{
                _productState.value = UIState(isLoading = false)
                _productState.value = UIState(message = result.message?:"Ocurrió un error")
                if(result.message == "Bad Request"){
                    _messageError.value = "Límite de publicaciones alcanzado"
                    _descriptionError.value = "Si quieres publicar más artículos, cambia tu suscripción."
                }else{
                    _messageError.value = "Error al publicar"
                    _descriptionError.value = "Hubo una falla al publicar el producto, porfavor intenta de nuevo"
                }
            }

    }

    private  suspend fun editProduct(productId: Int,urlImage: String) {

            val product = CreateProductDto(
                available = true,
                boost = _boost.value,
                description = _description.value,
                desiredObject = _objectChange.value,
                districtId = _districtSelected.value!!.id,
                image = urlImage,
                name = _name.value,
                price = _price.value.toInt(),
                productCategoryId = _categorySelected.value!!.id,
                userId = Constants.user!!.id
            )
            val result = productRepository.updateProduct(productId, product)
            if (result is Resource.Success) {
                _productState.value = UIState(isLoading = false)
                _productState.value = UIState(data = result.data)

            }
    }


    private fun isEmptyData(): Boolean{
        if (_name.value.isEmpty()) {
            _errorName.value = true
        }
        if (_description.value.isEmpty()) {
            _errorDescription.value = true
        }
        if (_price.value.isEmpty()) {
            _errorPrice.value = true
        }
        if (_objectChange.value.isEmpty()) {
            _errorObjectChange.value = true
        }
        if (_categorySelected.value == null) {
            _errorCategory.value = true
        }
        if (_countrySelected.value == null) {
            _errorCountry.value = true
        }
        if (_departmentSelected.value == null) {
            _errorDepartment.value = true
        }
        if (_districtSelected.value == null) {
            _errorDistrict.value = true
        }
        if (_image.value == null) {
            _errorImage.value = true
        }

        return _name.value.isEmpty() || _description.value.isEmpty() || _price.value.isEmpty() || _objectChange.value.isEmpty() || _categorySelected.value == null || _countrySelected.value == null || _departmentSelected.value == null || _districtSelected.value == null || _image.value == null
    }

    fun clearData(){
        _name.value = ""
        _description.value = ""
        _price.value = ""
        _objectChange.value = ""
        _categorySelected.value = null
        _countrySelected.value = null
        _departmentSelected.value = null
        _districtSelected.value = null
        _image.value = null
        _boost.value = false
        _productState.value = UIState()
    }
    
}