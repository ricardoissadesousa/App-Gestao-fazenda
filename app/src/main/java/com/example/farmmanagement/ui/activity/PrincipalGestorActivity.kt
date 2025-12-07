package com.example.farmmanagement.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.farmmanagement.MainActivity
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

        binding.cardProducao.setOnClickListener { startActivity(Intent(this, RegistrarProducaoDiariaActivity::class.java)) }
        binding.cardRelatorios.setOnClickListener { startActivity(Intent(this, RelatorioMensalActivity::class.java)) }
        binding.cardAnimais.setOnClickListener { startActivity(Intent(this, ListaDeAnimaisActivity::class.java)) }
        binding.cardNascimento.setOnClickListener { startActivity(Intent(this, RegistrarNascimentoActivity::class.java)) }
        binding.cardSolicitacoes.setOnClickListener { startActivity(Intent(this, SolicitacoesPendentesActivity::class.java)) }


        binding.cardDiesel.setOnClickListener { startActivity(Intent(this, HistoricoAbastecimentosActivity::class.java)) }

        //BOTÃO SAIR
        binding.btnSair.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            // Redireciona para a MainActivity (Tela de Escolha de Perfil)
            val intent = Intent(this, MainActivity::class.java)

            //Limpa o histórico de telas para que o botão "Voltar" não funcione
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish()
        }
    }
}