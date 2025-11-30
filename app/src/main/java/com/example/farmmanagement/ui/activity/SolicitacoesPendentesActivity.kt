package com.example.farmmanagement.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.farmmanagement.MainActivity
import com.example.farmmanagement.R
import com.example.farmmanagement.data.model.Solicitacao
import com.example.farmmanagement.databinding.ActivitySolicitacoesPendentesBinding
import com.example.farmmanagement.ui.adapter.SolicitacoesAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SolicitacoesPendentesActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySolicitacoesPendentesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySolicitacoesPendentesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_pendentes -> { }
                R.id.nav_historico -> {
                    startActivity(Intent(this, HistoricoSolicitacoesGlobalActivity::class.java))
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        binding.recyclerViewSolicitacoes.layoutManager = LinearLayoutManager(this)

        // LISTENER
        // Fica ouvindo se o Dialog mandou um "recusa_ok". Se sim, atualiza a lista!
        supportFragmentManager.setFragmentResultListener("recusa_ok", this) { _, _ ->
            carregarSolicitacoesReais()
        }
    }

    override fun onResume() {
        super.onResume()
        carregarSolicitacoesReais()
        binding.navView.setCheckedItem(R.id.nav_pendentes)
    }

    private fun carregarSolicitacoesReais() {
        FirebaseFirestore.getInstance().collection("solicitacoes_folga")
            .whereEqualTo("status", "PENDENTE")
            .get()
            .addOnSuccessListener { result ->
                val lista = result.map { doc ->
                    Solicitacao(
                        id = doc.id,
                        nome = doc.getString("usuario_nome") ?: "Sem nome",
                        email = doc.getString("usuario_email") ?: "",
                        data = doc.getString("data_solicitada") ?: "",
                        motivo = doc.getString("motivo") ?: ""
                    )
                }.toMutableList()

                if (lista.isEmpty()) {
                    binding.recyclerViewSolicitacoes.visibility = View.GONE
                    binding.layoutEmptyState.visibility = View.VISIBLE
                } else {
                    binding.recyclerViewSolicitacoes.visibility = View.VISIBLE
                    binding.layoutEmptyState.visibility = View.GONE
                    binding.recyclerViewSolicitacoes.adapter = SolicitacoesAdapter(lista)
                }
            }
    }
}