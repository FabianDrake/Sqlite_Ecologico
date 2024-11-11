package com.example.Practica09

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.example.Practica09.databinding.FragmentListadoGuardaBinding


// TODO: Rename parameter arguments, choose names that match
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ListadoGuarda.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListadoGuarda : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentListadoGuardaBinding? = null

    private lateinit var etListado2: EditText
    private lateinit var admin2: ControladorBDGuarda


    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListadoGuardaBinding.inflate(inflater, container, false)
        val view: View = binding.root

        etListado2 = view.findViewById(R.id.editDetalle2)
        admin2 = ControladorBDGuarda(requireContext(), "empresapatito2.db", null, 1)

        return view
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        val bd = admin2.readableDatabase
        val registro = bd.rawQuery("select * from guardias order by ID", null)
        val n = registro.count
        var nr = 0
        etListado2.setText("")
        if (n > 0) {
            registro.moveToFirst()
            do {
                etListado2.append(
                    "\nID:  ${registro.getString(0)}\n" +
                            "\nSueldo: ${registro.getString(1)}\n" +
                            "\nAccion de Animal: ${registro.getString(2)}\n" +
                            "\nAccion del Guarda: ${registro.getString(3)}\n" +
                            "\nGenero: ${registro.getString(4)}\n\n"
                )
                nr++
            } while (registro.moveToNext()) // Si existen más registros
        } else {
            Toast.makeText(requireContext(), "Sin registro de Guardias", Toast.LENGTH_SHORT).show()
        }
        bd.close()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ListadoGuarda.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ListadoGuarda().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}