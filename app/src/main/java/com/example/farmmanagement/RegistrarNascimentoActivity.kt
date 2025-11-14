package com.example.farmmanagement

//noinspection SuspiciousImport
import android.R
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.farmmanagement.databinding.ActivityRegistrarNascimentoBinding
import java.util.Calendar

class RegistrarNascimentoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrarNascimentoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarNascimentoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Configurar o Dropdown (Spinner) de Sexo
        setupSexoDropdown()

        // 2. Configurar o Seletor de Data (DatePicker)
        setupDatePicker()

        // 3. Configurar o clique do botão
        binding.btnRegistrarNascimento.setOnClickListener {
            registrarNascimento()
        }
    }

    private fun setupSexoDropdown() {
        val sexos = listOf("Fêmea", "Macho")
        val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, sexos)
        binding.autocompleteSexo.setAdapter(adapter)
    }

    private fun setupDatePicker() {
        binding.etDataNascimento.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    // O mês retorna de 0-11, então somamos 1
                    val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    binding.etDataNascimento.setText(date)
                },
                year, month, day
            )
            datePickerDialog.show()
        }
    }

    private fun registrarNascimento() {
        // Coletar os dados dos campos
        val numeroBrinco = binding.etNumeroBrinco.text.toString()
        val nomeBezerro = binding.etNomeBezerro.text.toString()
        val sexo = binding.autocompleteSexo.text.toString()
        val dataNascimento = binding.etDataNascimento.text.toString()
        val numeroMae = binding.etNumeroMae.text.toString()

        // TODO: Adicionar lógica de validação (verificar se campos estão vazios)

        // Lógica para salvar os dados (no banco de dados, API, etc.)
        // ...

        // Feedback para o usuário
        Toast.makeText(this, "Nascimento registrado!", Toast.LENGTH_SHORT).show()

        // Opcional: fechar a tela após o registro
        // finish()
    }
}