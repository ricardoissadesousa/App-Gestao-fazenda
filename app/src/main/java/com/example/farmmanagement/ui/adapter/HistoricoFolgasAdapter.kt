package com.example.farmmanagement.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.farmmanagement.R
import com.example.farmmanagement.data.model.HistoricoFolga
import com.example.farmmanagement.data.model.StatusFolga
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

        with(holder.binding) {
            tvNomeFuncionario.text = item.nome


            // Mostra a data no subtítulo (cinza)
            tvDataFolga.text = "Solicitado para o dia: ${item.data}"

            when (item.status) {
                StatusFolga.APROVADA -> {
                    tvStatus.text = "Aprovada"
                    tvStatus.setTextColor(Color.parseColor("#1B5E20"))
                    tvStatus.setBackgroundResource(R.drawable.bg_status_aprovada)
                }
                StatusFolga.PENDENTE -> {
                    tvStatus.text = "Pendente"
                    tvStatus.setTextColor(Color.parseColor("#F57F17"))
                    tvStatus.setBackgroundResource(R.drawable.bg_status_pendente)
                }
                StatusFolga.REPROVADA -> {
                    tvStatus.text = "Recusada"
                    tvStatus.setTextColor(Color.parseColor("#B71C1C"))
                    tvStatus.setBackgroundResource(R.drawable.bg_status_reprovada)
                }
            }
        }
    }
}