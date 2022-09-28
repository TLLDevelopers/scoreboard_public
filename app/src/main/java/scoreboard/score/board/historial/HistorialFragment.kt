package scoreboard.score.board.historial

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.DialogInterface
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.animation.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import scoreboard.score.board.R
import scoreboard.score.board.database.MatchsPlayers
import scoreboard.score.board.databinding.FragmentHistorialBinding


class HistorialFragment : Fragment() {

    private val _isAnimOn = MutableLiveData<Boolean>()
    val isAnimOn: LiveData<Boolean>
        get() = _isAnimOn

    init {
        _isAnimOn.value = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding: FragmentHistorialBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_historial, container, false
        )

        val adViewMainActivity = binding.adBannerHistorial
        adViewMainActivity.loadAd(AdRequest.Builder().build())

        val viewModel = HistorialViewModel(requireContext())

        val adapter = HistorialAdapter()

        binding.historialViewModel = viewModel

        binding.lifecycleOwner = viewLifecycleOwner

        //Get matches from viewmodel to set recyclerView list
        viewModel.getMatches.observe(viewLifecycleOwner) {
            Log.d("HERE", it.toString())
            if (it) {
                Log.d("HERE", "enter")
                adapter.data =
                    viewModel.matchsPlayers.value!!.sortedByDescending { it.match.matchId }
                adapter.dataSearch =
                    viewModel.matchsPlayers.value!!.sortedByDescending { it.match.matchId }
                binding.recyclerHistorial.adapter = adapter
            }
        }

        //Control information animation when user click button
        binding.information.setOnClickListener {
            if(isAnimOn.value == false){
                _isAnimOn.value = true
                animateInfoView(binding)
            }
        }

        //Navigate to selected item in recyclerView
        adapter.newSelected.observe(viewLifecycleOwner) {
            findNavController().navigate(HistorialFragmentDirections.actionHistorialFragmentToScoreFragment(
                adapter.newSelected.value!!))
        }


