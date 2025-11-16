package com.example.farmmanagement



import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.farmmanagement.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var funcaoLogin: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Inicializar Firebase
        auth = Firebase.auth
        db = Firebase.firestore

        // 2. Pegar a "função" que foi clicada na MainActivity
        funcaoLogin = intent.getStringExtra("FUNCAO_LOGIN")

        // 3. Atualizar o subtítulo
        if (funcaoLogin == "gestor") {
            binding.tvLoginSubtitle.text = "Acesso do Gestor"
        } else {
            binding.tvLoginSubtitle.text = "Acesso do Funcionário"
        }

        // 4. Ação do Botão
        binding.btnEntrar.setOnClickListener {
            fazerLogin()
        }
        // 5. ADICIONE ESTA NOVA AÇÃO
        binding.tvIrParaCadastro.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)

            intent.putExtra("FUNCAO_CADASTRO", funcaoLogin)

            startActivity(intent)
        }
    }

    private fun fazerLogin() {
        val email = binding.etEmail.text.toString()
        val senha = binding.etPassword.text.toString()

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        // 5. Autenticar com Firebase Auth
        auth.signInWithEmailAndPassword(email, senha)
            .addOnSuccessListener { authResult ->
                Log.d("LoginActivity", "Login Auth com sucesso.")
                // 6. Auth deu certo! Agora verificar a função no Firestore
                verificarFuncaoNoFirestore(authResult.user!!)
            }
            .addOnFailureListener { e ->
                Log.w("LoginActivity", "Falha no Login Auth", e)
                Toast.makeText(baseContext, "Email ou senha incorretos.", Toast.LENGTH_SHORT).show()
                setLoading(false)
            }
    }

    private fun verificarFuncaoNoFirestore(user: FirebaseUser) {
        // 7. Buscar o documento do usuário no Firestore usando seu UID
        db.collection("usuarios").document(user.uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // 8. Encontramos o usuário! Pegar a função real.
                    val funcaoReal = document.getString("funcao")

                    // 9. Comparar a função real com a função que ele tentou logar
                    if (funcaoReal == funcaoLogin) {
                        // SUCESSO! Acesso permitido.
                        Log.d("LoginActivity", "Acesso permitido como $funcaoReal")
                        redirecionarParaPainel(funcaoReal)
                    } else {
                        // Erro! O usuário é de outra função.
                        Log.w("LoginActivity", "Acesso negado. Função real ($funcaoReal) diferente da tentada ($funcaoLogin)")
                        Toast.makeText(this, "Acesso negado. Você não tem permissão de $funcaoLogin.", Toast.LENGTH_LONG).show()
                        auth.signOut() // Deslogar o usuário
                        setLoading(false)
                    }
                } else {
                    // Erro! Usuário autenticado mas sem registro no Firestore
                    Log.e("LoginActivity", "Usuário ${user.email} logado mas sem documento no Firestore.")
                    Toast.makeText(this, "Erro de configuração de conta.", Toast.LENGTH_SHORT).show()
                    auth.signOut()
                    setLoading(false)
                }
            }
            .addOnFailureListener { e ->
                Log.e("LoginActivity", "Erro ao buscar documento no Firestore", e)
                Toast.makeText(this, "Erro ao verificar permissões.", Toast.LENGTH_SHORT).show()
                auth.signOut()
                setLoading(false)
            }
    }

    private fun redirecionarParaPainel(funcao: String?) {
        // TODO: Substituir 'PrincipalGestorActivity' e 'PrincipalFuncionarioActivity' pelos nomes reais
        val intent = if (funcao == "gestor") {
            // Intent(this, PrincipalGestorActivity::class.java)
            Intent(this, SolicitacoesPendentesActivity::class.java) // Exemplo
        } else {
            // Intent(this, PrincipalFuncionarioActivity::class.java)
            Intent(this, RegistrarNascimentoActivity::class.java) // Exemplo
        }

        // Flags para limpar o histórico e não deixar o usuário voltar para o Login
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // Destrói a LoginActivity
    }


    private fun setLoading(isLoading: Boolean) {
        binding.progressBarLogin.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnEntrar.isEnabled = !isLoading
    }
}