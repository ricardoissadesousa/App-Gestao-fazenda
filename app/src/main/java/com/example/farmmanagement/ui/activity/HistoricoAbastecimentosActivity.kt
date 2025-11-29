package com.example.farmmanagement.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.farmmanagement.data.model.Abastecimento
import com.example.farmmanagement.databinding.ActivityHistoricoAbastecimentosBinding
import com.example.farmmanagement.ui.adapter.AbastecimentoAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Locale

class HistoricoAbastecimentosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoricoAbastecimentosBinding
    private val db = FirebaseFirestore.getInstance()

    // Listas para controle
    private val listaCompleta = mutableListOf<Abastecimento>() // Guarda tudo que veio do banco
    private val listaMeses = mutableListOf<String>() // Lista para o Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoricoAbastecimentosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerAbastecimentos.layoutManager = LinearLayoutManager(this)

        // Botão para adicionar novo
        binding.fabAddAbastecimento.setOnClickListener {
            startActivity(Intent(this, ControleDieselActivity::class.java))
        }

        // Configura o ouvinte do Spinner
        binding.spinnerFiltroMes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Evita crash se a lista estiver vazia
                if (listaMeses.isNotEmpty()) {
                    val mesSelecionado = listaMeses[position]
                    filtrarListaPorMes(mesSelecionado)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun onResume() {
        super.onResume()
        carregarDadosDoBanco()
    }

    private fun carregarDadosDoBanco() {
        db.collection("abastecimentos")
            .orderBy("data", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                listaCompleta.clear()
                val novosItens = result.toObjects(Abastecimento::class.java)
                listaCompleta.addAll(novosItens)

                // Atualiza o Spinner com os meses disponíveis nos dados
                atualizarSpinnerMeses()


            }
    }

    private fun atualizarSpinnerMeses() {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale("pt", "BR"))

        val mesesEncontrados = LinkedHashSet<String>() // LinkedHashSet mantém a ordem e evita duplicatas
        mesesEncontrados.add("Todos")

        // Varre a lista completa pegando os meses únicos
        listaCompleta.forEach { item ->
            item.data?.let { data ->
                val mesAno = sdf.format(data).replaceFirstChar { it.uppercase() }
                mesesEncontrados.add(mesAno)
            }
        }

        listaMeses.clear()
        listaMeses.addAll(mesesEncontrados)

        // Cria e define o adaptador para o Spinner
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listaMeses)
        binding.spinnerFiltroMes.adapter = spinnerAdapter
    }

    private fun filtrarListaPorMes(mesFiltro: String) {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale("pt", "BR"))

        val listaFiltrada = if (mesFiltro == "Todos") {
            listaCompleta // Se for todos, mostra tudo
        } else {
            // Filtra apenas os itens que batem com o mês selecionado
            listaCompleta.filter { item ->
                val mesItem = item.data?.let { sdf.format(it).replaceFirstChar { c -> c.uppercase() } }
                mesItem == mesFiltro
            }
        }

        // Atualiza o RecyclerView com a lista filtrada
        binding.recyclerAbastecimentos.adapter = AbastecimentoAdapter(listaFiltrada)
    }
}