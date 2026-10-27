package com.contascaseiras.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.contascaseiras.app.domain.model.Categoria
import com.contascaseiras.app.domain.model.StatusPagamento
import com.contascaseiras.app.domain.model.TipoTransacao
import kotlinx.datetime.LocalDate

@Entity(tableName = "transacoes")
data class TransacaoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titulo: String,
    val valor: Double,
    val tipo: TipoTransacao,
    val categoria: Categoria,
    val status: StatusPagamento,
    val dataVencimento: LocalDate,
    val dataPagamento: LocalDate? = null,
    val lembreteDiasAntes: Int? = null,
    val observacao: String? = null,
)
