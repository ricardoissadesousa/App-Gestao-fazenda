package com.example.farmmanagement.data.repository

import com.example.farmmanagement.data.model.ProducaoLeite
import com.example.farmmanagement.data.source.ProducaoLeiteDataSource
import kotlinx.coroutines.flow.Flow

class ProducaoLeiteRepository(private val dataSource: ProducaoLeiteDataSource) {

    fun obterProducoes(): Flow<List<ProducaoLeite>> =
        dataSource.obterProducoes()

    fun obterProducoesPorMes(mes: Int, ano: Int): Flow<List<ProducaoLeite>> =
        dataSource.obterProducoesPorMes(mes, ano)

    suspend fun adicionarProducao(producao: ProducaoLeite) =
        dataSource.adicionarProducao(producao)

    suspend fun atualizarProducao(producao: ProducaoLeite) =
        dataSource.atualizarProducao(producao)

    suspend fun excluirProducao(producao: ProducaoLeite) =
        dataSource.excluirProducao(producao)

    fun obterPrecoLeite(): Flow<Double> =
        dataSource.obterPrecoLeite()

    suspend fun atualizarPrecoLeite(novoPreco: Double) =
        dataSource.atualizarPrecoLeite(novoPreco)
}

