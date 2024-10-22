package com.techzo.cambiazo.presentation.articles

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.common.UIState
import com.techzo.cambiazo.data.remote.products.CreateProductDto
import com.techzo.cambiazo.data.remote.products.ProductCategory
import com.techzo.cambiazo.data.repository.LocationRepository
import com.techzo.cambiazo.data.repository.ProductCategoryRepository
import com.techzo.cambiazo.data.repository.ProductRepository
import com.techzo.cambiazo.domain.Country
import com.techzo.cambiazo.domain.Department
import com.techzo.cambiazo.domain.District
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PublishViewModel @Inject constructor(
    private val productCategoryRepository: ProductCategoryRepository,
    private val locationRepository: LocationRepository,
    private val productRepository: ProductRepository
):ViewModel() {

    private val _allCountries = mutableStateListOf<Country>()
    private val _allDepartments = mutableStateListOf<Department>()
    private val _allDistricts = mutableStateListOf<District>()

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
        _errorCategory.value = false
        _categorySelected.value = category
    }

    fun selectCountry(country: Country?) {
        _errorCountry.value = false
        _countrySelected.value = country
        _departments.value = UIState(data = _allDepartments.filter { it.id == country?.id })
    }

    fun selectDepartment(department: Department?) {
        _errorDepartment.value = false
        _departmentSelected.value = department
        _districts.value = UIState(data = _allDistricts.filter { it.id == department?.id })
    }

    fun selectDistrict(district: District?) {
        _errorDistrict.value = false
        _districtSelected.value = district
    }

    fun onPublish(){

        if(_name.value.isEmpty()){
            _errorName.value = true
        }
        if(_description.value.isEmpty()){
            _errorDescription.value = true
        }
        if(_price.value.isEmpty()){
            _errorPrice.value = true
        }
        if(_objectChange.value.isEmpty()){
            _errorObjectChange.value = true
        }
        if(_categorySelected.value == null){
            _errorCategory.value = true
        }
        if(_countrySelected.value == null){
            _errorCountry.value = true
        }
        if(_departmentSelected.value == null){
            _errorDepartment.value = true
        }
        if(_districtSelected.value == null){
            _errorDistrict.value = true
            return
        }

        createProduct()
    }


    fun selectImage(image: Uri?){
        _image.value = image
    }
    fun deselectImage(){
        _image.value = null
    }

     fun createProduct(){
        val product = CreateProductDto(
            available = true,
            boost = _boost.value,
            description = "hola soy jeremy",
            desiredObject = "quiero ser full stack",
            districtId = 1,
            image = Constants.DEFAULT_PROFILE_PICTURE,
            name = "jeremy de sanjuan",
            price = 10,
            productCategoryId = 1,
            userId = Constants.user!!.id
        )
        viewModelScope.launch {
            val result = productRepository.createProduct(product)
            if (result is Resource.Success) {
                //back()
            }
        }


    }



}