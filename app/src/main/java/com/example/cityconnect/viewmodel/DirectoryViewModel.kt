package com.example.cityconnect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.example.cityconnect.model.repositories.PlaceRepository
import com.example.cityconnect.model.schemas.Place

class DirectoryViewModel(
    private val repo: PlaceRepository = PlaceRepository(),
) : ViewModel() {

    private val _selectedCategory = MutableLiveData("Restaurants")
    val selectedCategory: LiveData<String> = _selectedCategory

    val places: LiveData<List<Place>> = _selectedCategory.switchMap { label: String ->
        repo.observePlaces(label)
    }

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    fun setCategory(label: String) {
        if (_selectedCategory.value != label) {
            _selectedCategory.value = label
        }
    }

    fun refresh() {
        val label = _selectedCategory.value ?: "Restaurants"
        _loading.value = true
        _error.value = null

        repo.refreshPlaces(label) { result ->
            _loading.postValue(false)
            result.onFailure { e -> _error.postValue(e.message ?: "Refresh failed") }
        }
    }
}
