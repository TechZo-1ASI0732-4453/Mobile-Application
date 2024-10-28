package com.techzo.cambiazo.presentation.explorer.filter

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.common.UIState
import com.techzo.cambiazo.data.repository.LocationRepository
import com.techzo.cambiazo.domain.Country
import com.techzo.cambiazo.domain.Department
import com.techzo.cambiazo.domain.District
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(
    private val locationRepository: LocationRepository
): ViewModel()  {

    //all list
    private val _allCountries = mutableStateOf<List<Country>>(emptyList())
    private val _allDepartments = mutableStateOf<List<Department>>(emptyList())
    private val _allDistricts = mutableStateOf<List<District>>(emptyList())
    //state of list to show and filter
    private val _stateCountries = mutableStateOf(UIState<List<Country>>())
    val stateCountries: State<UIState<List<Country>>> = _stateCountries
    private val _stateDepartments = mutableStateOf(UIState<List<Department>>())
    val stateDepartments: State<UIState<List<Department>>> = _stateDepartments
    private val _stateDistricts = mutableStateOf(UIState<List<District>>())
    val stateDistricts: State<UIState<List<District>>> = _stateDistricts

    //location  selected
    private val _countrySelected = mutableStateOf(UIState<Country?>())
    val countrySelected: State<UIState<Country?>> = _countrySelected
    private val _departmentSelected = mutableStateOf(UIState<Department?>())
    val departmentSelected: State<UIState<Department?>> = _departmentSelected
    private val _districtSelected = mutableStateOf(UIState<District?>())
    val districtSelected: State<UIState<District?>> = _districtSelected

    //range price
    private val _maxPriceText = mutableStateOf(UIState<String>(data = Constants.filterValues.maxPrice?.toString()?:""))
    val maxPriceText: State<UIState<String>> = _maxPriceText

    private val _minPriceText = mutableStateOf(UIState<String>(data = Constants.filterValues.minPrice?.toString()?:""))
    val minPriceText: State<UIState<String>> = _minPriceText

    fun onChangeMinPrice(minPrice: String) {
        _minPriceText.value = UIState(data = minPrice)
    }

    fun onChangeMaxPrice(maxPrice: String) {
        _maxPriceText.value = UIState(data = maxPrice)
    }


    fun getLocations(){
        viewModelScope.launch {
            val countryResult = locationRepository.getCountries()

            if (countryResult is Resource.Success) {
                _allCountries.value = countryResult.data ?: emptyList()
                _stateCountries.value = UIState(data = countryResult.data)
            } else {
                _stateCountries.value  = UIState(message = countryResult.message ?: "Ocurrió un error")
            }


            val departmentResult = locationRepository.getDepartments()
            if (departmentResult is Resource.Success) {
                _allDepartments.value = departmentResult.data ?: emptyList()
            }
            val districtResult = locationRepository.getDistricts()
            if (districtResult is Resource.Success) {
                _allDistricts.value = districtResult.data ?: emptyList()
            }

            _departmentSelected.value = UIState(data = _allDepartments.value.find { it.id == Constants.filterValues.departmentId })
            _countrySelected.value = UIState(data = _allCountries.value.find { it.id == Constants.filterValues.countryId })
            _districtSelected.value = UIState(data = _allDistricts.value.find { it.id == Constants.filterValues.districtId })
        }


    }

    fun onChangeCountry(country: Country?){
        //clear list
        _stateDepartments.value = UIState(data = emptyList())
        _stateDistricts.value = UIState(data = emptyList())
        //set selected
        _departmentSelected.value = UIState(data = null)
        _districtSelected.value = UIState(data = null)
        _countrySelected.value = UIState(data = country)

        _stateDepartments.value = UIState(data = _allDepartments.value.filter { it.countryId == country?.id })
    }

    fun onChangeDepartment(department: Department?){
        //clear list
        _stateDistricts.value = UIState(data = emptyList())
        //set selected
        _districtSelected.value = UIState(data = null)
        _departmentSelected.value = UIState(data = department)

        _stateDistricts.value = UIState(data = _allDistricts.value.filter { it.departmentId == department?.id })
    }

    fun onChangeDistrict(district: District?) {
        _districtSelected.value = UIState(data = district)
    }

    fun clearFilters(){
        Constants.filterValues.districtId = null
        Constants.filterValues.departmentId = null
        Constants.filterValues.countryId = null
        Constants.filterValues.minPrice = null
        Constants.filterValues.maxPrice = null
    }

    fun applyFilter(){
        Constants.filterValues.districtId = _districtSelected.value.data?.id
        Constants.filterValues.departmentId = _departmentSelected.value.data?.id
        Constants.filterValues.countryId = _countrySelected.value.data?.id
        Constants.filterValues.minPrice = _minPriceText.value.data?.toDoubleOrNull()
        Constants.filterValues.maxPrice = _maxPriceText.value.data?.toDoubleOrNull()
    }

}