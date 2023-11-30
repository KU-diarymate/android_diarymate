package ku.kpro.diary_mate.fragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import ku.kpro.diary_mate.R
import ku.kpro.diary_mate.custom_view.CustomToggleButton
import ku.kpro.diary_mate.databinding.FragmentSettingBinding
import ku.kpro.diary_mate.databinding.PopupMenuPeroidBinding
import ku.kpro.diary_mate.etc.DiaryMateApplication.Companion.pref
import ku.kpro.diary_mate.etc.DiaryMateApplication.Companion.setting

class SettingFragment : Fragment() {

    private lateinit var binding : FragmentSettingBinding

    private val themeColorImageViews: List<View> by lazy {
        listOf(
            binding.settingThemeRedIv,
            binding.settingThemeYellowIv,
            binding.settingThemeGreenIv,
            binding.settingThemeBlueIv,
            binding.settingThemePurpleIv,
            binding.settingThemePinkIv
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)

        // SharedPreferences에서 설정 불러오기
        setting.loadSettingData(pref)

//        토글위치 설정
        binding.settingAlarmBtn.setToggleOn(setting.useAINotification)
        binding.settingAutoDiaryBtn.setToggleOn(setting.useAutoDiaryCreation)
        binding.settingKeywordBtn.setToggleOn(setting.useKeywordExtract)
        binding.settingPastBtn.setToggleOn(setting.usePastDiaryFunc)
        setToggleSetting()


//        테마색상 불러오기 관련 설정
        // 테마색상 체크 표시 설정
        var checkedView = when (setting.themeColor) {
            "#FF8A7D" -> binding.settingThemeRedIv
            "#FFD776" -> binding.settingThemeYellowIv
            "#8ADC8C" -> binding.settingThemeGreenIv
            "#88C8FF" -> binding.settingThemeBlueIv
            "#C1A5FF" -> binding.settingThemePurpleIv
            "#FFAAD7" -> binding.settingThemePinkIv
            else -> {binding.settingThemeGreenIv}
        }
        selectThemeColor(checkedView)
        // 팝업메뉴 색상 불러오기
        binding.settingAnalysisPeriodTv.compoundDrawableTintList = ColorStateList.valueOf(Color.parseColor(setting.themeColor))



        // 테마색상 변경 클릭 리스너
        themeColorImageViews.forEach { imageView ->
            imageView.setOnClickListener {
                clearAllThemeColors()
                selectThemeColor(imageView)
                val themeColor = when (imageView) {
                    binding.settingThemeRedIv -> "#FF8A7D"
                    binding.settingThemeYellowIv -> "#FFD776"
                    binding.settingThemeGreenIv -> "#8ADC8C"
                    binding.settingThemeBlueIv -> "#88C8FF"
                    binding.settingThemePurpleIv -> "#C1A5FF"
                    binding.settingThemePinkIv -> "#FFAAD7"
                    // ...
                    else -> "#8ADC8C"
                }
                setting.themeColor = themeColor

                // CustomToggleButton의 테마 색상 업데이트
                if(binding.settingAlarmBtn.isLeftSelected()) binding.settingAlarmBtn.updateThemeColor(themeColor)
                if(binding.settingAutoDiaryBtn.isLeftSelected()) binding.settingAutoDiaryBtn.updateThemeColor(themeColor)
                if(binding.settingKeywordBtn.isLeftSelected()) binding.settingKeywordBtn.updateThemeColor(themeColor)
                if(binding.settingPastBtn.isLeftSelected()) binding.settingPastBtn.updateThemeColor(themeColor)

                // triangle.xml 테마 색상 업데이트
                binding.settingAnalysisPeriodTv.compoundDrawableTintList = ColorStateList.valueOf(Color.parseColor(setting.themeColor))

                // SharedPreferences에 테마색상 설정 저장
                setting.saveSettingData(pref)
            }
        }

        // setting 전역 설정 변경
//        if(setting.useAINotification) binding.settingAlarmBtn.toggle()
//        if(setting.useAutoDiaryCreation) binding.settingAutoDiaryBtn.toggle()
//        if(setting.useKeywordExtract) binding.settingKeywordBtn.toggle()
//        if(setting.usePastDiaryFunc) binding.settingPastBtn.toggle()

//        setting.useAINotification = binding.settingAlarmBtn.isLeftSelected()
//        setting.useAutoDiaryCreation = binding.settingAutoDiaryBtn.isLeftSelected()
//        setting.useKeywordExtract = binding.settingKeywordBtn.isLeftSelected()
//        setting.usePastDiaryFunc = binding.settingPastBtn.isLeftSelected()

        binding.settingAnalysisPeriodTv.setOnClickListener {
            handlePopup()
        }





        return binding.root
    }

    private fun handlePopup() {
        val popupBinding = PopupMenuPeroidBinding.inflate(LayoutInflater.from(context))
        val popupWindow = PopupWindow(
            popupBinding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popupBinding.periodPopup1weekBtn.setOnClickListener {
            setting.analysisPeriod = 7
            binding.settingAnalysisPeriodTv.text = "1주"
            popupWindow.dismiss()
        }
        popupBinding.periodPopup2weekBtn.setOnClickListener {
            setting.analysisPeriod = 14
            binding.settingAnalysisPeriodTv.text = "2주"
            popupWindow.dismiss()
        }
        popupBinding.periodPopup3weekBtn.setOnClickListener {
            setting.analysisPeriod = 21
            binding.settingAnalysisPeriodTv.text = "3주"
            popupWindow.dismiss()
        }
        popupBinding.periodPopup4weekBtn.setOnClickListener {
            setting.analysisPeriod = 28
            binding.settingAnalysisPeriodTv.text = "4주"
            popupWindow.dismiss()
        }
        // SharedPreferences에 분석기간 설정 저장
        setting.saveSettingData(pref)

        popupWindow.elevation = 50f
        popupWindow.showAsDropDown(binding.settingAnalysisPeriodTv)
    }

    private fun clearAllThemeColors() {
        themeColorImageViews.forEach { imageView ->
            (imageView as? ImageView)?.setImageDrawable(null) // Clear background
        }
    }

    private fun selectThemeColor(imageView: View) {

        // Set the check mark
        (imageView as ImageView).setImageResource(R.drawable.check)
    }

    private fun setToggleSetting() {
        binding.settingAlarmBtn.setOnToggleChangedListener(object : CustomToggleButton.OnToggleChangedListener {
            override fun toggleChanged(isOn: Boolean) {
                setting.useAINotification = isOn
                setting.saveSettingData(pref)
            }
        })

        binding.settingAutoDiaryBtn.setOnToggleChangedListener(object : CustomToggleButton.OnToggleChangedListener {
            override fun toggleChanged(isOn: Boolean) {
                setting.useAutoDiaryCreation = isOn
                setting.saveSettingData(pref)
            }
        })

        binding.settingKeywordBtn.setOnToggleChangedListener(object : CustomToggleButton.OnToggleChangedListener {
            override fun toggleChanged(isOn: Boolean) {
                setting.useKeywordExtract = isOn
                setting.saveSettingData(pref)
            }
        })

        binding.settingPastBtn.setOnToggleChangedListener(object : CustomToggleButton.OnToggleChangedListener {
            override fun toggleChanged(isOn: Boolean) {
                setting.usePastDiaryFunc = isOn
                setting.saveSettingData(pref)
            }
        })

    }

}