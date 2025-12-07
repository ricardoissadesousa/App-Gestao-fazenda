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

    // Lista observada pela Activity (contém apenas os itens filtrados para exibição)
    private val _animais = MutableStateFlow<List<Animal>>(emptyList())
    val animais: StateFlow<List<Animal>> = _animais.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _erro = MutableStateFlow<String?>(null)
    val erro: StateFlow<String?> = _erro.asStateFlow()

    // Armazena a lista original completa vinda do banco de dados
    private var listaCompleta: List<Animal> = emptyList()

    // Variáveis para controlar o estado atual dos filtros
    private var termoBuscaAtual: String = ""
    private var mesFiltroAtual: Int = 0 // 0 = Todos, 1 = Janeiro, 2 = Fevereiro.

    init {
        carregarAnimais()
    }

    private fun carregarAnimais() {
        viewModelScope.launch {
            try {
                _loading.value = true
                repository.obterAnimais().collect { lista ->
                    listaCompleta = lista
                    // Sempre que os dados mudam no banco, reaplicamos os filtros atuais
                    aplicarFiltrosCombinados()
                }
            } catch (e: Exception) {
                _erro.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    // Chamado quando o utilizador digita na barra de pesquisa
    fun filtrar(query: String) {
        termoBuscaAtual = query
        aplicarFiltrosCombinados()
    }

    // Chamado quando o utilizador seleciona um mês no Spinner
    fun filtrarPorMes(mesIndex: Int) {
        mesFiltroAtual = mesIndex
        aplicarFiltrosCombinados()
    }

    // Lógica central que combina os dois filtros
    private fun aplicarFiltrosCombinados() {
        var listaFiltrada = listaCompleta

        // 1. Filtro por Texto (Nome ou Número do Brinco)
        if (termoBuscaAtual.isNotEmpty()) {
            listaFiltrada = listaFiltrada.filter { animal ->
                animal.nome.contains(termoBuscaAtual, ignoreCase = true) ||
                        animal.numero.contains(termoBuscaAtual, ignoreCase = true)
            }
        }

        // 2. Filtro por Mês (se não for "Todos" / index 0)
        if (mesFiltroAtual > 0) {
            listaFiltrada = listaFiltrada.filter { animal ->
                ehDoMesSelecionado(animal.nascimento, mesFiltroAtual)
            }
        }

        _animais.value = listaFiltrada
    }

    // Auxiliar para extrair o mês da string "dd/MM/yyyy"
    private fun ehDoMesSelecionado(dataString: String, mesDesejado: Int): Boolean {
        // Exemplo de data: "15/05/2025"
        val partes = dataString.split("/")
        if (partes.size >= 2) {
            val mesAnimal = partes[1].toIntOrNull() // Pega o "05" e converte para 5
            return mesAnimal == mesDesejado
        }
        return false
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