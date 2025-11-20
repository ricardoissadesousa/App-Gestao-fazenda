package com.example.farmmanagement.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.farmmanagement.data.model.Solicitacao
import com.example.farmmanagement.databinding.ActivitySolicitacoesPendentesBinding
import com.example.farmmanagement.ui.adapter.SolicitacoesAdapter

class SolicitacoesPendentesActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySolicitacoesPendentesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySolicitacoesPendentesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Prepara os dados (exemplo)
        val listaDeSolicitacoes = carregarSolicitacoes()

        // 2. Cria o Adapter
        val adapter = SolicitacoesAdapter(listaDeSolicitacoes)

        // 3. Conecta o Adapter ao RecyclerView
        binding.recyclerViewSolicitacoes.adapter = adapter
    }

    // Função de exemplo para carregar dados
    private fun carregarSolicitacoes(): List<Solicitacao> {
        return listOf(
            Solicitacao("1", "Christiano", "10/09/2025", "Motivo: Consulta médica"),
            Solicitacao("2", "Wanderley", "15/09/2025", "Motivo: N/A"), // Motivo não está no print, adicionei N/A
            Solicitacao("3", "Bruno", "02/10/2025", "Motivo: Assuntos pessoais")
        )
    }
}