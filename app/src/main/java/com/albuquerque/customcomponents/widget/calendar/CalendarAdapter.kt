package com.albuquerque.customcomponents.widget.calendar

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.albuquerque.customcomponents.R
import com.albuquerque.customcomponents.extensions.getCurrentBrazilianCalendar
import com.albuquerque.customcomponents.extensions.toDate
import kotlinx.android.synthetic.main.item_calendar.view.*
import java.util.*

class CalendarAdapter(
    private var calendarAvailability: CustomCalendar.CalendarAvailability,
    private var currentMonth: Int,
    private val onItemClicked: (item: CustomDate, position: Int) -> Unit
): RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private var data: List<CustomDate> = arrayListOf()
    private var daysSelected: List<CustomDate> = arrayListOf()

    fun refresh(data: List<CustomDate>) {
        this.data = data
        notifyDataSetChanged()
    }

    fun refresh(data: List<CustomDate>, currentMonth: Int) {
        this.data = data
        this.currentMonth = currentMonth
        notifyDataSetChanged()
    }

    fun updateDaysSelected(daysSelected: List<CustomDate>) {
        this.daysSelected = daysSelected
    }

    fun getItemPosition(item: CustomDate): Int {
        return data.indexOf(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        return CalendarViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_calendar, parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(data[position])
    }

    inner class CalendarViewHolder(view: View): RecyclerView.ViewHolder(view) {

        fun bind(item: CustomDate) {

            with(itemView) {

                if(item.month == currentMonth) {

                    val check = when(calendarAvailability) {
                        CustomCalendar.CalendarAvailability.ALL -> false
                        CustomCalendar.CalendarAvailability.BEFORE_CURRENT_DAY -> isBeforeCurrentDay(item)
                        CustomCalendar.CalendarAvailability.AFTER_CURRENT_DAY -> isAfterCurrentDay(item)
                    }

                    if(check) {
                        item.clickable = true
                        day.setTextColor(Color.parseColor("#4A4A4A"))
                    } else {
                        item.clickable = false
                        day.setTextColor(Color.parseColor("#CCCCCC"))
                    }

                } else {
                    item.clickable = false
                    day.setTextColor(Color.parseColor("#CCCCCC"))
                }

                // Configurando qual drawable irá aparecer em cada dia. Existe o drawable com bordas
                // arredondas (para dias de inicio e fim da semana) e o drawable retangular, para o
                // restante dos dias
                if(adapterPosition % 7 == 0) {
                    container.background = ContextCompat.getDrawable(context, R.drawable.shape_calendar_day_background_oval_left)
                } else if(adapterPosition == 6 || adapterPosition == 13 || adapterPosition == 20 || adapterPosition == 27 || adapterPosition == 34) {
                    container.background = ContextCompat.getDrawable(context, R.drawable.shape_calendar_day_background_oval_right)
                } else {
                    container.background = ContextCompat.getDrawable(context, R.drawable.shape_calendar_day_background_rect)
                }

                // Configurando visual do dia referente a data de inicio e fim e dos dias que estão
                // entre essa data de inicio e fim
                val newItem = CustomDate(item.year, item.month, item.day)
                if(daysSelected.contains(newItem)) {

                    when (newItem) {
                        daysSelected.first() -> {
                            day.background = ContextCompat.getDrawable(context, R.drawable.shape_calendar_range_selected)
                            (day.background as GradientDrawable).setColor(Color.parseColor("#1592E6"))
                            day.setTextColor(Color.WHITE)

                            if(daysSelected.size > 1) {
                                container.background = ContextCompat.getDrawable(context, R.drawable.shape_calendar_end_day_background_right)
                            } else {
                                container.background = null
                            }

                        }
                        daysSelected.last() -> {
                            day.background = ContextCompat.getDrawable(context, R.drawable.shape_calendar_range_selected)
                            (day.background as GradientDrawable).setColor(Color.parseColor("#1592E6"))
                            day.setTextColor(Color.WHITE)
                            container.background = ContextCompat.getDrawable(context, R.drawable.shape_calendar_start_day_background_left)
                        }
                        else -> {
                            (day?.background as? GradientDrawable)?.setColor(Color.TRANSPARENT)
                            day.setTextColor(Color.parseColor("#1592E6"))
                            (container?.background as? GradientDrawable)?.setColor(Color.parseColor("#E8F4FC"))
                        }
                    }

                } else {
                    container.background = null
                    (day?.background as? GradientDrawable)?.setColor(Color.TRANSPARENT)
                }

                day.text = item.day.toString()

                setOnClickListener {
                    if(item.clickable)
                        onItemClicked(item, adapterPosition)
                }

            }
        }

        /**
         * Meses anteriores até o dia atual disponível
         * */
        private fun isBeforeCurrentDay(itemDate: CustomDate): Boolean {
            return itemDate.toDate() <= GregorianCalendar().getCurrentBrazilianCalendar().time
        }

        /**
         * Dia atual até proximos meses disponível
         * */
        private fun isAfterCurrentDay(itemDate: CustomDate): Boolean {
            return itemDate.toDate() >= GregorianCalendar().getCurrentBrazilianCalendar().time
        }

    }

}