package maliauka.sasha.pomodoro

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import maliauka.sasha.pomodoro.databinding.ActivityMainBinding
import maliauka.sasha.pomodoro.foreground.ForegroundService
import maliauka.sasha.pomodoro.stopwatch.Stopwatch
import maliauka.sasha.pomodoro.stopwatch.StopwatchAdapter
import maliauka.sasha.pomodoro.stopwatch.StopwatchListener
import maliauka.sasha.pomodoro.util.*
import java.util.*

class MainActivity : AppCompatActivity(), StopwatchListener, LifecycleObserver {

    private lateinit var binding: ActivityMainBinding

    private val stopwatchAdapter = StopwatchAdapter(this)
    private val stopwatches = mutableListOf<Stopwatch>()
    private var nextId = 0

    private val currTimer: Stopwatch?
        get() = stopwatches.find { it.isStarted }

    private var timer: CountDownTimer? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        if (currTimer != null) {
            val startIntent = Intent(this, ForegroundService::class.java)
            startIntent.putExtra(COMMAND_ID, COMMAND_START)
            startIntent.putExtra(UNTIL_FINISHED_MS, currTimer?.untilFinishedMs ?: 0L)
            startIntent.putExtra(SYSTEM_TIME_WHEN_START_FOREGROUND, System.currentTimeMillis())
            startService(startIntent)
        } else onAppForegrounded()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = stopwatchAdapter
        }

        binding.addNewStopwatchButton.setOnClickListener {
            val minutes = binding.editTextMinutes.text.toString().toLongOrNull() ?: 0
            val seconds = binding.editTextSeconds.text.toString().toLongOrNull() ?: 0

            val timerValueMs = (minutes * 60 + seconds) * 1000

            if (timerValueMs == 0L)
                return@setOnClickListener

            stopwatches.add(Stopwatch(nextId++, timerValueMs))
            stopwatchAdapter.submitList(stopwatches.toList())
        }
    }

    private fun startTimer(stopwatch: Stopwatch) {
        timer?.cancel()
        timer = getTimer(stopwatch)
        timer?.start()
    }

    private fun stopTimer() {
        timer?.cancel()
    }

    override fun start(id: Int) {
        changeStopwatch(id, true)
        startTimer(stopwatches.find { it.id == id }!!)
    }

    override fun stop(id: Int) {
        changeStopwatch(id, false)
        stopTimer()
    }

    override fun delete(id: Int) {
        stopwatches.remove(stopwatches.find { it.id == id })
        stopTimer()
        stopwatchAdapter.submitList(stopwatches.toList())
    }

    private fun changeStopwatch(id: Int, toStart: Boolean) {
        val newTimers = mutableListOf<Stopwatch>()

        if (toStart) {
            stopwatches.forEach {
                if (it.id == id) {
                    newTimers.add(it.copy(isStarted = toStart))
                } else {
                    newTimers.add(it.copy(isStarted = false))
                }
            }
        } else {
            stopwatches.forEach {
                newTimers.add(it.copy(isStarted = false))
            }
        }

        stopwatchAdapter.submitList(newTimers)
        stopwatches.clear()
        stopwatches.addAll(newTimers)
    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Exit?")
            .setMessage("Timers will be lost!")
            .setPositiveButton("Exit") { _, _ ->
                timer?.cancel()
                super.onBackPressed()
            }.setNegativeButton("Stay") { dialogInterface: DialogInterface, _ ->
                dialogInterface.cancel()
            }.show()
    }

    private fun getTimer(stopwatch: Stopwatch): CountDownTimer {
        return object : CountDownTimer(stopwatch.untilFinishedMs, HUNDRED_MS) {
            override fun onTick(untilFinishedMs: Long) {
                stopwatch.untilFinishedMs = untilFinishedMs
            }

            override fun onFinish() {
                stopwatch.untilFinishedMs = 0L
                stopwatch.isFinished = true
                stopwatch.isStarted = false
            }
        }
    }

}