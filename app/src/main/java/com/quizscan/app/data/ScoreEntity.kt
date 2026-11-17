package com.quizscan.app.data
import androidx.room.*
@Entity(tableName="scores")
data class ScoreEntity(@PrimaryKey(autoGenerate=true) val id:Int=0,val date:String,val category:String,val score:Int)
@Dao interface ScoreDao { @Insert suspend fun insert(item:ScoreEntity); @Query("SELECT * FROM scores ORDER BY id DESC") suspend fun all():List<ScoreEntity> }
@Database(entities=[ScoreEntity::class],version=1,exportSchema=false)
abstract class AppDatabase:RoomDatabase(){ abstract fun scores():ScoreDao }