        binding.matchSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }

        })

        //Show dialog when longclick in specific recyclerview item
        adapter.positionAdap.observe(viewLifecycleOwner){
            val position = adapter.positionAdap.value!!
            showAreYouSureDialog(adapter, viewModel, position)
        }

        //Create canvas when user swipe right to delete item
        itemTouchHelper(viewModel, adapter, binding)

        //Go to main fragment
        binding.buttonForwardToMain.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return binding.root
    }

    /*Animations for all the information views*/
    private fun animateInfoView(binding: FragmentHistorialBinding) {
        val animatorTextVisible = ObjectAnimator.ofFloat(binding.constraintHisto, View.ALPHA, 1f)
        animatorTextVisible.duration =  500
        animatorTextVisible.doOnStart {
            animateTouch(binding)
            animateSwipe(binding)}

        animatorTextVisible.start()

    }

    private fun animateInfoGone(binding: FragmentHistorialBinding) {
        val animatorTextGone = ObjectAnimator.ofFloat(binding.constraintHisto, View.ALPHA, 0f)
        animatorTextGone.duration =  500
        animatorTextGone.start()

        animatorTextGone.doOnEnd {
            _isAnimOn.value = false}
    }

    private fun animateTouch(binding: FragmentHistorialBinding) {

        val animatorTouchScaleX = ObjectAnimator.ofFloat(binding.touch, View.SCALE_X, 0.8f)
        animatorTouchScaleX.duration =  500

        val animatorTouchScaleY = ObjectAnimator.ofFloat(binding.touch, View.SCALE_Y, 0.8f)
        animatorTouchScaleY.duration =  500

        val animatorSetTouchScale = AnimatorSet()
        animatorSetTouchScale.play(animatorTouchScaleX).with(animatorTouchScaleY)


        val animatorTouchScaleXOut = ObjectAnimator.ofFloat(binding.touch, View.SCALE_X, 1f)

        val animatorTouchScaleYOut = ObjectAnimator.ofFloat(binding.touch, View.SCALE_Y, 1f)

        val animatorSetTouchScaleOut = AnimatorSet()
        animatorSetTouchScaleOut.play(animatorTouchScaleXOut).with(animatorTouchScaleYOut)


        val animatortouchVisible = ObjectAnimator.ofFloat(binding.touch, View.ALPHA, 1f)
        animatortouchVisible.duration =  500

        val animatorViewVisible = ObjectAnimator.ofFloat(binding.helpViewTouch, View.ALPHA, 1f)
        animatorViewVisible.duration =  500

        val animatorSetTouchViewVisible = AnimatorSet()
        animatorSetTouchViewVisible.play(animatortouchVisible).with(animatorViewVisible)


        val animatortouchGone = ObjectAnimator.ofFloat(binding.touch, View.ALPHA, 0f)
        animatortouchGone.duration =  500

        val animatorViewGone = ObjectAnimator.ofFloat(binding.helpViewTouch, View.ALPHA, 0f)
        animatorViewGone.duration =  500

        val animatorSetTouchViewGone = AnimatorSet()
        animatorSetTouchViewGone.play(animatortouchGone).with(animatorViewGone)


        val animatorCircleScaleX = ObjectAnimator.ofFloat(binding.circle, View.SCALE_X, 0.6f)
        animatorCircleScaleX.duration =  1500

        val animatorCircleScaleY = ObjectAnimator.ofFloat(binding.circle, View.SCALE_Y, 0.6f)
        animatorCircleScaleY.duration =  1500

        val animatorCircleVisible = ObjectAnimator.ofFloat(binding.circle, View.ALPHA, 0.7f)
        animatorCircleVisible.duration =  1500

        val animatorSetCircleScaleAlpha = AnimatorSet()
        animatorSetCircleScaleAlpha.play(animatorCircleScaleX).with(animatorCircleScaleY).with(animatorCircleVisible)


        val animatorCircleScaleXOut = ObjectAnimator.ofFloat(binding.circle, View.SCALE_X, 0f)

        val animatorCircleScaleYOut = ObjectAnimator.ofFloat(binding.circle, View.SCALE_Y, 0f)

        val animatorCircleVisibleOut = ObjectAnimator.ofFloat(binding.circle, View.ALPHA, 0f)

        val animatorSetCircleScaleAlphaOut = AnimatorSet()
        animatorSetCircleScaleAlphaOut.play(animatorCircleScaleXOut).with(animatorCircleScaleYOut).with(animatorCircleVisibleOut)


        val animatorSetTouchCircleScale = AnimatorSet()
        animatorSetTouchCircleScale.playSequentially(animatorSetTouchScale,
            animatorSetCircleScaleAlpha,
            animatorSetTouchScaleOut,
            animatorSetCircleScaleAlphaOut)

        val animatorSetTouchCircleScale2 = AnimatorSet()
        animatorSetTouchCircleScale2.playSequentially(animatorSetTouchScale,
            animatorSetCircleScaleAlpha,
            animatorSetTouchScaleOut,
            animatorSetCircleScaleAlphaOut)

        val animatorSetTouchCircleScale3 = AnimatorSet()
        animatorSetTouchCircleScale3.playSequentially(animatorSetTouchScale,
            animatorSetCircleScaleAlpha,
            animatorSetTouchScaleOut,
            animatorSetCircleScaleAlphaOut)

        val animatorSetTouchCircleScale4 = AnimatorSet()
        animatorSetTouchCircleScale4.playSequentially(animatorSetTouchScale,
            animatorSetCircleScaleAlpha,
            animatorSetTouchScaleOut,
            animatorSetCircleScaleAlphaOut)


        val animatorSetTouch = AnimatorSet()
        animatorSetTouch.playSequentially(animatorSetTouchViewVisible,
            animatorSetTouchCircleScale,
            animatorSetTouchCircleScale2,
            animatorSetTouchCircleScale3,
            animatorSetTouchCircleScale4,
            animatorSetTouchViewGone)
        animatorSetTouch.start()

        animatorSetTouch.doOnEnd { animateInfoGone(binding) }
    }

    var repeat = 0

    private fun animateSwipe(binding: FragmentHistorialBinding) {

        val animatorSwipeVisible = ObjectAnimator.ofFloat(binding.swipe, View.ALPHA, 1f)
        animatorSwipeVisible.duration =  500

        val animatorViewVisible = ObjectAnimator.ofFloat(binding.helpViewSwipe, View.ALPHA, 1f)
        animatorViewVisible.duration =  500

        val animatorSetSwipeViewVisible = AnimatorSet()
        animatorSetSwipeViewVisible.play(animatorSwipeVisible).with(animatorViewVisible)


        val animatorSwipeGone = ObjectAnimator.ofFloat(binding.swipe, View.ALPHA, 0f)
        animatorSwipeGone.duration =  500

        val animatorViewGone = ObjectAnimator.ofFloat(binding.helpViewSwipe, View.ALPHA, 0f)
        animatorViewGone.duration =  500

        val animatorSetSwipeViewGone = AnimatorSet()
        animatorSetSwipeViewGone.play(animatorSwipeGone).with(animatorViewGone)


        val animatorSwipeRotation = ObjectAnimator.ofFloat(binding.swipe, View.ROTATION, 50f)
        animatorSwipeRotation.repeatCount = 9
        animatorSwipeRotation.repeatMode = ObjectAnimator.REVERSE
        animatorSwipeRotation.duration =  1000

        val animatorSwipeTranslationX = ObjectAnimator.ofFloat(binding.swipe, View.TRANSLATION_X, 500f)
        animatorSwipeTranslationX.repeatCount = 9
        animatorSwipeTranslationX.repeatMode = ObjectAnimator.REVERSE
        animatorSwipeTranslationX.duration =  1000

        val animatorSwipeTranslationY = ObjectAnimator.ofFloat(binding.swipe, View.TRANSLATION_Y, -70f)
        animatorSwipeTranslationY.repeatCount = 19
        animatorSwipeTranslationY.repeatMode = ObjectAnimator.REVERSE
        animatorSwipeTranslationY.duration =  500

        val animatorViewTranslationX = ObjectAnimator.ofFloat(binding.helpViewSwipe, View.TRANSLATION_X, 1500f)
        animatorViewTranslationX.duration =  950

        val animatorViewTranslationXReturn = ObjectAnimator.ofFloat(binding.helpViewSwipe, View.TRANSLATION_X, 0f)
        animatorViewTranslationXReturn.duration =  10

        animatorSwipeTranslationX.doOnRepeat {
            repeat++
            if(repeat%2 == 0){
                animatorViewTranslationX.start()
            }
        }
        animatorSwipeTranslationX.doOnEnd {
            repeat=0
        }

        val animatorSetSwipeMovement = AnimatorSet()
        animatorSetSwipeMovement.play(animatorSwipeRotation)
            .with(animatorSwipeTranslationX)
            .with(animatorSwipeTranslationY)
            .with(animatorViewTranslationX)

        val animatorSetSwipe = AnimatorSet()
        animatorSetSwipe.playSequentially(animatorSetSwipeViewVisible, animatorSetSwipeMovement, animatorSetSwipeViewGone, animatorViewTranslationXReturn)
        animatorSetSwipe.start()

    }


    //Create ItemTouchHelper for draw behind swipe item in recyclerview
    private fun itemTouchHelper(
        viewModel: HistorialViewModel,
        adapter: HistorialAdapter,
        binding: FragmentHistorialBinding
    ){
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                showAreYouSureDialog(adapter, viewModel, viewHolder.adapterPosition, viewHolder)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean,
            ) {

                val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.delete_icon)
                val intrinsicWidth = deleteIcon!!.intrinsicWidth
                val intrinsicHeight = deleteIcon.intrinsicHeight
                val background = ColorDrawable()

                val itemView = viewHolder.itemView
                val itemHeight = itemView.bottom - itemView.top
                val isCanceled = dX == 0f && !isCurrentlyActive

                if (isCanceled) {
                    clearCanvas(c,
                        itemView.left + dX,
                        itemView.top.toFloat(),
                        itemView.left.toFloat(),
                        itemView.bottom.toFloat())
                    super.onChildDraw(c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive)
                    return
                }

                // Draw the red delete background
                background.color = Color.parseColor("#EF5350")
                background.setBounds(itemView.left + dX.toInt(),
                    itemView.top,
                    itemView.left,
                    itemView.bottom)
                background.draw(c)

                // Calculate position of delete icon
                val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
                val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
                val deleteIconLeft = itemView.left + deleteIconMargin
                val deleteIconRight = itemView.left + deleteIconMargin + intrinsicWidth
                val deleteIconBottom = deleteIconTop + intrinsicHeight

                // Draw the delete icon
                deleteIcon.setBounds(deleteIconLeft,
                    deleteIconTop,
                    deleteIconRight,
                    deleteIconBottom)
                deleteIcon.draw(c)


                super.onChildDraw(c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive)
            }

            fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
                val clearPaint =
                    Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }
                c?.drawRect(left, top, right, bottom, clearPaint)
            }

        }).attachToRecyclerView(binding.recyclerHistorial)
    }

    //Show dialog before user delete match
    fun showAreYouSureDialog(
        adapter: HistorialAdapter,
        viewModel: HistorialViewModel,
        position: Int,
        viewHolder: RecyclerView.ViewHolder? = null
    ) {
        val positiveButtonClick = { dialog: DialogInterface, which: Int ->
            Toast.makeText(requireContext(), R.string.match_deleted, Toast.LENGTH_SHORT).show()

            val newAdapterData = mutableListOf<MatchsPlayers>()

            viewModel.delete(adapter.data[position])

            adapter.data.forEachIndexed { index, matchsPlayers ->
                if (index != position) {
                    newAdapterData.add(matchsPlayers)
                }
            }
            adapter.notifyItemRemoved(position)
            adapter.data = newAdapterData
            adapter.dataSearch = newAdapterData
        }

        val negativeButtonClick = { dialog: DialogInterface, which: Int ->
            if (viewHolder != null) {
                adapter.notifyItemChanged(viewHolder.adapterPosition)
            }
            dialog.cancel()
        }

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.dialog_delete_title)
        builder.setMessage(getString(R.string.dialog_delete_text, adapter.data[position].match.matchName))
        builder.setPositiveButton(
            R.string.dialog_delete_delete,
            DialogInterface.OnClickListener(function = positiveButtonClick)
        )
        builder.setNegativeButton(
            R.string.dialog_delete_no,
            DialogInterface.OnClickListener(function = negativeButtonClick)
        )
        builder.setIcon(R.drawable.trash)

        val dialog: AlertDialog = builder.create()
        dialog.setCancelable(false)

        dialog.onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            dialog.cancel()
        }
        dialog.show()
    }
}