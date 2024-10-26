    package com.techzo.cambiazo.presentation.explorer

    import androidx.compose.runtime.State
    import androidx.compose.runtime.mutableStateOf
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.techzo.cambiazo.common.Constants
    import com.techzo.cambiazo.common.Resource
    import com.techzo.cambiazo.common.UIState
    import com.techzo.cambiazo.data.repository.LocationRepository
    import com.techzo.cambiazo.data.repository.ProductCategoryRepository
    import com.techzo.cambiazo.data.repository.ProductRepository
    import com.techzo.cambiazo.domain.Product
    import com.techzo.cambiazo.domain.ProductCategory
    import dagger.hilt.android.lifecycle.HiltViewModel
    import kotlinx.coroutines.launch
    import javax.inject.Inject


    @HiltViewModel
    class ExplorerListViewModel @Inject constructor (
        private val productRepository: ProductRepository,
        private val productCategoryRepository: ProductCategoryRepository) : ViewModel() {

        private val _allProducts = mutableStateOf<List<Product>>(emptyList())
        private val _state = mutableStateOf(UIState<List<Product>>())
        val state: State<UIState<List<Product>>> = _state

        private val _name = mutableStateOf("")
        val name: State<String> get() = _name

        private val _categoryId = mutableStateOf<Int?>(Constants.filterValues.categoryId)
        val categoryId: State<Int?> get() = _categoryId

        private val _productCategories = mutableStateOf(UIState<List<ProductCategory>>())
        val productCategories: State<UIState<List<ProductCategory>>> = _productCategories

        init {
            getProducts()
            getProductCategories()
            applyFilter()
        }

        private fun getProducts() {
            _state.value = UIState(isLoading = true)
            viewModelScope.launch {
                val result = productRepository.getProducts()
                if (result is Resource.Success) {
                    _allProducts.value = result.data ?: emptyList()
                    _state.value = UIState(data = result.data ?: emptyList(), isLoading = false)
                } else {
                    _state.value = UIState(message = result.message ?: "Ocurrió un error")
                }
                applyFilter()
            }

        }

        fun onProductCategorySelected(id: Int) {
            _categoryId.value = if(_categoryId.value == id) null else id
            Constants.filterValues.categoryId = _categoryId.value
            applyFilter()
        }


        fun onNameChanged(name: String) {
            _name.value = name
            applyFilter()
        }

        private fun applyFilter() {
            _state.value = UIState(isLoading = true)
            val filteredList = _allProducts.value.filter { product ->
                val matchesName = product.name.contains(_name.value, ignoreCase = true)

                val matchesCategory = Constants.filterValues.categoryId?.let { categoryId ->
                    product.productCategory.id == categoryId
                } ?: true

                val matchesCountry = Constants.filterValues.countryId?.let { countryId ->
                    product.location.countryId == countryId
                } ?: true

                val matchesDepartment = Constants.filterValues.departmentId?.let { departmentId ->
                    product.location.departmentId == departmentId
                } ?: true

                val matchesDistrictId = Constants.filterValues.districtId?.let { districtId ->
                    product.location.districtId == districtId
                } ?: true

                val matchesMinPrice = Constants.filterValues.minPrice?.let { minPrice ->
                    product.price >= minPrice
                } ?: true

                val matchesMaxPrice = Constants.filterValues.maxPrice?.let { maxPrice ->
                    product.price <= maxPrice
                } ?: true

                matchesName && matchesCategory && matchesCountry && matchesDepartment && matchesDistrictId && matchesMinPrice && matchesMaxPrice
            }
                _state.value = UIState(data = filteredList)
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

        fun getProductById(productId: Int): Product? {
            return _allProducts.value.find { it.id == productId }
        }
    }