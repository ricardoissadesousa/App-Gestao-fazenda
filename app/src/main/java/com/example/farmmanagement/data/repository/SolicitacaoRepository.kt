package com.example.farmmanagement.data.repository

import com.example.farmmanagement.data.model.Solicitacao
import com.example.farmmanagement.data.source.SolicitacaoDataSource
import kotlinx.coroutines.flow.Flow

class SolicitacaoRepository(private val dataSource: SolicitacaoDataSource) {

    fun obterSolicitacoes(): Flow<List<Solicitacao>> = dataSource.obterSolicitacoes()

    fun obterSolicitacoesPendentes(): Flow<List<Solicitacao>> =
        dataSource.obterSolicitacoesPendentes()

    suspend fun adicionarSolicitacao(solicitacao: Solicitacao) =
        dataSource.adicionarSolicitacao(solicitacao)

    suspend fun aprovarSolicitacao(solicitacaoId: String) =
        dataSource.aprovarSolicitacao(solicitacaoId)

    suspend fun reprovarSolicitacao(solicitacaoId: String, motivo: String) =
        dataSource.reprovarSolicitacao(solicitacaoId, motivo)

    suspend fun excluirSolicitacao(solicitacaoId: String) =
        dataSource.excluirSolicitacao(solicitacaoId)
}

