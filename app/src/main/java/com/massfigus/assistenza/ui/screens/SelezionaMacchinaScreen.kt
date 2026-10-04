package com.massfigus.assistenza.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.massfigus.assistenza.ui.AppViewModel
import com.massfigus.assistenza.ui.components.BarraSuperiore
import com.massfigus.assistenza.ui.components.MacchinaRiga
import com.massfigus.assistenza.ui.components.TestoVuoto

@Composable
fun SelezionaMacchinaScreen(
    vm: AppViewModel,
    onBack: () -> Unit,
    onSelezionata: (Long) -> Unit,
    onNuovoCliente: () -> Unit
) {
    val tutte by vm.tutteLeMacchine.collectAsState()
    var filtro by rememberSaveable { mutableStateOf("") }
    var inCreazione by remember { mutableStateOf(false) }

    val filtrate = remember(tutte, filtro) {
        if (filtro.isBlank()) tutte else tutte.filter {
            it.macchina.matricola.contains(filtro, ignoreCase = true) ||
                it.macchina.modello.contains(filtro, ignoreCase = true) ||
                it.cliente.nome.contains(filtro, ignoreCase = true)
        }
    }

    Scaffold(topBar = { BarraSuperiore("Nuovo intervento", onBack) }) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Text(
                "Scegli la macchina su cui intervieni",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)
            )
            OutlinedTextField(
                value = filtro,
                onValueChange = { filtro = it },
                placeholder = { Text("Filtra per cliente, modello o matricola") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            if (tutte.isEmpty()) {
                Column(Modifier.padding(horizontal = 16.dp)) {
                    TestoVuoto("Non ci sono ancora macchine. Crea un cliente e aggiungi la sua macchina.")
                    Button(onClick = onNuovoCliente) { Text("Crea cliente") }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filtrate, key = { it.macchina.id }) { mc ->
                        MacchinaRiga(mc) {
                            if (!inCreazione) {
                                inCreazione = true
                                onSelezionata(mc.macchina.id)
                            }
                        }
                    }
                }
            }
        }
    }
}
