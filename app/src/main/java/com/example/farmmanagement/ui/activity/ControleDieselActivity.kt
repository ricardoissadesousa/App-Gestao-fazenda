package com.example.farmmanagement.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import android.app.AlertDialog
import android.widget.TextView
import com.example.farmmanagement.R
import com.example.farmmanagement.databinding.ActivityControleDieselBinding

class ControleDieselActivity : AppCompatActivity() {
    private lateinit var binding: ActivityControleDieselBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityControleDieselBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 3. Configura o campo de Equipamento para abrir a lista de seleção (Substitui o Spinner/AutoComplete)
        setupEquipmentSelection()

        // 4. Define o comportamento do botão "Registrar Abastecimento"
        binding.btnRegistrarAbastecimento.setOnClickListener {
            registrarAbastecimento()
        }
    }

    // Função que substitui setupSpinner() e usa o TextView para abrir a seleção
    private fun setupEquipmentSelection() {
        // Encontra o TextView no binding (usamos 'text_equipamento_selecionado' no XML perfeito)
        val equipmentField = findViewById<TextView>(R.id.text_equipamento_selecionado)

        equipmentField.setOnClickListener {
            showEquipmentSelectionDialog(equipmentField)
        }
    }

    private fun showEquipmentSelectionDialog(textView: TextView) {
        val equipamentos = resources.getStringArray(R.array.equipamentos_array)

        AlertDialog.Builder(this)
            .setTitle("Selecione o Equipamento")
            .setItems(equipamentos) { dialog, which ->
                val selectedItem = equipamentos[which]
                // Atualiza o TextView com o item selecionado
                textView.text = selectedItem
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.cancel()
            }
            .create()
            .show()
    }

    private fun registrarAbastecimento() {
        // Coleta os dados dos campos
        val nome = binding.editSeuNome.text.toString().trim()
        val quantidade = binding.editQuantidadeLitros.text.toString().trim()
        val horimetro = binding.editHorimetro.text.toString().trim()
        val aplicacao = binding.editAplicacao.text.toString().trim()

        // Coleta o equipamento do TextView simulado
        val equipamento = findViewById<TextView>(R.id.text_equipamento_selecionado).text.toString()

        // TODO: Implementar lógica de validação dos campos

        // TODO: Implementar lógica de salvamento no banco de dados
    }
}