package com.example.farmmanagement.data.source

import com.example.farmmanagement.data.model.Solicitacao
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class SolicitacaoDataSource {

    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("solicitacoes")

    fun obterSolicitacoes(): Flow<List<Solicitacao>> = callbackFlow {
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val solicitacoes = snapshot?.documents?.mapNotNull {
                it.toObject(Solicitacao::class.java)
            } ?: emptyList()
            trySend(solicitacoes)
        }
        awaitClose { listener.remove() }
    }

    fun obterSolicitacoesPendentes(): Flow<List<Solicitacao>> = callbackFlow {
        val listener = collection
            .whereEqualTo("status", "PENDENTE")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val solicitacoes = snapshot?.documents?.mapNotNull {
                    it.toObject(Solicitacao::class.java)
                } ?: emptyList()
                trySend(solicitacoes)
            }
        awaitClose { listener.remove() }
    }

    suspend fun adicionarSolicitacao(solicitacao: Solicitacao) {
        collection.add(solicitacao).await()
    }

    suspend fun aprovarSolicitacao(solicitacaoId: String) {
        if (solicitacaoId.isNotEmpty()) {
            collection.document(solicitacaoId).update("status", "APROVADA").await()
        }
    }

    suspend fun reprovarSolicitacao(solicitacaoId: String, motivo: String) {
        if (solicitacaoId.isNotEmpty()) {
            collection.document(solicitacaoId).update(
                mapOf(
                    "status" to "REPROVADA",
                    "motivoReprovacao" to motivo
                )
            ).await()
        }
    }

    suspend fun excluirSolicitacao(solicitacaoId: String) {
        if (solicitacaoId.isNotEmpty()) {
            collection.document(solicitacaoId).delete().await()
        }
    }
}

