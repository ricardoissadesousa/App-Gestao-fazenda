package com.example.farmmanagement

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.farmmanagement.databinding.ActivityRegisterBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // 1. VAMOS GUARDAR A FUNÇÃO AQUI
    private var funcaoCadastro: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar Firebase
        auth = Firebase.auth
        db = Firebase.firestore

        // 2. RECEBER A FUNÇÃO DA LOGINACTIVITY
        funcaoCadastro = intent.getStringExtra("FUNCAO_CADASTRO")

        // 3. CONFIGURAR O CAMPO DE FUNÇÃO (NÃO MAIS UM SPINNER)
        if (funcaoCadastro.isNullOrEmpty()) {
            // Se, por algum motivo, a função não foi passada,
            // é um erro. Não devemos continuar.
            Log.e("RegisterActivity", "ERRO: Tela de cadastro aberta sem uma função definida.")
            Toast.makeText(this, "Erro ao abrir cadastro, função não definida.", Toast.LENGTH_LONG).show()
            finish() // Fecha a tela e volta
        } else {
            // Se a função VEIO CORRETA (ex: "gestor"):
            // Coloca o texto no campo
            binding.autocompleteFuncao.setText(funcaoCadastro, false)
            // E desabilita o campo inteiro (layout pai) para não ser clicável
            binding.layoutFuncao.isEnabled = false
        }

        // 4. Ação do botão de criar conta
        binding.btnCriarConta.setOnClickListener {
            registrarNovoUsuario()
        }
    }

    // A função setupRoleSpinner() não é mais necessária.

    private fun registrarNovoUsuario() {
        // 5. Coletar todos os dados dos campos
        val nome = binding.etNome.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val senha = binding.etPassword.text.toString().trim()

        // 6. USAR A VARIÁVEL QUE RECEBEMOS, NÃO O SPINNER
        val funcao = this.funcaoCadastro

        // 7. Validação
        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || funcao.isNullOrEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        // 8. Criar o usuário no Firebase Authentication
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnSuccessListener { authResult ->
                Log.d("RegisterActivity", "Usuário criado no Auth com UID: ${authResult.user?.uid}")

                val uid = authResult.user!!.uid
                // Passamos a 'funcao' da nossa variável
                salvarDadosExtrasNoFirestore(uid, nome, email, funcao)
            }
            .addOnFailureListener { e ->
                Log.w("RegisterActivity", "Falha ao criar usuário no Auth", e)
                Toast.makeText(baseContext, "Falha no cadastro: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                setLoading(false)
            }
    }

    private fun salvarDadosExtrasNoFirestore(uid: String, nome: String, email: String, funcao: String) {
        // 9. Criar um "mapa" de dados para o usuário
        val userMap = hashMapOf(
            "nome" to nome,
            "email" to email,
            "funcao" to funcao // A função ("gestor" ou "funcionario") será salva corretamente
        )

        // 10. Salvar esse mapa no Firestore
        db.collection("usuarios").document(uid).set(userMap)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Dados do usuário salvos no Firestore com sucesso.")
                setLoading(false)
                Toast.makeText(this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show()

                finish()
            }
            .addOnFailureListener { e ->
                Log.w("RegisterActivity", "Falha ao salvar dados no Firestore", e)
                setLoading(false)
                Toast.makeText(baseContext, "Falha ao salvar dados: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBarRegister.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnCriarConta.isEnabled = !isLoading
    }
}