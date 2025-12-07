package com.example.farmmanagement.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.farmmanagement.data.repository.FolgaRepository

class FolgaViewModelFactory(
    private val repository: FolgaRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FolgaViewModel::class.java)) {
            return FolgaViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel class desconhecida")
    }
}

