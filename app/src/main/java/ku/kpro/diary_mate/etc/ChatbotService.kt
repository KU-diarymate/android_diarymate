package ku.kpro.diary_mate.etc

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
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
            chatbot.sendRandomQuestion()
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
}
