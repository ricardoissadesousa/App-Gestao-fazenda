package com.example.farmmanagement.ui.activity

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.farmmanagement.databinding.ActivityRelatorioMensalBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.Calendar
import java.util.Locale

class RelatorioMensalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRelatorioMensalBinding
    private val db = FirebaseFirestore.getInstance()

    //Variáveis de Estado
    private var precoLeiteAtual: Double = 2.85 // Valor padrão
    private var totalLitrosCache: Double = 0.0

    private val meses = arrayOf(
        "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
        "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
    )

    private var mesSelecionadoIndex = Calendar.getInstance().get(Calendar.MONTH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRelatorioMensalBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.textSelecaoMes.text = meses[mesSelecionadoIndex]
        atualizarTextoPrecoUI()

        // Carregar o Preço do Firebase
        carregarPrecoDoFirestore()

        //Carregar Dados do Relatório
        loadReportData(mesSelecionadoIndex)

        //LISTENERS
        binding.textSelecaoMes.setOnClickListener {
            showMonthSelectionDialog(binding.textSelecaoMes)
        }

        binding.textPrecoLeite.setOnClickListener {
            showEditPriceDialog()
        }
    }

    private fun carregarPrecoDoFirestore() {
        // Busca no documento 'configuracoes/fazenda'
        db.collection("configuracoes").document("fazenda")
            .get()
            .addOnSuccessListener { document ->
                if (document.exists() && document.contains("preco_leite")) {
                    val precoSalvo = document.getDouble("preco_leite")
                    if (precoSalvo != null) {
                        precoLeiteAtual = precoSalvo
                        atualizarTextoPrecoUI()
                        // Se já tivermos o total carregado, recalcula o faturamento
                        if (totalLitrosCache > 0) recalcularFaturamento()
                    }
                }
            }
        // Se der erro ou não existir, mantém o padrão 2.85
    }

    private fun loadReportData(mesIndex: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, mesIndex)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val dataInicio = calendar.time

        calendar.add(Calendar.MONTH, 1)
        val dataFim = calendar.time

        db.collection("producao_leite")
            .whereGreaterThanOrEqualTo("data_registro", dataInicio)
            .whereLessThan("data_registro", dataFim)
            .get()
            .addOnSuccessListener { result ->
                var totalLitros = 0.0
                var diasComRegistro = 0

                for (doc in result) {
                    totalLitros += (doc.getDouble("total_litros") ?: 0.0)
                    diasComRegistro++
                }

                totalLitrosCache = totalLitros
                val media = if (diasComRegistro > 0) totalLitros / diasComRegistro else 0.0

                val localeBR = Locale("pt", "BR")
                binding.textTotalProduzido.text = String.format(localeBR, "%,.0f L", totalLitros)
                binding.textMediaDiaria.text = String.format(localeBR, "%,.0f L", media)

                recalcularFaturamento()
            }
    }

    private fun showEditPriceDialog() {
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        input.hint = "Ex: 3.10"
        input.setText(String.format(Locale.US, "%.2f", precoLeiteAtual))

        AlertDialog.Builder(this)
            .setTitle("Alterar Preço do Leite")
            .setMessage("Digite o novo valor do litro:")
            .setView(input)
            .setPositiveButton("Salvar") { dialog, _ ->
                val novoValorString = input.text.toString().replace(",", ".")
                val novoValor = novoValorString.toDoubleOrNull()

                if (novoValor != null && novoValor > 0) {
                    salvarPrecoNoFirestore(novoValor)
                } else {
                    Toast.makeText(this, "Valor inválido", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
            .show()
    }

    private fun salvarPrecoNoFirestore(novoPreco: Double) {
        precoLeiteAtual = novoPreco
        atualizarTextoPrecoUI()
        recalcularFaturamento()

        // Salva no Firestore usando merge para não apagar outros dados (como a lista de veículos)
        val dados = hashMapOf("preco_leite" to novoPreco)
        db.collection("configuracoes").document("fazenda")
            .set(dados, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "Preço salvo na nuvem!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao salvar na nuvem.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun atualizarTextoPrecoUI() {
        val localeBR = Locale("pt", "BR")
        binding.textPrecoLeite.text = String.format(localeBR, "R$ %.2f", precoLeiteAtual)
    }

    private fun recalcularFaturamento() {
        val faturamento = totalLitrosCache * precoLeiteAtual
        val localeBR = Locale("pt", "BR")
        binding.textFaturamentoEstimado.text = String.format(localeBR, "R$ %,.2f", faturamento)
    }

    private fun showMonthSelectionDialog(textView: TextView) {
        AlertDialog.Builder(this)
            .setTitle("Selecione o Mês")
            .setItems(meses) { dialog, which ->
                mesSelecionadoIndex = which
                textView.text = meses[which]
                loadReportData(mesSelecionadoIndex)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
            .create()
            .show()
    }
}