package com.example.farmmanagement.ui.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.farmmanagement.R
import com.example.farmmanagement.data.model.Abastecimento
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class AbastecimentoAdapter(private val lista: List<Abastecimento>) :
    RecyclerView.Adapter<AbastecimentoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textEquipamento: TextView = view.findViewById(R.id.text_equipamento)
        val textLitros: TextView = view.findViewById(R.id.text_litros)
        val textMotoristaData: TextView = view.findViewById(R.id.text_motorista_data)
        val textHorimetro: TextView = view.findViewById(R.id.text_horimetro)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_abastecimento, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        val context = holder.itemView.context

        //PREENCHIMENTO DOS DADOS NO CARD
        holder.textEquipamento.text = item.equipamento
        holder.textLitros.text = "%.1f L".format(item.litros)
        holder.textHorimetro.text = "Horímetro: ${item.horimetro}"

        // Formatação simples para a lista
        val sdfDia = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dataFormatada = item.data?.let { sdfDia.format(it) } ?: "--/--"

        holder.textMotoristaData.text = "${item.motorista} • $dataFormatada"

        //CLIQUE PARA VER DETALHES
        holder.itemView.setOnClickListener {

            // Configura o formatador de data  (Dia, Mês, Ano e Hora)
            val sdfDetalhes = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))

            // Força o horário de Brasília
            sdfDetalhes.timeZone = TimeZone.getTimeZone("America/Sao_Paulo")

            AlertDialog.Builder(context)
                .setTitle("Detalhes do Abastecimento")
                .setMessage(
                    "📅 Data: ${item.data?.let { sdfDetalhes.format(it) }}\n\n" +
                            "🚜 Veículo: ${item.equipamento}\n" +
                            "⛽ Litros: ${item.litros}\n" +
                            "👤 Motorista: ${item.motorista}\n" +
                            "⏱ Horímetro: ${item.horimetro}\n\n" +
                            "📝 Aplicação (Motivo):\n${item.aplicacao}"
                )
                .setPositiveButton("OK", null)
                .show()
        }
    }

    override fun getItemCount() = lista.size
}

