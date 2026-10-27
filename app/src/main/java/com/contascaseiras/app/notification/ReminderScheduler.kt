package com.contascaseiras.app.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.content.getSystemService
import com.contascaseiras.app.data.local.entity.TransacaoEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import javax.inject.Inject
import javax.inject.Singleton

private const val HORA_PADRAO_LEMBRETE = 9

/**
 * Agenda e cancela lembretes locais de vencimento via AlarmManager.
 * Alarmes exatos são perdidos no reboot, por isso o BootReceiver reagenda tudo.
 */
@Singleton
class ReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val alarmManager: AlarmManager? = context.getSystemService()

    fun agendar(transacao: TransacaoEntity) {
        val diasAntes = transacao.lembreteDiasAntes ?: return
        val dataLembrete = transacao.dataVencimento.minus(DatePeriod(days = diasAntes))
        val instanteBase = dataLembrete.atStartOfDayIn(TimeZone.currentSystemDefault())
        val horarioDisparo = instanteBase.toEpochMilliseconds() + HORA_PADRAO_LEMBRETE * 60 * 60 * 1000L

        if (horarioDisparo <= System.currentTimeMillis()) return

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_TRANSACAO_ID, transacao.id)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            transacao.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        alarmManager?.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, horarioDisparo, pendingIntent)
    }

    fun cancelar(transacaoId: Long) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            transacaoId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        alarmManager?.cancel(pendingIntent)
    }
}
