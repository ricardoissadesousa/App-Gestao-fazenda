package com.example.farmmanagement.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.farmmanagement.MainActivity // Importe a MainActivity
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

        binding.cardFolgas.setOnClickListener { startActivity(Intent(this, ControleFolgasFuncionarioActivity::class.java)) }


        binding.cardDiesel.setOnClickListener { startActivity(Intent(this, ControleDieselActivity::class.java)) }

        binding.cardNascimento.setOnClickListener { startActivity(Intent(this, RegistrarNascimentoActivity::class.java)) }
        binding.cardProducao.setOnClickListener { startActivity(Intent(this, RegistrarProducaoDiariaActivity::class.java)) }

        // BOTÃO SAIR
        binding.btnSair.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            // Redireciona para a MainActivity (Tela de Escolha de Perfil)
            val intent = Intent(this, MainActivity::class.java)

            // Limpa o histórico de telas
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish()
        }
    }
}