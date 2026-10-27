package com.contascaseiras.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.contascaseiras.app.ui.navigation.ContasCaseirasNavGraph
import com.contascaseiras.app.ui.theme.ContasCaseirasTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val solicitarPermissaoNotificacao = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { /* usuário decidiu; lembretes locais dependem dessa permissão no Android 13+ */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            solicitarPermissaoNotificacao.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            ContasCaseirasTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    ContasCaseirasNavGraph(navController = navController)
                }
            }
        }
    }
}
