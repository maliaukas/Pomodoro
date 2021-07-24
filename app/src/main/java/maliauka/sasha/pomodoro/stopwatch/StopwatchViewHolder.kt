package maliauka.sasha.pomodoro.stopwatch

import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import maliauka.sasha.pomodoro.R
import maliauka.sasha.pomodoro.databinding.ItemBinding
import maliauka.sasha.pomodoro.util.HUNDRED_MS
import maliauka.sasha.pomodoro.util.displayTime

class StopwatchViewHolder(
    private val binding: ItemBinding,
    private val listener: StopwatchListener
) : RecyclerView.ViewHolder(binding.root) {

    // timer that only updates text, doesn't count time
    private var timer: CountDownTimer? = null

    fun bind(stopwatch: Stopwatch) {
        if (stopwatch.isFinished) {
            setFinishedView(stopwatch)
        } else {
            setWorkingView(stopwatch)
        }

        if (stopwatch.isStarted) {
            startTimer(stopwatch)
        } else {
            stopTimer()
        }

        initButtonsListeners(stopwatch)
    }

    private fun setFinishedView(stopwatch: Stopwatch) {
        binding.stopwatchTimer.text = stopwatch.totalMs.displayTime()

        binding.customView.setPeriod(stopwatch.totalMs)
        binding.customView.setCurrent(stopwatch.totalMs - 1)

        binding.layout.background = getDrawable(binding.root.context, R.color.purple_200)

        binding.startPauseButton.isEnabled = false

        stopBlinking()
    }

    private fun setWorkingView(stopwatch: Stopwatch) {
        binding.stopwatchTimer.text = stopwatch.untilFinishedMs.displayTime()

        binding.customView.setPeriod(stopwatch.totalMs)
        binding.customView.setCurrent(stopwatch.totalMs - stopwatch.untilFinishedMs)

        binding.layout.background = getDrawable(binding.root.context, R.color.white)

        binding.startPauseButton.isEnabled = true
    }

    private fun initButtonsListeners(stopwatch: Stopwatch) {
        binding.startPauseButton.setOnClickListener {
            if (stopwatch.isStarted) {
                listener.stop(stopwatch.id)
            } else {
                listener.start(stopwatch.id)
            }
        }

        binding.deleteButton.setOnClickListener { listener.delete(stopwatch.id) }
    }

    private fun startTimer(stopwatch: Stopwatch) {
        binding.startPauseButton.text = binding.root.context.getString(R.string.stop)

        timer?.cancel()
        timer = getCountDownTimer(stopwatch)
        timer?.start()

        startBlinking()
    }

    private fun stopTimer() {
        binding.startPauseButton.text = binding.root.context.getString(R.string.start)

        timer?.cancel()

        stopBlinking()
    }

    private fun startBlinking() {
        binding.blinkingIndicator.isInvisible = false
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()
    }

    private fun stopBlinking() {
        binding.blinkingIndicator.isInvisible = true
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
    }

    private fun getCountDownTimer(stopwatch: Stopwatch): CountDownTimer {
        return object : CountDownTimer(stopwatch.untilFinishedMs, HUNDRED_MS) {
            override fun onTick(millisUntilFinished: Long) {
                binding.stopwatchTimer.text = stopwatch.untilFinishedMs.displayTime()
                binding.customView.setCurrent(stopwatch.totalMs - stopwatch.untilFinishedMs)
            }

            override fun onFinish() {
                setFinishedView(stopwatch)
            }
        }
    }
}