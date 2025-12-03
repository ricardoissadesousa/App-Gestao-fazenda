package com.example.farmmanagement.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmmanagement.data.model.HistoricoFolga
import com.example.farmmanagement.data.model.StatusFolga
import com.example.farmmanagement.data.repository.FolgaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FolgaViewModel(private val repository: FolgaRepository) : ViewModel() {

    private val _folgas = MutableStateFlow<List<HistoricoFolga>>(emptyList())
    val folgas: StateFlow<List<HistoricoFolga>> = _folgas.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _erro = MutableStateFlow<String?>(null)
    val erro: StateFlow<String?> = _erro.asStateFlow()

    init {
        carregarFolgas()
    }

    private fun carregarFolgas() {
        viewModelScope.launch {
            try {
                _loading.value = true
                repository.obterFolgas().collect { lista ->
                    _folgas.value = lista
                }
            } catch (e: Exception) {
                _erro.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun carregarFolgasPorUsuario(nomeUsuario: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                repository.obterFolgasPorUsuario(nomeUsuario).collect { lista ->
                    _folgas.value = lista
                }
            } catch (e: Exception) {
                _erro.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun adicionarFolga(folga: HistoricoFolga) {
        viewModelScope.launch {
            try {
                repository.adicionarFolga(folga)
            } catch (e: Exception) {
                _erro.value = e.message
            }
        }
    }

    fun atualizarStatusFolga(data: String, nome: String, novoStatus: StatusFolga) {
        viewModelScope.launch {
            try {
                repository.atualizarStatusFolga(data, nome, novoStatus)
            } catch (e: Exception) {
                _erro.value = e.message
            }
        }
    }

    fun excluirFolga(data: String, nome: String) {
        viewModelScope.launch {
            try {
                repository.excluirFolga(data, nome)
            } catch (e: Exception) {
                _erro.value = e.message
            }
        }
    }
}

