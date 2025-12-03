package com.example.farmmanagement.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.farmmanagement.data.repository.AuthRepository
import com.example.farmmanagement.data.source.AuthDataSource
import com.example.farmmanagement.databinding.ActivityRegisterBinding
import com.example.farmmanagement.ui.viewmodel.EstadoLogin
import com.example.farmmanagement.ui.viewmodel.LoginViewModel
import com.example.farmmanagement.ui.viewmodel.LoginViewModelFactory
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private var funcaoCadastro: String? = null

    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(AuthRepository(AuthDataSource()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        funcaoCadastro = intent.getStringExtra("FUNCAO_CADASTRO")

        if (funcaoCadastro.isNullOrEmpty()) {
            Log.e("RegisterActivity", "ERRO: Tela de cadastro aberta sem uma função definida.")
            Toast.makeText(this, "Erro ao abrir cadastro, função não definida.", Toast.LENGTH_LONG).show()
            finish()
        } else {
            binding.autocompleteFuncao.setText(funcaoCadastro, false)
            binding.layoutFuncao.isEnabled = false
        }

        configurarBotoes()
        observarEstadoRegistro()
    }

    private fun configurarBotoes() {
        binding.btnCriarConta.setOnClickListener {
            registrarNovoUsuario()
        }
    }

    private fun registrarNovoUsuario() {
        val nome = binding.etNome.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val senha = binding.etPassword.text.toString().trim()
        val tipo = funcaoCadastro?.uppercase() ?: "FUNCIONARIO"

        viewModel.registrar(email, senha, nome, tipo)
    }

    private fun observarEstadoRegistro() {
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
                        Toast.makeText(
                            this@RegisterActivity,
                            "Usuário cadastrado com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                    is EstadoLogin.Erro -> {
                        setLoading(false)
                        Toast.makeText(
                            this@RegisterActivity,
                            estado.mensagem,
                            Toast.LENGTH_LONG
                        ).show()
                        viewModel.limparErro()
                    }
                }
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBarRegister.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnCriarConta.isEnabled = !isLoading
    }
}