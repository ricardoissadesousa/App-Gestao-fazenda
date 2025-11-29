package com.example.farmmanagement.ui.activity

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.farmmanagement.data.model.Animal
import com.example.farmmanagement.databinding.ActivityRegistrarNascimentoBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.Locale

class RegistrarNascimentoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrarNascimentoBinding
    private val db = FirebaseFirestore.getInstance()

    private var animalParaEditar: Animal? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarNascimentoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSexoDropdown()
        setupDatePicker()

        animalParaEditar = intent.getSerializableExtra("animal_extra") as? Animal

        if (animalParaEditar != null) {
            preencherCamposParaEdicao(animalParaEditar!!)
        }

        binding.btnRegistrarNascimento.setOnClickListener {
            iniciarProcessoDeSalvamento()
        }
    }

    private fun preencherCamposParaEdicao(animal: Animal) {
        binding.tvTitulo.text = "Editar Animal"
        binding.btnRegistrarNascimento.text = "Salvar Alterações"

        binding.etNumeroBrinco.setText(animal.numero)
        binding.etNumeroBrinco.isEnabled = true

        binding.etNomeBezerro.setText(animal.nome)
        binding.autocompleteSexo.setText(animal.sexo, false)
        binding.etDataNascimento.setText(animal.nascimento)
        binding.etNumeroMae.setText(animal.mae)
    }

    private fun iniciarProcessoDeSalvamento() {
        val numeroBrinco = binding.etNumeroBrinco.text.toString().trim()
        val nomeBezerro = binding.etNomeBezerro.text.toString().trim()
        val numeroMae = binding.etNumeroMae.text.toString().trim()

        // Validação de Campos Vazios
        if (numeroBrinco.isEmpty() || nomeBezerro.isEmpty() || numeroMae.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios.", Toast.LENGTH_SHORT).show()
            return
        }

        //VALIDAÇÕES DE FORMATO

        // Validação: Brinco deve conter APENAS números
        // Se NÃO der match com dígitos (\d+), significa que tem letra ou símbolo
        if (!numeroBrinco.matches("\\d+".toRegex())) {
            binding.etNumeroBrinco.error = "Digite apenas números no brinco!"
            binding.etNumeroBrinco.requestFocus()
            return
        }

        // Validação: Mãe deve conter APENAS números
        if (!numeroMae.matches("\\d+".toRegex())) {
            binding.etNumeroMae.error = "Digite apenas números no campo Mãe!"
            binding.etNumeroMae.requestFocus()
            return
        }

        // Validação: Nome NÃO pode ser apenas números (inverso das de cima)
        if (nomeBezerro.matches("\\d+".toRegex())) {
            binding.etNomeBezerro.error = "O nome não pode ser um número!"
            binding.etNomeBezerro.requestFocus()
            return
        }

        // Validação: Nome muito curto
        if (nomeBezerro.length < 2) {
            binding.etNomeBezerro.error = "Nome muito curto."
            return
        }

        // Se passou por tudo, bloqueia botão e prossegue
        binding.btnRegistrarNascimento.isEnabled = false
        binding.btnRegistrarNascimento.text = "Verificando..."

        // LÓGICA DE DUPLICIDADE
        if (animalParaEditar != null && numeroBrinco == animalParaEditar!!.numero) {
            verificarNomeESalvar(numeroBrinco, nomeBezerro)
        } else {
            db.collection("animais")
                .whereEqualTo("numero", numeroBrinco)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        liberarBotao()
                        binding.etNumeroBrinco.error = "Este número já existe!"
                        Toast.makeText(this, "Erro: Brinco $numeroBrinco já cadastrado.", Toast.LENGTH_LONG).show()
                    } else {
                        verificarNomeESalvar(numeroBrinco, nomeBezerro)
                    }
                }
                .addOnFailureListener {
                    liberarBotao()
                    Toast.makeText(this, "Erro de conexão ao verificar número.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun verificarNomeESalvar(numero: String, nome: String) {
        if (animalParaEditar != null && nome.equals(animalParaEditar!!.nome, ignoreCase = true)) {
            salvarDadosFinais(animalParaEditar!!.id)
            return
        }

        db.collection("animais")
            .whereEqualTo("nome", nome)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    liberarBotao()
                    binding.etNomeBezerro.error = "Este nome já existe!"
                    Toast.makeText(this, "Erro: Animal '$nome' já existe.", Toast.LENGTH_LONG).show()
                } else {
                    val idParaSalvar = animalParaEditar?.id
                    salvarDadosFinais(idParaSalvar)
                }
            }
            .addOnFailureListener {
                liberarBotao()
                Toast.makeText(this, "Erro ao verificar nome.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun salvarDadosFinais(docId: String?) {
        val numeroBrinco = binding.etNumeroBrinco.text.toString().trim()
        val nomeBezerro = binding.etNomeBezerro.text.toString().trim()
        val sexo = binding.autocompleteSexo.text.toString()
        val dataNascimento = binding.etDataNascimento.text.toString()
        val numeroMae = binding.etNumeroMae.text.toString().trim()

        val animalMap = hashMapOf(
            "numero" to numeroBrinco,
            "nome" to nomeBezerro,
            "sexo" to sexo,
            "nascimento" to dataNascimento,
            "mae" to numeroMae
        )

        val docRef = if (docId != null) {
            db.collection("animais").document(docId)
        } else {
            db.collection("animais").document()
        }

        animalMap["id"] = docRef.id

        docRef.set(animalMap)
            .addOnSuccessListener {
                val msg = if (animalParaEditar == null) "Animal registrado!" else "Dados atualizados!"
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                liberarBotao()
                Toast.makeText(this, "Erro ao salvar dados finais.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun liberarBotao() {
        binding.btnRegistrarNascimento.isEnabled = true
        binding.btnRegistrarNascimento.text = if (animalParaEditar == null) "Registrar Nascimento" else "Salvar Alterações"
    }

    private fun setupSexoDropdown() {
        val sexos = listOf("Fêmea", "Macho")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, sexos)
        binding.autocompleteSexo.setAdapter(adapter)
    }

    private fun setupDatePicker() {
        binding.etDataNascimento.setOnClickListener {
            val locale = Locale("pt", "BR")
            Locale.setDefault(locale)

            val config = resources.configuration
            config.setLocale(locale)
            resources.updateConfiguration(config, resources.displayMetrics)

            val calendar = Calendar.getInstance(locale)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, day ->
                    val diaFormatado = String.format(locale, "%02d", day)
                    val mesFormatado = String.format(locale, "%02d", month + 1)
                    binding.etDataNascimento.setText("$diaFormatado/$mesFormatado/$year")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }
    }
}