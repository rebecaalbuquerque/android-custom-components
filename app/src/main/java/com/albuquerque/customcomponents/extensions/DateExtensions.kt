package com.albuquerque.customcomponents.extensions

import java.text.SimpleDateFormat
import java.util.*

/**
 * Transforma um [Date] em uma [String]
 * @param pattern padrão utilizado na transformação
 * */
fun Date.format(pattern: String): String = SimpleDateFormat(pattern, Locale("pt", "BR")).format(this)

/**
 * Gera uma [String] referente a um período partir de um [Pair] de [Date]. O primeiro elemento do
 * [Pair] é a data de início desse período, enquanto que o segundo elemento é a data de fim.
 * */
fun Pair<Date, Date>.getPeriod(): String {
    val calendarFrom = Calendar.getInstance().apply { time = first }
    val calendarTo = Calendar.getInstance().apply { time = second }

    val fromDate: String
    val toDate: String

    if(
        calendarFrom.get(Calendar.MONTH) == calendarTo.get(Calendar.MONTH) &&
        calendarFrom.get(Calendar.YEAR) == calendarTo.get(Calendar.YEAR)
    ) {
        fromDate = first.format("dd")
        toDate = second.format("dd 'de' MMMM 'de' yyyy")

    } else if(calendarFrom.get(Calendar.YEAR) == calendarTo.get(Calendar.YEAR)) {
        fromDate = first.format("dd 'de' MMMM")
        toDate = second.format("dd 'de' MMMM 'de' yyyy")

    } else {
        fromDate = first.format("dd 'de' MMMM 'de' yyyy")
        toDate = second.format("dd 'de' MMMM 'de' yyyy")
    }

    return "$fromDate à $toDate"
}

fun GregorianCalendar.getCurrentBrazilianCalendar(): GregorianCalendar {
    return GregorianCalendar(Locale("pt", "BR"))
}