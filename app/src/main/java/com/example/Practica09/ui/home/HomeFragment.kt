package com.example.Practica09.ui.home

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.Practica09.ControladorBDParques
import com.example.Practica09.R
import com.example.Practica09.databinding.FragmentHomeBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.sql.SQLException

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var btnAgregar: FloatingActionButton
    private lateinit var btnBuscar: FloatingActionButton
    private lateinit var btnActualizar: FloatingActionButton
    private lateinit var btnEliminar: FloatingActionButton

    private lateinit var IDPark: EditText
    private lateinit var size: EditText
    private lateinit var areaPlay: CheckBox
    private lateinit var spaces: CheckBox
    private lateinit var timeTable: Spinner
    private lateinit var timeSel: String

    private lateinit var lineal: RadioButton
    private lateinit var virtuals: RadioButton
    private lateinit var adaptador: ArrayAdapter<String>

    private lateinit var admin: ControladorBDParques

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view: View = binding.root

        IDPark = view.findViewById(R.id.editIDParque)
        size = view.findViewById(R.id.editTamanio)
        areaPlay = view.findViewById(R.id.cbJuego)
        spaces = view.findViewById(R.id.cbReposo)
        lineal = view.findViewById(R.id.rbLineales)
        virtuals = view.findViewById(R.id.rbVirtuales)
        btnAgregar = view.findViewById(R.id.btnInsertar)
        btnBuscar = view.findViewById(R.id.btnBuscar)
        btnEliminar = view.findViewById(R.id.btnEliminar)
        btnActualizar = view.findViewById(R.id.btnActualizar)
        timeTable = view.findViewById(R.id.spnHorario)

        val opciones = resources.getStringArray(R.array.listaHorario)
        adaptador = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, opciones)
        timeTable.adapter = adaptador
        timeSel = opciones[0]

        timeTable.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                timeSel = opciones[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        admin = ControladorBDParques(requireContext(), "empresapatito.db", null, 1)

        btnAgregar.setOnClickListener { registrarParque() }
        btnBuscar.setOnClickListener { buscarParque() }
        btnActualizar.setOnClickListener { actualizarParque() }
        btnEliminar.setOnClickListener { eliminarParque() }

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return view
    }

    private fun eliminarParque() {
        val bd = admin.writableDatabase
        val id = IDPark.text.toString()

        if (id.isNotEmpty()) {
            val cantidad = bd.delete("parques", "ID = $id", null)
            bd.close()

            IDPark.setText("")
            size.setText("")
            areaPlay.isChecked = false
            spaces.isChecked = false
            lineal.isChecked = false
            virtuals.isChecked = false
            IDPark.requestFocus()

            if (cantidad > 0) {
                Toast.makeText(requireContext(), "Parque eliminado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "El número de parque no existe", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Ingresa número de parque", Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizarParque() {
        val bd = admin.writableDatabase
        val numero = IDPark.text.toString()
        val tamanio = size.text.toString()
        val area = when {
            areaPlay.isChecked -> "Area de Juego"
            spaces.isChecked -> "Espacio de Reposo"
            else -> ""
        }
        val tipoParque = when {
            lineal.isChecked -> "lineal"
            virtuals.isChecked -> "virtual"
            else -> ""
        }
        val spinnerValue = timeTable.selectedItem.toString()

        if (numero.isNotEmpty() && tamanio.isNotEmpty()) {
            val registro = ContentValues()
            registro.put("ID", numero)
            registro.put("tamaño", tamanio)
            registro.put("area", area)
            registro.put("horario", spinnerValue)
            registro.put("tipoParque", tipoParque)

            val cantidad = bd.update("parques", registro, "ID=$numero", null)
            bd.close()

            IDPark.setText("")
            size.setText("")
            areaPlay.isChecked = false
            spaces.isChecked = false
            lineal.isChecked = false
            virtuals.isChecked = false
            IDPark.requestFocus()

            if (cantidad > 0) {
                Toast.makeText(requireContext(), "Datos del parque actualizados", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "El número de parque no existe", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Debes registrar primero los datos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buscarParque() {
        val bd = admin.readableDatabase
        val numeroID = IDPark.text.toString()

        if (numeroID.isNotEmpty()) {
            val fila = bd.rawQuery("select tamaño, area, horario, tipoParque from parques where ID = $numeroID", null)
            if (fila.moveToFirst()) {
                size.setText(fila.getString(0))
                val isArea = fila.getString(1)
                val isSpaces = fila.getString(1)
                areaPlay.isChecked = isArea == "Area de Juego"
                spaces.isChecked = isSpaces == "Espacio de Reposo"

                val spinnerValue = fila.getString(2)
                val spinnerPosition = adaptador.getPosition(spinnerValue)
                timeTable.setSelection(spinnerPosition)

                val tipoParque = fila.getString(3)
                lineal.isChecked = tipoParque == "lineal"
                virtuals.isChecked = tipoParque == "virtual"

                bd.close()
            } else {
                Toast.makeText(requireContext(), "Número de parque no existe", Toast.LENGTH_SHORT).show()
                IDPark.setText("")
                IDPark.requestFocus()
                bd.close()
            }
        } else {
            Toast.makeText(requireContext(), "Ingresa número de parque", Toast.LENGTH_SHORT).show()
            IDPark.requestFocus()
        }
    }

    private fun registrarParque() {
        val bd = admin.writableDatabase
        val id = IDPark.text.toString()
        val tamanio = size.text.toString()
        val parqueAreas = when {
            areaPlay.isChecked -> "Area de Juego"
            spaces.isChecked -> "Espacio de Reposo"
            else -> ""
        }
        val horarios = timeSel
        val parqueRadios = when {
            lineal.isChecked -> "lineal"
            virtuals.isChecked -> "virtual"
            else -> ""
        }

        if (id.isNotEmpty() && tamanio.isNotEmpty()) {
            val registro = ContentValues()
            registro.put("ID", id)
            registro.put("tamaño", tamanio)
            registro.put("area", parqueAreas)
            registro.put("horario", horarios)
            registro.put("tipoParque", parqueRadios)
            try {
                bd.insert("parques", null, registro)
            } catch (e: SQLException) {
                Log.e("Exception", "Error" + e.message.toString())
            }
            bd.close()

            IDPark.setText("")
            size.setText("")
            areaPlay.isChecked = false
            spaces.isChecked = false
            lineal.isChecked = false
            virtuals.isChecked = false
            IDPark.requestFocus()

            Toast.makeText(requireContext(), "Parque registrado", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Debes registrar primero los datos", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}