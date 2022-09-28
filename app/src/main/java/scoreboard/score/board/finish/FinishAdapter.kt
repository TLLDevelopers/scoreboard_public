package scoreboard.score.board.finish

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import scoreboard.score.board.R
import scoreboard.score.board.database.Player
import scoreboard.score.board.databinding.FinalListAdapterBinding

class FinishAdapter : RecyclerView.Adapter<FinishAdapter.ViewHolder>() {

    var data = listOf<Player>()
        set(value) {
            field = value
        }

    var color = 0

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, color)
    }

    class ViewHolder constructor(binding: FinalListAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val pointsPlayer: TextView = binding.pointsFinal
        val namePlayer: TextView = binding.nameFinal
        val position = binding.position
        val medal = binding.medalImage
        val shape1 = binding.shape1
        val shape2 = binding.shape2

        fun bind(item: Player, color: Int) {
            when(adapterPosition){
                0 -> {
                    medal.setImageResource(R.drawable.first)
                    pointsPlayer.text = item.points.toString()
                    namePlayer.text = item.playerName
                    val csl = ColorStateList.valueOf("#EABE3F".toColorInt())
                    shape1.backgroundTintList = csl
                    shape2.backgroundTintList = csl
                    shape2.background.alpha = 200
                }

                1 -> {
                    medal.setImageResource(R.drawable.second)
                    pointsPlayer.text = item.points.toString()
                    namePlayer.text = item.playerName
                    val csl = ColorStateList.valueOf("#C0C0C0".toColorInt())
                    shape1.backgroundTintList = csl
                    shape2.backgroundTintList = csl
                    shape2.background.alpha = 200
                }

                2 -> {
                    medal.setImageResource(R.drawable.third)
                    pointsPlayer.text = item.points.toString()
                    namePlayer.text = item.playerName
                    val csl = ColorStateList.valueOf("#CD7F32".toColorInt())
                    shape1.backgroundTintList = csl
                    shape2.backgroundTintList = csl
                    shape2.background.alpha = 200
                }

                else -> {
                    medal.visibility = View.INVISIBLE
                    position.text = "${adapterPosition+1}."
                    position.visibility = View.VISIBLE
                    pointsPlayer.text = item.points.toString()
                    namePlayer.text = item.playerName
                    val csl = ColorStateList.valueOf(color)
                    shape1.backgroundTintList = csl
                    shape2.backgroundTintList = csl
                    shape2.background.alpha = 200
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FinalListAdapterBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}