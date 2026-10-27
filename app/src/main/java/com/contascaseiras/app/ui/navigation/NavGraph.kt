package com.contascaseiras.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.contascaseiras.app.ui.screens.addedit.AddEditTransacaoScreen
import com.contascaseiras.app.ui.screens.home.HomeScreen

@Composable
fun ContasCaseirasNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onAdicionar = { navController.navigate(Screen.AddEdit.rotaParaNova()) },
                onEditar = { id -> navController.navigate(Screen.AddEdit.rotaParaEditar(id)) },
            )
        }
        composable(
            route = Screen.AddEdit.route,
            arguments = listOf(
                navArgument(Screen.AddEdit.ARG_TRANSACAO_ID) {
                    type = NavType.StringType
                    nullable = true
                },
            ),
        ) {
            AddEditTransacaoScreen(onSalvo = { navController.popBackStack() })
        }
    }
}
