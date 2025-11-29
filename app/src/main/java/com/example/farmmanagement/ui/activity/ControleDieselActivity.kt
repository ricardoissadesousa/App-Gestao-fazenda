package com.example.farmmanagement.ui.activity

import android.app.AlertDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.farmmanagement.R
import com.example.farmmanagement.databinding.ActivityControleDieselBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.Date

class ControleDieselActivity : AppCompatActivity() {
    private lateinit var binding: ActivityControleDieselBinding
    private val db = FirebaseFirestore.getInstance()

    private val listaEquipamentos = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityControleDieselBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Carrega do Firestore (Banco de Dados)
        carregarEquipamentosDoFirestore()

        setupEquipmentSelection()

        binding.btnNovoVeiculo.setOnClickListener {
            mostrarDialogNovoVeiculo()
        }

        binding.btnRegistrarAbastecimento.setOnClickListener {
            registrarAbastecimento()
        }
    }

    private fun carregarEquipamentosDoFirestore() {
        val docRef = db.collection("configuracoes").document("fazenda")

        docRef.get().addOnSuccessListener { document ->
            if (document.exists() && document.contains("veiculos")) {
                // Se já existe lista no banco, usa ela
                val listaSalva = document.get("veiculos") as? List<String>
                if (listaSalva != null) {
                    listaEquipamentos.clear()
                    listaEquipamentos.addAll(listaSalva)
                }
            } else {
                // Se NÃO existe (primeira vez), pega do XML e salva no banco para criar
                val padroes = resources.getStringArray(R.array.equipamentos_array).toList()
                listaEquipamentos.addAll(padroes)

                // Salva a lista padrão no Firestore para iniciar o documento
                docRef.set(mapOf("veiculos" to padroes), SetOptions.merge())
            }
        }.addOnFailureListener {
            // Em caso de erro (sem internet), carrega o básico para não travar
            val padroes = resources.getStringArray(R.array.equipamentos_array)
            listaEquipamentos.addAll(padroes)
            Toast.makeText(this, "Modo offline: lista padrão carregada.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupEquipmentSelection() {
        binding.textEquipamentoSelecionado.setOnClickListener {
            showEquipmentSelectionDialog(binding.textEquipamentoSelecionado)
        }
    }

    private fun showEquipmentSelectionDialog(textView: TextView) {
        AlertDialog.Builder(this)
            .setTitle("Selecione o Equipamento")
            .setItems(listaEquipamentos.toTypedArray()) { dialog, which ->
                val selectedItem = listaEquipamentos[which]
                textView.text = selectedItem
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
            .create()
            .show()
    }

    private fun mostrarDialogNovoVeiculo() {
        val input = EditText(this)
        input.hint = "Nome do Veículo (Ex: Caminhão 1313)"

        AlertDialog.Builder(this)
            .setTitle("Novo Veículo")
            .setMessage("Digite o nome do equipamento:")
            .setView(input)
            .setPositiveButton("Adicionar") { _, _ ->
                val novoNome = input.text.toString().trim()
                if (novoNome.isNotEmpty()) {
                    adicionarNovoEquipamento(novoNome)
                } else {
                    Toast.makeText(this, "Nome inválido", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun adicionarNovoEquipamento(nome: String) {
        // Adiciona na lista local para ver na hora
        listaEquipamentos.add(nome)
        binding.textEquipamentoSelecionado.text = nome

        // Adiciona no Firestore (ArrayUnion evita duplicatas e é eficiente)
        db.collection("configuracoes").document("fazenda")
            .update("veiculos", FieldValue.arrayUnion(nome))
            .addOnSuccessListener {
                Toast.makeText(this, "Veículo salvo na nuvem!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Se o documento não existir, o update falha. Então tentamos criar com set.
                val novaLista = hashMapOf("veiculos" to listaEquipamentos)
                db.collection("configuracoes").document("fazenda")
                    .set(novaLista, SetOptions.merge())
            }
    }

    private fun registrarAbastecimento() {
        val nome = binding.editSeuNome.text.toString().trim()
        val quantidade = binding.editQuantidadeLitros.text.toString().toDoubleOrNull()
        val horimetro = binding.editHorimetro.text.toString().toDoubleOrNull()
        val aplicacao = binding.editAplicacao.text.toString().trim()
        val equipamento = binding.textEquipamentoSelecionado.text.toString()

        if (quantidade == null || horimetro == null) {
            Toast.makeText(this, "Verifique os campos numéricos", Toast.LENGTH_SHORT).show()
            return
        }

        val abastecimentoMap = hashMapOf(
            "data" to Date(),
            "motorista" to nome,
            "litros" to quantidade,
            "horimetro" to horimetro,
            "aplicacao" to aplicacao,
            "equipamento" to equipamento,
            "usuario_uid" to FirebaseAuth.getInstance().currentUser?.uid
        )

        db.collection("abastecimentos")
            .add(abastecimentoMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Abastecimento salvo!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao salvar.", Toast.LENGTH_SHORT).show()
            }
    }
}