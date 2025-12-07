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

        // Verifica se veio dados para edição
        animalParaEditar = intent.getSerializableExtra("animal_extra") as? Animal

        if (animalParaEditar != null) {
            preencherCamposParaEdicao(animalParaEditar!!)
        }

        binding.btnRegistrarNascimento.setOnClickListener {
            validarEIniciarSalvamento()
        }
    }

    private fun preencherCamposParaEdicao(animal: Animal) {
        binding.tvTitulo.text = "Editar Animal"
        binding.btnRegistrarNascimento.text = "Salvar Alterações"

        // No modo edição, permitimos editar tudo, mas a validação vai impedir duplicatas
        binding.etNumeroBrinco.setText(animal.numero)
        binding.etNomeBezerro.setText(animal.nome)
        binding.autocompleteSexo.setText(animal.sexo, false)
        binding.etDataNascimento.setText(animal.nascimento)
        binding.etNumeroMae.setText(animal.mae)
    }

    private fun validarEIniciarSalvamento() {
        val numeroBrinco = binding.etNumeroBrinco.text.toString().trim()
        val nomeBezerro = binding.etNomeBezerro.text.toString().trim()
        val numeroMae = binding.etNumeroMae.text.toString().trim()
        val dataNascimento = binding.etDataNascimento.text.toString().trim()

        // 1. Validações Locais Básicas
        if (numeroBrinco.isEmpty() || nomeBezerro.isEmpty() || numeroMae.isEmpty() || dataNascimento.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios.", Toast.LENGTH_SHORT).show()
            return
        }

        // Validação de formato (Apenas números para brinco e mãe)
        if (!numeroBrinco.matches("\\d+".toRegex())) {
            binding.etNumeroBrinco.error = "Digite apenas números!"
            binding.etNumeroBrinco.requestFocus()
            return
        }
        if (!numeroMae.matches("\\d+".toRegex())) {
            binding.etNumeroMae.error = "Digite apenas números!"
            binding.etNumeroMae.requestFocus()
            return
        }
        // Nome não pode ser apenas números
        if (nomeBezerro.matches("\\d+".toRegex())) {
            binding.etNomeBezerro.error = "O nome não pode ser um número!"
            binding.etNomeBezerro.requestFocus()
            return
        }

        // Bloqueia botão para evitar múltiplos cliques
        setLoading(true)

        // 2. Validações de Unicidade no Banco (Cascata)
        verificarUnicidadeNumero(numeroBrinco, nomeBezerro)
    }

    // Passo 1: Verifica se o NÚMERO já existe
    private fun verificarUnicidadeNumero(numero: String, nome: String) {
        db.collection("animais")
            .whereEqualTo("numero", numero)
            .get()
            .addOnSuccessListener { documents ->
                // Filtra: Existe algum animal com esse número QUE NÃO SEJA o que estou editando?
                val duplicado = documents.any { doc ->
                    val idDoc = doc.id

                    // Se for edição, só é duplicata se o ID for diferente do atual.
                    animalParaEditar == null || idDoc != animalParaEditar?.id
                }

                if (duplicado) {
                    binding.etNumeroBrinco.error = "Este número já existe!"

                    Toast.makeText(this, "Erro: Brinco $numero já cadastrado.", Toast.LENGTH_LONG).show()
                    setLoading(false)
                } else {
                    // Número está livre, verifica o NOME
                    verificarUnicidadeNome(nome)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro de conexão ao verificar número.", Toast.LENGTH_SHORT).show()
                setLoading(false)
            }
    }

    // Passo 2: Verifica se o NOME já existe
    private fun verificarUnicidadeNome(nome: String) {
        db.collection("animais")
            .whereEqualTo("nome", nome)
            .get()
            .addOnSuccessListener { documents ->

                val duplicado = documents.any { doc ->
                    val idDoc = doc.id
                    animalParaEditar == null || idDoc != animalParaEditar?.id
                }

                if (duplicado) {
                    binding.etNomeBezerro.error = "Este nome já existe!"
                    Toast.makeText(this, "Erro: Animal '$nome' já existe.", Toast.LENGTH_LONG).show()
                    setLoading(false)
                } else {
                    // Nome também está livre, pode SALVAR
                    salvarDadosFinais()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro de conexão ao verificar nome.", Toast.LENGTH_SHORT).show()
                setLoading(false)
            }
    }

    //  Salva ou Atualiza
    private fun salvarDadosFinais() {
        val animalMap = hashMapOf(
            "numero" to binding.etNumeroBrinco.text.toString().trim(),
            "nome" to binding.etNomeBezerro.text.toString().trim(),
            "sexo" to binding.autocompleteSexo.text.toString(),
            "nascimento" to binding.etDataNascimento.text.toString(),
            "mae" to binding.etNumeroMae.text.toString().trim()
        )

        val docRef = if (animalParaEditar != null) {
            // Atualizar existente
            db.collection("animais").document(animalParaEditar!!.id)
        } else {
            // Criar novo
            db.collection("animais").document()
        }

        // Adiciona o ID ao map para garantir consistência
        animalMap["id"] = docRef.id

        docRef.set(animalMap)
            .addOnSuccessListener {
                val msg = if (animalParaEditar == null) "Nascimento registrado!" else "Dados atualizados!"
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                finish() // Fecha a tela e volta para a lista
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao salvar dados.", Toast.LENGTH_SHORT).show()
                setLoading(false)
            }
    }

    private fun setLoading(loading: Boolean) {
        binding.btnRegistrarNascimento.isEnabled = !loading
        binding.btnRegistrarNascimento.text = if (loading) "Verificando..." else (if (animalParaEditar == null) "Registrar Nascimento" else "Salvar Alterações")
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