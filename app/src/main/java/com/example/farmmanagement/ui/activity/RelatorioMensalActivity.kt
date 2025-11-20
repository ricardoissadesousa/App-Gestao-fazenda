package com.example.farmmanagement.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.farmmanagement.databinding.ActivityRelatorioMensalBinding
import android.app.AlertDialog
import android.widget.TextView

class RelatorioMensalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRelatorioMensalBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRelatorioMensalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadReportData()

        binding.textSelecaoMes.setOnClickListener {
            showMonthSelectionDialog(binding.textSelecaoMes)
        }
    }

    private fun loadReportData() {
        // Dados estáticos de exemplo
        binding.textTotalProduzido.text = "544.077 L"
        binding.textMediaDiaria.text = "18.136 L"
        binding.textPrecoLeite.text = "R$ 2,85"
        binding.textFaturamentoEstimado.text = "R$ 1.550.619,45"
    }

    private fun showMonthSelectionDialog(textView: TextView) {
        val meses = arrayOf("Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho")

        AlertDialog.Builder(this)
            .setTitle("Selecione o Mês")
            .setItems(meses) { dialog, which ->
                val selectedItem = meses[which]
                textView.text = selectedItem
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.cancel()
            }
            .create()
            .show()
    }
}