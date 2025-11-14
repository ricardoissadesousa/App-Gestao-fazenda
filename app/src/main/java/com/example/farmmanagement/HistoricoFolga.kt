package com.example.farmmanagement

// Enum para garantir que o status seja sempre um valor válido
enum class StatusFolga {
    PENDENTE,
    APROVADA,
    REPROVADA
}

// A Data class
data class HistoricoFolga(
    val nome: String,
    val data: String,
    val status: StatusFolga
)