package com.contascaseiras.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contascaseiras.app.data.local.entity.TransacaoEntity
import com.contascaseiras.app.data.repository.TransacaoRepository
import com.contascaseiras.app.domain.model.StatusPagamento
import com.contascaseiras.app.domain.model.TipoTransacao
import com.contascaseiras.app.notification.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import javax.inject.Inject

data class MesSelecionado(val ano: Int, val mes: Int) {
    val inicio: LocalDate get() = LocalDate(ano, mes, 1)
    val fim: LocalDate get() = inicio.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY)

    fun anterior() = if (mes == 1) MesSelecionado(ano - 1, 12) else MesSelecionado(ano, mes - 1)
    fun proximo() = if (mes == 12) MesSelecionado(ano + 1, 1) else MesSelecionado(ano, mes + 1)
}

data class HomeUiState(
    val mes: MesSelecionado,
    val transacoes: List<TransacaoEntity> = emptyList(),
    val saldoPrevisto: Double = 0.0,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TransacaoRepository,
    private val reminderScheduler: ReminderScheduler,
) : ViewModel() {

    private val hoje = Clock.System.todayIn(TimeZone.currentSystemDefault())
    private val mesAtual = MutableStateFlow(MesSelecionado(hoje.year, hoje.monthNumber))

    val uiState: StateFlow<HomeUiState> = mesAtual
        .flatMapLatest { mes ->
            repository.observarPorPeriodo(mes.inicio, mes.fim).combine(mesAtual) { transacoes, mesSelecionado ->
                HomeUiState(
                    mes = mesSelecionado,
                    transacoes = transacoes,
                    saldoPrevisto = calcularSaldoPrevisto(transacoes),
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState(mesAtual.value),
        )

    private fun calcularSaldoPrevisto(transacoes: List<TransacaoEntity>): Double =
        transacoes.sumOf { if (it.tipo == TipoTransacao.ENTRADA) it.valor else -it.valor }

    fun mesAnterior() {
        mesAtual.value = mesAtual.value.anterior()
    }

    fun proximoMes() {
        mesAtual.value = mesAtual.value.proximo()
    }

    fun alternarStatus(transacao: TransacaoEntity) {
        viewModelScope.launch {
            val novoStatus = if (transacao.status == StatusPagamento.PAGO) {
                StatusPagamento.PENDENTE
            } else {
                StatusPagamento.PAGO
            }
            val atualizada = transacao.copy(
                status = novoStatus,
                dataPagamento = if (novoStatus == StatusPagamento.PAGO) hoje else null,
            )
            repository.salvar(atualizada)
            if (novoStatus == StatusPagamento.PAGO) {
                reminderScheduler.cancelar(atualizada.id)
            } else {
                reminderScheduler.agendar(atualizada)
            }
        }
    }

    fun remover(transacao: TransacaoEntity) {
        viewModelScope.launch {
            repository.remover(transacao)
            reminderScheduler.cancelar(transacao.id)
        }
    }
}
