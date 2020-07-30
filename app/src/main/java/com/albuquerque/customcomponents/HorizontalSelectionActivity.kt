package com.albuquerque.customcomponents

import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_horizontal_selection.*

class HorizontalSelectionActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_horizontal_selection)

        supportActionBar?.let { actionBar ->
            actionBar.apply {
                this.setDisplayHomeAsUpEnabled(true)
                this.title = "HorizontalSelection"
            }
        }

        setupHorizontalSelection()
    }

    private fun setupHorizontalSelection() {
        val list = mutableListOf("Opção 1", "Opção 2", "Opção 3", "Opção 4", "Opção 5", "Último")

        with(horizontalSelection) {
            setupHorizontalSelection(list, 0, false, "Primeiro")

            setOnHeadSelected {
                Toast.makeText(context, "Head", Toast.LENGTH_LONG).show()
            }

            setOnItemSelected(list.size - 1) {
                Toast.makeText(context, "Seleção customizada, selecionando último index", Toast.LENGTH_LONG).show()
            }

            setOnItemSelected { position ->
                Toast.makeText(context, "Index $position selecionado", Toast.LENGTH_LONG).show()
            }

        }

        btnHorizontalSelection.setOnClickListener {
            horizontalSelection.forceSelection(list.size -1)
        }

    }

}