package com.contascaseiras.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.contascaseiras.app.R
import com.contascaseiras.app.data.repository.TransacaoRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: TransacaoRepository

    override fun onReceive(context: Context, intent: Intent) {
        val transacaoId = intent.getLongExtra(EXTRA_TRANSACAO_ID, -1L)
        if (transacaoId == -1L) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val transacao = repository.buscarPorId(transacaoId)
                if (transacao != null) {
                    NotificationHelper.notificar(
                        context = context,
                        notificationId = transacaoId.toInt(),
                        titulo = context.getString(R.string.notificacao_titulo, transacao.titulo),
                        texto = context.getString(
                            R.string.notificacao_texto,
                            transacao.dataVencimento.toString(),
                            transacao.titulo,
                        ),
                    )
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val EXTRA_TRANSACAO_ID = "extra_transacao_id"
    }
}
