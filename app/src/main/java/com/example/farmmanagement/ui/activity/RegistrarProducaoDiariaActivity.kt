package com.example.farmmanagement.ui.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.farmmanagement.databinding.ActivityRegistrarProducaoDiariaBinding

import java.util.*

class RegistrarProducaoDiariaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrarProducaoDiariaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarProducaoDiariaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Cria uma lista de EditTexts para facilitar o cálculo
        val tankInputs = listOf(
            binding.editTanque1,
            binding.editTanque2,
            binding.editTanque3
        )

        // 3. Adiciona um Listener para recalcular o total a cada mudança de texto
        tankInputs.forEach { editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    calculateTotal(tankInputs)
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }

        // 4. Define o comportamento do botão "Salvar Produção"
        binding.btnSalvarProducao.setOnClickListener {
            // Lógica de validação e salvamento da produção diária (Back-end)
            // Por enquanto, apenas um "TODO" para manter o foco no front-end
            // TODO: Implementar lógica de salvar a produção (e.g., enviar para o banco de dados)
        }

        // 5. Define o comportamento do botão "Adicionar Tanque" (se implementado dinamicamente)
        binding.btnAdicionarTanque.setOnClickListener {
            // TODO: Implementar lógica para adicionar dinamicamente novos campos de tanque
        }

        // 6. Inicia o cálculo do total
        calculateTotal(tankInputs)
    }

    /**
     * Calcula a soma dos valores de produção de todos os tanques.
     */
    private fun calculateTotal(tankInputs: List<EditText>) {
        var totalProduction = 0.0

        for (input in tankInputs) {
            val text = input.text.toString().trim()
            if (text.isNotEmpty()) {
                // Tenta converter o texto para Double
                totalProduction += text.toDoubleOrNull() ?: 0.0
            }
        }

        // Formata o total para exibir
        val totalText = String.format(Locale.getDefault(), "Total do Dia: %.2f Litros", totalProduction)
        binding.textTotalDoDia.text = totalText
    }
}