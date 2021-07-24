package maliauka.sasha.pomodoro.foreground

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import maliauka.sasha.pomodoro.MainActivity
import maliauka.sasha.pomodoro.R
import maliauka.sasha.pomodoro.util.*

class ForegroundService : Service() {

    private var isServiceStarted = false
    private var notificationManager: NotificationManager? = null

    private var timer: CountDownTimer? = null

    private val builder by lazy {
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Pomodoro Timer")
            .setGroup("Timer")
            .setGroupSummary(false)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(getPendingIntent())
            .setSilent(true)
            .setSmallIcon(R.drawable.ic_baseline_timer_24)
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        processCommand(intent)
        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun processCommand(intent: Intent?) {
        val command = intent?.extras?.getString(COMMAND_ID) ?: INVALID
        Log.d("TAG", "processCommand: $command")
        when (command) {
            COMMAND_START -> {
                val startTime = intent?.extras?.getLong(UNTIL_FINISHED_MS) ?: return
                val systemTimeWhenStart = intent.extras?.getLong(SYSTEM_TIME_WHEN_START_FOREGROUND)
                    ?: System.currentTimeMillis()
                commandStart(startTime, systemTimeWhenStart)
            }
            COMMAND_STOP -> commandStop()
            INVALID -> return
        }
    }

    private fun commandStart(startTime: Long, systemTime: Long) {
        if (isServiceStarted) {
            return
        }
        Log.i("TAG", "commandStart()")
        try {
            moveToStartedState()
            startForegroundAndShowNotification()
            continueTimer(startTime, systemTime)
        } finally {
            isServiceStarted = true
        }
    }

    private fun continueTimer(startTime: Long, systemTime: Long) {
        val correctedTime = startTime - (System.currentTimeMillis() - systemTime)

        timer = object : CountDownTimer(correctedTime, ONE_SECOND) {
            override fun onTick(untilFinishedMs: Long) {
                notificationManager?.notify(
                    NOTIFICATION_ID,
                    getNotification(
                        untilFinishedMs.displayTime()
                    )
                )
            }

            override fun onFinish() {
                notificationManager?.notify(
                    NOTIFICATION_ID,
                    getNotification(
                        START_TIME
                    )
                )
            }
        }

        timer?.start()
    }

    private fun commandStop() {
        if (!isServiceStarted) {
            return
        }
        try {
            timer?.cancel()
            stopForeground(true)
            stopSelf()
        } finally {
            isServiceStarted = false
        }
    }

    private fun moveToStartedState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("TAG", "moveToStartedState(): Running on Android O or higher")
            startForegroundService(Intent(this, ForegroundService::class.java))
        } else {
            Log.d("TAG", "moveToStartedState(): Running on Android N or lower")
            startService(Intent(this, ForegroundService::class.java))
        }
    }

    private fun startForegroundAndShowNotification() {
        createChannel()
        val notification = getNotification("content")
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun getNotification(content: String) = builder.setContentText(content).build()

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Pomodoro"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(
                CHANNEL_ID, channelName, importance
            )
            notificationManager?.createNotificationChannel(notificationChannel)
        }
    }

    private fun getPendingIntent(): PendingIntent? {
        val resultIntent = Intent(this, MainActivity::class.java)
        resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_SINGLE_TOP
        return PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_ONE_SHOT)
    }

    private companion object {

        private const val CHANNEL_ID = "Channel_ID"
        private const val NOTIFICATION_ID = 777
    }
}