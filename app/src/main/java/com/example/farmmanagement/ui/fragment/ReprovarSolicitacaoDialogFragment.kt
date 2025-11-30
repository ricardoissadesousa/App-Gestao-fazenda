package com.example.farmmanagement.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.farmmanagement.databinding.DialogReprovarSolicitacaoBinding
import com.google.firebase.firestore.FirebaseFirestore

class ReprovarSolicitacaoDialogFragment : DialogFragment() {
    private var _binding: DialogReprovarSolicitacaoBinding? = null
    private val binding get() = _binding!!

    private var idSolicitacao: String? = null
    private var nomeFuncionario: String? = null
    private var dataSolicitacao: String? = null

    companion object {
        const val TAG = "ReprovarSolicitacaoDialog"
        private const val ARG_ID = "id_solicitacao"
        private const val ARG_NOME = "nome_funcionario"
        private const val ARG_DATA = "data_solicitacao"

        fun newInstance(id: String, nome: String, data: String): ReprovarSolicitacaoDialogFragment {
            val args = Bundle()
            args.putString(ARG_ID, id)
            args.putString(ARG_NOME, nome)
            args.putString(ARG_DATA, data)
            val fragment = ReprovarSolicitacaoDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idSolicitacao = it.getString(ARG_ID)
            nomeFuncionario = it.getString(ARG_NOME)
            dataSolicitacao = it.getString(ARG_DATA)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogReprovarSolicitacaoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDialogSubtitle.text = "Reprovando solicitação de $nomeFuncionario para o dia $dataSolicitacao."

        binding.btnCancelarRecusa.setOnClickListener { dismiss() }

        binding.btnConfirmarRecusa.setOnClickListener {
            val motivoRecusa = binding.etMotivoRecusa.text.toString().trim()

            if (motivoRecusa.isEmpty()) {
                binding.etMotivoRecusa.error = "Informe o motivo"
                return@setOnClickListener
            }

            if (idSolicitacao != null) {
                FirebaseFirestore.getInstance().collection("solicitacoes_folga")
                    .document(idSolicitacao!!)
                    .update(
                        mapOf(
                            "status" to "REPROVADA",
                            "motivo_recusa" to motivoRecusa
                        )
                    )
                    .addOnSuccessListener {
                        Toast.makeText(context, "Solicitação reprovada.", Toast.LENGTH_SHORT).show()



                        parentFragmentManager.setFragmentResult("recusa_ok", Bundle())

                        dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Erro ao reprovar.", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}