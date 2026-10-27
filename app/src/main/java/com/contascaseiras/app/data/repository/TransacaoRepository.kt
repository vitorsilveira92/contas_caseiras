package com.contascaseiras.app.data.repository

import com.contascaseiras.app.data.local.entity.TransacaoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface TransacaoRepository {
    fun observarPorPeriodo(inicio: LocalDate, fim: LocalDate): Flow<List<TransacaoEntity>>
    suspend fun buscarPorId(id: Long): TransacaoEntity?
    suspend fun listarComLembretePendente(hoje: LocalDate): List<TransacaoEntity>
    suspend fun salvar(transacao: TransacaoEntity): Long
    suspend fun remover(transacao: TransacaoEntity)
}
