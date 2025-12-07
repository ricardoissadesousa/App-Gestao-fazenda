package com.example.farmmanagement.ui.activity

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.farmmanagement.databinding.ActivityRegistrarProducaoDiariaBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import java.util.Locale

class RegistrarProducaoDiariaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrarProducaoDiariaBinding
    private val db = FirebaseFirestore.getInstance()

    // 1. Lista dinâmica para controlar TODOS os campos (os 3 iniciais + os novos)
    private val listaDeTanques = mutableListOf<EditText>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarProducaoDiariaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Adiciona os 3 tanques que já existem no XML para a nossa lista de controle
        listaDeTanques.add(binding.editTanque1)
        listaDeTanques.add(binding.editTanque2)
        listaDeTanques.add(binding.editTanque3)

        // 3. Configura o cálculo automático para os tanques iniciais
        configurarListenerDeCalculo(binding.editTanque1)
        configurarListenerDeCalculo(binding.editTanque2)
        configurarListenerDeCalculo(binding.editTanque3)

        // 4. Lógica do Botão "+ Adicionar Tanque"
        binding.btnAdicionarTanque.setOnClickListener {
            criarNovoCampoDeTanque()
        }

        // 5. Configura o botão de Salvar
        binding.btnSalvarProducao.setOnClickListener {
            salvarProducaoNoFirestore()
        }
    }



    private fun criarNovoCampoDeTanque() {
        //  Cria o Layout do campo (TextInputLayout) para manter o estilo visual
        val novoLayout = TextInputLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 48)
            }
            hint = "Tanque ${listaDeTanques.size + 1} (Litros)"
            boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE
            setBoxCornerRadii(20f, 20f, 20f, 20f) // Arredondamento igual ao XML
        }

        //  Cria o campo de digitação (TextInputEditText)
        val novoEdit = TextInputEditText(novoLayout.context).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        }


        novoLayout.addView(novoEdit)

        //  Adiciona na tela (Visualmente)
        // Pega o layout pai do botão para inserir o novo campo LOGO ACIMA do botão
        val parentLayout = binding.btnAdicionarTanque.parent as LinearLayout
        val indexBotao = parentLayout.indexOfChild(binding.btnAdicionarTanque)
        parentLayout.addView(novoLayout, indexBotao)

        //  Adiciona na nossa lista de controle e coloca o listener de cálculo
        listaDeTanques.add(novoEdit)
        configurarListenerDeCalculo(novoEdit)
    }

    // Adiciona o ouvinte que recalcula o total sempre que digita algo
    private fun configurarListenerDeCalculo(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calculateTotal()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun calculateTotal() {
        var totalProduction = 0.0

        // Percorre a lista dinâmica (seja ela de 3 ou 20 tanques) e soma tudo
        for (input in listaDeTanques) {
            val text = input.text.toString().trim()
            if (text.isNotEmpty()) {
                totalProduction += text.toDoubleOrNull() ?: 0.0
            }
        }

        val totalText = String.format(Locale.getDefault(), "Total do Dia: %.2f Litros", totalProduction)
        binding.textTotalDoDia.text = totalText
    }

    private fun salvarProducaoNoFirestore() {
        var total = 0.0
        val mapTanques = hashMapOf<String, Any>()

        // Percorre a lista
        // Vai gerar: "tanque1": 100, "tanque2": 50, "tanque3": 20, "tanque4": 10
        listaDeTanques.forEachIndexed { index, input ->
            val valor = input.text.toString().toDoubleOrNull() ?: 0.0
            total += valor
            mapTanques["tanque${index + 1}"] = valor
        }

        if (total == 0.0) {
            Toast.makeText(this, "Insira valor em pelo menos um tanque", Toast.LENGTH_SHORT).show()
            return
        }

        // Adiciona os metadados finais
        mapTanques["data_registro"] = Date()
        mapTanques["total_litros"] = total
        mapTanques["usuario_id"] = FirebaseAuth.getInstance().currentUser?.uid ?: "anonimo"

        db.collection("producao_leite")
            .add(mapTanques)
            .addOnSuccessListener {
                Toast.makeText(this, "Produção salva com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao salvar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}