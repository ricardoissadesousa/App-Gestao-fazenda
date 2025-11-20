package com.example.farmmanagement.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.farmmanagement.data.model.CalendarDay
import com.example.farmmanagement.data.model.FolgaStatus
import com.example.farmmanagement.databinding.ActivityControleFolgasFuncionarioBinding
import com.example.farmmanagement.ui.adapter.CalendarAdapter
import java.util.*
import java.text.SimpleDateFormat

class ControleFolgasFuncionarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityControleFolgasFuncionarioBinding
    private lateinit var adapter: CalendarAdapter
    private val calendar = Calendar.getInstance()

    // Dados SIMULADOS (Mock Data) para testar o front-end
    private val fakeFolgasData = mapOf(
        "10/11/2025" to FolgaStatus.APROVADA,
        "24/11/2025" to FolgaStatus.PENDENTE,
        "26/11/2025" to FolgaStatus.REPROVADA
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityControleFolgasFuncionarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Estrutura de inicialização:
        setupCalendarRecyclerView()
        updateCalendarDisplay()

        // Configura navegação simples (sem lógica complexa)
        binding.btnMesAnterior.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateCalendarDisplay()
        }

        binding.btnProximoMes.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updateCalendarDisplay()
        }
    }

    private fun setupCalendarRecyclerView() {
        adapter = CalendarAdapter(emptyList()) { day ->
            if (day.status == FolgaStatus.DISPONIVEL) {
                // TODO: Abrir DialogFragment de Solicitação de Folga (Figura 6)
                Toast.makeText(this, "Solicitar folga para: ${day.fullDate}", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(
                    this,
                    "Dia ${day.fullDate} está como: ${day.status}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.recyclerCalendario.layoutManager = GridLayoutManager(this, 7)
        binding.recyclerCalendario.adapter = adapter

        // TODO: Configurar o clique no text_funcionario_selecionado para trocar o nome.
        // TODO: Configurar o clique no botão de Cancelar Solicitação.
    }

    // Lógica para desenhar o calendário e aplicar o status
    private fun updateCalendarDisplay() {
        val daysList = ArrayList<CalendarDay>()
        val tempCal = calendar.clone() as Calendar

        val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale("pt", "BR"))
        binding.textMesAno.text = monthYearFormat.format(calendar.time).replaceFirstChar { it.uppercaseChar() } // Atualiza Título

        val dayMonthYearFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // 1. Cálculo de Preenchimento (Alinhar o dia 1)
        tempCal.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK)
        val maxDaysInMonth = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (i in 1 until firstDayOfWeek) {
            daysList.add(CalendarDay(0, FolgaStatus.VAZIO))
        }

        // 2. Adiciona os dias reais do mês e verifica o status
        for (day in 1..maxDaysInMonth) {
            tempCal.set(Calendar.DAY_OF_MONTH, day)
            val fullDateString = dayMonthYearFormat.format(tempCal.time) // Ex: "20/11/2025"

            // Verifica o status usando os dados SIMULADOS
            val status = fakeFolgasData[fullDateString] ?: FolgaStatus.DISPONIVEL

            daysList.add(CalendarDay(day, status, fullDateString))
        }

        // 3. Atualiza o Adaptador para desenhar
        adapter.updateDays(daysList)
    }
}