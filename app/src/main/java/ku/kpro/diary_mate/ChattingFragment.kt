package ku.kpro.diary_mate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ku.kpro.diary_mate.databinding.FragmentChattingBinding

class ChattingFragment : Fragment() {

    private lateinit var binding: FragmentChattingBinding
    private val messages = mutableListOf(
        ChatMessage("User1", "Hello!"),
        ChatMessage("User2", "Hi there!")
    )

    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChattingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ChatAdapter를 초기화하고 RecyclerView에 연결합니다.
        chatAdapter = ChatAdapter(messages)
        binding.recyclerView.adapter = chatAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 전송 버튼에 클릭 리스너를 등록합니다.
        binding.chattingSendBtn.setOnClickListener {
            sendMessage()
        }
    }

    // 메시지를 전송하는 메서드를 정의합니다.
    private fun sendMessage() {
        // EditText에서 텍스트를 가져옵니다.
        val messageText = binding.chattingMessageEt.text.toString()
        // 텍스트가 비어있지 않은 경우에만 처리합니다.
        if (messageText.isNotEmpty()) {
            // 새로운 메시지를 생성합니다.
            val newMessage = ChatMessage("User1", messageText)
            // 메시지를 목록에 추가하고 어댑터에 알립니다.
            messages.add(newMessage)
            chatAdapter.notifyItemInserted(messages.size - 1)
            // EditText를 지웁니다.
            binding.chattingMessageEt.text.clear()
        }
    }

}