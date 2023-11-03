package ku.kpro.diary_mate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ku.kpro.diary_mate.databinding.FragmentAnalysisBinding

class AnalysisFragment : Fragment() {

    private lateinit var binding : FragmentAnalysisBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAnalysisBinding.inflate(inflater, container, false)
        
        return binding.root
    }

}