package com.albuquerque.customcomponents

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.albuquerque.customcalendar.CustomCalendar
import com.albuquerque.customcomponents.CustomCalendarActivity.Companion.CALENDAR_AVAILABILITY
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnGoToHorizontalSelection.setOnClickListener {
            startActivity(Intent(this, HorizontalSelectionActivity::class.java))
        }

        btnGoToLoading.setOnClickListener {
            startActivity(Intent(this, LoadingActivity::class.java))
        }

        btnGoToCustomCalendarStandard.setOnClickListener {
            startActivity(Intent(this, CustomCalendarActivity::class.java).apply {
                putExtra(CALENDAR_AVAILABILITY, CustomCalendar.CalendarAvailability.ALL.value)
            })
        }

        btnGoToCustomCalendarBefore.setOnClickListener {
            startActivity(Intent(this, CustomCalendarActivity::class.java).apply {
                putExtra(CALENDAR_AVAILABILITY, CustomCalendar.CalendarAvailability.BEFORE_CURRENT_DAY.value)
            })
        }

        btnGoToCustomCalendarAfter.setOnClickListener {
            startActivity(Intent(this, CustomCalendarActivity::class.java).apply {
                putExtra(CALENDAR_AVAILABILITY, CustomCalendar.CalendarAvailability.AFTER_CURRENT_DAY.value)
            })
        }

    }

}
