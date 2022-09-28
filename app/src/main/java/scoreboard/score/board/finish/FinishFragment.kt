package scoreboard.score.board.finish

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import scoreboard.score.board.R
import scoreboard.score.board.databinding.FragmentFinishBinding

class FinishFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentFinishBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_finish, container, false
        )

        val adViewMainActivity = binding.adBannerFinish
        adViewMainActivity.loadAd(AdRequest.Builder().build())

        val args = FinishFragmentArgs.fromBundle(requireArguments())

        val matchId = args.matchId

        val viewModel = FinishViewModel(requireContext(), matchId)

        val adapter = FinishAdapter()

        binding.finishViewModel = viewModel

        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.getPlayers()

        viewModel.listMatchPlayers.observe(viewLifecycleOwner){
            Log.d("LISTA PLAYERS", it[0].match.matchName)
            adapter.data = it[0].players.sortedByDescending { it.points }
            adapter.color = it[0].match.color

            binding.matchNameFinal.text = "\" ${it[0].match.matchName} \""

            binding.finalRecycler.adapter = adapter
        }

        binding.buttonNewMatch.setOnClickListener {
            findNavController().navigate(FinishFragmentDirections.actionFinishFragmentToMainFragment())
        }

        return binding.root
    }
}