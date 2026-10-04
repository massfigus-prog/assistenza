@file:OptIn(ExperimentalMaterial3Api::class)

package com.massfigus.assistenza.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.massfigus.assistenza.model.Macchina
import com.massfigus.assistenza.ui.AppViewModel
import com.massfigus.assistenza.ui.components.BarraSuperiore
import com.massfigus.assistenza.ui.components.InterventoCard
import com.massfigus.assistenza.ui.components.RigaInfo
import com.massfigus.assistenza.ui.components.TestoVuoto
import com.massfigus.assistenza.ui.components.TitoloSezione

@Composable
fun DettaglioClienteScreen(
    vm: AppViewModel,
    clienteId: Long,
    onBack: () -> Unit,
    onModifica: () -> Unit,
    onNuovoIntervento: (Long) -> Unit,
    onApriIntervento: (Long) -> Unit
) {
    val cliente by remember(clienteId) { vm.cliente(clienteId) }.collectAsState(initial = null)
    val macchine by remember(clienteId) { vm.macchineDi(clienteId) }.collectAsState(initial = emptyList())
    val interventi by remember(clienteId) { vm.interventiDi(clienteId) }.collectAsState(initial = emptyList())

    var modello by rememberSaveable { mutableStateOf("") }
    var matricola by rememberSaveable { mutableStateOf("") }
    var confermaEliminaCliente by remember { mutableStateOf(false) }
    var macchinaDaEliminare by remember { mutableStateOf<Macchina?>(null) }

    Scaffold(
        topBar = {
            BarraSuperiore(cliente?.nome ?: "Cliente", onBack, azioni = {
                IconButton(onClick = onModifica) { Icon(Icons.Filled.Edit, "Modifica cliente") }
                IconButton(onClick = { confermaEliminaCliente = true }) { Icon(Icons.Filled.Delete, "Elimina cliente") }
            })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .imePadding(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            cliente?.let { c ->
                item {
                    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                        Column(Modifier.padding(16.dp)) {
                            Text(c.nome, style = MaterialTheme.typography.titleLarge)
                            Spacer(Modifier.size(6.dp))
                            RigaInfo(Icons.Filled.Phone, c.telefono)
                            RigaInfo(Icons.Filled.Email, c.email)
                            RigaInfo(Icons.Filled.Place, c.indirizzo)
                        }
                    }
                }
            }

            item { TitoloSezione("Macchine") }
            if (macchine.isEmpty()) item { TestoVuoto("Nessuna macchina associata. Aggiungila qui sotto.") }
            items(macchine, key = { it.id }) { m ->
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(start = 16.dp, end = 4.dp, top = 10.dp, bottom = 10.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(m.modello, style = MaterialTheme.typography.titleMedium)
                            Text("Matricola ${m.matricola}", style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        FilledTonalButton(onClick = { onNuovoIntervento(m.id) }) {
                            Icon(Icons.Filled.Build, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Intervento")
                        }
                        IconButton(onClick = { macchinaDaEliminare = m }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Elimina macchina")
                        }
                    }
                }
            }

            item {
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Aggiungi una macchina", style = MaterialTheme.typography.titleSmall)
                        OutlinedTextField(
                            value = modello, onValueChange = { modello = it },
                            label = { Text("Modello") }, singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = matricola, onValueChange = { matricola = it },
                            label = { Text("Matricola") }, singleLine = true,
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                vm.aggiungiMacchina(clienteId, modello, matricola)
                                modello = ""; matricola = ""
                            },
                            enabled = modello.isNotBlank() && matricola.isNotBlank(),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Aggiungi macchina")
                        }
                    }
                }
            }

            item { TitoloSezione("Storico interventi") }
            if (interventi.isEmpty()) item { TestoVuoto("Ancora nessun intervento per questo cliente.") }
            items(interventi, key = { "i${it.intervento.id}" }) { d ->
                InterventoCard(d) { onApriIntervento(d.intervento.id) }
            }
        }
    }

    if (confermaEliminaCliente) {
        AlertDialog(
            onDismissRequest = { confermaEliminaCliente = false },
            title = { Text("Eliminare il cliente?") },
            text = { Text("Verranno eliminate anche tutte le sue macchine e i relativi interventi.") },
            confirmButton = {
                TextButton(onClick = {
                    confermaEliminaCliente = false
                    cliente?.let { vm.eliminaCliente(it, onBack) }
                }) { Text("Elimina") }
            },
            dismissButton = { TextButton(onClick = { confermaEliminaCliente = false }) { Text("Annulla") } }
        )
    }

    macchinaDaEliminare?.let { m ->
        AlertDialog(
            onDismissRequest = { macchinaDaEliminare = null },
            title = { Text("Eliminare ${m.modello}?") },
            text = { Text("Verranno eliminati anche gli interventi registrati su questa macchina.") },
            confirmButton = {
                TextButton(onClick = { vm.eliminaMacchina(m); macchinaDaEliminare = null }) { Text("Elimina") }
            },
            dismissButton = { TextButton(onClick = { macchinaDaEliminare = null }) { Text("Annulla") } }
        )
    }
}
