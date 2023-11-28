package ku.kpro.diary_mate.data

import com.renju_note.isoo.util.PreferenceUtil

data class DiaryMateSetting(
    var useAINotification : Boolean,
    var useAutoDiaryCreation : Boolean,
    var useKeywordExtract : Boolean,
    var usePastDiaryFunc : Boolean,
    var analysisPeriod : Int,
    var themeColor : String
) {

    companion object {
        fun getDefaultSetting() : DiaryMateSetting {
            return DiaryMateSetting(true, true, true, true, 28, "#8ADC8C")
        }
    }

    // 설정을 SharedPreferences에 저장
    fun saveSettingData(pref : PreferenceUtil) {
        pref.setBoolean("useAINotification", useAINotification)
        pref.setBoolean("useAutoDiaryCreation", useAutoDiaryCreation)
        pref.setBoolean("useKeywordExtract", useKeywordExtract)
        pref.setBoolean("usePastDiaryFunc", usePastDiaryFunc)
        pref.setInt("analysisPeriod", analysisPeriod)
        pref.setString("themeColor", themeColor)
    }

    // 설정을 SharedPreferences에서 불러오기
    fun loadSettingData(pref : PreferenceUtil) {
        useAINotification = pref.getBoolean("useAINotification", true)
        useAutoDiaryCreation = pref.getBoolean("useAutoDiaryCreation", true)
        useKeywordExtract = pref.getBoolean("useKeywordExtract", true)
        usePastDiaryFunc = pref.getBoolean("usePastDiaryFunc", true)
        analysisPeriod = pref.getInt("analysisPeriod", 28)
        themeColor = pref.getString("themeColor", "#8ADC8C")
    }

}
