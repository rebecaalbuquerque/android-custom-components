package com.albuquerque.customcalendar

data class CustomDate(
        val year: Int,
        val month: Int,
        val day: Int,
        var clickable: Boolean = true
)