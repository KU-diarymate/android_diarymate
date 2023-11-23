package ku.kpro.diary_mate.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.realm.Realm
import ku.kpro.diary_mate.data.Diary
import ku.kpro.diary_mate.databinding.FragmentAnalysisBinding
import java.text.SimpleDateFormat
import java.util.Locale

class AnalysisFragment : Fragment() {

    private lateinit var binding : FragmentAnalysisBinding
    enum class Keyword {
        DAILY, EMOTION
    }
    private var keyword = Keyword.EMOTION

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAnalysisBinding.inflate(inflater, container, false)

        binding.analysisTitleTv.setOnClickListener {
            if(keyword == Keyword.EMOTION) {
                binding.analysisTitleTv.text = "일상키워드"
                keyword = Keyword.DAILY
            } else {
                binding.analysisTitleTv.text = "감정키워드"
                keyword = Keyword.EMOTION
            }
        }
        binding.analysisDownChevronIv.setOnClickListener {
            if(keyword == Keyword.EMOTION) {
                binding.analysisTitleTv.text = "일상키워드"
                keyword = Keyword.DAILY
            } else {
                binding.analysisTitleTv.text = "감정키워드"
                keyword = Keyword.EMOTION
            }
            getMainKeyword(30, Keyword.EMOTION)
        }

        return binding.root
    }

    private fun getMainKeyword(period : Int, keyword : Keyword) : List<String> {
        val realm = Realm.getDefaultInstance()
        val diaryList = realm.where(Diary::class.java).findAll().sortedWith { t1, t2 ->
            try {
                val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA)
                val date1 = dateFormat.parse(t1.date.substring(0, 13))
                val date2 = dateFormat.parse(t2.date.substring(0, 13))
                date2?.compareTo(date1) ?: 0
            } catch (e: Exception) {
                Log.d("isoo", "getMainKeyword: ${e.stackTraceToString()}")
                0
            }
        }
        val allHashtags = diaryList.flatMap { it.hashtags }
        val hashtagCountMap = allHashtags.groupingBy { it }.eachCount()
        val topHashtags = hashtagCountMap.entries.sortedByDescending { it.value }.take(7)
        return topHashtags.map { it.key }
    }

}