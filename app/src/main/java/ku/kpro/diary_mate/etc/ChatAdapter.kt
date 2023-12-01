package ku.kpro.diary_mate.etc

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ku.kpro.diary_mate.R
import ku.kpro.diary_mate.data.ChatMessage
import ku.kpro.diary_mate.databinding.ItemChattingChatbotBinding
import ku.kpro.diary_mate.databinding.ItemChattingUserBinding
import ku.kpro.diary_mate.etc.DiaryMateApplication.Companion.setting

class ChatAdapter(private val messages: List<ChatMessage>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        // 뷰 타입 상수 정의
        private const val VIEW_TYPE_USER_MESSAGE = 1
        private const val VIEW_TYPE_CHATBOT_MESSAGE = 2
    }

    // ViewHolder 클래스 정의
    class UserMessageViewHolder(binding: ItemChattingUserBinding) : RecyclerView.ViewHolder(binding.root) {
        val messageTextView = binding.chattingUserTv
    }

    class ChatbotMessageViewHolder(binding: ItemChattingChatbotBinding) : RecyclerView.ViewHolder(binding.root) {
        val messageTextView = binding.chattingChatbotTv
        val icon = binding.chattingChatbotImg
    }

    // ViewHolder 객체를 생성, 반환
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_USER_MESSAGE -> {
                val binding = ItemChattingUserBinding.inflate(inflater, parent, false)
                UserMessageViewHolder(binding)
            }
            VIEW_TYPE_CHATBOT_MESSAGE -> {
                val binding = ItemChattingChatbotBinding.inflate(inflater, parent, false)
                ChatbotMessageViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    // 각 아이템의 데이터를 ViewHolder에 바인딩
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        when (holder.itemViewType) {
            VIEW_TYPE_USER_MESSAGE -> {
                val userViewHolder = holder as UserMessageViewHolder
                userViewHolder.messageTextView.text = message.message
                userViewHolder.messageTextView.backgroundTintList = ColorStateList.valueOf(Color.parseColor(setting.themeColor))
            }
            VIEW_TYPE_CHATBOT_MESSAGE -> {
                val chatbotViewHolder = holder as ChatbotMessageViewHolder
                chatbotViewHolder.messageTextView.text = message.message
                chatbotViewHolder.icon.backgroundTintList = ColorStateList.valueOf(Color.parseColor(setting.themeColor))
            }
        }
    }

    // 아이템의 개수 반환
    override fun getItemCount(): Int {
        return messages.size
    }

    // 각 아이템의 뷰 타입 지정
    override fun getItemViewType(position: Int): Int {
        return if (messages[position].sender == "Chatbot") {
            VIEW_TYPE_CHATBOT_MESSAGE
        } else {
            VIEW_TYPE_USER_MESSAGE
        }
    }
}
