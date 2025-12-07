package com.example.farmmanagement.data.source

import com.example.farmmanagement.data.model.HistoricoFolga
import com.example.farmmanagement.data.model.StatusFolga
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FolgaDataSource {

    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("folgas")

    fun obterFolgas(): Flow<List<HistoricoFolga>> = callbackFlow {
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val folgas = snapshot?.documents?.mapNotNull {
                it.toObject(HistoricoFolga::class.java)
            } ?: emptyList()
            trySend(folgas)
        }
        awaitClose { listener.remove() }
    }

    fun obterFolgasPorUsuario(nomeUsuario: String): Flow<List<HistoricoFolga>> = callbackFlow {
        val listener = collection
            .whereEqualTo("nome", nomeUsuario)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val folgas = snapshot?.documents?.mapNotNull {
                    it.toObject(HistoricoFolga::class.java)
                } ?: emptyList()
                trySend(folgas)
            }
        awaitClose { listener.remove() }
    }

    suspend fun adicionarFolga(folga: HistoricoFolga) {
        collection.add(folga).await()
    }

    suspend fun atualizarStatusFolga(data: String, nome: String, novoStatus: StatusFolga) {
        val query = collection
            .whereEqualTo("data", data)
            .whereEqualTo("nome", nome)
            .get()
            .await()

        query.documents.forEach { document ->
            document.reference.update("status", novoStatus.name).await()
        }
    }

    suspend fun excluirFolga(data: String, nome: String) {
        val query = collection
            .whereEqualTo("data", data)
            .whereEqualTo("nome", nome)
            .get()
            .await()

        query.documents.forEach { document ->
            document.reference.delete().await()
        }
    }
}

