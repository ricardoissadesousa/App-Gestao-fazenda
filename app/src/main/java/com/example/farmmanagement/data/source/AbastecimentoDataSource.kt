package com.example.farmmanagement.data.source

import com.example.farmmanagement.data.model.Abastecimento
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AbastecimentoDataSource {

    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("abastecimentos")

    fun obterAbastecimentos(): Flow<List<Abastecimento>> = callbackFlow {
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val abastecimentos = snapshot?.documents?.mapNotNull {
                it.toObject(Abastecimento::class.java)
            } ?: emptyList()
            trySend(abastecimentos)
        }
        awaitClose { listener.remove() }
    }

    suspend fun adicionarAbastecimento(abastecimento: Abastecimento) {
        collection.add(abastecimento).await()
    }

    suspend fun atualizarAbastecimento(abastecimento: Abastecimento) {
        if (abastecimento.id.isNotEmpty()) {
            collection.document(abastecimento.id).set(abastecimento).await()
        }
    }

    suspend fun excluirAbastecimento(abastecimento: Abastecimento) {
        if (abastecimento.id.isNotEmpty()) {
            collection.document(abastecimento.id).delete().await()
        }
    }
}

