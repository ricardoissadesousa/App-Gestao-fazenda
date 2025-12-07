
package com.example.farmmanagement.data.repository

import com.example.farmmanagement.data.model.Animal
import com.example.farmmanagement.data.source.AnimalDataSource
import kotlinx.coroutines.flow.Flow

class AnimalRepository(private val dataSource: AnimalDataSource) {

    fun obterAnimais(): Flow<List<Animal>> = dataSource.obterAnimais()

    suspend fun adicionarAnimal(animal: Animal) = dataSource.adicionarAnimal(animal)

    suspend fun atualizarAnimal(animal: Animal) = dataSource.atualizarAnimal(animal)

    suspend fun excluirAnimal(animal: Animal) = dataSource.excluirAnimal(animal)
}