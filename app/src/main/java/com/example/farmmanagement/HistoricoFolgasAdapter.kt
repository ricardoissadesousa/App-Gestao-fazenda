package com.example.farmmanagement

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.farmmanagement.databinding.ItemHistoricoFolgaBinding

class HistoricoFolgasAdapter(private val historico: List<HistoricoFolga>) :
    RecyclerView.Adapter<HistoricoFolgasAdapter.HistoricoViewHolder>() {

    inner class HistoricoViewHolder(val binding: ItemHistoricoFolgaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoricoViewHolder {
        val binding = ItemHistoricoFolgaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoricoViewHolder(binding)
    }

    override fun getItemCount() = historico.size

    override fun onBindViewHolder(holder: HistoricoViewHolder, position: Int) {
        val item = historico[position]
        val context = holder.itemView.context

        with(holder.binding) {
            // 1. Define os textos
            tvNomeFuncionario.text = item.nome
            tvDataFolga.text = item.data

            // 2. Lógica para mudar o badge de status
            when (item.status) {
                StatusFolga.APROVADA -> {
                    tvStatus.text = "Aprovada"
                    tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_aprovada_principal))
                    tvStatus.background = ContextCompat.getDrawable(context, R.drawable.bg_status_aprovada)
                }

                StatusFolga.PENDENTE -> {
                    tvStatus.text = "Pendente"
                    tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_pendente_principal))
                    tvStatus.background = ContextCompat.getDrawable(context, R.drawable.bg_status_pendente)
                }

                StatusFolga.REPROVADA -> {
                    tvStatus.text = "Reprovada" // ou "Cancelada"
                    tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_reprovada))
                    tvStatus.background = ContextCompat.getDrawable(context, R.drawable.bg_status_reprovada)
                }
            }
        }
    }
}