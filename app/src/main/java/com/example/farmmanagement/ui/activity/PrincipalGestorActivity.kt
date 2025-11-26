package com.example.farmmanagement.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.farmmanagement.databinding.ActivityPrincipalGestorBinding
import com.google.firebase.auth.FirebaseAuth

class PrincipalGestorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrincipalGestorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrincipalGestorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
    }

    private fun setupNavigation() {
        // 1. Navegação para Produção Diária
        binding.cardProducao.setOnClickListener {
            startActivity(Intent(this, RegistrarProducaoDiariaActivity::class.java))
        }

        // 2. Navegação para Relatório Mensal
        binding.cardRelatorios.setOnClickListener {
            startActivity(Intent(this, RelatorioMensalActivity::class.java))
        }

        // 3. Navegação para Lista de Animais
        binding.cardAnimais.setOnClickListener {
            startActivity(Intent(this, ListaDeAnimaisActivity::class.java))
        }

        // 4. Navegação para Registro de Nascimento
        binding.cardNascimento.setOnClickListener {
            startActivity(Intent(this, RegistrarNascimentoActivity::class.java))
        }

        // 5. Navegação para Solicitações Pendentes (Folgas)
        binding.cardSolicitacoes.setOnClickListener {
            startActivity(Intent(this, SolicitacoesPendentesActivity::class.java))
        }

        // 6. Navegação para Controle de Diesel
        binding.cardDiesel.setOnClickListener {
            startActivity(Intent(this, ControleDieselActivity::class.java))
        }

        // Botão Sair
        binding.btnSair.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            // Limpa o histórico para o usuário não voltar com o botão "voltar"
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}