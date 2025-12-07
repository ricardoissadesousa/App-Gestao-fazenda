package com.example.farmmanagement.data.source

import com.example.farmmanagement.data.model.ConfiguracaoPreco
import com.example.farmmanagement.data.model.ProducaoLeite
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class ProducaoLeiteDataSource {

    private val firestore = FirebaseFirestore.getInstance()
    private val collectionProducao = firestore.collection("producao_leite")
    private val collectionConfig = firestore.collection("configuracoes")

    // Observar todas as produções
    fun obterProducoes(): Flow<List<ProducaoLeite>> = callbackFlow {
        val listener = collectionProducao
            .orderBy("data", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val producoes = snapshot?.documents?.mapNotNull {
                    it.toObject(ProducaoLeite::class.java)?.apply { id = it.id }
                } ?: emptyList()
                trySend(producoes)
            }
        awaitClose { listener.remove() }
    }

    // Observar produções de um mês específico
    fun obterProducoesPorMes(mes: Int, ano: Int): Flow<List<ProducaoLeite>> = callbackFlow {
        val calendar = Calendar.getInstance()
        calendar.set(ano, mes - 1, 1, 0, 0, 0)
        val inicioMes = calendar.time

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val fimMes = calendar.time

        val listener = collectionProducao
            .whereGreaterThanOrEqualTo("data", inicioMes)
            .whereLessThanOrEqualTo("data", fimMes)
            .orderBy("data", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val producoes = snapshot?.documents?.mapNotNull {
                    it.toObject(ProducaoLeite::class.java)?.apply { id = it.id }
                } ?: emptyList()
                trySend(producoes)
            }
        awaitClose { listener.remove() }
    }

    // Adicionar produção
    suspend fun adicionarProducao(producao: ProducaoLeite) {
        collectionProducao.add(producao).await()
    }

    // Atualizar produção
    suspend fun atualizarProducao(producao: ProducaoLeite) {
        if (producao.id.isNotEmpty()) {
            collectionProducao.document(producao.id).set(producao).await()
        }
    }

    // Excluir produção
    suspend fun excluirProducao(producao: ProducaoLeite) {
        if (producao.id.isNotEmpty()) {
            collectionProducao.document(producao.id).delete().await()
        }
    }

    // Observar preço do leite
    fun obterPrecoLeite(): Flow<Double> = callbackFlow {
        val listener = collectionConfig
            .document("preco_atual")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val preco = snapshot?.getDouble("preco_leite") ?: 2.85
                trySend(preco)
            }
        awaitClose { listener.remove() }
    }

    // Atualizar preço do leite
    suspend fun atualizarPrecoLeite(novoPreco: Double) {
        val config = ConfiguracaoPreco(precoLeite = novoPreco)
        collectionConfig.document("preco_atual").set(config).await()
    }
}

