package com.example.farmmanagement.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmmanagement.data.model.Usuario
import com.example.farmmanagement.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _estadoLogin = MutableStateFlow<EstadoLogin>(EstadoLogin.Inicial)
    val estadoLogin: StateFlow<EstadoLogin> = _estadoLogin.asStateFlow()

    private val _usuarioAtual = MutableStateFlow<Usuario?>(null)
    val usuarioAtual: StateFlow<Usuario?> = _usuarioAtual.asStateFlow()

    init {
        observarUsuarioAtual()
    }

    fun login(email: String, senha: String) {
        if (email.isBlank() || senha.isBlank()) {
            _estadoLogin.value = EstadoLogin.Erro("Preencha todos os campos")
            return
        }

        viewModelScope.launch {
            _estadoLogin.value = EstadoLogin.Carregando
            val resultado = repository.login(email, senha)

            resultado.fold(
                onSuccess = { usuario ->
                    _estadoLogin.value = EstadoLogin.Sucesso(usuario)
                },
                onFailure = { erro ->
                    _estadoLogin.value = EstadoLogin.Erro(
                        erro.message ?: "Erro ao fazer login"
                    )
                }
            )
        }
    }

    fun registrar(email: String, senha: String, nome: String, tipo: String) {
        if (email.isBlank() || senha.isBlank() || nome.isBlank()) {
            _estadoLogin.value = EstadoLogin.Erro("Preencha todos os campos")
            return
        }

        viewModelScope.launch {
            _estadoLogin.value = EstadoLogin.Carregando
            val resultado = repository.registrar(email, senha, nome, tipo)

            resultado.fold(
                onSuccess = { usuario ->
                    _estadoLogin.value = EstadoLogin.Sucesso(usuario)
                },
                onFailure = { erro ->
                    _estadoLogin.value = EstadoLogin.Erro(
                        erro.message ?: "Erro ao registrar"
                    )
                }
            )
        }
    }

    private fun observarUsuarioAtual() {
        viewModelScope.launch {
            repository.obterUsuarioAtual().collect { usuario ->
                _usuarioAtual.value = usuario
            }
        }
    }

    fun logout() {
        repository.logout()
        _estadoLogin.value = EstadoLogin.Inicial
        _usuarioAtual.value = null
    }

    fun limparErro() {
        if (_estadoLogin.value is EstadoLogin.Erro) {
            _estadoLogin.value = EstadoLogin.Inicial
        }
    }
}

sealed class EstadoLogin {
    object Inicial : EstadoLogin()
    object Carregando : EstadoLogin()
    data class Sucesso(val usuario: Usuario) : EstadoLogin()
    data class Erro(val mensagem: String) : EstadoLogin()
}

