package ku.kpro.diary_mate.fragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import ku.kpro.diary_mate.R
import ku.kpro.diary_mate.databinding.FragmentSettingBinding

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

        return binding.root
    }

    private fun clearAllThemeColors() {
        themeColorImageViews.forEach { imageView ->
            (imageView as? ImageView)?.setImageDrawable(null) // Clear background
        }
    }

    private fun selectThemeColor(imageView: View) {
//        imageView.setBackgroundResource(R.drawable.circle) // Set background to circle drawable
//        imageView.backgroundTintList = ColorStateList.valueOf(
//            ContextCompat.getColor(requireContext(), R.color.selected_color)
//        )
        // Set other attributes as needed
        // ...

        // Set the check mark
        (imageView as ImageView).setImageResource(R.drawable.check)
    }

}