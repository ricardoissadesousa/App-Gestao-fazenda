package com.example.farmmanagement.ui.activity

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.farmmanagement.databinding.ActivityRelatorioMensalBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.Locale

class RelatorioMensalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRelatorioMensalBinding
    private val db = FirebaseFirestore.getInstance()

    // Variáveis de Estado
    private var precoLeiteAtual: Double = 2.85 // Valor padrão inicial
    private var totalLitrosCache: Double = 0.0 // Guarda o total para recalcular sem ir no banco

    private val meses = arrayOf(
        "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
        "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
    )

    private var mesSelecionadoIndex = Calendar.getInstance().get(Calendar.MONTH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRelatorioMensalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Recuperar o preço salvo na memória do celular (SharedPreferences)
        val sharedPref = getSharedPreferences("ConfiguracoesFazenda", Context.MODE_PRIVATE)
        // Se não tiver nada salvo, usa 2.85f
        precoLeiteAtual = sharedPref.getFloat("preco_leite", 2.85f).toDouble()

        // Define textos iniciais
        binding.textSelecaoMes.text = meses[mesSelecionadoIndex]
        atualizarTextoPrecoUI() // Mostra o preço recuperado

        // Carrega dados
        loadReportData(mesSelecionadoIndex)

        // LISTENERS (Cliques)

        //  Trocar Mês
        binding.textSelecaoMes.setOnClickListener {
            showMonthSelectionDialog(binding.textSelecaoMes)
        }

        // B. Editar Preço
        binding.textPrecoLeite.setOnClickListener {
            showEditPriceDialog()
        }
    }

    private fun loadReportData(mesIndex: Int) {
        // Cálculo das datas
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

                // Salva no CACHE para podermos usar quando editar o preço
                totalLitrosCache = totalLitros

                // Cálculos
                val media = if (diasComRegistro > 0) totalLitros / diasComRegistro else 0.0

                // UI - ATUALIZADA COM PONTOS DE MILHAR
                val localeBR = Locale("pt", "BR")

                // Note o "%,.0f": a vírgula adiciona o separador de milhar (ponto)
                binding.textTotalProduzido.text = String.format(localeBR, "%,.0f L", totalLitros)
                binding.textMediaDiaria.text = String.format(localeBR, "%,.0f L", media)

                // Recalcula o faturamento com o preço atual
                recalcularFaturamento()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Função que abre o popup para digitar o preço
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
                // Troca vírgula por ponto caso o teclado do usuário seja BR
                val novoValorString = input.text.toString().replace(",", ".")
                val novoValor = novoValorString.toDoubleOrNull()

                if (novoValor != null && novoValor > 0) {
                    // 1. Atualiza variável
                    precoLeiteAtual = novoValor

                    // 2. Salva
                    val sharedPref = getSharedPreferences("ConfiguracoesFazenda", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putFloat("preco_leite", novoValor.toFloat())
                        apply()
                    }

                    // 3. Atualiza a tela
                    atualizarTextoPrecoUI()
                    recalcularFaturamento()

                    Toast.makeText(this, "Preço atualizado!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Valor inválido", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
            .show()
    }

    private fun atualizarTextoPrecoUI() {
        val localeBR = Locale("pt", "BR")
        binding.textPrecoLeite.text = String.format(localeBR, "R$ %.2f", precoLeiteAtual)
    }

    // Recalcula o faturamento instantaneamente usando o Cache + Preço Novo
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