package com.quizscan.app
import android.app.Application
import androidx.room.Room
import com.quizscan.app.data.AppDatabase
class QuizScanApplication: Application() { val database by lazy { Room.databaseBuilder(this,AppDatabase::class.java,"quizscan.db").build() } }