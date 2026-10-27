package com.contascaseiras.app.ui.screens.addedit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.contascaseiras.app.domain.model.Categoria
import com.contascaseiras.app.domain.model.StatusPagamento
import com.contascaseiras.app.domain.model.TipoTransacao
import com.contascaseiras.app.ui.theme.labelRes
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransacaoScreen(
    onSalvo: () -> Unit,
    viewModel: AddEditTransacaoViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.salvo) {
        if (uiState.salvo) onSalvo()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Transação") }) },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {

            OutlinedTextField(
                value = uiState.titulo,
                onValueChange = viewModel::onTituloChange,
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = uiState.valorTexto,
                onValueChange = viewModel::onValorChange,
                label = { Text("Valor") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            )

            LazyRow(modifier = Modifier.padding(top = 8.dp)) {
                items(TipoTransacao.entries) { tipo ->
                    FilterChip(
                        selected = uiState.tipo == tipo,
                        onClick = { viewModel.onTipoChange(tipo) },
                        label = { Text(if (tipo == TipoTransacao.ENTRADA) "Entrada" else "Saída") },
                        modifier = Modifier.padding(end = 8.dp),
                    )
                }
            }

            LazyRow(modifier = Modifier.padding(top = 8.dp)) {
                items(Categoria.entries) { categoria ->
                    FilterChip(
                        selected = uiState.categoria == categoria,
                        onClick = { viewModel.onCategoriaChange(categoria) },
                        label = { Text(stringResource(categoria.labelRes())) },
                        modifier = Modifier.padding(end = 8.dp),
                    )
                }
            }

            LazyRow(modifier = Modifier.padding(top = 8.dp)) {
                items(StatusPagamento.entries) { status ->
                    FilterChip(
                        selected = uiState.status == status,
                        onClick = { viewModel.onStatusChange(status) },
                        label = { Text(if (status == StatusPagamento.PAGO) "Pago" else "Pendente") },
                        modifier = Modifier.padding(end = 8.dp),
                    )
                }
            }

            var mostrarDatePicker by remember { mutableStateOf(false) }
            Text(
                text = "Vencimento: ${uiState.dataVencimento}",
                modifier = Modifier.padding(top = 16.dp).clickable { mostrarDatePicker = true },
            )

            if (mostrarDatePicker) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = uiState.dataVencimento
                        .atStartOfDayIn(TimeZone.currentSystemDefault())
                        .toEpochMilliseconds(),
                )
                DatePickerDialog(
                    onDismissRequest = { mostrarDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val data = Instant.fromEpochMilliseconds(millis)
                                    .toLocalDateTime(TimeZone.UTC).date
                                viewModel.onDataVencimentoChange(data)
                            }
                            mostrarDatePicker = false
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { mostrarDatePicker = false }) { Text("Cancelar") }
                    },
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            OutlinedTextField(
                value = uiState.lembreteDiasAntes?.toString() ?: "",
                onValueChange = { texto -> viewModel.onLembreteChange(texto.toIntOrNull()) },
                label = { Text("Lembrar quantos dias antes") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            )

            Button(
                onClick = viewModel::salvar,
                enabled = uiState.podeSalvar,
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
            ) {
                Text("Salvar")
            }
        }
    }
}
