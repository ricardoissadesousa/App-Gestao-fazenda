package com.example.farmmanagement.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.farmmanagement.data.model.Solicitacao
import com.example.farmmanagement.databinding.ItemSolicitacaoPendenteBinding
import com.example.farmmanagement.ui.fragment.ReprovarSolicitacaoDialogFragment
import com.google.firebase.firestore.FirebaseFirestore

// coloquei para MutableList para poder remover itens
class SolicitacoesAdapter(private val solicitacoes: MutableList<Solicitacao>) :
    RecyclerView.Adapter<SolicitacoesAdapter.SolicitacaoViewHolder>() {

    inner class SolicitacaoViewHolder(val binding: ItemSolicitacaoPendenteBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SolicitacaoViewHolder {
        val binding = ItemSolicitacaoPendenteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SolicitacaoViewHolder(binding)
    }

    override fun getItemCount() = solicitacoes.size

    override fun onBindViewHolder(holder: SolicitacaoViewHolder, position: Int) {
        val solicitacao = solicitacoes[position]
        val context = holder.itemView.context

        with(holder.binding) {
            //Nome em cima, Email em baixo
            tvNomeFuncionario.text = "${solicitacao.nome}\n${solicitacao.email}"

            tvDataSolicitacao.text = "Data: ${solicitacao.data}"
            tvMotivo.text = solicitacao.motivo

            // APROVAR
            btnAprovar.setOnClickListener {
                FirebaseFirestore.getInstance().collection("solicitacoes_folga")
                    .document(solicitacao.id)
                    .update("status", "APROVADA")
                    .addOnSuccessListener {
                        Toast.makeText(context, "Aprovada!", Toast.LENGTH_SHORT).show()
                        removerItem(position) // Remove da tela
                    }
            }

            // REPROVAR
            btnReprovar.setOnClickListener {
                val fragmentManager = (context as? AppCompatActivity)?.supportFragmentManager
                if (fragmentManager != null) {
                    val dialog = ReprovarSolicitacaoDialogFragment.newInstance(
                        solicitacao.id,
                        solicitacao.nome,
                        solicitacao.data
                    )
                    // Configura um callback para quando o dialog fechar com sucesso

                    dialog.show(fragmentManager, ReprovarSolicitacaoDialogFragment.TAG)
                }
            }
        }
    }

    // Função auxiliar para remover item visualmente
    fun removerItem(position: Int) {
        if (position >= 0 && position < solicitacoes.size) {
            solicitacoes.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, solicitacoes.size)
        }
    }
}