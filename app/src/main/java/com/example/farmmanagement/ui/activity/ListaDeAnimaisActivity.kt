// ui/activity/ListaDeAnimaisActivity.kt
package com.example.farmmanagement.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.farmmanagement.data.repository.AnimalRepository
import com.example.farmmanagement.data.source.AnimalDataSource
import com.example.farmmanagement.databinding.ActivityListaDeAnimaisBinding
import com.example.farmmanagement.ui.adapter.AnimalAdapter
import com.example.farmmanagement.ui.viewmodel.AnimalViewModel
import com.example.farmmanagement.ui.viewmodel.AnimalViewModelFactory
import kotlinx.coroutines.launch

class ListaDeAnimaisActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListaDeAnimaisBinding

    private val viewModel: AnimalViewModel by viewModels {
        AnimalViewModelFactory(AnimalRepository(AnimalDataSource()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaDeAnimaisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarRecyclerView()
        observarViewModel()
    }

    private fun configurarRecyclerView() {
        binding.recyclerListaAnimais.layoutManager = LinearLayoutManager(this)
    }

    private fun observarViewModel() {
        lifecycleScope.launch {
            viewModel.animais.collect { animais ->
                val adapter = AnimalAdapter(
                    listaAnimais = animais,
                    onEditClick = { animal -> viewModel.atualizarAnimal(animal) },
                    onDeleteClick = { animal -> viewModel.excluirAnimal(animal) }
                )
                binding.recyclerListaAnimais.adapter = adapter
            }
        }

        lifecycleScope.launch {
            viewModel.loading.collect { isLoading ->
                // Mostrar/ocultar loading
            }
        }

        lifecycleScope.launch {
            viewModel.erro.collect { mensagem ->
                mensagem?.let {
                    // Mostrar erro
                }
            }
        }
    }
}