package maliauka.sasha.pomodoro.util

const val START_TIME = "00:00:00"

const val INVALID = "INVALID"
const val COMMAND_START = "COMMAND_START"
const val COMMAND_STOP = "COMMAND_STOP"

const val COMMAND_ID = "COMMAND_ID"
const val UNTIL_FINISHED_MS = "UNTIL_FINISHED_MS"
const val SYSTEM_TIME_WHEN_START_FOREGROUND = "SYSTEM_TIME_WHEN_START_FOREGROUND"

const val HUNDRED_MS = 100L
const val ONE_SECOND = 1000L


fun Long.displayTime(): String {
    if (this <= 0L) {
        return START_TIME
    }
    val h = this / 1000 / 3600
    val m = this / 1000 % 3600 / 60
    val s = this / 1000 % 60

    return "${displaySlot(h)}:${displaySlot(m)}:${displaySlot(s)}"
}

fun displaySlot(count: Long): String {
    return if (count / 10L > 0) {
        "$count"
    } else {
        "0$count"
    }
}