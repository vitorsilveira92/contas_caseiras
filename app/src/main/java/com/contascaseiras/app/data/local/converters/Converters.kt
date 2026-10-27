package com.contascaseiras.app.data.local.converters

import androidx.room.TypeConverter
import com.contascaseiras.app.domain.model.Categoria
import com.contascaseiras.app.domain.model.StatusPagamento
import com.contascaseiras.app.domain.model.TipoTransacao
import kotlinx.datetime.LocalDate

class Converters {

    @TypeConverter
    fun fromEpochDay(epochDay: Long?): LocalDate? =
        epochDay?.let { LocalDate.fromEpochDays(it.toInt()) }

    @TypeConverter
    fun toEpochDay(date: LocalDate?): Long? = date?.toEpochDays()?.toLong()

    @TypeConverter
    fun fromTipo(value: String): TipoTransacao = TipoTransacao.valueOf(value)

    @TypeConverter
    fun toTipo(tipo: TipoTransacao): String = tipo.name

    @TypeConverter
    fun fromStatus(value: String): StatusPagamento = StatusPagamento.valueOf(value)

    @TypeConverter
    fun toStatus(status: StatusPagamento): String = status.name

    @TypeConverter
    fun fromCategoria(value: String): Categoria = Categoria.valueOf(value)

    @TypeConverter
    fun toCategoria(categoria: Categoria): String = categoria.name
}
