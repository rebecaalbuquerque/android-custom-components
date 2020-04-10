package com.albuquerque.custoncomponents

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupView()
    }

    private fun setupView() {
        setupHorizontalSelection()
        setupCalendar()
        setupTextLoading()
    }

    private fun setupTextLoading() {

    }

    private fun setupCalendar() {

    }

    private fun setupHorizontalSelection() {
        val list = mutableListOf("Opção 1", "Opção 2", "Opção 3", "Opção 4", "Opção 5")

        with(horizontalSelection) {
            setupHorizontalSelection(list, 0, false, "Primeiro", "Último")

            setOnHeadSelected {
                Toast.makeText(context, "Head", Toast.LENGTH_LONG).show()
            }

            setOnTailSelected {
                Toast.makeText(context, "Tail", Toast.LENGTH_LONG).show()
            }

            setOnItemSelected { position ->
                Toast.makeText(context, "Index $position selecionado", Toast.LENGTH_LONG).show()
            }
        }

    }

}
