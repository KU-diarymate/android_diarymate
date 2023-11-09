package ku.kpro.diary_mate

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ku.kpro.diary_mate.databinding.FragmentDiaryBinding
import java.text.DateFormatSymbols
import java.util.Calendar

class DiaryFragment : Fragment() {

    private lateinit var binding : FragmentDiaryBinding
    private val calendar = Calendar.getInstance()
    private val monthArray = arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiaryBinding.inflate(inflater, container, false)

        binding.diaryCalendarMonthNumberTv.text = DateFormatSymbols().months[calendar.get(Calendar.MONTH)]
        binding.diaryCalendarMonthWordTv.text = monthArray[calendar.get(Calendar.MONTH)]
        binding.diaryCalendarYearTv.text = calendar.get(Calendar.YEAR).toString() + "년"

        return binding.root
    }
}