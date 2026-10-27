package com.contascaseiras.app.data.repository

import com.contascaseiras.app.data.local.dao.TransacaoDao
import com.contascaseiras.app.data.local.entity.TransacaoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import javax.inject.Inject

class TransacaoRepositoryImpl @Inject constructor(
    private val dao: TransacaoDao,
) : TransacaoRepository {

    override fun observarPorPeriodo(inicio: LocalDate, fim: LocalDate): Flow<List<TransacaoEntity>> =
        dao.observarPorPeriodo(inicio, fim)

    override suspend fun buscarPorId(id: Long): TransacaoEntity? = dao.buscarPorId(id)

    override suspend fun listarComLembretePendente(hoje: LocalDate): List<TransacaoEntity> =
        dao.listarComLembretePendente(hoje)

    override suspend fun salvar(transacao: TransacaoEntity): Long =
        if (transacao.id == 0L) {
            dao.inserir(transacao)
        } else {
            dao.atualizar(transacao)
            transacao.id
        }

    override suspend fun remover(transacao: TransacaoEntity) = dao.remover(transacao)
}
