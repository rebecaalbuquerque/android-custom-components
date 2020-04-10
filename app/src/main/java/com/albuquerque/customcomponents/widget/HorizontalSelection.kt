package com.albuquerque.customcomponents.widget

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


class HorizontalSelection: ConstraintLayout {

    var selectedIndex: Int = -2

    private var body: List<String> = arrayListOf()
    private var head: String? = null
    private var tail: String? = null
    private var itemSelectedColor: Int = 0

    private var onHeadSelected: (() -> Unit)? = null
    private var onTailSelected: (() -> Unit)? = null
    private var onItemSelected: ((position: Int) -> Unit)? = null

    private var headView: TextView? = null

    private lateinit var horizontalAdapter: HorizontalSelectionAdapter

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        val attributes = context?.obtainStyledAttributes(attrs, R.styleable.HorizontalSelection)
        itemSelectedColor = attributes?.getColor(R.styleable.HorizontalSelection_hsSelectedItemColor, Color.GRAY) ?: 0

        attributes?.recycle()
    }

    fun setupHorizontalSelection(body: MutableList<String>, defaultIndexSelected: Int, isHeadSelectable: Boolean = true, head: String? = null, tail: String? = null) {
        View.inflate(context, R.layout.layout_horizontal_selection, this)
        setup(body, defaultIndexSelected, isHeadSelectable, head, tail)
    }

    private fun setup(body: MutableList<String>, defaultIndexSelected: Int, isHeadSelectable: Boolean = true, head: String? = null, tail: String? = null) {
        this.body = body
        this.head = head
        this.tail = tail

        headView = findViewById(R.id.headText)

        tail?.let { body.add(it) }

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

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, recyclerViewSelection.measuredHeight)
    }

    fun setOnHeadSelected(onHead: () -> Unit) {
        onHeadSelected = onHead
    }

    fun setOnTailSelected(onTail: () -> Unit) {
        onTailSelected = onTail
    }

    fun setOnItemSelected(onItem: (position: Int) -> Unit) {
        onItemSelected = onItem
    }

    fun selectTailManually() {
        if(tail != null) {
            selectedIndex = body.size - 1
            horizontalAdapter.notifyDataSetChanged()
            recyclerViewSelection.layoutManager?.scrollToPosition(body.size-1)
        }
    }

    private inner class HorizontalSelectionAdapter(
            private val data: List<String>,
            private val color: Int
    ) : RecyclerView.Adapter<HorizontalSelectionAdapter.HorizontalSelectionViewHolder>() {

        private var forceSelection = false

        fun setForceSelection(force: Boolean) {
            forceSelection = force
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalSelectionViewHolder {
            return HorizontalSelectionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_horizontal_selection, parent, false))
        }

        override fun onBindViewHolder(holder: HorizontalSelectionViewHolder, position: Int) {
            holder.bind(data[position], selectedIndex == position || forceSelection)
            forceSelection = false
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

                        if(adapterPosition == itemCount - 1 && tail != null) {
                            onTailSelected?.invoke()
                        } else {
                            onItemSelected?.invoke(adapterPosition)
                        }

                        (headText?.background as? GradientDrawable)?.setColor(Color.TRANSPARENT)
                        notifyDataSetChanged()
                    }

                }

            }

        }

    }

}