package com.mathtrainer.app

import android.app.Application
import com.mathtrainer.app.data.database.MathTrainerDatabase
import com.mathtrainer.app.domain.generator.QuestionGenerator
import com.mathtrainer.app.domain.repository.MathTrainerRepository

/**
 * 应用程序主类
 */
class MathTrainerApplication : Application() {
    
    // 数据库实例
    val database by lazy { MathTrainerDatabase.getDatabase(this) }
    
    // 仓库实例
    val repository by lazy {
        MathTrainerRepository(
            practiceRecordDao = database.practiceRecordDao(),
            wrongQuestionDao = database.wrongQuestionDao(),
            userSettingsDao = database.userSettingsDao(),
            mixedPracticeConfigDao = database.mixedPracticeConfigDao()
        )
    }
    
    // 题目生成器实例
    val questionGenerator by lazy { QuestionGenerator() }
}
