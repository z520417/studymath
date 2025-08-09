package com.mathtrainer.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mathtrainer.app.data.entity.OperationType
import com.mathtrainer.app.ui.screen.home.HomeScreen
import com.mathtrainer.app.ui.screen.practice.PracticeScreen
import com.mathtrainer.app.ui.screen.wrongquestions.WrongQuestionsScreen
import com.mathtrainer.app.ui.screen.teaching.TeachingScreen
import com.mathtrainer.app.ui.screen.settings.SettingsScreen
import com.mathtrainer.app.ui.screen.mixedpractice.MixedPracticeConfigScreen

/**
 * 应用导航图
 */
@Composable
fun MathTrainerNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToPractice = { operationType ->
                    navController.navigate("${Screen.Practice.route}/${operationType.name}")
                },
                onNavigateToWrongQuestions = {
                    navController.navigate(Screen.WrongQuestions.route)
                },
                onNavigateToTeaching = {
                    navController.navigate(Screen.Teaching.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToMixedPractice = {
                    navController.navigate(Screen.MixedPracticeConfig.route)
                }
            )
        }
        
        composable("${Screen.Practice.route}/{operationType}") { backStackEntry ->
            val operationTypeName = backStackEntry.arguments?.getString("operationType")
            val isMixedPractice = operationTypeName == "MIXED"
            val operationType = if (isMixedPractice) {
                null
            } else {
                operationTypeName?.let { OperationType.valueOf(it) } ?: OperationType.ADDITION
            }

            PracticeScreen(
                operationType = operationType,
                isMixedPractice = isMixedPractice,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.WrongQuestions.route) {
            WrongQuestionsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToPractice = { operationType ->
                    navController.navigate("${Screen.Practice.route}/${operationType.name}")
                }
            )
        }
        
        composable(Screen.Teaching.route) {
            TeachingScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.MixedPracticeConfig.route) {
            MixedPracticeConfigScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onStartPractice = {
                    navController.navigate("${Screen.Practice.route}/MIXED")
                }
            )
        }
    }
}

/**
 * 屏幕路由定义
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Practice : Screen("practice")
    object WrongQuestions : Screen("wrong_questions")
    object Teaching : Screen("teaching")
    object Settings : Screen("settings")
    object MixedPracticeConfig : Screen("mixed_practice_config")
}
