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
            updateAnalysisView(keyword)
        }
        binding.analysisDownChevronIv.setOnClickListener {
            if(keyword == Keyword.EMOTION) {
                binding.analysisTitleTv.text = "일상키워드"
                keyword = Keyword.DAILY
            } else {
                binding.analysisTitleTv.text = "감정키워드"
                keyword = Keyword.EMOTION
            }
            updateAnalysisView(keyword)
        }
        updateAnalysisView(keyword)

        return binding.root
    }

    fun updateAnalysisView(keyword : Keyword) {
        setMainKeyword(getMainKeyword(30, keyword))
    }

    private fun setMainKeyword(list : List<String>) {
        if(list.isNotEmpty())
            binding.analysisKeyword1Tv.text = list[0]
        else
            binding.analysisKeyword1Tv.text = ""
        if(list.size > 1)
            binding.analysisKeyword5Tv.text = list[1]
        else
            binding.analysisKeyword5Tv.text = ""
        if(list.size > 2)
            binding.analysisKeyword4Tv.text = list[2]
        else
            binding.analysisKeyword4Tv.text = ""
        if(list.size > 3)
            binding.analysisKeyword7Tv.text = list[3]
        else
            binding.analysisKeyword7Tv.text = ""
        if(list.size > 4)
            binding.analysisKeyword6Tv.text = list[4]
        else
            binding.analysisKeyword6Tv.text = ""
        if(list.size > 5)
            binding.analysisKeyword2Tv.text = list[5]
        else
            binding.analysisKeyword2Tv.text = ""
        if(list.size > 6)
            binding.analysisKeyword3Tv.text = list[6]
        else
            binding.analysisKeyword3Tv.text = ""
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
        return if(keyword == Keyword.DAILY) {
            val allHashtags = diaryList.flatMap { it.dailyHashtags }
            val hashtagCountMap = allHashtags.groupingBy { it }.eachCount()
            val topHashtags = hashtagCountMap.entries.sortedByDescending { it.value }.take(7)
            topHashtags.map { it.key }
        } else if(keyword == Keyword.EMOTION) {
            val allHashtags = diaryList.flatMap { it.emotionalHashtags }
            val hashtagCountMap = allHashtags.groupingBy { it }.eachCount()
            val topHashtags = hashtagCountMap.entries.sortedByDescending { it.value }.take(7)
            topHashtags.map { it.key }
        } else ArrayList()
    }

}