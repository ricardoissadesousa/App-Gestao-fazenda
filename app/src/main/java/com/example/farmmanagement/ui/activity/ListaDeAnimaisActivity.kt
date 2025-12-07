package com.example.farmmanagement.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.farmmanagement.data.model.Animal
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
        configurarBusca()
        configurarFiltroMes()
        observarViewModel()
    }

    private fun configurarRecyclerView() {
        binding.recyclerListaAnimais.layoutManager = LinearLayoutManager(this)
        // O Adapter é configurado dentro do observarViewModel para reagir às mudanças da lista
    }

    private fun configurarBusca() {
        binding.searchViewAnimais.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchViewAnimais.clearFocus() // Esconde o teclado
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filtrar(newText ?: "")
                return true
            }
        })
    }

    private fun configurarFiltroMes() {
        val opcoesMeses = arrayOf(
            "Todos", // Index 0
            "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            opcoesMeses
        )

        binding.spinnerFiltroMesNascimento.adapter = adapter

        binding.spinnerFiltroMesNascimento.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // position 0 = Todos, 1 = Janeiro... bate certinho com a lógica do ViewModel
                viewModel.filtrarPorMes(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun observarViewModel() {
        // Observa a lista de animais (já filtrada)
        lifecycleScope.launch {
            viewModel.animais.collect { animais ->
                val adapter = AnimalAdapter(
                    listaAnimais = animais,
                    onEditClick = { animal -> editarAnimal(animal) },
                    onDeleteClick = { animal -> confirmarExclusao(animal) }
                )
                binding.recyclerListaAnimais.adapter = adapter
            }
        }

        // Observa erros eventuais
        lifecycleScope.launch {
            viewModel.erro.collect { mensagem ->
                mensagem?.let {
                    Toast.makeText(this@ListaDeAnimaisActivity, it, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Ação do botão Editar (Lápis)
    private fun editarAnimal(animal: Animal) {
        val intent = Intent(this, RegistrarNascimentoActivity::class.java)
        // Envia o objeto Animal para a tela de cadastro preencher os campos
        intent.putExtra("animal_extra", animal)
        startActivity(intent)
    }

    // Ação do botão Excluir (Lixeira)
    private fun confirmarExclusao(animal: Animal) {
        AlertDialog.Builder(this)
            .setTitle("Excluir Animal")
            .setMessage("Tem a certeza que deseja excluir o animal nº ${animal.numero} (${animal.nome})?\nEsta ação não pode ser desfeita.")
            .setPositiveButton("Excluir") { dialog, _ ->
                viewModel.excluirAnimal(animal)
                Toast.makeText(this, "Animal excluído com sucesso.", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}