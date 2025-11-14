package com.example.farmmanagement
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.farmmanagement.databinding.DialogReprovarSolicitacaoBinding
// TESTE PARA O GIT
class ReprovarSolicitacaoDialogFragment : DialogFragment() {
    private var _binding: DialogReprovarSolicitacaoBinding? = null
    private val binding get() = _binding!!

    // Variáveis para guardar os dados recebidos
    private var nomeFuncionario: String? = null
    private var dataSolicitacao: String? = null

    // 1. Padrão NewInstance para passar dados para o Dialog
    companion object {
        const val TAG = "ReprovarSolicitacaoDialog"
        private const val ARG_NOME = "nome_funcionario"
        private const val ARG_DATA = "data_solicitacao"

        fun newInstance(nome: String, data: String): ReprovarSolicitacaoDialogFragment {
            val args = Bundle()
            args.putString(ARG_NOME, nome)
            args.putString(ARG_DATA, data)
            val fragment = ReprovarSolicitacaoDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Pega os dados
        arguments?.let {
            nomeFuncionario = it.getString(ARG_NOME)
            dataSolicitacao = it.getString(ARG_DATA)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogReprovarSolicitacaoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 2. Atualiza o texto do subtítulo com os dados
        binding.tvDialogSubtitle.text = "Reprovando solicitação de $nomeFuncionario para o dia $dataSolicitacao."

        // 3. Ação do Botão Cancelar
        binding.btnCancelarRecusa.setOnClickListener {
            dismiss() // Fecha o pop-up
        }

        // 4. Ação do Botão Confirmar Recusa
        binding.btnConfirmarRecusa.setOnClickListener {
            val motivo = binding.etMotivoRecusa.text.toString()

            // TODO: Adicionar lógica para enviar a recusa (com o ID, motivo, etc)
            // ...

            Toast.makeText(context, "Solicitação reprovada!", Toast.LENGTH_SHORT).show()
            dismiss() // Fecha o pop-up
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}