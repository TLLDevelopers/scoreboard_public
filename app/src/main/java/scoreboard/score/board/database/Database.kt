package scoreboard.score.board.database

import android.content.Context
import androidx.room.*
import androidx.room.ForeignKey.CASCADE


/**
* DATABASE to save matchs and players
 *
 * Each match have a list of Players that is created with an embedded class call MatchsPlayers, which
 * with the parameters matchId(matchs) and matchParentId(players) relates this two data classes.
*
* **/

@androidx.room.Database(entities = [Match::class, Player::class], version = 4, exportSchema = false)
abstract class Database: RoomDatabase() {
    abstract val matchsDao: MatchsDao
    abstract val playersDao: PlayersDao

    companion object {
        const val DATABASE_NAME = "db_matchs"
    }
}

private lateinit var INSTANCE: Database

fun getDatabase(context: Context): Database {
    synchronized(Database::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                Database::class.java,
                Database.DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return INSTANCE
}

@Dao
interface MatchsDao{

    @Insert
    suspend fun insert(match: Match)

    @Update
    suspend fun update(match: Match)

    @Query("DELETE FROM matchs")
    suspend fun delete()

    @Query("DELETE FROM matchs WHERE matchId = :key")
    suspend fun deleteMatch(key: Int)

    @Query("SELECT * FROM matchs ORDER BY matchId DESC LIMIT 1")
    suspend fun getLastMatch(): Match

    //@Transaction
    //@Query("SELECT * FROM matchs WHERE matchId = :key")
    //fun getPlayersFromMatch(key: Int): List<MatchsPlayers?>?

    @Query("SELECT * FROM matchs")
    fun getAllMatchs(): List<Match>

    @Transaction
    @Query("SELECT * FROM matchs WHERE matchId = :key")
    fun getPlayersFromMatch(key: Int): List<MatchsPlayers>

    @Transaction
    @Query("SELECT * FROM matchs")
    fun getAllPlayersFromMatchs(): List<MatchsPlayers>
}

@Dao
interface PlayersDao{

    @Insert
    suspend fun insert(player: Player)

    @Update
    suspend fun update(player: Player)

    //@Transaction
    //@Query("SELECT * FROM players")
    //fun getPlayersFromMatch(): List<MatchsPlayers?>?
}


@Entity(tableName = "matchs")
data class Match(
    @PrimaryKey(autoGenerate = true)
    //@ColumnInfo(name = "matchId")
    var matchId:Int,

    @ColumnInfo(name = "matchName")
    var matchName:String,

    @ColumnInfo(name = "numPlayers")
    var numPlayers:Int,

    @ColumnInfo(name = "color")
    var color:Int
)

//@Entity(
//    tableName = "players",
//    foreignKeys = [ForeignKey(
//        entity = Match::class,
//        parentColumns = ["matchId"],
//        childColumns = ["playerId"]
//    )]
//)

@Entity(tableName = "players", foreignKeys = [
    ForeignKey(
        entity = Match::class,
        parentColumns = ["matchId"],
        childColumns = ["matchParentId"],
        onDelete = CASCADE
    )
])
data class Player(
    @PrimaryKey(autoGenerate = true)
    //@ColumnInfo(name = "playerId")
    var playerId:Int,

    @ColumnInfo(name = "playerName")
    var playerName: String,

    @ColumnInfo(name = "points")
    var points: Int,

    var matchParentId:Int
)

class MatchsPlayers {
    @Embedded
    lateinit var match: Match

    @Relation(
        parentColumn = "matchId",
        entityColumn = "matchParentId"
    )

    lateinit var players: List<Player>
}