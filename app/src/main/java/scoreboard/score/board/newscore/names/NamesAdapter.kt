package scoreboard.score.board.newscore.names

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import scoreboard.score.board.databinding.ListNamesBinding

class NamesAdapter : RecyclerView.Adapter<NamesAdapter.ViewHolder>() {

    var data = listOf<String>()
        set(value) {
            field = value
        }

    private var resultNames = Array<String>(10){""}

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, resultNames)
        //println(data.size)
        //println(resultNames[1])
    }

    fun getNames(): Array<String> {
        return resultNames
    }


    class ViewHolder constructor(binding: ListNamesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val listNumJugador: TextView = binding.numJugador
        val listPlayerName: EditText = binding.playerName

        fun bind(item: String, resultNames: Array<String>) {
            listNumJugador.text = item

            listPlayerName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    resultNames[adapterPosition] = s.toString()

                }

                override fun afterTextChanged(s: Editable) {}
            })
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListNamesBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}