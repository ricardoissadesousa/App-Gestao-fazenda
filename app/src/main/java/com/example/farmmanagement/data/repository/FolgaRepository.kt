package com.example.farmmanagement.data.repository

import com.example.farmmanagement.data.model.HistoricoFolga
import com.example.farmmanagement.data.model.StatusFolga
import com.example.farmmanagement.data.source.FolgaDataSource
import kotlinx.coroutines.flow.Flow

class FolgaRepository(private val dataSource: FolgaDataSource) {

    fun obterFolgas(): Flow<List<HistoricoFolga>> = dataSource.obterFolgas()

    fun obterFolgasPorUsuario(nomeUsuario: String): Flow<List<HistoricoFolga>> =
        dataSource.obterFolgasPorUsuario(nomeUsuario)

    suspend fun adicionarFolga(folga: HistoricoFolga) =
        dataSource.adicionarFolga(folga)

    suspend fun atualizarStatusFolga(data: String, nome: String, novoStatus: StatusFolga) =
        dataSource.atualizarStatusFolga(data, nome, novoStatus)

    suspend fun excluirFolga(data: String, nome: String) =
        dataSource.excluirFolga(data, nome)
}

