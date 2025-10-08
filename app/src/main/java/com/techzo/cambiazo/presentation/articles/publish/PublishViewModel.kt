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
import com.techzo.cambiazo.data.remote.ai.ProductSuggestionDto
import com.techzo.cambiazo.data.remote.products.CreateProductDto
import com.techzo.cambiazo.data.repository.AiRepository
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.Normalizer
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class PublishViewModel @Inject constructor(
    private val productCategoryRepository: ProductCategoryRepository,
    private val locationRepository: LocationRepository,
    private val productRepository: ProductRepository,
    private val aiRepository: AiRepository
) : ViewModel() {

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

    // Errores
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

    // IA
    private val _aiLoading = mutableStateOf(false)
    val aiLoading: State<Boolean> get() = _aiLoading

    private val _aiSuggestion = mutableStateOf<ProductSuggestionDto?>(null)
    val aiSuggestion: State<ProductSuggestionDto?> get() = _aiSuggestion

    private val _isBanned = mutableStateOf(false)
    val isBanned: State<Boolean> get() = _isBanned

    private val _banRemainingMs = mutableStateOf(0L)
    val banRemainingMs: State<Long> get() = _banRemainingMs

    private val _showAiTips = mutableStateOf(false)
    val showAiTips: State<Boolean> get() = _showAiTips
    fun hideAiTips() { _showAiTips.value = false }

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
                _boost.value == productToEdit.value?.boost)
    }

    private val _messageError = mutableStateOf<String?>(null)
    val messageError: State<String?> get() = _messageError
    private val _descriptionError = mutableStateOf<String?>(null)
    val descriptionError: State<String?> get() = _descriptionError

    fun analyzeImageWithAI(
        context: Context,
        userId: Long,
        forceOverride: Boolean = false
    ) {
        val uri = _image.value ?: return
        if (_aiLoading.value) return

        _aiLoading.value = true
        viewModelScope.launch {
            try {
                val part = uriToPart(context, uri)
                when (val res = aiRepository.suggestFromImage(userId = userId, filePart = part)) {
                    is Resource.Success -> {
                        val s = res.data
                        _aiSuggestion.value = s

                        if (forceOverride || _name.value.isBlank()) _name.value = s?.name.orEmpty()
                        if (forceOverride || _description.value.isBlank()) _description.value = s?.description.orEmpty()
                        if (forceOverride || _price.value.isBlank()) {
                            val digits = s?.price?.filter { it.isDigit() } ?: ""
                            if (digits.isNotEmpty()) _price.value = digits
                        }
                        if (forceOverride || _categorySelected.value == null) {
                            s?.category?.let { catName ->
                                _categorySelected.value = (_categories.value.data ?: emptyList())
                                    .firstOrNull { equalsIgnoreAccents(it.name, catName) }
                                    ?: _categorySelected.value
                            }
                        }
                        if (!s?.suggest.isNullOrBlank() || (s?.score ?: 0) > 0) _showAiTips.value = true
                    }
                    is Resource.Error -> {
                        val raw = res.message.orEmpty().trim()
                        val looksJson = raw.startsWith("{") && raw.endsWith("}")
                        if (looksJson) {
                            try {
                                val json = org.json.JSONObject(raw)
                                val type = json.optString("violationType").takeIf { it.isNotBlank() }
                                val message = json.optString("message").takeIf { it.isNotBlank() }
                                val minutes = json.optInt("banDurationMinutes", 0)
                                val policy = json.optString("policyReference").takeIf { it.isNotBlank() }

                                val h = minutes / 60
                                val m = minutes % 60
                                val duration = if (minutes > 0) "${h}h ${m}m" else "sin restricción de tiempo"

                                _messageError.value = "La imagen no cumple la política de contenido"
                                _descriptionError.value = buildString {
                                    type?.let { appendLine("Tipo: $it") }
                                    message?.let { appendLine("Mensaje: $it") }
                                    appendLine("Sanción: $duration")
                                    policy?.let { append("Política: $it") }
                                }.trim()
                            } catch (_: Exception) {
                                _messageError.value = "No se pudo analizar la imagen"
                                _descriptionError.value = raw.ifBlank { "Inténtalo nuevamente" }
                            }
                        } else {
                            _messageError.value = "No se pudo analizar la imagen"
                            _descriptionError.value = raw.ifBlank { "Inténtalo nuevamente" }
                        }

                        _aiSuggestion.value = null
                        _showAiTips.value = false
                    }
                    else -> Unit
                }
            } catch (e: Exception) {
                _messageError.value = "Error al comunicarse con IA"
                _descriptionError.value = e.message ?: "Inténtalo nuevamente"
                _aiSuggestion.value = null
                _showAiTips.value = false
            } finally {
                _aiLoading.value = false
            }
        }
    }

    private fun startBanCountdown(totalMinutes: Int) {
        val totalMs = (totalMinutes.coerceAtLeast(0)) * 60_000L
        _isBanned.value = totalMs > 0
        _banRemainingMs.value = totalMs

        if (!_isBanned.value) return

        viewModelScope.launch {
            while (_banRemainingMs.value > 0L) {
                kotlinx.coroutines.delay(1_000)
                _banRemainingMs.value = (_banRemainingMs.value - 1_000L).coerceAtLeast(0L)
            }
            _isBanned.value = false
        }
    }

    private fun uriToPart(context: Context, uri: Uri): MultipartBody.Part {
        val mime = context.contentResolver.getType(uri) ?: "image/*"
        val name = (uri.lastPathSegment?.substringAfterLast('/') ?: "image")
            .ifBlank { "image" } + when {
            mime.contains("jpeg") -> ".jpg"
            mime.contains("png") -> ".png"
            mime.contains("webp") -> ".webp"
            else -> ""
        }

        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: throw IllegalStateException("No se pudo leer la imagen")

        val body = bytes.toRequestBody(mime.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("file", name, body)
    }

    private fun normalize(text: String): String =
        Normalizer.normalize(text.lowercase(), Normalizer.Form.NFD)
            .replace("\\p{M}+".toRegex(), "")
            .trim()

    private fun equalsIgnoreAccents(a: String?, b: String?): Boolean {
        if (a.isNullOrBlank() || b.isNullOrBlank()) return false
        return normalize(a) == normalize(b)
    }

    fun validateReachingLimit(list: List<Product>) {
        val planId = Constants.userSubscription?.plan?.id ?: 1
        val limit = when (planId) {
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

        val productsAllowed = list.count { p ->
            val created = p.createdAt ?: return@count false
            (created.after(startOfMonth) && created.before(endOfMonth)) ||
                    created == startOfMonth || created == endOfMonth
        }

        if (productsAllowed >= limit) {
            limitReached.value = true
            _messageError.value = "Límite de publicaciones alcanzado"
            _descriptionError.value = "Si quieres publicar más artículos, cambia tu suscripción."
        }
    }


    fun clearError() { _messageError.value = null; _descriptionError.value = null }
    fun hideDialog() { limitReached.value = false }

    fun productDataToEdit(product: Product?) {
        product?.let {
            productToEdit.value = product
            _name.value = product.name
            _description.value = product.description
            _price.value = product.price.toString()
            _objectChange.value = product.desiredObject
            _categorySelected.value = product.productCategory
            _countrySelected.value = Country(product.location.countryId, product.location.countryName)
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

    fun onChangeName(v: String) { _errorName.value = false; _name.value = v }
    fun onChangeDescription(v: String) { _errorDescription.value = false; _description.value = v }
    fun onChangePrice(v: String) { _errorPrice.value = false; _price.value = v }
    fun onChangeObjectChange(v: String) { _errorObjectChange.value = false; _objectChange.value = v }
    fun onChangeBoost(v: Boolean) { _errorObjectChange.value = false; _boost.value = v }

    fun selectCategory(v: ProductCategory?) { if (v != null) _errorCategory.value = false; _categorySelected.value = v }
    fun selectCountry(v: Country?) {
        if (v != null) _errorCountry.value = false
        _countrySelected.value = v
        _departmentSelected.value = null
        _districtSelected.value = null
        _departments.value = UIState(data = _allDepartments.value.filter { it.countryId == v?.id })
    }
    fun selectDepartment(v: Department?) {
        if (v != null) _errorDepartment.value = false
        _departmentSelected.value = v
        _districtSelected.value = null
        _districts.value = UIState(data = _allDistricts.value.filter { it.departmentId == v?.id })
    }
    fun selectDistrict(v: District?) { if (v != null) _errorDistrict.value = false; _districtSelected.value = v }

    fun selectImage(uri: Uri?) {
        if (uri != null) _errorImage.value = false
        val isNew = uri?.toString() != _image.value?.toString()
        _image.value = uri

        if (isNew && uri != null) {
            _aiSuggestion.value = null
            _showAiTips.value = false
        }
    }

    fun deselectImage() { _image.value = null }

    private fun getLocations() {
        viewModelScope.launch {
            val countryResult = locationRepository.getCountries()
            _allCountries.value = (countryResult as? Resource.Success)?.data ?: emptyList()
            _countries.value = if (countryResult is Resource.Success)
                UIState(data = countryResult.data) else UIState(message = countryResult.message ?: "Ocurrió un error")

            val departmentResult = locationRepository.getDepartments()
            if (departmentResult is Resource.Success) _allDepartments.value = departmentResult.data ?: emptyList()

            val districtResult = locationRepository.getDistricts()
            if (districtResult is Resource.Success) _allDistricts.value = districtResult.data ?: emptyList()

            productToEdit.value?.let { product ->
                _departments.value = UIState(data = _allDepartments.value.filter { it.countryId == product.location.countryId })
                _districts.value = UIState(data = _allDistricts.value.filter { it.departmentId == product.location.departmentId })
            }
        }
    }

    private fun getCategories() {
        viewModelScope.launch {
            val result = productCategoryRepository.getProductCategories()
            _categories.value = if (result is Resource.Success)
                UIState(data = result.data) else UIState(message = result.message ?: "Ocurrió un error")
        }
    }

    fun validateDataToUploadImage(context: Context) {
        _productState.value = UIState(isLoading = true)
        viewModelScope.launch {
            if (isEmptyData()) { _productState.value = UIState(isLoading = false); return@launch }

            productToEdit.value?.let { product ->
                if (_image.value.toString() == product.image) { editProduct(product.id, product.image); return@launch }
            }

            productToEdit.value?.let { deleteProductFromFirebase(context) } ?: uploadImageToFirebase(context)
        }
    }

    private suspend fun uploadImageToFirebase(context: Context) {
        uploadImageToFirebase(
            context = context,
            fileUri = _image.value!!,
            onSuccess = { imageUrl ->
                productToEdit.value?.let { editProduct(it.id, imageUrl) } ?: createProduct(imageUrl)
            },
            onFailure = {
                _productState.value = UIState(isLoading = false)
                _messageError.value = "Ocurrió un error al subir la imagen"
                _descriptionError.value = "Hubo un error al subir la imagen, porfavor intenta de nuevo"
            },
            onUploadStateChange = { },
            path = "products"
        )
    }

    private suspend fun deleteProductFromFirebase(context: Context) {
        deleteImageFromFirebase(
            imageUrl = productToEdit.value?.image.toString(),
            onSuccess = { uploadImageToFirebase(context) },
            onFailure = { _productState.value = UIState(isLoading = false) }
        )
    }

    private suspend fun createProduct(urlImage: String) {
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
        } else {
            _productState.value = UIState(isLoading = false)
            _productState.value = UIState(message = result.message ?: "Ocurrió un error")
            if (result.message == "Bad Request") {
                _messageError.value = "Límite de publicaciones alcanzado"
                _descriptionError.value = "Si quieres publicar más artículos, cambia tu suscripción."
            } else {
                _messageError.value = "Error al publicar"
                _descriptionError.value = "Hubo una falla al publicar el producto, porfavor intenta de nuevo"
            }
        }
    }

    private suspend fun editProduct(productId: Int, urlImage: String) {
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

    private fun isEmptyData(): Boolean {
        if (_name.value.isEmpty()) _errorName.value = true
        if (_description.value.isEmpty()) _errorDescription.value = true
        if (_price.value.isEmpty()) _errorPrice.value = true
        if (_objectChange.value.isEmpty()) _errorObjectChange.value = true
        if (_categorySelected.value == null) _errorCategory.value = true
        if (_countrySelected.value == null) _errorCountry.value = true
        if (_departmentSelected.value == null) _errorDepartment.value = true
        if (_districtSelected.value == null) _errorDistrict.value = true
        if (_image.value == null) _errorImage.value = true

        return _name.value.isEmpty() || _description.value.isEmpty() || _price.value.isEmpty() ||
                _objectChange.value.isEmpty() || _categorySelected.value == null ||
                _countrySelected.value == null || _departmentSelected.value == null ||
                _districtSelected.value == null || _image.value == null
    }

    fun clearData() {
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
        _aiSuggestion.value = null
        _showAiTips.value = false
    }
}