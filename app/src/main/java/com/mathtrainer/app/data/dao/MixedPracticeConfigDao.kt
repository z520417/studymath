package com.mathtrainer.app.data.dao

import androidx.room.*
import com.mathtrainer.app.data.entity.MixedPracticeConfig
import kotlinx.coroutines.flow.Flow

/**
 * 混合练习配置数据访问对象
 */
@Dao
interface MixedPracticeConfigDao {
    
    /**
     * 获取混合练习配置
     */
    @Query("SELECT * FROM mixed_practice_configs WHERE id = :id")
    fun getMixedPracticeConfig(id: String = "default"): Flow<MixedPracticeConfig?>
    
    /**
     * 获取混合练习配置（同步）
     */
    @Query("SELECT * FROM mixed_practice_configs WHERE id = :id")
    suspend fun getMixedPracticeConfigSync(id: String = "default"): MixedPracticeConfig?
    
    /**
     * 插入或更新混合练习配置
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateMixedPracticeConfig(config: MixedPracticeConfig)
    
    /**
     * 删除混合练习配置
     */
    @Delete
    suspend fun deleteMixedPracticeConfig(config: MixedPracticeConfig)
    
    /**
     * 重置为默认配置
     */
    @Query("DELETE FROM mixed_practice_configs WHERE id = :id")
    suspend fun resetToDefault(id: String = "default")
}
