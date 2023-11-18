package ku.kpro.diary_mate.etc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ku.kpro.diary_mate.R
import ku.kpro.diary_mate.data.ChatMessage

class ChatAdapter(private val messages: List<ChatMessage>) : RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    // ViewHolder 클래스를 정의합니다.
    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val senderTextView: TextView = itemView.findViewById(R.id.senderTextView)
//        val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)
    }

    // onCreateViewHolder 메서드: ViewHolder 객체를 생성하여 반환합니다.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chatting_user, parent, false)
        return MessageViewHolder(view)
    }

    // onBindViewHolder 메서드: 각 아이템의 데이터를 ViewHolder에 바인딩합니다.
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
//        holder.senderTextView.text = message.sender
//        holder.messageTextView.text = message.message
    }

    // getItemCount 메서드: 아이템의 개수를 반환합니다.
    override fun getItemCount(): Int {
        return messages.size
    }
}
