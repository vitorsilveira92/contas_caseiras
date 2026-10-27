package com.contascaseiras.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.contascaseiras.app.data.local.entity.TransacaoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Dao
interface TransacaoDao {

    @Query("SELECT * FROM transacoes WHERE dataVencimento BETWEEN :inicio AND :fim ORDER BY dataVencimento ASC")
    fun observarPorPeriodo(inicio: LocalDate, fim: LocalDate): Flow<List<TransacaoEntity>>

    @Query("SELECT * FROM transacoes WHERE id = :id")
    suspend fun buscarPorId(id: Long): TransacaoEntity?

    @Query(
        "SELECT * FROM transacoes WHERE lembreteDiasAntes IS NOT NULL " +
            "AND status = 'PENDENTE' AND dataVencimento >= :hoje",
    )
    suspend fun listarComLembretePendente(hoje: LocalDate): List<TransacaoEntity>

    @Insert
    suspend fun inserir(transacao: TransacaoEntity): Long

    @Update
    suspend fun atualizar(transacao: TransacaoEntity)

    @Delete
    suspend fun remover(transacao: TransacaoEntity)
}
