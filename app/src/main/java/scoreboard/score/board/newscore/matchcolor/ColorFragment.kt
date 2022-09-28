package scoreboard.score.board.newscore.matchcolor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import scoreboard.score.board.R
import scoreboard.score.board.databinding.FragmentColorBinding


class ColorFragment : Fragment() {

    private var selectedColor = R.color.black

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentColorBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_color, container, false
        )

        val viewModel = ColorViewModel()

        binding.colorViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val args = ColorFragmentArgs.fromBundle(requireArguments())

        val defaultColor = args.colorToSelect
        val matchName = args.matchName
        val players = args.players

        selectedColor = defaultColor

        binding.buttonSelected.setBackgroundColor(ContextCompat.getColor(requireContext(), defaultColor))

        binding.buttonRed.setOnClickListener{
            binding.buttonSelected.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
            newSelectedColor(R.color.red)
        }

        binding.buttonOrange.setOnClickListener{
            binding.buttonSelected.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.orange))
            newSelectedColor(R.color.orange)
        }

        binding.buttonYellow.setOnClickListener{
            binding.buttonSelected.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.yellow))
            newSelectedColor(R.color.yellow)
        }

        binding.buttonPistaccio.setOnClickListener{
            binding.buttonSelected.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.pistaccio))
            newSelectedColor(R.color.pistaccio)
        }


        binding.buttonGreen.setOnClickListener{
            binding.buttonSelected.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
            newSelectedColor(R.color.green)
        }

        binding.buttonTeal.setOnClickListener{
            binding.buttonSelected.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.teal))
            newSelectedColor(R.color.teal)
        }

        binding.buttonLightBlue.setOnClickListener{
            binding.buttonSelected.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_blue))
            newSelectedColor(R.color.light_blue)
        }

        binding.buttonBlue.setOnClickListener{
            binding.buttonSelected.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue))
            newSelectedColor(R.color.blue)
        }


        binding.buttonDarkBlue.setOnClickListener{
            binding.buttonSelected.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dark_blue))
            newSelectedColor(R.color.dark_blue)
        }

        binding.buttonPurple.setOnClickListener{
            binding.buttonSelected.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple))
            newSelectedColor(R.color.purple)
        }

        binding.buttonPink.setOnClickListener{
            binding.buttonSelected.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.pink))
            newSelectedColor(R.color.pink)
        }

        binding.buttonWine.setOnClickListener{
            binding.buttonSelected.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.wine))
            newSelectedColor(R.color.wine)
        }


        binding.buttonWhite.setOnClickListener{
            binding.buttonSelected.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            newSelectedColor(R.color.white)
        }

        binding.buttonGray.setOnClickListener{
            binding.buttonSelected.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray))
            newSelectedColor(R.color.gray)
        }

        binding.buttonBrown.setOnClickListener{
            binding.buttonSelected.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.brown))
            newSelectedColor(R.color.brown)
        }

        binding.buttonBlack.setOnClickListener{
            binding.buttonSelected.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black))
            newSelectedColor(R.color.black)
        }

        binding.buttonBack.setOnClickListener {

            this.findNavController().navigate(ColorFragmentDirections.actionColorFragmentToMatchFragment(selectedColor, matchName, players))
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(ColorFragmentDirections.actionColorFragmentToMatchFragment(selectedColor, matchName, players))
        }

        return binding.root
    }

    fun newSelectedColor(color: Int){
        selectedColor = color
        println(selectedColor)
    }

}