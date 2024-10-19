package com.techzo.cambiazo.data.repository

import com.techzo.cambiazo.common.Resource
import com.techzo.cambiazo.data.remote.location.CountryService
import com.techzo.cambiazo.data.remote.location.DepartmentService
import com.techzo.cambiazo.data.remote.location.DistrictService
import com.techzo.cambiazo.data.remote.location.toCountry
import com.techzo.cambiazo.data.remote.location.toDepartment
import com.techzo.cambiazo.data.remote.location.toDistrict
import com.techzo.cambiazo.domain.Country
import com.techzo.cambiazo.domain.Department
import com.techzo.cambiazo.domain.District
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocationRepository(
    private val countryService: CountryService,
    private val departmentService: DepartmentService,
    private val districtService: DistrictService
) {

    suspend fun getCountries(): Resource<List<Country>> = withContext(Dispatchers.IO) {
        try {
            val response = countryService.getCountries()
            if (response.isSuccessful) {
                response.body()?.let{ countriesDto->
                    val countries = mutableListOf<Country>()
                    countriesDto.forEach{ countryDto->
                        countries.add(countryDto.toCountry())

                    }
                    return@withContext Resource.Success(data = countries)
                }
                return@withContext Resource.Error("No se encontraron paises")
            }
            return@withContext Resource.Error(response.message())
        } catch (e: Exception) {
            return@withContext Resource.Error(e.message ?: "Ocurrió un error")
        }
    }

    suspend fun getDepartments(): Resource<List<Department>> = withContext(Dispatchers.IO) {
        try {
            val response = departmentService.getDepartments()
            if (response.isSuccessful) {
                response.body()?.let{ departmentsDto->
                    val departments = mutableListOf<Department>()
                    departmentsDto.forEach{ departmentDto->
                        departments.add(departmentDto.toDepartment())

                    }
                    return@withContext Resource.Success(data = departments)
                }
                return@withContext Resource.Error("No se encontraron departamentos")
            }
            return@withContext Resource.Error(response.message())
        } catch (e: Exception) {
            return@withContext Resource.Error(e.message ?: "Ocurrió un error")
        }
    }

    suspend fun getDistricts(): Resource<List<District>> = withContext(Dispatchers.IO) {
        try {
            val response = districtService.getDistricts()
            if (response.isSuccessful) {
                response.body()?.let{ districtsDto->
                    val districts = mutableListOf<District>()
                    districtsDto.forEach{ districtDto->
                        districts.add(districtDto.toDistrict())
                    }
                    return@withContext Resource.Success(data = districts)
                }
                return@withContext Resource.Error("No se encontraron distritos")
            }
            return@withContext Resource.Error(response.message())
        } catch (e: Exception) {
            return@withContext Resource.Error(e.message ?: "Ocurrió un error")
        }
    }

    suspend fun getCountryById(id: Int): Resource<Country> = withContext(Dispatchers.IO) {
        try {
            val response = countryService.getCountryById(id)
            if (response.isSuccessful) {
                response.body()?.let{ countryDto->
                    return@withContext Resource.Success(data = countryDto.toCountry())
                }
                return@withContext Resource.Error("No se encontró el país")
            }
            return@withContext Resource.Error(response.message())
        } catch (e: Exception) {
            return@withContext Resource.Error(e.message ?: "Ocurrió un error")
        }
    }

    suspend fun getDepartmentById(id: Int): Resource<Department> = withContext(Dispatchers.IO) {
        try {
            val response = departmentService.getDepartmentById(id)
            if (response.isSuccessful) {
                response.body()?.let{ departmentDto->
                    return@withContext Resource.Success(data = departmentDto.toDepartment())
                }
                return@withContext Resource.Error("No se encontró el departamento")
            }
            return@withContext Resource.Error(response.message())
        } catch (e: Exception) {
            return@withContext Resource.Error(e.message ?: "Ocurrió un error")
        }
    }


    suspend fun getDistrictById(id: Int): Resource<District> = withContext(Dispatchers.IO) {
        try {
            val response = districtService.getDistrictById(id)
            if (response.isSuccessful) {
                response.body()?.let{ districtDto->
                    return@withContext Resource.Success(data = districtDto.toDistrict())
                }
                return@withContext Resource.Error("No se encontró el distrito")
            }
            return@withContext Resource.Error(response.message())
        } catch (e: Exception) {
            return@withContext Resource.Error(e.message ?: "Ocurrió un error")
        }
    }

}