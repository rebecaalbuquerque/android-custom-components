package com.albuquerque.customcomponents.widget.calendar

import java.util.*

enum class MonthCustomDate(val text: String, val value: Int) {
    JANUARY("Janeiro", Calendar.JANUARY),
    FEBRUARY("Fevereiro", Calendar.FEBRUARY),
    MARCH("Março", Calendar.MARCH),
    APRIL("Abril", Calendar.APRIL),
    MAY("Maio", Calendar.MAY),
    JUNE("Junho", Calendar.JUNE),
    JULY("Julho", Calendar.JULY),
    AUGUST("Agosto", Calendar.AUGUST),
    SEPTEMBER("Setembro", Calendar.SEPTEMBER),
    OCTOBER("Outubro", Calendar.OCTOBER),
    NOVEMBER("Novembro", Calendar.NOVEMBER),
    DECEMBER("Dezembro", Calendar.DECEMBER);

    companion object {
        fun getByValue(i: Int): MonthCustomDate {
            return values().find { it.value == i } ?: JANUARY
        }
    }
}