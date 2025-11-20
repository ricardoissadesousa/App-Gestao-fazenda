package com.example.farmmanagement.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.farmmanagement.data.model.HistoricoFolga
import com.example.farmmanagement.data.model.StatusFolga
import com.example.farmmanagement.databinding.ActivityHistoricoFolgasBinding
import com.example.farmmanagement.ui.adapter.HistoricoFolgasAdapter

class HistoricoFolgasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoricoFolgasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoricoFolgasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Prepara os dados (exemplo)
        val listaHistorico = carregarHistorico()

        // 2. Cria o Adapter
        val adapter = HistoricoFolgasAdapter(listaHistorico)

        // 3. Conecta o Adapter ao RecyclerView
        binding.recyclerViewHistorico.adapter = adapter
    }

    // Função de exemplo para carregar dados
    private fun carregarHistorico(): List<HistoricoFolga> {
        return listOf(
            HistoricoFolga("Bno", "02/10/2025", StatusFolga.PENDENTE),
            HistoricoFolga("Wanderley", "15/09/2025", StatusFolga.PENDENTE),
            HistoricoFolga("Ana", "14/09/2025", StatusFolga.REPROVADA),
            HistoricoFolga("Christiano", "10/09/2025", StatusFolga.PENDENTE),
            HistoricoFolga("Bruno", "08/09/2025", StatusFolga.APROVADA)
        )
    }
}