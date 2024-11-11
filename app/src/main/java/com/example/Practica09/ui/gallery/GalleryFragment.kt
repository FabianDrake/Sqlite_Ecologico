package com.example.Practica09.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.Practica09.ControladorBDParques
import com.example.Practica09.R
import com.example.Practica09.databinding.FragmentGalleryBinding

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private lateinit var etListado: EditText
    private lateinit var admin: ControladorBDParques

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val view: View = binding.root

        etListado = view.findViewById(R.id.editDetalle)
        admin = ControladorBDParques(requireContext(), "empresapatito.db", null, 1)

        val textView: TextView = binding.textGallery
        galleryViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        val bd = admin.readableDatabase
        val registro = bd.rawQuery("select * from parques order by ID", null)
        val n = registro.count
        var nr = 0
        etListado.setText("")
        if (n > 0) {
            registro.moveToFirst()
            do {
                etListado.append(
                    "\nID:  ${registro.getString(0)}\n" +
                            "\nTamaño del parque: ${registro.getString(1)}\n" +
                            "\nTipo Area: ${registro.getString(2)}\n" +
                            "\nHorario: ${registro.getString(3)}\n" +
                            "\nTipo Parque: ${registro.getString(4)}\n\n"
                )
                nr++
            } while (registro.moveToNext())
        } else {
            Toast.makeText(requireContext(), "Sin registro de parques", Toast.LENGTH_SHORT).show()
        }

        bd.close()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}