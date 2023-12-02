package ku.kpro.diary_mate.etc

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class ChatbotService: Service() {

    private lateinit var chatbot: Chatbot

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
                    return
                }
                notify(notificationId, createNotification(randomQuestion))
            }*/
        }
    }

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