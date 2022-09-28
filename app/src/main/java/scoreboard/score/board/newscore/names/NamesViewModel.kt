package scoreboard.score.board.newscore.names

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import scoreboard.score.board.database.Match
import scoreboard.score.board.database.Player
import scoreboard.score.board.database.getDatabase

class NamesViewModel(
    var matchName: String,
    var players: Int,
    context: Context,
    var matchColor: Int,
) : ViewModel() {

    var matchsDao = getDatabase(context).matchsDao
    var playersDao = getDatabase(context).playersDao

    private val _actualMatch = MutableLiveData<Match>()
    val actualMatch: LiveData<Match>
        get() = _actualMatch

    private val _matchId = MutableLiveData<Int>()
    val matchId: MutableLiveData<Int>
        get() = _matchId

    private val _isMatchCreated = MutableLiveData<Boolean>()
    val isMatchCreated: MutableLiveData<Boolean>
        get() = _isMatchCreated

    init {
        _isMatchCreated.value = false
    }

    fun createMatch() {
        CoroutineScope(IO).launch {
            val newMatch = Match(0, matchName, players, matchColor)
            println(matchColor)
            matchsDao.insert(
                newMatch
            )
            _isMatchCreated.postValue(true)
        }
    }

    fun addPlayersToMatch(namesArray: Array<String>) {
        val newNames = Array(players) { "" }
        CoroutineScope(IO).launch {
            val id = matchsDao.getLastMatch().matchId

            newNames.forEachIndexed { index, _ ->
                newNames[index] = namesArray[index].trim()
            }
            newNames.forEach { playersDao.insert(Player(0, it, 0, id)) }
            println("Players Inserted")
            _matchId.postValue(id)
        }
    }
}