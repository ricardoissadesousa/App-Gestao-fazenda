package com.example.farmmanagement.data.model

import java.io.Serializable

data class Usuario(
    val id: String = "",
    val email: String = "",
    val nome: String = "",
    val tipo: TipoUsuario = TipoUsuario.FUNCIONARIO
) : Serializable

enum class TipoUsuario {
    GESTOR,
    FUNCIONARIO
}

