package com.example.farmmanagement.data.source

import com.example.farmmanagement.data.model.Animal
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AnimalDataSource {

    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("animais")

    fun obterAnimais(): Flow<List<Animal>> = callbackFlow {
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val animais = snapshot?.documents?.mapNotNull {
                it.toObject(Animal::class.java)?.apply { id = it.id }
            } ?: emptyList()
            trySend(animais)
        }
        awaitClose { listener.remove() }
    }

    suspend fun adicionarAnimal(animal: Animal) {
        collection.add(animal).await()
    }

    suspend fun atualizarAnimal(animal: Animal) {
        if (animal.id.isNotEmpty()) {
            collection.document(animal.id).set(animal).await()
        }
    }

    suspend fun excluirAnimal(animal: Animal) {
        if (animal.id.isNotEmpty()) {
            collection.document(animal.id).delete().await()
        }
    }
}