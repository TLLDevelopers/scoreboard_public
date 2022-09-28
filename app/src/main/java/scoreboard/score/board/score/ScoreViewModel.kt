package scoreboard.score.board.score

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import scoreboard.score.board.database.Match
import scoreboard.score.board.database.MatchsPlayers
import scoreboard.score.board.database.getDatabase

class ScoreViewModel(context: Context, var key: Int) : ViewModel() {

    var matchsDao = getDatabase(context).matchsDao
    var playersDao = getDatabase(context).playersDao

    private val _actualMatchName = MutableLiveData<String>()
    val actualMatchName: LiveData<String>
        get() = _actualMatchName

    private val _actualPlayersNames = MutableLiveData<String>()
    val actualPlayersNames: LiveData<String>
        get() = _actualPlayersNames

    private val _actualMatch = MutableLiveData<Match>()
    val actualMatch: LiveData<Match>
        get() = _actualMatch

    private val _actualMatchsPlayers = MutableLiveData<List<MatchsPlayers>>()
    val actualMatchsPlayers: LiveData<List<MatchsPlayers>>
        get() = _actualMatchsPlayers

    private val _allMatchs = MutableLiveData<List<Match>>()
    val allMatchs: LiveData<List<Match>>
        get() = _allMatchs

    init {
        CoroutineScope(Dispatchers.IO).launch {
            _actualMatchsPlayers.postValue(matchsDao.getPlayersFromMatch(key))
        }
    }

    fun setValue(){
        _actualMatchName.value = _actualMatchsPlayers.value!![0].match.matchName.toString()

        //_actualMatchsPlayers.value!![0]?.players!!.forEach { _actualPlayersNames.value += "1 " + it.playerName }

        _actualPlayersNames.value = ""

        _actualMatchsPlayers.value!![0].players.forEach {
            //_actualPlayersNames.value += "Partida "+it.matchId + " Players " + it.numPlayers + "\n" +
            _actualPlayersNames.value += "Partida " + _actualMatchsPlayers.value!![0].match.matchName + "Jugadors :" + it.playerName + "YA \n"
        }
    }

}