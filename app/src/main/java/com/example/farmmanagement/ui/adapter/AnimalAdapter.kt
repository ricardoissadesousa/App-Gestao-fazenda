package com.example.farmmanagement.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.farmmanagement.R

data class Animal(
    val numero: String,
    val nome: String,
    val sexo: String,
    val nascimento: String,
    val mae: String
)

class AnimalAdapter(private val listaAnimais: List<Animal>) :
    RecyclerView.Adapter<AnimalAdapter.AnimalViewHolder>() {

    class AnimalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // IDs do item_animal_row.xml
        val textNumero: TextView = itemView.findViewById(R.id.text_numero)
        val textNome: TextView = itemView.findViewById(R.id.text_nome)
        val textSexo: TextView = itemView.findViewById(R.id.text_sexo)
        val textNascimento: TextView = itemView.findViewById(R.id.text_nascimento)
        val textMae: TextView = itemView.findViewById(R.id.text_mae)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_animal_row, parent, false)
        return AnimalViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
        val animal = listaAnimais[position]
        holder.textNumero.text = animal.numero
        holder.textNome.text = animal.nome
        holder.textSexo.text = animal.sexo
        holder.textNascimento.text = animal.nascimento
        holder.textMae.text = animal.mae
    }

    override fun getItemCount(): Int {
        return listaAnimais.size
    }
}