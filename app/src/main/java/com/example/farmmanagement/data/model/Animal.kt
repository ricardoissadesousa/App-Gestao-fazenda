package com.example.farmmanagement.data.model

import java.io.Serializable

data class Animal(
    var id: String = "",
    val numero: String = "",
    val nome: String = "",
    val sexo: String = "",
    val nascimento: String = "",
    val mae: String = ""
) : Serializable