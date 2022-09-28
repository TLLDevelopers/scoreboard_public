package scoreboard.score.board.historial

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import scoreboard.score.board.database.MatchsPlayers
import scoreboard.score.board.databinding.HistorialListAdapterBinding
import java.util.*


class HistorialAdapter : RecyclerView.Adapter<HistorialAdapter.ViewHolder>() , Filterable {

    var data = listOf<MatchsPlayers>()
        set(value) {
            field = value
        }

    var dataSearch = listOf<MatchsPlayers>()
        set(value) {
            field = value
        }

    private val _newSelected = MutableLiveData<Int>()
    val newSelected: LiveData<Int>
        get() = _newSelected

    private val _matchName = MutableLiveData<String>()
    val matchName: LiveData<String>
        get() = _matchName

    private val _positionAdap = MutableLiveData<Int>()
    val positionAdap: LiveData<Int>
        get() = _positionAdap

    override fun getItemCount() = dataSearch.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataSearch[position]
        holder.bind(item, _newSelected, _matchName, _positionAdap)
    }

    class ViewHolder constructor(binding: HistorialListAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val matchNameHistorial: TextView = binding.matchNameHistorial
        val numOfPlayers: TextView = binding.numOfPlayers
        val nameOfFirst: TextView = binding.nameOfFirst
        val linearBack = binding.linearBackHisto

        fun bind(
            item: MatchsPlayers,
            _newSelected: MutableLiveData<Int>,
            _matchName: MutableLiveData<String>,
            _positionAdap: MutableLiveData<Int>
        ) {
            matchNameHistorial.text = item.match.matchName
            numOfPlayers.text = item.match.numPlayers.toString()
            val first = item.players.sortedByDescending { it.points }
            nameOfFirst.text = first[0].playerName


            //Style to specific item
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            shape.cornerRadius = 100f
            shape.setStroke(20, item.match.color)

            val csl = ColorStateList.valueOf(item.match.color)
            Log.d("COLORS", item.match.color.toString())

            val contentDrawable = GradientDrawable()
            contentDrawable.setColor(Color.WHITE)
            contentDrawable.cornerRadius = 100f

            val ripple = RippleDrawable(csl, contentDrawable, null)


            linearBack.background = ripple
            linearBack.isClickable = true
            linearBack.foreground = shape

            //Go to match
            linearBack.setOnClickListener {
                _matchName.value = item.match.matchName
                _newSelected.value = item.match.matchId
            }

            //Control for delete match
            linearBack.setOnLongClickListener {
                _positionAdap.value = adapterPosition
                true
            }

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = HistorialListAdapterBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    //Search filter for recycler view
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    dataSearch = data
                } else {
                    val resultList = mutableListOf<MatchsPlayers>()
                    for (item in data) {
                        if (item.match.matchName.lowercase().contains(charSearch.lowercase(Locale.ROOT))
                        ) {
                            resultList.add(item)
                        }
                    }
                    dataSearch = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = dataSearch
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                dataSearch = results?.values as List<MatchsPlayers>
                notifyDataSetChanged()
            }

        }
    }
}
