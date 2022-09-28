package scoreboard.score.board.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import scoreboard.score.board.R
import scoreboard.score.board.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentMainBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_main, container, false
        )

        val adViewMainActivity = binding.adBannerMain
        adViewMainActivity.loadAd(AdRequest.Builder().build())

        val viewModel = MainViewModel()

        binding.mainViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.buttonNewmatch.setOnClickListener {
            this.findNavController().navigate(MainFragmentDirections.actionMainFragmentToMatchFragment(R.color.black, "", 1))
        }

        binding.buttonHistorial.setOnClickListener {
            this.findNavController().navigate(MainFragmentDirections.actionMainFragmentToHistorialFragment())
        }

        return binding.root
    }
}