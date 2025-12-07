package com.example.farmmanagement.data.repository

import com.example.farmmanagement.data.model.Abastecimento
import com.example.farmmanagement.data.source.AbastecimentoDataSource
import kotlinx.coroutines.flow.Flow

class AbastecimentoRepository(private val dataSource: AbastecimentoDataSource) {

    fun obterAbastecimentos(): Flow<List<Abastecimento>> = dataSource.obterAbastecimentos()

    suspend fun adicionarAbastecimento(abastecimento: Abastecimento) =
        dataSource.adicionarAbastecimento(abastecimento)

    suspend fun atualizarAbastecimento(abastecimento: Abastecimento) =
        dataSource.atualizarAbastecimento(abastecimento)

    suspend fun excluirAbastecimento(abastecimento: Abastecimento) =
        dataSource.excluirAbastecimento(abastecimento)
}

