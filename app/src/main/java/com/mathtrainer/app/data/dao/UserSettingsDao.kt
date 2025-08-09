package com.mathtrainer.app.data.dao

import androidx.room.*
import com.mathtrainer.app.data.entity.UserSettings
import kotlinx.coroutines.flow.Flow

/**
 * 用户设置数据访问对象
 */
@Dao
interface UserSettingsDao {
    
    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    fun getUserSettings(): Flow<UserSettings?>
    
    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    suspend fun getUserSettingsSync(): UserSettings?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSettings(settings: UserSettings)
    
    @Update
    suspend fun updateSettings(settings: UserSettings)
    
    @Query("DELETE FROM user_settings")
    suspend fun deleteAllSettings()
}
