package com.example.farmmanagement.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.farmmanagement.R
import com.example.farmmanagement.data.model.Animal

class AnimalAdapter(
    private val listaAnimais: List<Animal>,
    private val onEditClick: (Animal) -> Unit,
    private val onDeleteClick: (Animal) -> Unit
) : RecyclerView.Adapter<AnimalAdapter.AnimalViewHolder>() {

    class AnimalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNumero: TextView = itemView.findViewById(R.id.tv_numero_brinco)
        val tvNome: TextView = itemView.findViewById(R.id.tv_nome_animal)
        val tvDetalhes: TextView = itemView.findViewById(R.id.tv_detalhes)
        val btnEditar: ImageView = itemView.findViewById(R.id.btn_editar)
        val btnExcluir: ImageView = itemView.findViewById(R.id.btn_excluir)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_animal_row, parent, false)
        return AnimalViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
        val animal = listaAnimais[position]


        holder.tvNumero.text = animal.numero

        holder.tvNome.text = animal.nome
        holder.tvDetalhes.text = "${animal.sexo} • Mãe: ${animal.mae} • Nasc: ${animal.nascimento}"

        holder.btnEditar.setOnClickListener { onEditClick(animal) }
        holder.btnExcluir.setOnClickListener { onDeleteClick(animal) }
    }

    override fun getItemCount() = listaAnimais.size
}