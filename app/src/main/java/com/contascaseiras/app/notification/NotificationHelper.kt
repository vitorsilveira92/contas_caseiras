package com.contascaseiras.app.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.contascaseiras.app.R

object NotificationHelper {
    const val CANAL_LEMBRETES = "lembretes_contas"

    fun criarCanal(context: Context) {
        val canal = NotificationChannel(
            CANAL_LEMBRETES,
            context.getString(R.string.notificacao_canal_lembretes),
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
    }

    @SuppressLint("MissingPermission") // POST_NOTIFICATIONS é solicitada na MainActivity
    fun notificar(context: Context, notificationId: Int, titulo: String, texto: String) {
        val notificacao = NotificationCompat.Builder(context, CANAL_LEMBRETES)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notificacao)
    }
}
