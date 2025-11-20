package com.example.farmmanagement.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.farmmanagement.databinding.DialogSolicitarFolgaBinding

class SolicitarFolgaDialogFragment : DialogFragment() {

    private var _binding: DialogSolicitarFolgaBinding? = null
    private val binding get() = _binding!!

    private var dataSolicitada: String? = null

    // 1. Usamos isso para criar o dialog, passando a data
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogSolicitarFolgaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 2. Atualiza o texto com a data recebida
        binding.tvDialogSubtitle.text = "Você está solicitando folga para o dia $dataSolicitada."

        // 3. Ação do Botão Cancelar
        binding.btnCancelar.setOnClickListener {
            dismiss() // Fecha o pop-up
        }

        // 4. Ação do Botão Confirmar
        binding.btnConfirmar.setOnClickListener {
            val motivo = binding.etMotivo.text.toString()

            // TODO: Adicionar lógica para enviar a solicitação (com a data e o motivo)
            // ...

            Toast.makeText(context, "Folga solicitada!", Toast.LENGTH_SHORT).show()
            dismiss() // Fecha o pop-up
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}