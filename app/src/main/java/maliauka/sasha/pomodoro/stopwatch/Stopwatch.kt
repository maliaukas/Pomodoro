package maliauka.sasha.pomodoro.stopwatch

data class Stopwatch(
    val id: Int,
    val totalMs: Long,
    var untilFinishedMs: Long = totalMs,
    var isStarted: Boolean = false,
    var isFinished: Boolean = false
)