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
                _state.value = UIState(data = result.data)
            }else{
                _state.value = UIState(message = result.message?:"Ocurrió un error")
            }
        }
    }

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

    fun onProductCategorySelected(id: Int) {
        _selectedCategoryId.value = id
        _state.value = UIState(isLoading = true)
        viewModelScope.launch {
            val result = productRepository.getProductsByCategoryId(id)
            if(result is Resource.Success){
                _state.value = UIState(data = result.data, message = "Se cargaron los productos")
            }else{
                _state.value = UIState(message = result.message?:"Ocurrió un error")
            }
        }
    }

    fun onNameChanged(name: String) {
        _name.value = name
    }
}