package ku.kpro.diary_mate

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import ku.kpro.diary_mate.databinding.ActivityDiaryBinding


class DiaryActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDiaryBinding
    private enum class Mode {
        READ, EDIT
    }
    private var mode = Mode.READ

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.diaryActivityDateTv.text = intent.getStringExtra("date")
        binding.diaryActivityBackBtn.setOnClickListener {
            saveDiary()
            finish()
        }

        val toast = Toast(this)
        binding.diaryActivityWriteBtn.setOnClickListener {
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

        val layoutManager = FlexboxLayoutManager(this)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        binding.diaryActivityHashtagRv.layoutManager = layoutManager

        val dataList = listOf("피아노", "배드민턴", "친구들", "침대", "운동", "1등")

        val adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_hashtag, parent, false)
                return object : RecyclerView.ViewHolder(view) {}
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val data = dataList[position]
                val textView: TextView = holder.itemView.findViewById(R.id.hashtag_title_tv)
                textView.text = data
            }

            override fun getItemCount(): Int {
                return dataList.size
            }
        }

        binding.diaryActivityHashtagRv.adapter = adapter
    }

    private fun saveDiary() {

    }

}