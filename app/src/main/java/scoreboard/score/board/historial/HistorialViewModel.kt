package scoreboard.score.board.historial

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import scoreboard.score.board.database.MatchsPlayers
import scoreboard.score.board.database.getDatabase

class HistorialViewModel(context: Context) : ViewModel(){

    var matchsDao = getDatabase(context).matchsDao

    private val _getMatches = MutableLiveData<Boolean>()
    val getMatches: LiveData<Boolean>
        get() = _getMatches

    private val _matchsPlayers = MutableLiveData<List<MatchsPlayers>>()
    val matchsPlayers: LiveData<List<MatchsPlayers>>
        get() = _matchsPlayers


    init{
        _getMatches.value = false
        CoroutineScope(IO).launch {
            _matchsPlayers.postValue(matchsDao.getAllPlayersFromMatchs())
            _getMatches.postValue(true)
        }
    }

    fun delete(matchsPlayers: MatchsPlayers) {
        CoroutineScope(IO).launch {
            matchsDao.deleteMatch(matchsPlayers.match.matchId)
        }
    }
}