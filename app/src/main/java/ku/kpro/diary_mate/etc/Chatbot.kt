package ku.kpro.diary_mate.etc

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ku.kpro.diary_mate.R
import ku.kpro.diary_mate.activity.MainActivity
import ku.kpro.diary_mate.fragment.ChattingFragment

class Chatbot(private val context: Context) {

    private val questions = listOf(
        "오늘 어떤 일이 있었나요?",
        "하루 중 가장 기억에 남는 순간은 무엇이었나요?",
        "오늘 특별한 일이 있었나요?",
        // TODO: ChatGPT로 생성한 질문 리스트로 대체
    )

    fun sendRandomQuestion() {
        val randomQuestion = questions.random()

        // Notification Channel 생성 (Android Oreo 이상에서 필요)
        val channelId = "chatbot_channel"
        val channelName = "Chatbot Channel"
        val channelDescription = "Channel for Chatbot Notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDescription
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        // Notification 생성
        val notificationBuilder = NotificationCompat.Builder(context, "chatbot_channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Random Question")
            .setContentText(randomQuestion)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)  // Auto-cancel the notification when tapped

        // 알림을 터치했을 때 ChattingFragment로 이동
        val resultIntent = Intent(context, MainActivity::class.java)
        resultIntent.putExtra("fragment_to_load", ChattingFragment::class.java.name)
        val resultPendingIntent = PendingIntent.getActivity(
            context,
            0,
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        notificationBuilder.setContentIntent(resultPendingIntent)

        // 알림을 표시
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: 권한이 부여되지 않았을 때, 권한을 요청하고 사용자가 권한을 부여할 경우에 대한 처리
            }
            notify(1, notificationBuilder.build()) // Use a unique notification ID
        }
    }
}