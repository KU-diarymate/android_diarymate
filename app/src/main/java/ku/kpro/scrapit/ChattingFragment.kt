package ku.kpro.scrapit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ku.kpro.scrapit.databinding.FragmentChattingBinding

class ChattingFragment : Fragment() {

    private lateinit var binding : FragmentChattingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChattingBinding.inflate(inflater, container, false)

        return binding.root
    }

}