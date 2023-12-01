package ku.kpro.diary_mate.etc

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.IntentService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.SystemClock
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ku.kpro.diary_mate.R
import ku.kpro.diary_mate.activity.MainActivity
import ku.kpro.diary_mate.fragment.ChattingFragment
import java.util.Timer
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timerTask

class ChatbotService : IntentService("ChatbotService")  {

    private lateinit var chatbot: Chatbot

    companion object {
        private val ALARM_INTERVAL = TimeUnit.MINUTES.toMillis(0.25.toLong())
        private const val CHANNEL_ID = "chatbot_channel"
    }

    override fun onHandleIntent(intent: Intent?) {
        chatbot = Chatbot()
        sendRandomQuestion()
    }

    override fun onCreate() {
        super.onCreate()
        chatbot = Chatbot()
        startBackgroundTask()
    }

    private fun startBackgroundTask() {
        val timer = Timer()
        timer.schedule(timerTask {sendRandomQuestion()}, 0, 15000)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun sendRandomQuestion() {
        val randomQuestion = chatbot.getQuestions()

        // Generate a unique notification ID based on current timestamp
        val notificationId = System.currentTimeMillis().toInt()

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@ChatbotService,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling ActivityCompat#requestPermissions
                return
            }
            if (randomQuestion != "")
                notify(notificationId, createNotification(randomQuestion))
        }
    }

    //showNotification
    private fun createNotificationChannel() {
        val channelName = "Chatbot Channel"
        val channelDescription = "Channel for Chatbot Notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, channelName, importance).apply {
            description = channelDescription
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(content: String): Notification {
        val resultIntent = Intent(this, MainActivity::class.java)
        resultIntent.putExtra("fragment_to_load", ChattingFragment::class.java.name)
        val resultPendingIntent = PendingIntent.getActivity(
            this,
            0,
            resultIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Random Question")
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setOngoing(false)
            .setContentIntent(resultPendingIntent)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Reschedule the service to run after the specified interval
        scheduleService()
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleService() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ChatbotService::class.java)
        val pendingIntent =
            PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Schedule the service to run at the specified interval
        val triggerAtMillis = SystemClock.elapsedRealtime() + ALARM_INTERVAL
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingIntent)
    }
}
