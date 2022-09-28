package scoreboard.score.board.score

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import scoreboard.score.board.R
import scoreboard.score.board.database.Player
import scoreboard.score.board.databinding.FragmentScoreBinding


class ScoreFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        val binding: FragmentScoreBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_score, container, false
        )

        val toast = Toast.makeText(context, R.string.toast_points_fill, Toast.LENGTH_LONG)

        val adViewMainActivity = binding.adBannerScore
        adViewMainActivity.loadAd(AdRequest.Builder().build())

        val args = ScoreFragmentArgs.fromBundle(requireArguments())

        val matchId = args.matchId

        val viewModel = ScoreViewModel(requireContext(), matchId)

        binding.scoreViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val adapter = ScoreAdapter(requireContext())

        viewModel.actualMatchsPlayers.observe(viewLifecycleOwner) {
            viewModel.setValue()
            val players = it[0].players.sortedByDescending { it.points }
            val color = it[0].match.color

            val unwrappedDrawable = AppCompatResources.getDrawable(requireContext(), scoreboard.score.board.R.drawable.fondo_score)
            val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
            DrawableCompat.setTint(wrappedDrawable, color)

            binding.backScore.setImageDrawable(wrappedDrawable)

            println(color)

            adapter.color = color
            adapter.data = players as MutableList<Player>

            binding.scoreList.adapter = adapter
            binding.scoreList.setItemViewCacheSize(players.size)

            adapter.itemNewPos.observe(viewLifecycleOwner) {
                adapter.notifyItemMoved(adapter.itemAntPos.value!!, adapter.itemNewPos.value!!)
            }
        }

        binding.buttonFinishMatch.setOnClickListener {
            this.findNavController().navigate(ScoreFragmentDirections.actionScoreFragmentToFinishFragment(matchId))
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(ScoreFragmentDirections.actionScoreFragmentToMainFragment())
            toast.cancel()
        }

        adapter.falsePoints.observe(viewLifecycleOwner){
            if(it) {
                toast.show()
                adapter.cancelFalsePoints()
            }

        }

        return binding.root
    }

}