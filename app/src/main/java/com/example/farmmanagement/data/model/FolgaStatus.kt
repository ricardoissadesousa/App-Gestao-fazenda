package com.example.farmmanagement.data.model

// Enum para representar os estados possíveis de um dia
enum class FolgaStatus {
    DISPONIVEL,
    PENDENTE,
    APROVADA,
    REPROVADA,
    VAZIO // Para dias de outros meses (espaços em branco)
}

data class CalendarDay(
    val day: Int,
    val status: FolgaStatus = FolgaStatus.DISPONIVEL,
    val fullDate: String = "" // Formato: "dd/MM/yyyy" para facilitar comparações
)