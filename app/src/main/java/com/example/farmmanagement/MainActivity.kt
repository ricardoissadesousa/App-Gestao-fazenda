package com.example.farmmanagement

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.farmmanagement.databinding.ActivityMainBinding // 1. Importar ViewBinding
import com.example.farmmanagement.ui.activity.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding // 2. Declarar o binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // 3. Inflar o layout com ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root) // 4. Usar binding.root

        // 5. Configurar cliques nos cards
        binding.cardFuncionario.setOnClickListener {
            abrirLogin("funcionario")
        }

        binding.cardGestor.setOnClickListener {
            abrirLogin("gestor")
        }
    }

    // 6. Função auxiliar para abrir a tela de login
    private fun abrirLogin(funcao: String) {
        val intent = Intent(this, LoginActivity::class.java)
        // 7. Passar a função clicada para a LoginActivity
        intent.putExtra("FUNCAO_LOGIN", funcao)
        startActivity(intent)
    }
}