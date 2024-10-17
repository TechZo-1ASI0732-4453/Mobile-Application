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
        private val productCategoryRepository: ProductCategoryRepository,
        private val locationRepository: LocationRepository) : ViewModel() {

        private val _allProducts = mutableStateOf<List<Product>>(emptyList())
        private val _state = mutableStateOf(UIState<List<Product>>())
        val state: State<UIState<List<Product>>> = _state

        private val _name = mutableStateOf("")
        val name: State<String> get() = _name

        private val _productCategories = mutableStateOf(UIState<List<ProductCategory>>())
        val productCategories: State<UIState<List<ProductCategory>>> = _productCategories

        init {
            getProducts()
            getProductCategories()
            applyFilter()
        }

        fun getProducts() {
            _state.value = UIState(isLoading = true)
            viewModelScope.launch {
                val districtResources = locationRepository.getDistricts()
                val districts = districtResources.data?: emptyList()
                val departmentResources = locationRepository.getDepartments()
                val departments = departmentResources.data?: emptyList()
                val countryResources = locationRepository.getCountries()
                val countries = countryResources.data?: emptyList()

                val result = productRepository.getProducts()
                if(result is Resource.Success){
                    val products = result.data?: emptyList()
                    products.forEach { product ->
                        product.district = districts.find { it.id == product.districtId }
                        product.department = departments.find { it.id == product.district?.departmentId }
                        product.country = countries.find { it.id == product.department?.countryId }
                    }
                    _allProducts.value = products
                    _state.value = UIState(data = products)
                }else{
                    _state.value = UIState(message = result.message?:"Ocurrió un error")
                }
                applyFilter()
            }
        }

        fun onProductCategorySelected(id: Int) {
            Constants.filterValues.categoryId = if (Constants.filterValues.categoryId==id) null else id
            applyFilter()
        }


        fun onNameChanged(name: String) {
            _name.value = name
            applyFilter()
        }

        private fun applyFilter() {

                val filteredList = _allProducts.value.filter { product ->
                    val matchesName = product.name.contains(_name.value, ignoreCase = true)

                    val matchesCategory = Constants.filterValues.categoryId?.let { categoryId ->
                        product.productCategoryId == categoryId
                    } ?: true

                    val matchesCountry = Constants.filterValues.countryId?.let { countryId ->
                        product.department?.countryId == countryId
                    } ?: true

                    val matchesDepartment = Constants.filterValues.departmentId?.let { departmentId ->
                        product.district?.departmentId == departmentId
                    } ?: true

                    val matchesDistrictId = Constants.filterValues.districtId?.let { districtId ->
                        product.districtId == districtId
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
    }