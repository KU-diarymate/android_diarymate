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
import ku.kpro.diary_mate.databinding.FragmentSettingBinding
import ku.kpro.diary_mate.databinding.PopupMenuPeroidBinding

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

        // Set initial state (e.g., the first theme color as selected)
        selectThemeColor(binding.settingThemeGreenIv)

        // Set click listeners for each theme color ImageView
        themeColorImageViews.forEach { imageView ->
            imageView.setOnClickListener {
                clearAllThemeColors()
                selectThemeColor(imageView)
            }
        }

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
            popupWindow.dismiss()
        }
        popupBinding.periodPopup2weekBtn.setOnClickListener {
            popupWindow.dismiss()
        }
        popupBinding.periodPopup3weekBtn.setOnClickListener {
            popupWindow.dismiss()
        }
        popupBinding.periodPopup4weekBtn.setOnClickListener {
            popupWindow.dismiss()
        }
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



}