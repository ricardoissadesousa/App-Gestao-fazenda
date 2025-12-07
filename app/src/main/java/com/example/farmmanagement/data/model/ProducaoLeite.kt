package com.example.farmmanagement.data.model

import java.io.Serializable
import java.util.Date

data class ProducaoLeite(
    var id: String = "",
    val data: Date? = null,
    val dataFormatada: String = "",
    val litrosManha: Double = 0.0,
    val litrosTarde: Double = 0.0,
    val totalLitros: Double = 0.0,
    val observacoes: String = ""
) : Serializable

data class RelatorioMensal(
    val mes: String = "",
    val ano: Int = 0,
    val totalLitros: Double = 0.0,
    val precoLeite: Double = 0.0,
    val faturamento: Double = 0.0,
    val mediaLitrosDia: Double = 0.0
) : Serializable

data class ConfiguracaoPreco(
    var id: String = "preco_atual",
    val precoLeite: Double = 2.85
) : Serializable

