package com.example.farmmanagement.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.farmmanagement.data.repository.SolicitacaoRepository

class SolicitacaoViewModelFactory(
    private val repository: SolicitacaoRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SolicitacaoViewModel::class.java)) {
            return SolicitacaoViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel class desconhecida")
    }
}

