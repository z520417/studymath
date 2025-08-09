package com.mathtrainer.app.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.mathtrainer.app.data.converter.Converters
import com.mathtrainer.app.data.dao.PracticeRecordDao
import com.mathtrainer.app.data.dao.UserSettingsDao
import com.mathtrainer.app.data.dao.WrongQuestionDao
import com.mathtrainer.app.data.dao.MixedPracticeConfigDao
import com.mathtrainer.app.data.entity.PracticeRecord
import com.mathtrainer.app.data.entity.UserSettings
import com.mathtrainer.app.data.entity.WrongQuestion
import com.mathtrainer.app.data.entity.MixedPracticeConfig

/**
 * 数学练习器数据库
 */
@Database(
    entities = [
        PracticeRecord::class,
        WrongQuestion::class,
        UserSettings::class,
        MixedPracticeConfig::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MathTrainerDatabase : RoomDatabase() {
    
    abstract fun practiceRecordDao(): PracticeRecordDao
    abstract fun wrongQuestionDao(): WrongQuestionDao
    abstract fun userSettingsDao(): UserSettingsDao
    abstract fun mixedPracticeConfigDao(): MixedPracticeConfigDao
    
    companion object {
        @Volatile
        private var INSTANCE: MathTrainerDatabase? = null
        
        fun getDatabase(context: Context): MathTrainerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MathTrainerDatabase::class.java,
                    "math_trainer_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
