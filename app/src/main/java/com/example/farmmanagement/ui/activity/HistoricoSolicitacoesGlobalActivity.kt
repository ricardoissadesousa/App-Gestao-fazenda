package com.example.farmmanagement.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.farmmanagement.data.model.HistoricoFolga
import com.example.farmmanagement.data.model.StatusFolga
import com.example.farmmanagement.databinding.ActivityHistoricoFolgasBinding
import com.example.farmmanagement.ui.adapter.HistoricoFolgasAdapter
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoricoSolicitacoesGlobalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoricoFolgasBinding
    private val db = FirebaseFirestore.getInstance()

    private val listaCompleta = mutableListOf<HistoricoFolga>()
    private val listaMeses = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoricoFolgasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewHistorico.layoutManager = LinearLayoutManager(this)

        binding.spinnerFiltroMesFolga.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (listaMeses.isNotEmpty()) {
                    filtrarListaPorMes(listaMeses[position])
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun onResume() {
        super.onResume()
        carregarHistoricoGlobal()
    }

    private fun carregarHistoricoGlobal() {
        db.collection("solicitacoes_folga")
            .whereIn("status", listOf("APROVADA", "REPROVADA"))
            .get()
            .addOnSuccessListener { result ->
                listaCompleta.clear()

                for (doc in result) {
                    val statusStr = doc.getString("status")
                    // Pega os dados, mas remove espaços em branco extras
                    val nomeFuncionario = (doc.getString("usuario_nome") ?: "").trim()
                    val emailFuncionario = (doc.getString("usuario_email") ?: "").trim()
                    val dataSolicitada = doc.getString("data_solicitada") ?: ""

                    val statusEnum = when(statusStr) {
                        "APROVADA" -> StatusFolga.APROVADA
                        "REPROVADA" -> StatusFolga.REPROVADA
                        else -> StatusFolga.PENDENTE
                    }


                    // Se o nome for igual ao email (ou vazio), mostra só o email.
                    // Se forem diferentes, mostra: Nome (em cima) + Email (em baixo).
                    val textoExibicao = if (nomeFuncionario.equals(emailFuncionario, ignoreCase = true) || nomeFuncionario.isEmpty()) {
                        emailFuncionario
                    } else {
                        "$nomeFuncionario\n$emailFuncionario"
                    }

                    listaCompleta.add(
                        HistoricoFolga(
                            nome = textoExibicao,
                            data = dataSolicitada,
                            status = statusEnum
                        )
                    )
                }

                atualizarSpinnerOrdenado() // Chamamos a função  de ordenação

                if (listaCompleta.isNotEmpty()) {
                    binding.recyclerViewHistorico.adapter = HistoricoFolgasAdapter(listaCompleta)
                }
            }
    }

    // LÓGICA DE ORDENAÇÃO
    private fun atualizarSpinnerOrdenado() {
        val sdfEntrada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val sdfSaida = SimpleDateFormat("MMMM yyyy", Locale("pt", "BR"))

        // Set para guardar datas únicas (usamos Date para poder ordenar corretamente)
        val datasUnicas = HashSet<Date>()

        listaCompleta.forEach { item ->
            try {
                // Tenta ler a data do item (ex: 20/11/2025)
                val dataObj = sdfEntrada.parse(item.data)
                if (dataObj != null) {
                    // Normaliza para o dia 1 do mês (para agrupar dias diferentes do mesmo mês)
                    dataObj.date = 1
                    datasUnicas.add(dataObj)
                }
            } catch (e: Exception) { }
        }

        // Ordena as datas do mais recente para o mais antigo
        val datasOrdenadas = datasUnicas.sortedDescending()

        // Converte para texto (String) para colocar no Spinner
        listaMeses.clear()
        listaMeses.add("Todos")

        for (data in datasOrdenadas) {
            val mesFormatado = sdfSaida.format(data).replaceFirstChar { it.uppercase() }
            listaMeses.add(mesFormatado)
        }

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listaMeses)
        binding.spinnerFiltroMesFolga.adapter = spinnerAdapter
    }

    private fun filtrarListaPorMes(mesFiltro: String) {
        if (mesFiltro == "Todos") {
            binding.recyclerViewHistorico.adapter = HistoricoFolgasAdapter(listaCompleta)
            return
        }

        val sdfEntrada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val sdfSaida = SimpleDateFormat("MMMM yyyy", Locale("pt", "BR"))

        val listaFiltrada = listaCompleta.filter { item ->
            try {
                val dataObj = sdfEntrada.parse(item.data)
                // Formata a data do item para ver se bate com o texto do Spinner (Ex: "Novembro 2025")
                val mesItem = dataObj?.let { sdfSaida.format(it).replaceFirstChar { c -> c.uppercase() } }
                mesItem == mesFiltro
            } catch (e: Exception) {
                false
            }
        }

        binding.recyclerViewHistorico.adapter = HistoricoFolgasAdapter(listaFiltrada)
    }
}