package com.example.farmmanagement.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.farmmanagement.databinding.ActivityPrincipalFuncionarioBinding
import com.google.firebase.auth.FirebaseAuth

class PrincipalFuncionarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrincipalFuncionarioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrincipalFuncionarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
    }

    private fun setupNavigation() {
        // 1. Navegação para Controle de Folgas (Portal do Funcionário)
        binding.cardFolgas.setOnClickListener {
            // Esta activity já existe nos seus arquivos e é o "Portal do Funcionário"
            startActivity(Intent(this, ControleFolgasFuncionarioActivity::class.java))
        }

        // 2. Navegação para Controle de Diesel
        binding.cardDiesel.setOnClickListener {
            startActivity(Intent(this, ControleDieselActivity::class.java))
        }

        // 3. Navegação para Registrar Nascimento
        binding.cardNascimento.setOnClickListener {
            startActivity(Intent(this, RegistrarNascimentoActivity::class.java))
        }

        // 4. Navegação para Registrar Produção
        binding.cardProducao.setOnClickListener {
            startActivity(Intent(this, RegistrarProducaoDiariaActivity::class.java))
        }

        // Botão Sair
        binding.btnSair.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            // Limpa o histórico para não voltar
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}