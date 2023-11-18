package ku.kpro.diary_mate.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ku.kpro.diary_mate.custom_view.CustomCalendarView
import ku.kpro.diary_mate.activity.DiaryActivity
import ku.kpro.diary_mate.databinding.FragmentDiaryBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DiaryFragment : Fragment() {

    private lateinit var binding : FragmentDiaryBinding
    private val calendar = Calendar.getInstance()
    private val monthArray = arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDiaryBinding.inflate(inflater, container, false)

        binding.diaryCalendarMonthNumberTv.text = (calendar.get(Calendar.MONTH) + 1).toString() + "월"
        binding.diaryCalendarMonthWordTv.text = monthArray[calendar.get(Calendar.MONTH)]
        binding.diaryCalendarYearTv.text = calendar.get(Calendar.YEAR).toString() + "년"
        binding.diaryCustomCalendar.setOnCalendarTouchListener(object :
            CustomCalendarView.OnCalendarTouchListener {
            override fun getSelectedDate(date: Int) {
                calendar.set(Calendar.DAY_OF_MONTH, date)
                if(Calendar.getInstance().after(calendar)) {
                    val dateString =
                        SimpleDateFormat("yyyy년 MM월 dd일 E요일", Locale.KOREA).format(calendar.time)
                    val intent = Intent(activity, DiaryActivity::class.java)
                    intent.putExtra("date", dateString)
                    startActivity(intent)
                }
            }
        })

        return binding.root
    }
}