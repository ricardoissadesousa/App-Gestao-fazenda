package com.example.farmmanagement.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmmanagement.data.model.ProducaoLeite
import com.example.farmmanagement.data.repository.ProducaoLeiteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProducaoLeiteViewModel(private val repository: ProducaoLeiteRepository) : ViewModel() {

    private val _producoes = MutableStateFlow<List<ProducaoLeite>>(emptyList())
    val producoes: StateFlow<List<ProducaoLeite>> = _producoes.asStateFlow()

    private val _precoLeite = MutableStateFlow(2.85)
    val precoLeite: StateFlow<Double> = _precoLeite.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _erro = MutableStateFlow<String?>(null)
    val erro: StateFlow<String?> = _erro.asStateFlow()

    private val _sucessoOperacao = MutableStateFlow(false)
    val sucessoOperacao: StateFlow<Boolean> = _sucessoOperacao.asStateFlow()

    init {
        carregarProducoes()
        carregarPrecoLeite()
    }

    private fun carregarProducoes() {
        viewModelScope.launch {
            try {
                _loading.value = true
                repository.obterProducoes().collect { lista ->
                    _producoes.value = lista
                }
            } catch (e: Exception) {
                _erro.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun carregarProducoesPorMes(mes: Int, ano: Int) {
        viewModelScope.launch {
            try {
                _loading.value = true
                repository.obterProducoesPorMes(mes, ano).collect { lista ->
                    _producoes.value = lista
                }
            } catch (e: Exception) {
                _erro.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    private fun carregarPrecoLeite() {
        viewModelScope.launch {
            try {
                repository.obterPrecoLeite().collect { preco ->
                    _precoLeite.value = preco
                }
            } catch (e: Exception) {
                _erro.value = e.message
            }
        }
    }

    fun adicionarProducao(producao: ProducaoLeite) {
        viewModelScope.launch {
            try {
                _loading.value = true
                repository.adicionarProducao(producao)
                _sucessoOperacao.value = true
            } catch (e: Exception) {
                _erro.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun atualizarProducao(producao: ProducaoLeite) {
        viewModelScope.launch {
            try {
                _loading.value = true
                repository.atualizarProducao(producao)
                _sucessoOperacao.value = true
            } catch (e: Exception) {
                _erro.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun excluirProducao(producao: ProducaoLeite) {
        viewModelScope.launch {
            try {
                repository.excluirProducao(producao)
            } catch (e: Exception) {
                _erro.value = e.message
            }
        }
    }

    fun atualizarPrecoLeite(novoPreco: Double) {
        viewModelScope.launch {
            try {
                _loading.value = true
                repository.atualizarPrecoLeite(novoPreco)
                _sucessoOperacao.value = true
            } catch (e: Exception) {
                _erro.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun limparSucesso() {
        _sucessoOperacao.value = false
    }

    fun limparErro() {
        _erro.value = null
    }
}

