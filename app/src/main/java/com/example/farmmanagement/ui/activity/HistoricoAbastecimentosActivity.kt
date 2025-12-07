package com.example.farmmanagement.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.farmmanagement.data.model.Abastecimento
import com.example.farmmanagement.data.repository.AbastecimentoRepository
import com.example.farmmanagement.data.source.AbastecimentoDataSource
import com.example.farmmanagement.databinding.ActivityHistoricoAbastecimentosBinding
import com.example.farmmanagement.ui.adapter.AbastecimentoAdapter
import com.example.farmmanagement.ui.viewmodel.AbastecimentoViewModel
import com.example.farmmanagement.ui.viewmodel.AbastecimentoViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class HistoricoAbastecimentosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoricoAbastecimentosBinding

    private val viewModel: AbastecimentoViewModel by viewModels {
        AbastecimentoViewModelFactory(AbastecimentoRepository(AbastecimentoDataSource()))
    }

    private val listaCompleta = mutableListOf<Abastecimento>()
    private val listaMeses = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoricoAbastecimentosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarRecyclerView()
        configurarBotoes()
        configurarSpinner()
        observarViewModel()
    }

    private fun configurarRecyclerView() {
        binding.recyclerAbastecimentos.layoutManager = LinearLayoutManager(this)
    }

    private fun configurarBotoes() {
        binding.fabAddAbastecimento.setOnClickListener {
            startActivity(Intent(this, ControleDieselActivity::class.java))
        }
    }

    private fun configurarSpinner() {
        binding.spinnerFiltroMes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (listaMeses.isNotEmpty()) {
                    val mesSelecionado = listaMeses[position]
                    filtrarListaPorMes(mesSelecionado)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun observarViewModel() {
        lifecycleScope.launch {
            viewModel.abastecimentos.collect { abastecimentos ->
                listaCompleta.clear()
                listaCompleta.addAll(abastecimentos)
                atualizarSpinnerMeses()
            }
        }

        lifecycleScope.launch {
            viewModel.loading.collect { isLoading ->
                // TODO: Adicionar ProgressBar se necessário
            }
        }
    }

    private fun atualizarSpinnerMeses() {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("pt-BR"))

        val mesesEncontrados = LinkedHashSet<String>()
        mesesEncontrados.add("Todos")

        listaCompleta.forEach { item ->
            item.data?.let { data ->
                val mesAno = sdf.format(data).replaceFirstChar { it.uppercase() }
                mesesEncontrados.add(mesAno)
            }
        }

        listaMeses.clear()
        listaMeses.addAll(mesesEncontrados)

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listaMeses)
        binding.spinnerFiltroMes.adapter = spinnerAdapter
    }

    private fun filtrarListaPorMes(mesFiltro: String) {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("pt-BR"))

        val listaFiltrada = if (mesFiltro == "Todos") {
            listaCompleta
        } else {
            listaCompleta.filter { item ->
                val mesItem = item.data?.let { sdf.format(it).replaceFirstChar { c -> c.uppercase() } }
                mesItem == mesFiltro
            }
        }

        binding.recyclerAbastecimentos.adapter = AbastecimentoAdapter(listaFiltrada)
    }
}

