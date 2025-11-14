package com.example.farmmanagement

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.farmmanagement.databinding.ItemSolicitacaoPendenteBinding

// O adapter recebe a lista de dados
class SolicitacoesAdapter(private val solicitacoes: List<Solicitacao>) :
    RecyclerView.Adapter<SolicitacoesAdapter.SolicitacaoViewHolder>() {

    // 1. ViewHolder
    inner class SolicitacaoViewHolder(val binding: ItemSolicitacaoPendenteBinding) :
        RecyclerView.ViewHolder(binding.root)

    // 2. onCreateViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SolicitacaoViewHolder {
        val binding = ItemSolicitacaoPendenteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SolicitacaoViewHolder(binding)
    }

    // 3. getItemCount
    override fun getItemCount() = solicitacoes.size

    // 4. onBindViewHolder
    override fun onBindViewHolder(holder: SolicitacaoViewHolder, position: Int) {
        val solicitacao = solicitacoes[position]
        // (A linha 'val context' foi movida para dentro do listener)

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
                val context = holder.itemView.context // Pega o contexto

                // 1. Pega o FragmentManager (o "gerente de pop-ups") da Activity
                val fragmentManager = (context as? AppCompatActivity)?.supportFragmentManager

                if (fragmentManager != null) {
                    // 2. Cria e exibe o novo Dialog que acabamos de fazer
                    val dialog = ReprovarSolicitacaoDialogFragment.newInstance(solicitacao.nome, solicitacao.data)
                    dialog.show(fragmentManager, ReprovarSolicitacaoDialogFragment.TAG)
                }

                else {
                    Toast.makeText(context, "Erro ao abrir dialog.", Toast.LENGTH_SHORT).show()
                }
            }
         }
    }
}