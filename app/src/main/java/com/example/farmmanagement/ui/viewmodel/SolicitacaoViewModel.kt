package com.example.farmmanagement.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmmanagement.data.model.Solicitacao
import com.example.farmmanagement.data.repository.SolicitacaoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SolicitacaoViewModel(private val repository: SolicitacaoRepository) : ViewModel() {

    private val _solicitacoes = MutableStateFlow<List<Solicitacao>>(emptyList())
    val solicitacoes: StateFlow<List<Solicitacao>> = _solicitacoes.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _erro = MutableStateFlow<String?>(null)
    val erro: StateFlow<String?> = _erro.asStateFlow()

    init {
        carregarSolicitacoes()
    }

    private fun carregarSolicitacoes() {
        viewModelScope.launch {
            try {
                _loading.value = true
                repository.obterSolicitacoes().collect { lista ->
                    _solicitacoes.value = lista
                }
            } catch (e: Exception) {
                _erro.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun carregarSolicitacoesPendentes() {
        viewModelScope.launch {
            try {
                _loading.value = true
                repository.obterSolicitacoesPendentes().collect { lista ->
                    _solicitacoes.value = lista
                }
            } catch (e: Exception) {
                _erro.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun adicionarSolicitacao(solicitacao: Solicitacao) {
        viewModelScope.launch {
            try {
                repository.adicionarSolicitacao(solicitacao)
            } catch (e: Exception) {
                _erro.value = e.message
            }
        }
    }

    fun aprovarSolicitacao(solicitacaoId: String) {
        viewModelScope.launch {
            try {
                repository.aprovarSolicitacao(solicitacaoId)
            } catch (e: Exception) {
                _erro.value = e.message
            }
        }
    }

    fun reprovarSolicitacao(solicitacaoId: String, motivo: String) {
        viewModelScope.launch {
            try {
                repository.reprovarSolicitacao(solicitacaoId, motivo)
            } catch (e: Exception) {
                _erro.value = e.message
            }
        }
    }

    fun excluirSolicitacao(solicitacaoId: String) {
        viewModelScope.launch {
            try {
                repository.excluirSolicitacao(solicitacaoId)
            } catch (e: Exception) {
                _erro.value = e.message
            }
        }
    }
}

