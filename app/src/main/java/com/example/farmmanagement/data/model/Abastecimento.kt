package com.example.farmmanagement.data.model

import java.util.Date

data class Abastecimento(
    val id: String = "",
    val data: Date? = null,
    val motorista: String = "",
    val litros: Double = 0.0,
    val equipamento: String = "",
    val horimetro: Double = 0.0,
    val aplicacao: String = ""
)