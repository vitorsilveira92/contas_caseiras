package com.contascaseiras.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.contascaseiras.app.data.repository.TransacaoRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject

/** Alarmes exatos não sobrevivem a um reboot: reagenda todos os lembretes pendentes. */
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: TransacaoRepository

    @Inject
    lateinit var scheduler: ReminderScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val hoje = Clock.System.todayIn(TimeZone.currentSystemDefault())
                repository.listarComLembretePendente(hoje).forEach { scheduler.agendar(it) }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
