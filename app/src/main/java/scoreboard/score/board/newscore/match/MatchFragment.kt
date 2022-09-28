package scoreboard.score.board.newscore.match

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import scoreboard.score.board.R
import scoreboard.score.board.databinding.FragmentMatchBinding
import scoreboard.score.board.isShown
import scoreboard.score.board.rewardedAd


class MatchFragment : Fragment() {

    init {
        isShown.value = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val binding: FragmentMatchBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_match, container, false
        )

        val adViewMainActivity = binding.adBannerMatch
        adViewMainActivity.loadAd(AdRequest.Builder().build())

        val args = MatchFragmentArgs.fromBundle(requireArguments())

        val defaultColor = args.color
        var players = args.players
        var matchName = args.matchName

        println(defaultColor)

        val viewModel = MatchViewModel()

        binding.matchViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.color.setBackgroundColor(resources.getColor(defaultColor))
        binding.matchName.text = matchName.toEditable()

        val toast = Toast.makeText(context, scoreboard.score.board.R.string.toast_name_fill, Toast.LENGTH_LONG)



        val array = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        ArrayAdapter(
            requireContext(),
            scoreboard.score.board.R.layout.spinner_text,
            array
        ).also { adapter ->
            adapter.setDropDownViewResource(scoreboard.score.board.R.layout.spinner_adapter)
            binding.spinner.adapter = adapter
        }

        binding.spinner.setSelection(players-1)


        binding.color.setOnClickListener{
            matchName = binding.matchName.text.toString()
            players = binding.spinner.selectedItem.toString().toInt()
            this.findNavController().navigate(MatchFragmentDirections.actionMatchFragmentToColorFragment(defaultColor, matchName, players))
        }

        binding.buttonGoToNames.setOnClickListener {
            players = binding.spinner.selectedItem.toString().toInt()
            matchName = binding.matchName.text.toString()

            if (matchName != ""){
                if(players > 4){
                    showDialog(players, matchName, defaultColor)
                }else{
                    this.findNavController().navigate(MatchFragmentDirections.actionMatchFragmentToNamesFragment(players, matchName, defaultColor))
                }
                toast.cancel()
            }else{
                toast.show()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(MatchFragmentDirections.actionMatchFragmentToMainFragment())
            toast.cancel()
        }

        binding.buttonBackToMain.setOnClickListener {
            findNavController().navigate(MatchFragmentDirections.actionMatchFragmentToMainFragment())
            toast.cancel()
        }

        return binding.root
    }

    fun showDialog(players: Int, matchName: String, defaultColor: Int) {

        val positiveButtonClick = { dialog: DialogInterface, which: Int ->
            showRewarded(players, matchName, defaultColor)
        }

        val negativeButtonClick = { dialog: DialogInterface, which: Int ->
            dialog.cancel()
        }

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.dialog_play_title)
        builder.setMessage(R.string.dialog_play_text)
        builder.setPositiveButton(
            R.string.dialog_play_ad,
            DialogInterface.OnClickListener(function = positiveButtonClick)
        )
        builder.setNegativeButton(
            R.string.dialog_play_dont,
            DialogInterface.OnClickListener(function = negativeButtonClick)
        )
        builder.setIcon(R.drawable.logo_splash_bueno)

        val dialog: AlertDialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()
    }

    fun showRewarded(players: Int, matchName: String, defaultColor: Int) {

        if (rewardedAd != null) {
            rewardedAd!!.show(
                requireActivity()
            ) { rewardItem ->
                if (rewardItem.amount > 0){
                    isShown.value = true
                    this.findNavController().navigate(MatchFragmentDirections.actionMatchFragmentToNamesFragment(players, matchName, defaultColor))
                }
            }
        }else{
            Toast.makeText(requireContext(), "Internet Connection Failed. Restart APP", Toast.LENGTH_SHORT).show()
        }
    }

    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)
}