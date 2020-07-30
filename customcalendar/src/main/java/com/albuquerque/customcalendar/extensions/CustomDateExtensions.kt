package com.albuquerque.customcalendar.extensions


import com.albuquerque.customcalendar.CustomDate
import java.util.*

fun Date.toCustomDate(): CustomDate {
    val calendar = GregorianCalendar().getCurrentBrazilianCalendar().apply { time = this@toCustomDate }
    return CustomDate(
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(
            Calendar.DAY_OF_MONTH
        )
    )
}

fun CustomDate.toDate() = GregorianCalendar(this.year, this.month, this.day).time