package com.albuquerque.customcomponents.widget.calendar

data class CustomDate(
        val year: Int,
        val month: Int,
        val day: Int,
        var clickable: Boolean = true
)