package com.contascaseiras.app.ui.screens.addedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contascaseiras.app.data.local.entity.TransacaoEntity
import com.contascaseiras.app.data.repository.TransacaoRepository
import com.contascaseiras.app.domain.model.Categoria
import com.contascaseiras.app.domain.model.StatusPagamento
import com.contascaseiras.app.domain.model.TipoTransacao
import com.contascaseiras.app.notification.ReminderScheduler
import com.contascaseiras.app.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject

data class AddEditUiState(
    val id: Long = 0,
    val titulo: String = "",
    val valorTexto: String = "",
    val tipo: TipoTransacao = TipoTransacao.SAIDA,
    val categoria: Categoria = Categoria.OUTRAS,
    val status: StatusPagamento = StatusPagamento.PENDENTE,
    val dataVencimento: kotlinx.datetime.LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    val lembreteDiasAntes: Int? = 1,
    val salvo: Boolean = false,
) {
    val tituloValido: Boolean get() = titulo.isNotBlank()
    val valorValido: Boolean get() = valorTexto.toDoubleOrNull()?.let { it > 0 } == true
    val podeSalvar: Boolean get() = tituloValido && valorValido
}

@HiltViewModel
class AddEditTransacaoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: TransacaoRepository,
    private val reminderScheduler: ReminderScheduler,
) : ViewModel() {

    private val transacaoId: Long? = savedStateHandle.get<String>(Screen.AddEdit.ARG_TRANSACAO_ID)?.toLongOrNull()

    private val _uiState = MutableStateFlow(AddEditUiState())
    val uiState: StateFlow<AddEditUiState> = _uiState.asStateFlow()

    init {
        transacaoId?.let { id ->
            viewModelScope.launch {
                repository.buscarPorId(id)?.let { transacao ->
                    _uiState.value = AddEditUiState(
                        id = transacao.id,
                        titulo = transacao.titulo,
                        valorTexto = transacao.valor.toString(),
                        tipo = transacao.tipo,
                        categoria = transacao.categoria,
                        status = transacao.status,
                        dataVencimento = transacao.dataVencimento,
                        lembreteDiasAntes = transacao.lembreteDiasAntes,
                    )
                }
            }
        }
    }

    fun onTituloChange(valor: String) {
        _uiState.value = _uiState.value.copy(titulo = valor)
    }

    fun onValorChange(valor: String) {
        _uiState.value = _uiState.value.copy(valorTexto = valor)
    }

    fun onTipoChange(tipo: TipoTransacao) {
        _uiState.value = _uiState.value.copy(tipo = tipo)
    }

    fun onCategoriaChange(categoria: Categoria) {
        _uiState.value = _uiState.value.copy(categoria = categoria)
    }

    fun onStatusChange(status: StatusPagamento) {
        _uiState.value = _uiState.value.copy(status = status)
    }

    fun onDataVencimentoChange(data: kotlinx.datetime.LocalDate) {
        _uiState.value = _uiState.value.copy(dataVencimento = data)
    }

    fun onLembreteChange(dias: Int?) {
        _uiState.value = _uiState.value.copy(lembreteDiasAntes = dias)
    }

    fun salvar() {
        val estado = _uiState.value
        if (!estado.podeSalvar) return

        viewModelScope.launch {
            val transacao = TransacaoEntity(
                id = estado.id,
                titulo = estado.titulo.trim(),
                valor = estado.valorTexto.toDouble(),
                tipo = estado.tipo,
                categoria = estado.categoria,
                status = estado.status,
                dataVencimento = estado.dataVencimento,
                dataPagamento = if (estado.status == StatusPagamento.PAGO) {
                    Clock.System.todayIn(TimeZone.currentSystemDefault())
                } else {
                    null
                },
                lembreteDiasAntes = estado.lembreteDiasAntes,
            )
            val idSalvo = repository.salvar(transacao)
            val transacaoSalva = transacao.copy(id = idSalvo)

            reminderScheduler.cancelar(transacaoSalva.id)
            if (transacaoSalva.status == StatusPagamento.PENDENTE) {
                reminderScheduler.agendar(transacaoSalva)
            }

            _uiState.value = _uiState.value.copy(salvo = true)
        }
    }
}
