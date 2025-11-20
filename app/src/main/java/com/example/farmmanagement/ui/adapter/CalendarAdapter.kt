package com.example.farmmanagement.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.farmmanagement.R
import com.example.farmmanagement.data.model.CalendarDay
import com.example.farmmanagement.data.model.FolgaStatus

class CalendarAdapter(
    private var days: List<CalendarDay>,
    private val onDayClick: (CalendarDay) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.DayViewHolder>() {

    inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textDay: TextView = itemView.findViewById(R.id.text_day)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dia_calendario, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = days[position]

        // Se for dia vazio (espaço em branco), esconde o texto e o fundo
        if (day.status == FolgaStatus.VAZIO) {
            holder.textDay.text = ""
            holder.textDay.background = null
            return
        }

        holder.textDay.text = day.day.toString()

        // Define a COR (redonda) baseada no status
        val bgRes = when (day.status) {
            FolgaStatus.APROVADA -> R.drawable.cal_dia_aprovado
            FolgaStatus.PENDENTE -> R.drawable.cal_dia_pendente
            FolgaStatus.REPROVADA -> R.drawable.cal_dia_reprovado
            else -> 0 // Sem fundo (dia normal)
        }
        holder.textDay.setBackgroundResource(bgRes)

        // Muda a cor do texto para Branco se o fundo for verde (Aprovada)
        if (day.status == FolgaStatus.APROVADA) {
            holder.textDay.setTextColor(Color.WHITE)
        } else {
            holder.textDay.setTextColor(Color.BLACK)
        }

        holder.itemView.setOnClickListener { onDayClick(day) }
    }

    override fun getItemCount(): Int = days.size

    fun updateDays(newDays: List<CalendarDay>) {
        days = newDays
        notifyDataSetChanged()
    }
}