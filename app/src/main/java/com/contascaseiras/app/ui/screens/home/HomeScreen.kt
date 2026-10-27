package com.contascaseiras.app.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.contascaseiras.app.data.local.entity.TransacaoEntity
import com.contascaseiras.app.domain.model.StatusPagamento
import com.contascaseiras.app.domain.model.TipoTransacao
import com.contascaseiras.app.ui.theme.labelRes
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAdicionar: () -> Unit,
    onEditar: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val formatoMoeda = remember(Locale.getDefault()) {
        NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(onClick = viewModel::mesAnterior) {
                            Icon(Icons.Filled.ChevronLeft, contentDescription = "Mês anterior")
                        }
                        Text("${uiState.mes.mes}/${uiState.mes.ano}")
                        IconButton(onClick = viewModel::proximoMes) {
                            Icon(Icons.Filled.ChevronRight, contentDescription = "Próximo mês")
                        }
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAdicionar) {
                Icon(Icons.Filled.Add, contentDescription = "Adicionar transação")
            }
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Saldo previsto do mês")
                    Text(formatoMoeda.format(uiState.saldoPrevisto))
                }
            }

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(uiState.transacoes, key = { it.id }) { transacao ->
                    TransacaoItem(
                        transacao = transacao,
                        formatoMoeda = formatoMoeda,
                        onClick = { onEditar(transacao.id) },
                        onToggleStatus = { viewModel.alternarStatus(transacao) },
                    )
                }
            }
        }
    }
}

@Composable
private fun TransacaoItem(
    transacao: TransacaoEntity,
    formatoMoeda: NumberFormat,
    onClick: () -> Unit,
    onToggleStatus: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(transacao.titulo)
                Text(stringResource(transacao.categoria.labelRes()))
                Text(transacao.dataVencimento.toString())
            }
            Column(horizontalAlignment = Alignment.End) {
                val sinal = if (transacao.tipo == TipoTransacao.ENTRADA) "+" else "-"
                Text("$sinal${formatoMoeda.format(transacao.valor)}")
                Text(
                    text = if (transacao.status == StatusPagamento.PAGO) "Pago" else "Pendente",
                    modifier = Modifier.clickable(onClick = onToggleStatus),
                )
            }
        }
    }
}
