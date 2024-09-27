package com.techzo.cambiazo.presentation.explorer

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.common.UIState
import com.techzo.cambiazo.data.repository.ProductCategoryRepository
import com.techzo.cambiazo.data.repository.ProductRepository
import com.techzo.cambiazo.domain.model.Product
import com.techzo.cambiazo.domain.model.ProductCategory
import kotlinx.coroutines.launch

class ExplorerListViewModel(
    private val productRepository: ProductRepository,
    private val productCategoryRepository: ProductCategoryRepository) : ViewModel() {

    private val _allProducts = mutableStateOf<List<Product>>(emptyList())

    private val _name = mutableStateOf("")
    val name: State<String> get() = _name


    private val _state = mutableStateOf(UIState<List<Product>>())
    val state: State<UIState<List<Product>>> = _state

    private val _productCategories = mutableStateOf(UIState<List<ProductCategory>>())
    val productCategories: State<UIState<List<ProductCategory>>> = _productCategories

    private val _selectedCategoryId = mutableStateOf<Int?>(null)
    val selectedCategoryId: State<Int?> get() = _selectedCategoryId

    init {
        getProducts()
        getProductCategories()
    }


    fun getProducts() {
        _state.value = UIState(isLoading = true)
        viewModelScope.launch {
            val result = productRepository.getProducts()
            if(result is Resource.Success){
                _allProducts.value = result.data ?: emptyList()
                _state.value = UIState(data = result.data)
            }else{
                _state.value = UIState(message = result.message?:"Ocurrió un error")
            }
        }
    }

    fun onProductCategorySelected(id: Int) {
        _selectedCategoryId.value = if (_selectedCategoryId.value == id) null else id
        _state.value = UIState(isLoading = true)
        viewModelScope.launch {
            val result = if (_selectedCategoryId.value == null) {
                productRepository.getProducts()
            } else {
                productRepository.getProductsByCategoryId(id)
            }
            if (result is Resource.Success) {
                _allProducts.value = result.data ?: emptyList()
                applyFilter()
            } else {
                _state.value = UIState(message = result.message ?: "Ocurrió un error")
            }
        }
    }

    // Actualiza el nombre ingresado en el campo de búsqueda
    fun onNameChanged(name: String) {
        _name.value = name
        applyFilter() // Filtrar productos mientras se escribe en el buscador
    }

    // Aplica el filtro tanto por nombre como por categoría
    private fun applyFilter() {
        val filteredList = _allProducts.value.filter { product ->
            val matchesName = product.name.contains(_name.value, ignoreCase = true)
            val matchesCategory = _selectedCategoryId.value?.let { categoryId ->
                product.productCategoryId == categoryId
            } ?: true
            matchesName && matchesCategory
        }
        _state.value = UIState(data = filteredList)
    }

    // Función para obtener las categorías de productos
    fun getProductCategories() {
        _productCategories.value = UIState(isLoading = true)
        viewModelScope.launch {
            val result = productCategoryRepository.getProductCategories()
            if(result is Resource.Success){
                _productCategories.value = UIState(data = result.data)
            }else{
                _productCategories.value = UIState(message = result.message?:"Ocurrió un error")
            }
        }
    }
}