package ku.kpro.diary_mate.fragment

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.realm.Realm
import ku.kpro.diary_mate.data.ChatMessage
import ku.kpro.diary_mate.data.Diary
import ku.kpro.diary_mate.data.DiaryMateSetting
import ku.kpro.diary_mate.databinding.FragmentChattingBinding
import ku.kpro.diary_mate.etc.ChatAdapter
import ku.kpro.diary_mate.etc.Chatbot
import ku.kpro.diary_mate.etc.DiaryMateApplication.Companion.addNewMessage
import ku.kpro.diary_mate.etc.DiaryMateApplication.Companion.pref
import ku.kpro.diary_mate.etc.DiaryMateApplication.Companion.setting
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChattingFragment : Fragment() {

    lateinit var binding: FragmentChattingBinding
    lateinit var messages : MutableList<ChatMessage>
    private lateinit var realm : Realm
    lateinit var chatAdapter: ChatAdapter

    private var apiHandler = Chatbot()
    interface FabClickListener {
        fun onFabClick()
    }

    private var fabClickListener: FabClickListener? = null

    // FabClickListener의 setter 메서드
    fun setFabClickListener(listener: FabClickListener) {
        fabClickListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChattingBinding.inflate(inflater, container, false)
        realm = Realm.getDefaultInstance()
        getMessages()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ChatAdapter를 초기화하고 RecyclerView에 연결합니다.
        chatAdapter = ChatAdapter(messages)
        binding.recyclerView.adapter = chatAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.scrollToPosition(chatAdapter.itemCount - 1)

        // 전송 버튼에 클릭 리스너를 등록합니다.
        binding.chattingSendBtn.setOnClickListener {
            // 응답하는 부분
            apiHandler.callApi_forchat(sendMessage(), object : Chatbot.ApiListener {
                override fun onResponse(response: Any) {
                    arriveMessage(response.toString())
                }

                override fun onFailure(error: String) {
                    Log.d("tintin", "onFailure: $error")
                    Toast.makeText(context, "네트워크 오류", Toast.LENGTH_SHORT).show()
                }
            })
            //Handler(Looper.getMainLooper()).postDelayed({ arriveMessage("응답") }, 1000)
        }

        binding.fab.setOnClickListener {
            //fabClickListener?.onFabClick()
            val chatBot = Chatbot()
            chatBot.callApi_extract(getLogs(), object : Chatbot.ApiListener {
                override fun onResponse(response: Any) {
                    Log.d("tintin", "onResponse: $response")
                    val realm = Realm.getDefaultInstance()
                    val dateString =
                        SimpleDateFormat("yyyy년 MM월 dd일 E요일", Locale.KOREA).format(Date(System.currentTimeMillis()))
                    val diary = realm.where(Diary::class.java).equalTo("date", dateString).findFirst() ?: Diary()
                    realm.beginTransaction()
                    if(diary.date.isNullOrEmpty()) diary.date = dateString
                    diary.hashtags.addAll(response.toString().split(","))
                    realm.copyToRealmOrUpdate(diary)
                    realm.commitTransaction()

                    chatBot.callApi_question_first(response.toString(),
                        object : Chatbot.ApiListener {
                            override fun onResponse(response: Any) {
                                arriveMessage(response.toString())
                            }

                            override fun onFailure(error: String) {
                                Log.d("tintin", "question onFailure: $error")
                                Toast.makeText(context, "네트워크 오류", Toast.LENGTH_SHORT).show()
                            }
                        })
                }

                override fun onFailure(error: String) {
                    Log.d("tintin", "extract onFailure: $error")
                    Toast.makeText(context, "네트워크 오류", Toast.LENGTH_SHORT).show()
                }
            })
        }

        binding.chattingSendBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor(setting.themeColor))
        setting.addSaveDataOrder(object : DiaryMateSetting.SaveDataOrder {
            @SuppressLint("NotifyDataSetChanged")
            override fun order() {
                binding.chattingSendBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor(setting.themeColor))
                (binding.recyclerView.adapter as ChatAdapter).notifyDataSetChanged()
            }
        })

    }

    private fun getLogs(): String {
        val realm = Realm.getDefaultInstance()

        val parsedDate = Date(System.currentTimeMillis())
        val targetFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
        val formattedDateStr: String = targetFormat.format(parsedDate)

        val userMessages = realm.where(ChatMessage::class.java).equalTo("date", formattedDateStr)
            .equalTo("sender", "User").findAll()
        val messageStringBuilder = StringBuilder()

        // 각 메시지를 문자열에 추가
        for (message in userMessages) {
            messageStringBuilder.append(message.message)
            messageStringBuilder.append(",,") // 콤마로 구분
        }
        return messageStringBuilder.toString()
    }

    private fun getMessages() {
        messages = realm.where(ChatMessage::class.java).findAll().sort("index").toMutableList()
    }

    // 메시지를 전송하는 메서드를 정의합니다.
    private fun sendMessage() : String {
        val messageText = binding.chattingMessageEt.text.toString()
        if (messageText.isNotEmpty()) {
            addMessage(messageText, "User")
            chatAdapter.notifyItemInserted(messages.size - 1)
            binding.chattingMessageEt.text.clear()
            binding.recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
        }
        return messageText
    }

    private fun arriveMessage(msg : String) {
        addMessage(msg, "Chatbot")
        chatAdapter.notifyItemInserted(messages.size - 1)
        binding.recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
    }

    fun addMessage(msg: String, sender: String) {
        val chat = ChatMessage()
        chat.index = addNewMessage(pref)
        chat.sender = sender
        chat.message = msg
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
        val currentDate = Date(System.currentTimeMillis())
        chat.date = dateFormat.format(currentDate)

        messages.add(chat)
        realm.beginTransaction()
        realm.copyToRealmOrUpdate(chat)
        realm.commitTransaction()
    }

}