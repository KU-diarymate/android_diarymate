package ku.kpro.diary_mate.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.realm.Realm
import ku.kpro.diary_mate.data.ChatMessage
import ku.kpro.diary_mate.databinding.FragmentChattingBinding
import ku.kpro.diary_mate.etc.ChatAdapter
import ku.kpro.diary_mate.etc.DiaryMateApplication
import ku.kpro.diary_mate.etc.DiaryMateApplication.Companion.addNewMessage
import ku.kpro.diary_mate.etc.DiaryMateApplication.Companion.pref
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChattingFragment : Fragment() {

    lateinit var binding: FragmentChattingBinding
    lateinit var messages : MutableList<ChatMessage>
    private lateinit var realm : Realm
    lateinit var chatAdapter: ChatAdapter

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
            sendMessage()
            Handler(Looper.getMainLooper()).postDelayed({ arriveMessage() }, 1000)
        }

        binding.fab.setOnClickListener {
            fabClickListener?.onFabClick()
        }
    }

    private fun getMessages() {
        messages = realm.where(ChatMessage::class.java).findAll().sort("index").toMutableList()
    }

    // 메시지를 전송하는 메서드를 정의합니다.
    private fun sendMessage() {
        val messageText = binding.chattingMessageEt.text.toString()
        if (messageText.isNotEmpty()) {
            addMessage(messageText, "User")
            chatAdapter.notifyItemInserted(messages.size - 1)
            binding.chattingMessageEt.text.clear()
            binding.recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
        }
    }

    private fun arriveMessage() {
        addMessage("응답", "Chatbot")
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

    /*override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DiaryMateApplication) {
            (context as DiaryMateApplication).currentChattingFragment = this
            Log.e("ChattingFragment", "fragment attached")
        }
    }

    override fun onDetach() {
        super.onDetach()
        if (context is DiaryMateApplication) {
            (context as DiaryMateApplication).currentChattingFragment = null
            Log.e("ChattingFragment", "fragment detached")
        }
    }*/

}