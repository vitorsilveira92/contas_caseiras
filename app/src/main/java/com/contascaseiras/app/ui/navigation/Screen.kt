package com.contascaseiras.app.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")

    data object AddEdit : Screen("add_edit?transacaoId={transacaoId}") {
        const val ARG_TRANSACAO_ID = "transacaoId"
        fun rotaParaNova() = "add_edit"
        fun rotaParaEditar(id: Long) = "add_edit?transacaoId=$id"
    }
}
