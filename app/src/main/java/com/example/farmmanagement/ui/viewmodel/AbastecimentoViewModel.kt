package com.example.farmmanagement.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmmanagement.data.model.Abastecimento
import com.example.farmmanagement.data.repository.AbastecimentoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AbastecimentoViewModel(private val repository: AbastecimentoRepository) : ViewModel() {

    private val _abastecimentos = MutableStateFlow<List<Abastecimento>>(emptyList())
    val abastecimentos: StateFlow<List<Abastecimento>> = _abastecimentos.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _erro = MutableStateFlow<String?>(null)
    val erro: StateFlow<String?> = _erro.asStateFlow()

    init {
        carregarAbastecimentos()
    }

    private fun carregarAbastecimentos() {
        viewModelScope.launch {
            try {
                _loading.value = true
                repository.obterAbastecimentos().collect { lista ->
                    _abastecimentos.value = lista
                }
            } catch (e: Exception) {
                _erro.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun adicionarAbastecimento(abastecimento: Abastecimento) {
        viewModelScope.launch {
            try {
                repository.adicionarAbastecimento(abastecimento)
            } catch (e: Exception) {
                _erro.value = e.message
            }
        }
    }

    fun atualizarAbastecimento(abastecimento: Abastecimento) {
        viewModelScope.launch {
            try {
                repository.atualizarAbastecimento(abastecimento)
            } catch (e: Exception) {
                _erro.value = e.message
            }
        }
    }

    fun excluirAbastecimento(abastecimento: Abastecimento) {
        viewModelScope.launch {
            try {
                repository.excluirAbastecimento(abastecimento)
            } catch (e: Exception) {
                _erro.value = e.message
            }
        }
    }
}

