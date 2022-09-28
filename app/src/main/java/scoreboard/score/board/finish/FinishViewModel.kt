package scoreboard.score.board.finish

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import scoreboard.score.board.database.MatchsPlayers
import scoreboard.score.board.database.getDatabase

class FinishViewModel(context: Context, var matchId: Int): ViewModel() {

    var matchsDao = getDatabase(context).matchsDao

    private val _listMatchPlayers = MutableLiveData<List<MatchsPlayers>>()
    val listMatchPlayers: MutableLiveData<List<MatchsPlayers>>
        get() = _listMatchPlayers

    fun getPlayers(){
        CoroutineScope(IO).launch {
            _listMatchPlayers.postValue(matchsDao.getPlayersFromMatch(matchId))
        }
    }
}