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
        "오늘 아침에 눈을 뜨고 가장 먼저 한 일은 무엇이었나요?",
        "아침에 일어나서 가장 먼저 떠오른 생각은 무엇이었나요?",
        "아침 식사로 먹은 음식은 무엇이었나요?",
        "오늘의 날씨는 어떤가요?",
        "출근이나 학교 등 일상적인 시작을 어떻게 했나요?",
        "오늘의 일정 중에서 가장 기대되는 부분은 무엇인가요?",
        "가장 어려웠던 순간은 무엇이었나요?",
        "점심 식사로 먹은 것은 무엇이었나요?",
        "오후에 특별한 일이 있었나요?",
        "일상 속에서 웃은 일이 있었나요?",
        "오늘 마주한 도전적인 상황에 대처한 방법은 무엇이었나요?",
        "가장 기억에 남는 대화는 어떤 내용이었나요?",
        "저녁 식사로 먹은 것은 무엇이었나요?",
        "일상 생활에서 새롭게 시도한 것이 있었나요?",
        "오늘 하루를 한 마디로 표현한다면 무엇이어야 할까요?",
        "가장 힘들었던 순간에 힘을 얻은 곳은 어디인가요?",
        "하루 중에 가장 편안한 순간은 언제였나요?",
        "오늘의 일상 속에서 느낀 감사한 순간이 있었나요?",
        "가장 큰 도전이었던 일은 무엇이었나요?",
        "하루 동안 어떤 사람들을 만났나요?",
        "일상 속에서 느낀 성취감이 있는 일이 있었나요?",
        "가장 인상 깊은 사건이나 경험은 무엇이었나요?",
        "오늘 하루 동안 배운 것이 있었나요?",
        "일상 생활에서 가장 중요하게 생각하는 가치는 무엇인가요?",
        "가장 기대하는 일은 무엇인가요?",
        "하루를 정리하면서 가장 뿌듯했던 순간은 언제였나요?",
        "가장 소중한 물건이나 사람에 대한 생각이 있나요?",
        "하루 중에 자주 하는 생각이나 고민이 있었나요?",
        "가장 좋아하는 일상적인 활동은 무엇인가요?",
        "오늘 하루 동안의 목표를 어떻게 설정했나요?",
        "일상 속에서 자주 하는 생각 중 하나를 공유해 주세요.",
        "가장 행복한 순간은 언제였나요?",
        "오늘 하루 동안 무엇을 배운 것 같아요?",
        "일상에서 자주 하는 습관이 있나요?",
        "가장 좋아하는 날씨는 무엇인가요?",
        "하루를 마무리하면서 가장 소중한 생각은 무엇인가요?",
        "오늘 하루 동안의 변화를 느낀 순간이 있었나요?",
        "일상 속에서 자주 듣는 음악 장르가 있나요?",
        "가장 피곤한 순간은 언제였나요?",
        "오늘의 일정을 기록한 것 중에서 가장 중요한 일은 무엇인가요?",
        "가장 좋아하는 색깔은 무엇인가요?",
        "일상 생활에서 자주 마주치는 문제 중 하나는 무엇인가요?",
        "가장 기억에 남는 꿈을 최근에 꾸었나요?",
        "하루 동안 가장 많이 한 활동은 무엇인가요?",
        "오늘 하루 동안의 목표를 어떻게 설정했나요?"
    )


    fun getQuestions() : String {
        return questions.random()
    }

}