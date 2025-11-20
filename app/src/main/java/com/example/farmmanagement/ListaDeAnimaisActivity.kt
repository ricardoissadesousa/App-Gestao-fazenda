package com.example.farmmanagement

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.farmmanagement.databinding.ActivityListaDeAnimaisBinding

class ListaDeAnimaisActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListaDeAnimaisBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaDeAnimaisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadAnimaisData()
    }

    private fun setupRecyclerView() {
        // ID do RecyclerView: 'recycler_lista_animais'
        binding.recyclerListaAnimais.layoutManager = LinearLayoutManager(this)
    }

    private fun loadAnimaisData() {
        // Dados de simulação
        val listaAnimais = listOf(
            Animal("20", "N/A", "Macho", "14/01/25", "RAINHA"),
            Animal("21", "N/A", "Macho", "21/01/25", "MONTANHA"),
            Animal("23", "N/A", "Femea", "14/01/25", "LUA"),
            Animal("26", "JOSEFA", "Femea", "03/01/25", "NOVICA"),
            Animal("27", "DOMESTICA", "Femea", "03/01/25", "ANIZIA"),
            Animal("28", "SINHA", "Femea", "04/01/25", "CRISTALINA")
        )

        val adapter = AnimalAdapter(listaAnimais)
        binding.recyclerListaAnimais.adapter = adapter
    }
}