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

class Chatbot() {

    private val questions = listOf(
        "오늘 어떤 일이 있었나요?",
        "하루 중 가장 기억에 남는 순간은 무엇이었나요?",
        "오늘 특별한 일이 있었나요?",
        // TODO: ChatGPT로 생성한 질문 리스트로 대체
    )

    fun getQuestions() : String {
        return questions.random()
    }

}