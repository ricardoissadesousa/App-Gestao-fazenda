package com.example.farmmanagement.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.farmmanagement.data.model.CalendarDay
import com.example.farmmanagement.data.model.FolgaStatus
import com.example.farmmanagement.data.model.HistoricoFolga
import com.example.farmmanagement.data.model.StatusFolga
import com.example.farmmanagement.databinding.ActivityControleFolgasFuncionarioBinding
import com.example.farmmanagement.ui.adapter.CalendarAdapter
import com.example.farmmanagement.ui.adapter.HistoricoFolgasAdapter
import com.example.farmmanagement.ui.fragment.SolicitarFolgaDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ControleFolgasFuncionarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityControleFolgasFuncionarioBinding
    private lateinit var calendarAdapter: CalendarAdapter

    private val calendar = Calendar.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityControleFolgasFuncionarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //CONFIGURAÇÃO DA TOOLBAR
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Minhas Folgas"

        //Busca o nome REAL no banco de dados para o subtítulo
        buscarNomeDoFuncionario()



        setupCalendarRecyclerView()
        setupHistoryRecyclerView()

        // Carrega os dados (Calendário + Lista)
        updateCalendarDisplay()

        binding.btnMesAnterior.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateCalendarDisplay()
        }

        binding.btnProximoMes.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updateCalendarDisplay()
        }
    }

    private fun buscarNomeDoFuncionario() {
        val user = auth.currentUser
        if (user != null) {
            // Busca na coleção 'usuarios' pelo ID do login atual
            db.collection("usuarios").document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val nomeReal = document.getString("nome") ?: "Funcionário"
                        // Atualiza o subtítulo da Toolbar
                        supportActionBar?.subtitle = "Olá, $nomeReal"
                    } else {
                        supportActionBar?.subtitle = "Olá, Funcionário"
                    }
                }
                .addOnFailureListener {
                    supportActionBar?.subtitle = "Olá, Funcionário"
                }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun setupCalendarRecyclerView() {
        calendarAdapter = CalendarAdapter(emptyList()) { day ->
            if (day.status == FolgaStatus.DISPONIVEL) {
                val dialog = SolicitarFolgaDialogFragment.newInstance(day.fullDate)
                dialog.show(supportFragmentManager, SolicitarFolgaDialogFragment.TAG)
            } else {
                val statusTexto = when(day.status) {
                    FolgaStatus.PENDENTE -> "Pendente"
                    FolgaStatus.APROVADA -> "Aprovada"
                    FolgaStatus.REPROVADA -> "Recusada"
                    else -> ""
                }
                Toast.makeText(this, "Dia ${day.day}: Solicitação $statusTexto", Toast.LENGTH_SHORT).show()
            }
        }

        binding.recyclerCalendario.layoutManager = GridLayoutManager(this, 7)
        binding.recyclerCalendario.adapter = calendarAdapter
    }

    private fun setupHistoryRecyclerView() {
        binding.recyclerHistoricoPessoal.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()
        updateCalendarDisplay()
    }

    private fun updateCalendarDisplay() {
        val user = auth.currentUser ?: return

        val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale("pt", "BR"))
        binding.textMesAno.text = monthYearFormat.format(calendar.time).replaceFirstChar { it.uppercase() }

        // Busca TODAS as solicitações
        db.collection("solicitacoes_folga")
            .whereEqualTo("usuario_id", user.uid)
            .get()
            .addOnSuccessListener { documents ->

                val folgasMap = mutableMapOf<String, FolgaStatus>()
                val listaHistorico = ArrayList<HistoricoFolga>()

                // Formatador para converter a data string em data real (para ordenar)
                val sdfOrdenacao = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                for (doc in documents) {
                    val dataSolicitada = doc.getString("data_solicitada") ?: continue
                    val statusStr = doc.getString("status")

                    // Mapeia para o calendário
                    val statusEnum = when(statusStr) {
                        "APROVADA" -> FolgaStatus.APROVADA
                        "REPROVADA" -> FolgaStatus.REPROVADA
                        "PENDENTE" -> FolgaStatus.PENDENTE
                        else -> FolgaStatus.DISPONIVEL
                    }
                    folgasMap[dataSolicitada] = statusEnum

                    // Mapeia para a lista de baixo
                    val statusHistorico = when(statusStr) {
                        "APROVADA" -> StatusFolga.APROVADA
                        "REPROVADA" -> StatusFolga.REPROVADA
                        else -> StatusFolga.PENDENTE
                    }

                    listaHistorico.add(
                        HistoricoFolga(
                            //Título genérico, já que a data aparece embaixo
                            nome = "Solicitação de Folga",
                            data = dataSolicitada,
                            status = statusHistorico
                        )
                    )
                }

                // Renderiza o Calendário
                renderizarCalendario(folgasMap)

                // Ordena a lista da mais recente para a mais antiga
                // Converte a string "dd/MM/yyyy" em Date para comparar corretamente
                listaHistorico.sortByDescending {
                    try {
                        sdfOrdenacao.parse(it.data)
                    } catch (e: Exception) {
                        null
                    }
                }

                // Renderiza a Lista de Baixo
                binding.recyclerHistoricoPessoal.adapter = HistoricoFolgasAdapter(listaHistorico)
            }
            .addOnFailureListener {
                renderizarCalendario(emptyMap())
                Toast.makeText(this, "Erro ao carregar dados.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun renderizarCalendario(folgasMap: Map<String, FolgaStatus>) {
        val daysList = ArrayList<CalendarDay>()
        val tempCal = calendar.clone() as Calendar

        tempCal.set(Calendar.DAY_OF_MONTH, 1)

        val firstDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK)
        val maxDaysInMonth = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val dayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        for (i in 1 until firstDayOfWeek) {
            daysList.add(CalendarDay(0, FolgaStatus.VAZIO))
        }

        for (day in 1..maxDaysInMonth) {
            tempCal.set(Calendar.DAY_OF_MONTH, day)
            val fullDate = dayFormat.format(tempCal.time)

            val status = folgasMap[fullDate] ?: FolgaStatus.DISPONIVEL
            daysList.add(CalendarDay(day, status, fullDate))
        }

        calendarAdapter.updateDays(daysList)
    }
}