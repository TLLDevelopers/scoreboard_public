package scoreboard.score.board.newscore.names

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import scoreboard.score.board.R
import scoreboard.score.board.databinding.FragmentNamesBinding


class NamesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        val args = NamesFragmentArgs.fromBundle(requireArguments())

        val matchName = args.matchName
        val players = args.players
        var matchColor = args.matchColor

        var newMatchColor = ContextCompat.getColor(requireContext(), matchColor)

        //println(newMatchColor)
        println(matchName)
        println(players)

        val binding: FragmentNamesBinding = DataBindingUtil.inflate(
            inflater, scoreboard.score.board.R.layout.fragment_names, container, false
        )

        val adViewMainActivity = binding.adBannerNames
        adViewMainActivity.loadAd(AdRequest.Builder().build())

        val viewModel = NamesViewModel(matchName, players, requireContext(), newMatchColor)

        val adapter = NamesAdapter()

        adapter.data = List(players) {getString(scoreboard.score.board.R.string.player , it+1)}

        binding.namesViewModel = viewModel
        binding.namesList.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner
        binding.matchNameNames.text = matchName

        val unwrappedDrawable = AppCompatResources.getDrawable(requireContext(), scoreboard.score.board.R.drawable.fondo_score)
        val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
        DrawableCompat.setTint(wrappedDrawable, newMatchColor)

        binding.backNames.setImageDrawable(wrappedDrawable)

        val toast = Toast.makeText(context, scoreboard.score.board.R.string.toast_players_fill, Toast.LENGTH_LONG)

        binding.namesList.setItemViewCacheSize(players)

        binding.buttonBackToMatch.setOnClickListener {
            requireActivity().onBackPressed()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            toast.cancel()
            remove()
            requireActivity().onBackPressed()
        }


        //Control all players have name
        binding.buttonStartMatch.setOnClickListener {
            val namesAdapter = adapter.getNames()
            var control = false

            for(i in 0 until players){
                if(namesAdapter[i]==""){
                    toast.show()
                    control = false
                    break
                }else{
                    control = true
                }
            }

            if(control){
                    viewModel.createMatch()

                    viewModel.isMatchCreated.observe(viewLifecycleOwner){
                        if (it){
                            //viewModel.addPlayersToMatch(adapter.getNames())
                            viewModel.addPlayersToMatch(namesAdapter)
                        }
                    }

                viewModel.matchId.observe(viewLifecycleOwner){
                    this.findNavController().navigate(NamesFragmentDirections.actionNamesFragmentToScoreFragment(viewModel.matchId.value!!))
                    toast.cancel()
                }

            }

        }

        return binding.root
    }

}