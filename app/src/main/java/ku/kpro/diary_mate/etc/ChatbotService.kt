package ku.kpro.diary_mate.etc

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import ku.kpro.diary_mate.R
import ku.kpro.diary_mate.activity.MainActivity
import ku.kpro.diary_mate.fragment.ChattingFragment
import java.util.concurrent.TimeUnit

class ChatbotService: Service() {

    private lateinit var chatbot: Chatbot


    companion object {
        //private const val CHANNEL_ID = "chatbot_channel"
    }

    override fun onCreate() {
        super.onCreate()
        chatbot = Chatbot()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //createNotificationChannel()
        sendRandomQuestion()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun sendRandomQuestion() {
        val randomQuestion = chatbot.getQuestions()
        Log.e("randomQuestion", randomQuestion)

        // ChattingFragment가 현재 활성화되어 있는지 확인
        val chattingFragment = (applicationContext as? DiaryMateApplication)?.currentChattingFragment
        Log.e("ChatbotService", "randomQuestion.isNotBlank(): " + randomQuestion.isNotBlank().toString())
        Log.e("ChatbotService", "chattingFragment != null: " + (chattingFragment != null).toString())
        if (randomQuestion.isNotBlank() && chattingFragment != null) {
            // 얻은 질문을 채팅 기록에 추가
            chattingFragment.addMessage(randomQuestion, "Chatbot")
            Log.e("ChatbotService", "chattingFragment.addMessage")
            chattingFragment.chatAdapter.notifyItemInserted(chattingFragment.messages.size - 1)
            chattingFragment.binding.recyclerView.scrollToPosition(chattingFragment.chatAdapter.itemCount - 1)

            /*val notificationId = System.currentTimeMillis().toInt()

            with(NotificationManagerCompat.from(this)) {
                if (ActivityCompat.checkSelfPermission(
                        this@ChatbotService,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: ActivityCompat#requestPermissions를 호출하는 것을 고려
                    return
                }
                notify(notificationId, createNotification(randomQuestion))
            }*/
        }
    }

    //showNotification
    /*private fun createNotificationChannel() {
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
            .setContentTitle("오늘의 질문")
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setOngoing(false)
            .setContentIntent(resultPendingIntent)
            .build()
    }*/
}