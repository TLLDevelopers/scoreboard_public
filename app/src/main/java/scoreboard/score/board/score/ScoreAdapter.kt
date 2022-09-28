package scoreboard.score.board.score

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.graphics.toColorInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import scoreboard.score.board.database.MatchsDao
import scoreboard.score.board.database.Player
import scoreboard.score.board.database.PlayersDao
import scoreboard.score.board.database.getDatabase
import scoreboard.score.board.databinding.ScoreListAdapterBinding


class ScoreAdapter(val context: Context) : RecyclerView.Adapter<ScoreAdapter.ViewHolder>() {

    var data = mutableListOf<Player>()
    var color = 10

    var matchsDao = getDatabase(context).matchsDao
    var playersDao = getDatabase(context).playersDao

    private val _itemNewPos = MutableLiveData<Int>()
    val itemNewPos: LiveData<Int>
        get() = _itemNewPos

    private val _itemAntPos = MutableLiveData<Int>()
    val itemAntPos: LiveData<Int>
        get() = _itemAntPos

    private val _falsePoints = MutableLiveData<Boolean>()
    val falsePoints: LiveData<Boolean>
        get() = _falsePoints

    init {
        _itemAntPos.value = 0
        _itemNewPos.value = 0
        _falsePoints.value = false
    }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        data.sortedByDescending { it.points }
        val item = data[position]

        val shape = GradientDrawable()
        shape.shape = GradientDrawable.RECTANGLE
        var floatRad = 200f
        shape.cornerRadii = floatArrayOf(floatRad, floatRad, floatRad, floatRad, floatRad, floatRad, floatRad, floatRad)
        shape.setStroke(10, "#000000".toColorInt())
        shape.setColor(color)
        //shape.setStroke(20, borderColor)

        holder.background.background = shape
        //holder.background.backgroundTintList = AppCompatResources.getColorStateList(context, color)
        println(color)
        holder.background.background.alpha = 170

        holder.bind(item,
            matchsDao,
            playersDao,
            data,
            _itemNewPos,
            _itemAntPos,
            _falsePoints)
    }

    fun cancelFalsePoints() {
        _falsePoints.value = false
    }

    class ViewHolder constructor(binding: ScoreListAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val points: TextView = binding.pointsText
        val playerName: TextView = binding.playerNameText
        val sumar: Button = binding.sumarButton
        val restar: Button = binding.restarButton
        val pointsTo: EditText = binding.pointsTo
        val background = binding.cardScore

        var cont = false

        fun bind(
            item: Player,
            matchsDao: MatchsDao,
            playersDao: PlayersDao,
            data: MutableList<Player>,
            _itemNewPos: MutableLiveData<Int>,
            _itemAntPos: MutableLiveData<Int>,
            _falsePoints: MutableLiveData<Boolean>
        ) {
            playerName.text = item.playerName

            pointsTo.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    if (s.toString().length == 1 && s.toString().startsWith("0")) {
                        s!!.clear();
                    }
                }
            })

            points.text = item.points.toString()

            sumar.setOnClickListener {
                if (pointsTo.text.toString() == "") {
                    _falsePoints.value = true
                } else {
                    val suma = points.text.toString().toInt() + pointsTo.text.toString().toInt()
                    points.text = suma.toString()

                    item.points = suma

                    CoroutineScope(Dispatchers.IO).launch {
                        playersDao.update(item)
                    }

                    var podium = 0
                    data.forEachIndexed { pos, player ->
                        if (player.points <= item.points) {
                            podium++
                        }
                    }
                    val position = data.size - podium

                    if (adapterPosition != position) {
                        _itemAntPos.value = (adapterPosition)
                        _itemNewPos.value = (position)
                    }
                }

            }

            restar.setOnClickListener {
                if (pointsTo.text.toString() == "") {
                    _falsePoints.value = true

                } else {
                    val resta = points.text.toString().toInt() - pointsTo.text.toString().toInt()
                    points.text = resta.toString()

                    item.points = resta

                    CoroutineScope(Dispatchers.IO).launch {
                        playersDao.update(item)
                    }

                    var podium = 0
                    data.forEachIndexed { pos, player ->
                        if (player.points <= item.points) {
                            podium++
                        }
                    }
                    val position = data.size - podium

                    if (adapterPosition != position) {
                        _itemAntPos.value = (adapterPosition)
                        _itemNewPos.value = (position)
                    }
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ScoreListAdapterBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

}