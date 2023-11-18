package ku.kpro.diary_mate.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import ku.kpro.diary_mate.fragment.AnalysisFragment
import ku.kpro.diary_mate.fragment.ChattingFragment
import ku.kpro.diary_mate.fragment.DiaryFragment
import ku.kpro.diary_mate.R
import ku.kpro.diary_mate.fragment.SettingFragment
import ku.kpro.diary_mate.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainPager.isUserInputEnabled = true
        binding.mainPager.adapter = ViewPagerAdapter(supportFragmentManager,lifecycle)
        binding.mainPager.registerOnPageChangeCallback(PageChangeCallback())
        binding.mainBottomNav.setOnItemSelectedListener { navigationSelected(it) }
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

}