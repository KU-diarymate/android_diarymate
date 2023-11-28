package ku.kpro.diary_mate.etc

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ku.kpro.diary_mate.R
import ku.kpro.diary_mate.activity.MainActivity
import ku.kpro.diary_mate.fragment.ChattingFragment

class ChatbotService : Service() {

    private lateinit var chatbot: Chatbot
    private val handler = Handler()

    override fun onCreate() {
        super.onCreate()
        chatbot = Chatbot(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //handler.postDelayed(chatbotRunnable, 5 * 60 * 1000) // 5분마다 실행
        handler.postDelayed(chatbotRunnable, 15000) // Test: 15초마다 실행
        return START_STICKY
    }

    private val chatbotRunnable = object : Runnable {
        override fun run() {
            sendRandomQuestion()
            //handler.postDelayed(this, 5 * 60 * 1000) // 다음 실행을 5분 후로 예약
            handler.postDelayed(this, 15000) // Test: 다음 실행을 15초 후로 예약
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(chatbotRunnable)
    }

    private fun sendRandomQuestion() {
        val randomQuestion = chatbot.getQuestions()

        // Notification Channel 생성 (Android Oreo 이상에서 필요)
        val channelId = "chatbot_channel"
        val channelName = "Chatbot Channel"
        val channelDescription = "Channel for Chatbot Notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDescription
        }


        val notificationManager =
            baseContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        // Notification 생성
        val notificationBuilder = NotificationCompat.Builder(baseContext, "chatbot_channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Random Question")
            .setContentText(randomQuestion)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)  // Auto-cancel the notification when tapped

        // 알림을 터치했을 때 ChattingFragment로 이동
        val resultIntent = Intent(baseContext, MainActivity::class.java)
        resultIntent.putExtra("fragment_to_load", ChattingFragment::class.java.name)
        val resultPendingIntent = PendingIntent.getActivity(
            baseContext,
            0,
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        notificationBuilder.setContentIntent(resultPendingIntent)

        // 알림을 표시
        with(NotificationManagerCompat.from(baseContext)) {
            if (ActivityCompat.checkSelfPermission(
                    baseContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: 권한이 부여되지 않았을 때, 권한을 요청하고 사용자가 권한을 부여할 경우에 대한 처리
            }
            notify(1, notificationBuilder.build()) // Use a unique notification ID
        }
    }
}
