package ku.kpro.diary_mate.data

import com.renju_note.isoo.util.PreferenceUtil

data class DiaryMateSetting(
    var useAINotification : Boolean,
    var useAutoDiaryCreation : Boolean,
    var useFutureLetterFunc : Boolean,
    var usePastDiaryFunc : Boolean,
    var analysisPeriod : Int,
    var themeColor : String
) {

    companion object {
        fun getDefaultSetting() : DiaryMateSetting {
            return DiaryMateSetting(true, true, true, true, 28, "#8ADC8C")
        }
    }

    fun saveSettingData(pref : PreferenceUtil) {
        pref.setString("", "")
    }

    fun setSettingData(data : String) {

    }

}
