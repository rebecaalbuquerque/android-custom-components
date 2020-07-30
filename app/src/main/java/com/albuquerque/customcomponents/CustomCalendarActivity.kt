package com.albuquerque.customcomponents

import android.os.Bundle
import android.widget.Toast
import com.albuquerque.customcalendar.CustomCalendar
import com.albuquerque.customcalendar.CustomCalendar.CalendarAvailability.*
import com.albuquerque.customcalendar.extensions.format
import com.albuquerque.customcomponents.extensions.setGone
import com.albuquerque.customcomponents.extensions.setVisible
import kotlinx.android.synthetic.main.activity_custom_calendar.*

class CustomCalendarActivity : BaseActivity() {

    private lateinit var calendarAvailability: CustomCalendar.CalendarAvailability

    companion object {
        const val CALENDAR_AVAILABILITY = "CALENDAR_AVAILABILITY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_calendar)

        calendarAvailability = CustomCalendar.CalendarAvailability.getByValue(intent.extras?.getInt(CALENDAR_AVAILABILITY) ?: ALL.value)

        supportActionBar?.let { actionBar ->
            actionBar.apply {
                this.setDisplayHomeAsUpEnabled(true)
                this.title = "Custom Calendar (${calendarAvailability.description})"
            }
        }

        setupCalendar()

    }

    private fun setupCalendar() {
        when(calendarAvailability) {

            ALL -> {
                calendarStandard.setVisible()
                calendarAfter.setGone()
                calendarBefore.setGone()

                with(calendarStandard) {
                    setOnConfirmRange { start, end ->
                        Toast.makeText(
                            this@CustomCalendarActivity,
                            "Start: ${start.format("dd/MM/yy")}, End: ${end.format("dd/MM/yy")}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            BEFORE_CURRENT_DAY -> {
                calendarBefore.setVisible()
                calendarAfter.setGone()
                calendarStandard.setGone()

                with(calendarBefore) {
                    setOnConfirmRange { start, end ->
                        Toast.makeText(
                            this@CustomCalendarActivity,
                            "Start: ${start.format("dd/MM/yy")}, End: ${end.format("dd/MM/yy")}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            AFTER_CURRENT_DAY -> {
                calendarAfter.setVisible()
                calendarBefore.setGone()
                calendarStandard.setGone()

                with(calendarAfter) {
                    setOnConfirmRange { start, end ->
                        Toast.makeText(
                            this@CustomCalendarActivity,
                            "Start: ${start.format("dd/MM/yy")}, End: ${end.format("dd/MM/yy")}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

        }
    }

}