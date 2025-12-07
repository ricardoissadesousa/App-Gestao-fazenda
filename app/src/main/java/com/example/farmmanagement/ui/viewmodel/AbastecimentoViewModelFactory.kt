package com.example.farmmanagement.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.farmmanagement.data.repository.AbastecimentoRepository

class AbastecimentoViewModelFactory(
    private val repository: AbastecimentoRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AbastecimentoViewModel::class.java)) {
            return AbastecimentoViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel class desconhecida")
    }
}

