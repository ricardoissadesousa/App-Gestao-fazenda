package com.example.farmmanagement

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.farmmanagement.databinding.ItemSolicitacaoPendenteBinding

// O adapter recebe a lista de dados
class SolicitacoesAdapter(private val solicitacoes: List<Solicitacao>) :
    RecyclerView.Adapter<SolicitacoesAdapter.SolicitacaoViewHolder>() {

    // 1. ViewHolder: Guarda as referências para os componentes de layout (TextViews, etc)
    inner class SolicitacaoViewHolder(val binding: ItemSolicitacaoPendenteBinding) :
        RecyclerView.ViewHolder(binding.root)

    // 2. onCreateViewHolder: Cria um novo "item" (card) inflando o layout XML
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SolicitacaoViewHolder {
        val binding = ItemSolicitacaoPendenteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SolicitacaoViewHolder(binding)
    }

    // 3. getItemCount: Informa ao RecyclerView quantos itens existem na lista
    override fun getItemCount() = solicitacoes.size

    // 4. onBindViewHolder: Pega os dados de um item e os exibe no layout
    override fun onBindViewHolder(holder: SolicitacaoViewHolder, position: Int) {
        val solicitacao = solicitacoes[position]

        with(holder.binding) {
            tvNomeFuncionario.text = solicitacao.nome
            tvDataSolicitacao.text = solicitacao.data
            tvMotivo.text = solicitacao.motivo

            // Ações dos botões
            btnAprovar.setOnClickListener {
                // TODO: Adicionar lógica real de aprovação
                Toast.makeText(holder.itemView.context, "Aprovado: ${solicitacao.nome}", Toast.LENGTH_SHORT).show()
                // Aqui você pode, por exemplo, remover o item da lista ou notificar a Activity
            }

            btnReprovar.setOnClickListener {
                // TODO: Adicionar lógica real de reprovação
                Toast.makeText(holder.itemView.context, "Reprovado: ${solicitacao.nome}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}