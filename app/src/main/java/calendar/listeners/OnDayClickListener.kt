package calendar.listeners

import calendar.EventDay

interface OnDayClickListener {
    fun onDayClick(eventDay: EventDay?)
}