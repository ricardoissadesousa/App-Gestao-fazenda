package com.example.farmmanagement

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.farmmanagement.databinding.ActivityHistoricoFolgasBinding

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