package com.example.farmmanagement.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.farmmanagement.data.model.TipoUsuario
import com.example.farmmanagement.data.repository.AuthRepository
import com.example.farmmanagement.data.source.AuthDataSource
import com.example.farmmanagement.databinding.ActivityLoginBinding
import com.example.farmmanagement.ui.viewmodel.EstadoLogin
import com.example.farmmanagement.ui.viewmodel.LoginViewModel
import com.example.farmmanagement.ui.viewmodel.LoginViewModelFactory
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var funcaoLogin: String? = null

    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(AuthRepository(AuthDataSource()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        funcaoLogin = intent.getStringExtra("FUNCAO_LOGIN")

        if (funcaoLogin == "gestor") {
            binding.tvLoginSubtitle.text = "Acesso do Gestor"
        } else {
            binding.tvLoginSubtitle.text = "Acesso do Funcionário"
        }

        configurarBotoes()
        observarEstadoLogin()
    }

    private fun configurarBotoes() {
        binding.btnEntrar.setOnClickListener {
            fazerLogin()
        }

        binding.tvIrParaCadastro.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.putExtra("FUNCAO_CADASTRO", funcaoLogin)
            startActivity(intent)
        }
    }

    private fun fazerLogin() {
        val email = binding.etEmail.text.toString()
        val senha = binding.etPassword.text.toString()

        viewModel.login(email, senha)
    }

    private fun observarEstadoLogin() {
        lifecycleScope.launch {
            viewModel.estadoLogin.collect { estado ->
                when (estado) {
                    is EstadoLogin.Inicial -> {
                        setLoading(false)
                    }
                    is EstadoLogin.Carregando -> {
                        setLoading(true)
                    }
                    is EstadoLogin.Sucesso -> {
                        setLoading(false)
                        val tipoUsuario = estado.usuario.tipo
                        val tipoEsperado = if (funcaoLogin == "gestor") TipoUsuario.GESTOR else TipoUsuario.FUNCIONARIO

                        if (tipoUsuario == tipoEsperado) {
                            redirecionarParaPainel(tipoUsuario)
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "Acesso negado. Você não tem permissão de $funcaoLogin.",
                                Toast.LENGTH_LONG
                            ).show()
                            viewModel.logout()
                        }
                    }
                    is EstadoLogin.Erro -> {
                        setLoading(false)
                        Toast.makeText(this@LoginActivity, estado.mensagem, Toast.LENGTH_SHORT).show()
                        viewModel.limparErro()
                    }
                }
            }
        }
    }

    private fun redirecionarParaPainel(tipo: TipoUsuario) {
        val intent = when (tipo) {
            TipoUsuario.GESTOR -> Intent(this, PrincipalGestorActivity::class.java)
            TipoUsuario.FUNCIONARIO -> Intent(this, PrincipalFuncionarioActivity::class.java)
        }

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }


    private fun setLoading(isLoading: Boolean) {
        binding.progressBarLogin.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnEntrar.isEnabled = !isLoading
    }
}