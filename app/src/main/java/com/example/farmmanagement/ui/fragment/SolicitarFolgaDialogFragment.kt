package com.example.farmmanagement.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.farmmanagement.databinding.DialogSolicitarFolgaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class SolicitarFolgaDialogFragment : DialogFragment() {

    private var _binding: DialogSolicitarFolgaBinding? = null
    private val binding get() = _binding!!
    private var dataSolicitada: String? = null

    companion object {
        const val TAG = "SolicitarFolgaDialog"
        private const val ARG_DATA = "data_solicitada"

        fun newInstance(data: String): SolicitarFolgaDialogFragment {
            val args = Bundle()
            args.putString(ARG_DATA, data)
            val fragment = SolicitarFolgaDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dataSolicitada = it.getString(ARG_DATA)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogSolicitarFolgaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDialogSubtitle.text = "Você está solicitando folga para o dia $dataSolicitada."

        binding.btnCancelar.setOnClickListener { dismiss() }

        binding.btnConfirmar.setOnClickListener {
            val motivo = binding.etMotivo.text.toString()
            val user = FirebaseAuth.getInstance().currentUser

            if (user != null && dataSolicitada != null) {
                //Busca o nome real no Firestore antes de salvar
                FirebaseFirestore.getInstance().collection("usuarios")
                    .document(user.uid)
                    .get()
                    .addOnSuccessListener { document ->
                        // Pega o nome do cadastro ou usa "Funcionário" se falhar
                        val nomeReal = document.getString("nome") ?: "Funcionário"

                        salvarSolicitacao(user.uid, nomeReal, user.email ?: "", motivo)
                    }
                    .addOnFailureListener {
                        // Se der erro ao buscar nome, usa o email como fallback (para não travar)
                        salvarSolicitacao(user.uid, user.email ?: "Funcionário", user.email ?: "", motivo)
                    }
            } else {
                Toast.makeText(context, "Erro de usuário.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun salvarSolicitacao(uid: String, nome: String, email: String, motivo: String) {
        val solicitacao = hashMapOf(
            "usuario_id" to uid,
            "usuario_nome" to nome,
            "usuario_email" to email,
            "data_solicitada" to dataSolicitada,
            "motivo" to motivo,
            "status" to "PENDENTE",
            "data_criacao" to Date()
        )

        FirebaseFirestore.getInstance().collection("solicitacoes_folga")
            .add(solicitacao)
            .addOnSuccessListener {
                Toast.makeText(context, "Solicitação enviada!", Toast.LENGTH_SHORT).show()
                dismiss()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Erro ao enviar.", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}