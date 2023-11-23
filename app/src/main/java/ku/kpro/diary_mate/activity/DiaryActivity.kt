package ku.kpro.diary_mate.activity

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import io.realm.Realm
import ku.kpro.diary_mate.R
import ku.kpro.diary_mate.data.Diary
import ku.kpro.diary_mate.databinding.ActivityDiaryBinding


class DiaryActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDiaryBinding
    private enum class Mode {
        READ, EDIT
    }
    private var mode = Mode.READ
    private val context = this
    private var diary = Diary()
    private lateinit var date : String

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        date = intent.getStringExtra("date").toString()
        getDiary()
        binding.diaryActivityDateTv.text = date
        binding.diaryActivityBackBtn.setOnClickListener {
            finish()
        }
        binding.diaryActivityWriteAreaEt.addTextChangedListener {
            diary.context = binding.diaryActivityWriteAreaEt.text.toString()
        }

        val layoutManager = FlexboxLayoutManager(this)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        binding.diaryActivityHashtagRv.layoutManager = layoutManager

        val adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_hashtag, parent, false)
                return object : RecyclerView.ViewHolder(view) {}
            }

            @SuppressLint("SetTextI18n")
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val textView: TextView = holder.itemView.findViewById(R.id.hashtag_title_tv)
                if(mode == Mode.EDIT && position == diary.hashtags.size) {
                    textView.visibility = View.GONE
                    holder.itemView.findViewById<TextView>(R.id.hashtag_hash).text = "\u271B"
                    holder.itemView.setOnClickListener {
                        val view = LayoutInflater.from(context).inflate(R.layout.dialog_add_hashtag, null)
                        val editText = view.findViewById<EditText>(R.id.dialog_hashtag_et)
                        AlertDialog.Builder(context)
                            .setTitle("해시태그 추가")
                            .setView(view)
                            .setPositiveButton("확인") { p0, p1 ->
                                diary.hashtags.add(editText.text.toString())
                                notifyItemInserted(diary.hashtags.size - 1)
                            }
                            .create().show()
                    }
                } else {
                    textView.visibility = View.VISIBLE
                    holder.itemView.findViewById<TextView>(R.id.hashtag_hash).text = "#"
                    val data = diary.hashtags[position]
                    if (mode == Mode.EDIT) {
                        textView.text = "$data \u2a2f"
                        textView.setOnClickListener {
                            AlertDialog.Builder(context)
                                .setTitle("삭제")
                                .setMessage("정말로 해당 해시태그를 삭제하시겠습니까?")
                                .setPositiveButton("네") { p0, p1 ->
                                    diary.hashtags.removeAt(position)
                                    notifyItemRemoved(position)
                                }
                                .setNegativeButton("아니오") { p0, p1 -> }
                                .create().show()
                        }
                    } else if (mode == Mode.READ) {
                        textView.text = data
                        textView.setOnClickListener {  }
                    }
                }
            }

            override fun getItemCount(): Int {
                return if(mode == Mode.READ) {
                    diary.hashtags.size
                } else if(mode == Mode.EDIT) {
                    diary.hashtags.size + 1
                } else 0
            }
        }

        binding.diaryActivityHashtagRv.adapter = adapter

        val toast = Toast(this)
        binding.diaryActivityWriteBtn.setOnClickListener {
            adapter.notifyDataSetChanged()
            toast.cancel()
            if(mode == Mode.READ) {
                binding.diaryActivityWriteBtn.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green_theme))
                binding.diaryActivityWriteAreaEt.isEnabled = true
                binding.diaryActivityModeTv.text = "편집 모드"
                toast.setText("편집 모드")
                toast.show()
                mode = Mode.EDIT
            } else if(mode == Mode.EDIT) {
                binding.diaryActivityWriteBtn.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black))
                binding.diaryActivityWriteAreaEt.isEnabled = false
                binding.diaryActivityModeTv.text = "읽기 모드"
                toast.setText("읽기 모드")
                toast.show()
                mode = Mode.READ
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sortHashtags()
        saveDiary()
    }

    private fun getDiary() {
        val realm = Realm.getDefaultInstance()
        val diary = realm.where(Diary::class.java).equalTo("date", date).findFirst()
        this.diary.date = date
        if(diary == null) this.diary.context = ""
        else this.diary.context = diary.context
        diary?.hashtags?.let { this.diary.hashtags.addAll(it) }
        realm.close()
        binding.diaryActivityWriteAreaEt.setText(this.diary.context)
    }

    private fun saveDiary() {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.copyToRealmOrUpdate(diary)
        realm.commitTransaction()
        realm.close()
    }

    private fun sortHashtags() {
        // 해시태그 분류 하는 로직
        diary.emotionalHashtags.addAll(diary.hashtags)
    }

}