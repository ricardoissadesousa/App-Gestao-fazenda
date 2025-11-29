package com.example.farmmanagement.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.farmmanagement.data.model.Animal
import com.example.farmmanagement.databinding.ActivityListaDeAnimaisBinding
import com.example.farmmanagement.ui.adapter.AnimalAdapter
import com.google.firebase.firestore.FirebaseFirestore

class ListaDeAnimaisActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListaDeAnimaisBinding
    private val db = FirebaseFirestore.getInstance()

    private val listaCompleta = mutableListOf<Animal>()
    private val mesesFiltro = arrayOf("Todos", "Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaDeAnimaisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerListaAnimais.layoutManager = LinearLayoutManager(this)

        setupFiltros()
    }

    override fun onResume() {
        super.onResume()
        carregarAnimais()
    }

    private fun setupFiltros() {
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, mesesFiltro)
        binding.spinnerFiltroMesNascimento.adapter = adapterSpinner

        binding.spinnerFiltroMesNascimento.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                aplicarFiltros()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.searchViewAnimais.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                aplicarFiltros()
                return true
            }
        })
    }

    private fun carregarAnimais() {
        db.collection("animais")
            .get()
            .addOnSuccessListener { result ->
                listaCompleta.clear()
                val novos = result.toObjects(Animal::class.java)
                listaCompleta.addAll(novos)

                aplicarFiltros()
            }
    }

    private fun aplicarFiltros() {
        val termoBusca = binding.searchViewAnimais.query.toString().trim()
        val mesSelecionadoIndex = binding.spinnerFiltroMesNascimento.selectedItemPosition

        val listaFiltrada = listaCompleta.filter { animal ->

            //FILTRO DE TEXTO
            val matchTexto = if (termoBusca.isEmpty()) {
                true
            } else {
                // Tenta converter a busca e o número do animal para Inteiros
                val buscaComoInt = termoBusca.toIntOrNull()
                val animalNumComoInt = animal.numero.toIntOrNull()

                // Lógica de Número EXATO (Ignora zeros à esquerda: 02 == 2)
                val matchNumero = if (buscaComoInt != null && animalNumComoInt != null) {
                    buscaComoInt == animalNumComoInt
                } else {
                    // Se não for número (ex: "A10"), compara o texto exato
                    animal.numero.equals(termoBusca, ignoreCase = true)
                }

                // Lógica de Nome
                val matchNome = animal.nome.contains(termoBusca, ignoreCase = true)

                // Retorna verdadeiro se bater o Número Exato OU o Nome
                matchNumero || matchNome
            }

            // Filtro de Mês
            // Se tiver busca digitada, IGNORAMOS o filtro de mês para facilitar achar o animal
            val matchMes = if (termoBusca.isNotEmpty()) {
                true
            } else {
                if (mesSelecionadoIndex == 0) true else {
                    try {
                        val partesData = animal.nascimento.split("/")
                        if (partesData.size >= 2) {
                            val mesAnimal = partesData[1].toInt()
                            mesAnimal == mesSelecionadoIndex
                        } else false
                    } catch (e: Exception) {
                        false
                    }
                }
            }

            matchTexto && matchMes
        }.sortedBy { animal ->
            animal.numero.toIntOrNull() ?: Int.MAX_VALUE
        }

        val adapter = AnimalAdapter(
            listaFiltrada,
            onEditClick = { animal -> editarAnimal(animal) },
            onDeleteClick = { animal -> confirmarExclusao(animal) }
        )
        binding.recyclerListaAnimais.adapter = adapter
    }

    private fun editarAnimal(animal: Animal) {
        val intent = Intent(this, RegistrarNascimentoActivity::class.java)
        intent.putExtra("animal_extra", animal)
        startActivity(intent)
    }

    private fun confirmarExclusao(animal: Animal) {
        AlertDialog.Builder(this)
            .setTitle("Excluir Animal")
            .setMessage("Tem certeza que deseja excluir o animal ${animal.numero}?")
            .setPositiveButton("Sim") { _, _ ->
                val docId = if (animal.id.isNotEmpty()) animal.id else animal.numero
                db.collection("animais").document(docId).delete()
                    .addOnSuccessListener {
                        carregarAnimais()
                        Toast.makeText(this, "Excluído", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Não", null)
            .show()
    }
}