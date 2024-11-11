package com.example.Practica09.ui.slideshow

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.Practica09.ControladorBDGuarda
import com.example.Practica09.R
import com.example.Practica09.databinding.FragmentSlideshowBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.sql.SQLException

class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null
    private val binding get() = _binding!!
    private lateinit var btnAgregar: FloatingActionButton
    private lateinit var btnBuscar: FloatingActionButton
    private lateinit var btnActualizar: FloatingActionButton
    private lateinit var btnEliminar: FloatingActionButton

    private lateinit var IDGuard: EditText
    private lateinit var salari: EditText
    private lateinit var feed: CheckBox
    private lateinit var shower: CheckBox
    private lateinit var action: Spinner
    private lateinit var actionSel: String

    private lateinit var male: RadioButton
    private lateinit var femele: RadioButton
    private lateinit var adaptador: ArrayAdapter<String>

    private lateinit var admin2: ControladorBDGuarda

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val view: View = binding.root

        IDGuard = view.findViewById(R.id.IDNombreGuarda)
        salari = view.findViewById(R.id.editSueldo)
        feed = view.findViewById(R.id.cbAlimentar)
        shower = view.findViewById(R.id.cbBañar)
        male = view.findViewById(R.id.rbMasculino)
        femele = view.findViewById(R.id.rbFemenino)
        btnAgregar = view.findViewById(R.id.btnInsertar2)
        btnBuscar = view.findViewById(R.id.btnBuscar2)
        btnEliminar = view.findViewById(R.id.btnEliminar2)
        btnActualizar = view.findViewById(R.id.btnActualizar2)
        action = view.findViewById(R.id.spnAcciones)

        val opciones = resources.getStringArray(R.array.listaAcciones)
        adaptador = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, opciones)
        action.adapter = adaptador
        actionSel = opciones[0]

        action.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                actionSel = opciones[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        admin2 = ControladorBDGuarda(requireContext(), "empresapatito2.db", null, 1)

        btnAgregar.setOnClickListener { registrarGuardia() }
        btnBuscar.setOnClickListener { buscarGuardia() }
        btnActualizar.setOnClickListener { actualizarGuardia() }
        btnEliminar.setOnClickListener { eliminarGuardia() }

        val textView: TextView = binding.textSlideshow
        slideshowViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return view
    }

    private fun eliminarGuardia() {
        val bd = admin2.writableDatabase
        val id = IDGuard.text.toString()

        if (id.isNotEmpty()) {
            val cantidad = bd.delete("guardias", "ID = $id", null)
            bd.close()

            IDGuard.setText("")
            salari.setText("")
            feed.isChecked = false
            shower.isChecked = false
            male.isChecked = false
            femele.isChecked = false
            IDGuard.requestFocus()

            if (cantidad > 0) {
                Toast.makeText(requireContext(), "Guardabosques eliminado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "El número de guardabosques no existe", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Ingresa número de guardabosques", Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizarGuardia() {
        val bd = admin2.writableDatabase
        val numero = IDGuard.text.toString()
        val sueldo = salari.text.toString()
        val animales = when {
            feed.isChecked -> "Alimentar animal"
            shower.isChecked -> "Bañar animal"
            else -> ""
        }
        val genero = when {
            male.isChecked -> "masculino"
            femele.isChecked -> "femenino"
            else -> ""
        }
        val spinnerValue = action.selectedItem.toString()

        if (numero.isNotEmpty() && sueldo.isNotEmpty()) {
            val registro = ContentValues()
            registro.put("ID", numero)
            registro.put("sueldo", sueldo)
            registro.put("animal", animales)
            registro.put("accion", spinnerValue)
            registro.put("genero", genero)

            val cantidad = bd.update("guardias", registro, "ID=$numero", null)
            bd.close()

            IDGuard.setText("")
            salari.setText("")
            feed.isChecked = false
            shower.isChecked = false
            male.isChecked = false
            femele.isChecked = false
            IDGuard.requestFocus()

            if (cantidad > 0) {
                Toast.makeText(requireContext(), "Datos del guardabosques actualizados", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "El número de guardabosques no existe", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Debes registrar primero los datos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buscarGuardia() {
        val bd = admin2.readableDatabase
        val numeroID = IDGuard.text.toString()

        if (numeroID.isNotEmpty()) {
            val fila = bd.rawQuery("select sueldo, animal, accion, genero from guardias where ID = $numeroID", null)
            if (fila.moveToFirst()) {
                salari.setText(fila.getString(0))
                val isAlimentar = fila.getString(1)
                val isBañar = fila.getString(1)
                feed.isChecked = isAlimentar == "Alimentar animal"
                shower.isChecked = isBañar == "Bañar animal"

                val spinnerValue = fila.getString(2)
                val spinnerPosition = adaptador.getPosition(spinnerValue)
                action.setSelection(spinnerPosition)

                val genero = fila.getString(3)
                male.isChecked = genero == "masculino"
                femele.isChecked = genero == "femenino"

                bd.close()
            } else {
                Toast.makeText(requireContext(), "Número de guardabosques no existe", Toast.LENGTH_SHORT).show()
                IDGuard.setText("")
                IDGuard.requestFocus()
                bd.close()
            }
        } else {
            Toast.makeText(requireContext(), "Ingresa número de guardabosques", Toast.LENGTH_SHORT).show()
            IDGuard.requestFocus()
        }
    }

    private fun registrarGuardia() {
        val bd = admin2.writableDatabase
        val id = IDGuard.text.toString()
        val sueldo = salari.text.toString()
        val guardaAnimals = when {
            feed.isChecked -> "Alimentar animal"
            shower.isChecked -> "Bañar animal"
            else -> ""
        }
        val acciones = actionSel
        val generos = when {
            male.isChecked -> "masculino"
            femele.isChecked -> "femenino"
            else -> ""
        }

        if (id.isNotEmpty() && sueldo.isNotEmpty()) {
            val registro = ContentValues()
            registro.put("ID", id)
            registro.put("sueldo", sueldo)
            registro.put("animal", guardaAnimals)
            registro.put("accion", acciones)
            registro.put("genero", generos)
            try {
                bd.insert("guardias", null, registro)
            } catch (e: SQLException) {
                Log.e("Exception", "Error" + e.message.toString())
            }
            bd.close()

            IDGuard.setText("")
            salari.setText("")
            feed.isChecked = false
            shower.isChecked = false
            male.isChecked = false
            femele.isChecked = false
            IDGuard.requestFocus()

            Toast.makeText(requireContext(), "Guardabosques registrado", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Debes registrar primero los datos", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}