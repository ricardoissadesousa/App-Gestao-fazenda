package com.example.farmmanagement.data.repository

import com.example.farmmanagement.data.model.Usuario
import com.example.farmmanagement.data.source.AuthDataSource
import kotlinx.coroutines.flow.Flow

class AuthRepository(private val dataSource: AuthDataSource) {

    suspend fun login(email: String, senha: String): Result<Usuario> =
        dataSource.login(email, senha)

    suspend fun registrar(email: String, senha: String, nome: String, tipo: String): Result<Usuario> =
        dataSource.registrar(email, senha, nome, tipo)

    fun obterUsuarioAtual(): Flow<Usuario?> =
        dataSource.obterUsuarioAtual()

    fun logout() = dataSource.logout()

    fun isUsuarioLogado(): Boolean = dataSource.isUsuarioLogado()
}

