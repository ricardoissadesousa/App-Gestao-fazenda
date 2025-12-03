package com.example.farmmanagement.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmmanagement.data.model.Animal
import com.example.farmmanagement.data.repository.AnimalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AnimalViewModel(private val repository: AnimalRepository) : ViewModel() {

    private val _animais = MutableStateFlow<List<Animal>>(emptyList())
    val animais: StateFlow<List<Animal>> = _animais.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _erro = MutableStateFlow<String?>(null)
    val erro: StateFlow<String?> = _erro.asStateFlow()

    init {
        carregarAnimais()
    }

    private fun carregarAnimais() {
        viewModelScope.launch {
            try {
                _loading.value = true
                repository.obterAnimais().collect { lista ->
                    _animais.value = lista
                }
            } catch (e: Exception) {
                _erro.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    @Suppress("unused")
    fun adicionarAnimal(animal: Animal) {
        viewModelScope.launch {
            try {
                repository.adicionarAnimal(animal)
            } catch (e: Exception) {
                _erro.value = e.message
            }
        }
    }

    fun atualizarAnimal(animal: Animal) {
        viewModelScope.launch {
            try {
                repository.atualizarAnimal(animal)
            } catch (e: Exception) {
                _erro.value = e.message
            }
        }
    }

    fun excluirAnimal(animal: Animal) {
        viewModelScope.launch {
            try {
                repository.excluirAnimal(animal)
            } catch (e: Exception) {
                _erro.value = e.message
            }
        }
    }
}