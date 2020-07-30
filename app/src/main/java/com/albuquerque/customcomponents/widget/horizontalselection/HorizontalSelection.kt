package com.albuquerque.customcomponents.widget.horizontalselection

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.albuquerque.customcomponents.R
import kotlinx.android.synthetic.main.item_horizontal_selection.view.*
import kotlinx.android.synthetic.main.layout_horizontal_selection.view.*
import java.lang.Exception

/**
 * Simples componente que possui um cabeçalho e uma lista de itens clicáveis, onde é possível
 * dizer o que acontece ao clicar em cada item
 * */
class HorizontalSelection: ConstraintLayout {

    private var selectedIndex: Int = -2

    private var body: List<String> = arrayListOf()
    private var head: String? = null

    private var itemSelectedColor: Int = 0
    private var isHeadSelectable: Boolean = true

    private var onHeadSelected: (() -> Unit)? = null
    private var onItemSelected: ((position: Int) -> Unit)? = null
    private var onItemSelectedByIndex: (() -> Unit)? = null
    private var indexCustomSelection: Int? = null

    private var headView: TextView? = null

    private lateinit var horizontalAdapter: HorizontalSelectionAdapter

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        val attributes = context?.obtainStyledAttributes(attrs, R.styleable.HorizontalSelection)
        itemSelectedColor = attributes?.getColor(R.styleable.HorizontalSelection_hsSelectedItemColor, Color.GRAY) ?: 0

        attributes?.recycle()
    }

    /**
     * @param body lista dos itens que serão exibidos no [HorizontalSelection]
     * @param defaultIndexSelected diz qual item estará selecionado assim que o app rodar pela primeira vez
     * @param isHeadSelectable variável que diz se é possível existir um listener de click no cabeçalho do [HorizontalSelection]
     * @param head texto do cabeçalho do [HorizontalSelection]
     * */
    fun setupHorizontalSelection(body: MutableList<String>, defaultIndexSelected: Int, isHeadSelectable: Boolean = true, head: String? = null) {
        View.inflate(context, R.layout.layout_horizontal_selection, this)
        setup(body, defaultIndexSelected, isHeadSelectable, head)
    }

    /**
     * Inicializa as views do componente
     * */
    private fun setup(body: MutableList<String>, defaultIndexSelected: Int, isHeadSelectable: Boolean = true, head: String? = null) {
        this.body = body
        this.head = head

        this.isHeadSelectable = isHeadSelectable

        headView = findViewById(R.id.headText)

        headView?.text = head

        if(isHeadSelectable) {
            headView?.setOnClickListener {
                selectedIndex = -1
                recyclerViewSelection.adapter?.notifyDataSetChanged()
                (headView?.background as GradientDrawable).setColor(itemSelectedColor)
                headView?.setTextColor(Color.WHITE)
            }
        }

        if(body.isNotEmpty()) selectedIndex = defaultIndexSelected

        horizontalAdapter = HorizontalSelectionAdapter(body, itemSelectedColor)

        recyclerViewSelection.adapter = horizontalAdapter

        requestLayout()
    }

    /**
     * Obriga a altura do [HorizontalSelection] ter a mesma altura que a sua [RecyclerView]
     * */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, recyclerViewSelection?.measuredHeight ?: 100)
    }

    fun setOnHeadSelected(onHead: () -> Unit) {
        if(isHeadSelectable) onHeadSelected = onHead
    }

    fun setOnItemSelected(onItem: (position: Int) -> Unit) {
        onItemSelected = onItem
    }

    fun setOnItemSelected(index: Int, onItem: () -> Unit) {
        if(body.isNotEmpty() && body.getOrNull(index) != null) {
            indexCustomSelection = index
            onItemSelectedByIndex = onItem
        } else {
            throw Exception("setOnItemSelected: Invalid index")
        }
    }

    fun forceSelection(index: Int) {
        if(body.isNotEmpty() && body.getOrNull(index) != null) {
            selectedIndex = index
            horizontalAdapter.notifyDataSetChanged()
            recyclerViewSelection.layoutManager?.scrollToPosition(index)
        } else {
            throw Exception("forceSelection: Invalid index")
        }
    }

    private inner class HorizontalSelectionAdapter(
            private val data: List<String>,
            private val color: Int
    ) : RecyclerView.Adapter<HorizontalSelectionAdapter.HorizontalSelectionViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalSelectionViewHolder {
            return HorizontalSelectionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_horizontal_selection, parent, false))
        }

        override fun onBindViewHolder(holder: HorizontalSelectionViewHolder, position: Int) {
            holder.bind(data[position], selectedIndex == position)
        }

        override fun getItemCount(): Int = data.size

        private inner class HorizontalSelectionViewHolder(view: View): RecyclerView.ViewHolder(view) {

            fun bind(value: String, selected: Boolean) {

                with(itemView) {

                    itemSelection.text = value

                    if(selected) {
                        (itemSelection.background as GradientDrawable).setColor(color)
                        itemSelection.setTextColor(Color.WHITE)

                        (headView?.background as GradientDrawable).setColor(Color.TRANSPARENT)
                        headView?.setTextColor(Color.parseColor("#8E8E93"))

                    } else {
                        (itemSelection.background as GradientDrawable).setColor(Color.TRANSPARENT)
                        itemSelection.setTextColor(Color.parseColor("#3E3F40"))
                    }

                    setOnClickListener {
                        selectedIndex = adapterPosition

                        if((indexCustomSelection != null && onItemSelectedByIndex != null) && indexCustomSelection == adapterPosition)
                            onItemSelectedByIndex?.invoke()
                        else
                            onItemSelected?.invoke(adapterPosition)

                        (headText?.background as? GradientDrawable)?.setColor(Color.TRANSPARENT)
                        notifyDataSetChanged()
                    }

                }

            }

        }

    }

}