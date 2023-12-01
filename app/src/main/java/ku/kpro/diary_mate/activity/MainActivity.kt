package ku.kpro.diary_mate.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import ku.kpro.diary_mate.fragment.AnalysisFragment
import ku.kpro.diary_mate.fragment.ChattingFragment
import ku.kpro.diary_mate.fragment.DiaryFragment
import ku.kpro.diary_mate.R
import ku.kpro.diary_mate.data.DiaryMateSetting
import ku.kpro.diary_mate.fragment.SettingFragment
import ku.kpro.diary_mate.databinding.ActivityMainBinding
import ku.kpro.diary_mate.etc.ChatbotService
import ku.kpro.diary_mate.etc.DiaryMateApplication.Companion.pref
import ku.kpro.diary_mate.etc.DiaryMateApplication.Companion.setting

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainPager.isUserInputEnabled = true
        binding.mainPager.adapter = ViewPagerAdapter(supportFragmentManager,lifecycle)
        binding.mainPager.registerOnPageChangeCallback(PageChangeCallback())
        binding.mainBottomNav.setOnItemSelectedListener {
            navigationSelected(it)
        }

        val colorStateList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_selected),
                intArrayOf(-android.R.attr.state_selected)
            ),
            intArrayOf(
                Color.parseColor(setting.themeColor),
                ContextCompat.getColor(this@MainActivity, R.color.grey)
            )
        )

        binding.mainBottomNav.itemIconTintList = colorStateList
        binding.mainBottomNav.itemTextColor = colorStateList
        setting.addSaveDataOrder(object : DiaryMateSetting.SaveDataOrder {
            override fun order() {
                val colorStateList2 = ColorStateList(
                    arrayOf(
                        intArrayOf(android.R.attr.state_selected),
                        intArrayOf(-android.R.attr.state_selected)
                    ),
                    intArrayOf(
                        Color.parseColor(setting.themeColor),
                        ContextCompat.getColor(this@MainActivity, R.color.grey)
                    )
                )

                binding.mainBottomNav.itemIconTintList = colorStateList2
                binding.mainBottomNav.itemTextColor = colorStateList2
            }
        })

        val serviceIntent = Intent(this, ChatbotService::class.java)
        startService(serviceIntent)

        // fragment_to_load extra 확인
        val fragmentToLoad = intent.getStringExtra("fragment_to_load")
        if (fragmentToLoad == ChattingFragment::class.java.name) {
            this.PageChangeCallback().onPageSelected(1)
        }
    }

    private fun navigationSelected(item: MenuItem): Boolean {
        val checked = item.setChecked(true)

        when (checked.itemId) {
            R.id.diary_nav -> {
                binding.mainPager.currentItem = 0
                return true
            }
            R.id.chatting_nav -> {
                binding.mainPager.currentItem = 1
                return true
            }
            R.id.analysis_nav -> {
                binding.mainPager.currentItem = 2
                return true
            }
            R.id.setting_nav -> {
                binding.mainPager.currentItem = 3
                return true
            }
        }
        return false
    }

    private inner class ViewPagerAdapter (fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager,lifecycle){
        override fun getItemCount(): Int = 4

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> DiaryFragment()
                1 -> ChattingFragment()
                2 -> AnalysisFragment()
                3 -> SettingFragment()
                else -> error("no such position: $position")
            }
        }

    }

    private inner class PageChangeCallback: ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            binding.mainBottomNav.selectedItemId = when (position) {
                0 -> R.id.diary_nav
                1 -> R.id.chatting_nav
                2 -> R.id.analysis_nav
                3 -> R.id.setting_nav
                else -> error("no such position: $position")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        setting.saveSettingData(pref)
    }

}
