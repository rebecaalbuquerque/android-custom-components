package com.albuquerque.customcalendar

import android.content.ClipDescription
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.albuquerque.customcalendar.extensions.format
import com.albuquerque.customcalendar.extensions.getCurrentBrazilianCalendar
import com.albuquerque.customcalendar.extensions.getPeriod
import com.albuquerque.customcalendar.extensions.toCustomDate
import kotlinx.android.synthetic.main.layout_custom_calendar.view.*
import java.util.*


class CustomCalendar @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var type: CalendarType
    private var availability: CalendarAvailability

    private var calendarAdapter: CalendarAdapter

    private var currentMonth = 0
    private var currentYear = 0

    private var start: Date? = null
    private var end: Date? = null
    private var now = GregorianCalendar().getCurrentBrazilianCalendar()

    private var isSelectingInicio = true

    private var onConfirmRange: ((start: Date, end: Date) -> Unit) = {_, _ ->  }
    private var onConfirmSelection: ((date: Date) -> Unit) = {_ ->  }

    init {
        View.inflate(context, R.layout.layout_custom_calendar, this)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.CustomCalendar)

        attributes.getInteger(R.styleable.CustomCalendar_customCalendarType, -1).let { value ->
            type = if(value == -1)
                CalendarType.SELECTION
            else
                CalendarType.getByValue(
                    value
                )

        }

        attributes.getInteger(R.styleable.CustomCalendar_customCalendarAvailability, -1).let { value ->
            availability = if(value == -1)
                CalendarAvailability.ALL
            else
                CalendarAvailability.getByValue(
                    value
                )
        }

        currentMonth = GregorianCalendar().getCurrentBrazilianCalendar().get(Calendar.MONTH)
        currentYear = GregorianCalendar().getCurrentBrazilianCalendar().get(Calendar.YEAR)

        when(type) {
            CalendarType.RANGE -> {
                inicio.visibility = View.VISIBLE
                fim.visibility = View.VISIBLE
                updateSelectionPeriodView()
            }

            CalendarType.SELECTION -> {
                inicio.visibility = View.GONE
                fim.visibility = View.GONE
            }
        }

        calendarAdapter = CalendarAdapter(availability, currentMonth) { daySelected, position ->

            when(type) {
                CalendarType.RANGE -> notifyCalendarRangeSetChanged(daySelected, position)
                CalendarType.SELECTION -> notifyCalendarSelectionSetChanged(daySelected, position)
            }

        }

        rvDays.adapter = calendarAdapter

        updateCurrentCalendar(currentYear, currentMonth)

        updateMonthButtonView()

        setupListeners()

        attributes.recycle()
    }

    private fun setupListeners() {
        inicio.setOnClickListener {
            isSelectingInicio = true
            updateSelectionPeriodView()
        }

        fim.setOnClickListener {
            isSelectingInicio = false
            updateSelectionPeriodView()
        }

        btnConfirmar.setOnClickListener {

            when(type) {

                CalendarType.RANGE -> {
                    if(start != null && end != null) {
                        onConfirmRange(start!!, end!!)
                    } else {
                        Toast.makeText(context, "É preciso selecionar uma data inicial e final.", Toast.LENGTH_LONG).show()
                    }
                }

                CalendarType.SELECTION -> onConfirmSelection(start!!)

            }

        }
    }

    /*
    * Funções de atualização das views
    * */

    private fun notifyCalendarRangeSetChanged(daySelected: CustomDate, position: Int) {
        val date = GregorianCalendar(currentYear, daySelected.month, daySelected.day).time

        if (isSelectingInicio) {
            // Se não tiver uma data final selecionada ou se tiver uma data final selecionada e
            // a data inicial atual for menor que a data final existente
            if ((end == null) || (end != null && date < end)) {
                if (start != null) {
                    val lastPosition = calendarAdapter.getItemPosition(start!!.toCustomDate())
                    calendarAdapter.notifyItemChanged(lastPosition)
                }

                start = date
                calendarAdapter.updateDaysSelected(listOf(start!!.toCustomDate()))
                calendarAdapter.notifyItemChanged(position)

            } else if ((end != null && date > end)) {
                start = date
                end = null
                calendarAdapter.updateDaysSelected(listOf(start!!.toCustomDate()))
                calendarAdapter.notifyDataSetChanged()

            }

        } else {
            // Se a data final atual for maior que a data inicial
            if (date > start) {
                end = date
                calendarAdapter.updateDaysSelected(generateDaysInRange(start!!, end!!))
            }
        }

        // Se exister tanto data atual quanto data final, mostra o periodo selecionado
        if (start != null && end != null) {
            periosoSelecionado.text = Pair(start!!, end!!).getPeriod()
            calendarAdapter.updateDaysSelected(generateDaysInRange(start!!, end!!))
            calendarAdapter.notifyDataSetChanged()
        } else {
            periosoSelecionado.text = ""
        }

    }

    private fun notifyCalendarSelectionSetChanged(daySelected: CustomDate, position: Int) {
        val date = GregorianCalendar(currentYear, daySelected.month, daySelected.day).time

        if(start != null) {
            val lastPosition = calendarAdapter.getItemPosition(start!!.toCustomDate())
            calendarAdapter.notifyItemChanged(lastPosition)
        }

        start = date
        periosoSelecionado.text = date.format("dd 'de' MMMM 'de' yyyy")
        calendarAdapter.updateDaysSelected(listOf(start!!.toCustomDate()))
        calendarAdapter.notifyItemChanged(position)
    }

    /**
     * Atualiza as views referente aos botões de próximo mês e mês anterior, se o [CalendarAvailability]
     * for ALL então os dois botões estarão sempre disponíveis; se for AFTER_CURRENT_DAY então não
     * será possível mudar para meses anteriores ao mês referente ao dia atual, apenas meses futuros;
     * se for BEFORE_CURRENT_DAY então não será possível mudar para meses depois do mês referente
     * ao dia atual, apenas meses anteriores;
     * */
    private fun updateMonthButtonView() {

        when(availability) {
            CalendarAvailability.ALL -> {
                nextMonth.setOnClickListener { nextMonth() }
                nextMonth.setColorFilter(Color.parseColor("#1592E6"))
                lastMonth.setOnClickListener { previousMonth() }
                lastMonth.setColorFilter(Color.parseColor("#1592E6"))
            }

            // Meses anteriores até o dia atual disponível
            CalendarAvailability.BEFORE_CURRENT_DAY -> {
                if(currentMonth < now.get(Calendar.MONTH) || currentYear < now.get(Calendar.YEAR)) {
                    nextMonth.setOnClickListener { nextMonth() }
                    nextMonth.setColorFilter(Color.parseColor("#1592E6"))
                } else {
                    nextMonth.setOnClickListener {}
                    nextMonth.setColorFilter(Color.parseColor("#CCCCCC"))
                }

                lastMonth.setOnClickListener { previousMonth() }
            }

            // Dia atual até proximos meses disponível
            CalendarAvailability.AFTER_CURRENT_DAY -> {
                if(currentMonth > now.get(Calendar.MONTH) || currentYear > now.get(Calendar.YEAR)) {
                    lastMonth.setOnClickListener { previousMonth() }
                    lastMonth.setColorFilter(Color.parseColor("#1592E6"))
                } else {
                    lastMonth.setOnClickListener {}
                    lastMonth.setColorFilter(Color.parseColor("#CCCCCC"))
                }

                nextMonth.setOnClickListener { nextMonth() }

            }
        }

    }

    private fun updateSelectionPeriodView() {
        if(isSelectingInicio) {
            (inicio.background as GradientDrawable).setColor(Color.parseColor("#1592E6"))
            inicio.setTextColor(Color.WHITE)
            (fim.background as GradientDrawable).setColor(Color.TRANSPARENT)
            fim.setTextColor(Color.parseColor("#3E3F40"))
        } else {
            (fim.background as GradientDrawable).setColor(Color.parseColor("#1592E6"))
            fim.setTextColor(Color.WHITE)
            (inicio.background as GradientDrawable).setColor(Color.TRANSPARENT)
            inicio.setTextColor(Color.parseColor("#3E3F40"))
        }
    }

    private fun nextMonth() {
        if(currentMonth == Calendar.DECEMBER) {
            currentMonth = Calendar.JANUARY
            currentYear++
        } else {
            currentMonth++
        }

        updateMonthButtonView()
        updateCurrentCalendar(currentYear, currentMonth)
    }

    private fun previousMonth() {
        if(currentMonth == Calendar.JANUARY) {
            currentMonth = Calendar.DECEMBER
            currentYear--
        } else {
            currentMonth--
        }

        updateMonthButtonView()
        updateCurrentCalendar(currentYear, currentMonth)
    }

    /**
     * Atualiza labels que exibem qual o mês e ano atual, assim como os dias disponível daquele mês
     * */
    private fun updateCurrentCalendar(currentYear: Int, currentMonth: Int) {
        calendarAdapter.refresh(generateCurrentMonth(currentYear, currentMonth), currentMonth)
        month.text = MonthCustomDate.getByValue(currentMonth).text
        year.text = currentYear.toString()
    }

    /*
    * Funções auxiliares para geração dos dias e meses do calendário
    * */
    private fun generateMonth(year: Int, month: Int): MutableList<CustomDate> {

        val mMonth: Int
        val mYear: Int

        when {
            month > Calendar.DECEMBER -> {
                mMonth = Calendar.JANUARY
                mYear = year + 1
            }
            month < Calendar.JANUARY -> {
                mMonth = Calendar.DECEMBER
                mYear = year - 1
            }
            else -> {
                mMonth = month
                mYear = year
            }
        }

        return (1..GregorianCalendar(mYear, mMonth, 1).getActualMaximum(Calendar.DAY_OF_MONTH))
                .map { value ->
                    CustomDate(
                        mYear,
                        mMonth,
                        value
                    )
                }.toMutableList()

    }

    private fun generateCurrentMonth(year: Int, month: Int): MutableList<CustomDate> {
        val calendar = GregorianCalendar()
        val result = mutableListOf<CustomDate>()

        val lastMonth = generateMonth(year, month - 1)
        val currentMonth = generateMonth(year, month)
        val nextMonth = generateMonth(year, month + 1)

        calendar.set(year, month, currentMonth.first().day)
        val startAt = calendar.get(Calendar.DAY_OF_WEEK)

        calendar.set(Calendar.DAY_OF_MONTH, currentMonth.last().day)
        val endAt = calendar.get(Calendar.DAY_OF_WEEK)

        val lastDays = lastMonth.takeLast(startAt - 1)
        val nextDays = nextMonth.take(7 - endAt)

        result.addAll(lastDays)
        result.addAll(currentMonth)
        result.addAll(nextDays)

        return result
    }

    /**
     * Função que gera uma lista de [CustomDate] a partir de um dia inicial e um dia final.
     * */
    private fun generateDaysInRange(start: Date, end: Date): List<CustomDate> {
        val result = mutableListOf<CustomDate>()
        var current = start
        val calendar = GregorianCalendar().getCurrentBrazilianCalendar()

        while (current.before(end)) {
            calendar.time = current

            result.add(
                CustomDate(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
            )

            calendar.add(Calendar.DATE, 1)
            current = calendar.time
        }

        result.add(
            CustomDate(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        )

        return result
    }

    /*
    * Ações
    * */
    fun setOnConfirmRange(onConfirmRange:(start: Date, end: Date) -> Unit) {
        this.onConfirmRange = onConfirmRange
    }

    fun setOnConfirmSelection(onConfirmSelection:(date: Date) -> Unit) {
        this.onConfirmSelection = onConfirmSelection
    }

    fun setCalendarAvailability(availability: CalendarAvailability){
        this.availability = availability
        requestLayout()
    }

    enum class CalendarType(val value: Int) {
        RANGE(1), SELECTION(0);

        companion object {
            fun getByValue(i: Int): CalendarType {
                return values().find { it.value == i } ?: SELECTION
            }
        }
    }

    enum class CalendarAvailability(val value: Int, val description: String) {
        ALL(0, "Standard"),                 // todos os meses disponívels
        BEFORE_CURRENT_DAY(1, "Before Current Day"),  // meses anteriores até o dia atual disponível
        AFTER_CURRENT_DAY(2, "After Current Day");   // dia atual até proximos meses disponível

        companion object {

            fun getByValue(i: Int): CalendarAvailability {
                return values().find { it.value == i } ?: ALL
            }
        }
    }

}