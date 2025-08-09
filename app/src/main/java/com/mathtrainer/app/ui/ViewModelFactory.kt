package com.mathtrainer.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.mathtrainer.app.MathTrainerApplication
import com.mathtrainer.app.ui.screen.home.HomeViewModel
import com.mathtrainer.app.ui.screen.practice.PracticeViewModel
import com.mathtrainer.app.ui.screen.settings.SettingsViewModel
import com.mathtrainer.app.ui.screen.wrongquestions.WrongQuestionsViewModel
import com.mathtrainer.app.ui.screen.mixedpractice.MixedPracticeConfigViewModel

/**
 * ViewModel工厂类
 */
class ViewModelFactory(private val application: MathTrainerApplication) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return when (modelClass) {
            HomeViewModel::class.java -> {
                HomeViewModel(application.repository) as T
            }
            PracticeViewModel::class.java -> {
                PracticeViewModel(application.repository, application.questionGenerator) as T
            }
            WrongQuestionsViewModel::class.java -> {
                WrongQuestionsViewModel(application.repository, application.questionGenerator) as T
            }
            SettingsViewModel::class.java -> {
                SettingsViewModel(application.repository) as T
            }
            MixedPracticeConfigViewModel::class.java -> {
                MixedPracticeConfigViewModel(application.repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
