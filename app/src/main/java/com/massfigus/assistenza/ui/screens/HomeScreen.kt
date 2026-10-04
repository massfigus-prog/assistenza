package com.massfigus.assistenza.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.massfigus.assistenza.ui.AppViewModel
import com.massfigus.assistenza.ui.components.BarraSuperiore
import com.massfigus.assistenza.ui.components.ClienteRiga
import com.massfigus.assistenza.ui.components.InterventoCard
import com.massfigus.assistenza.ui.components.MacchinaRiga
import com.massfigus.assistenza.ui.components.TestoVuoto
import com.massfigus.assistenza.ui.components.TitoloSezione

@Composable
fun HomeScreen(
    vm: AppViewModel,
    onApriCliente: (Long) -> Unit,
    onApriIntervento: (Long) -> Unit,
    onNuovoCliente: () -> Unit,
    onNuovoIntervento: () -> Unit,
    onImpostazioni: () -> Unit
) {
    val ricerca by vm.ricerca.collectAsState()
    val clienti by vm.clientiTrovati.collectAsState()
    val macchine by vm.macchineTrovate.collectAsState()
    val aperti by vm.interventiAperti.collectAsState()
    var menuAperto by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            BarraSuperiore("Assistenza tecnica", azioni = {
                IconButton(onClick = onImpostazioni) {
                    Icon(Icons.Filled.Settings, contentDescription = "Dati tecnico")
                }
            })
        },
        floatingActionButton = {
            Box {
                FloatingActionButton(
                    onClick = { menuAperto = true },
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = Color(0xFF2B1D00)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Crea nuovo")
                }
                DropdownMenu(expanded = menuAperto, onDismissRequest = { menuAperto = false }) {
                    DropdownMenuItem(
                        text = { Text("Nuovo intervento") },
                        leadingIcon = { Icon(Icons.Filled.Build, contentDescription = null) },
                        onClick = { menuAperto = false; onNuovoIntervento() }
                    )
                    DropdownMenuItem(
                        text = { Text("Nuovo cliente") },
                        leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                        onClick = { menuAperto = false; onNuovoCliente() }
                    )
                }
            }
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = ricerca,
                onValueChange = vm::aggiornaRicerca,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Cerca cliente o matricola") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (ricerca.isNotEmpty()) {
                        IconButton(onClick = { vm.aggiornaRicerca("") }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Svuota ricerca")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (ricerca.isNotBlank()) {
                    item { TitoloSezione("Clienti") }
                    if (clienti.isEmpty()) item { TestoVuoto("Nessun cliente con questo nome.") }
                    items(clienti, key = { "c${it.id}" }) { cliente ->
                        ClienteRiga(cliente) { onApriCliente(cliente.id) }
                    }
                    item { TitoloSezione("Macchine") }
                    if (macchine.isEmpty()) item { TestoVuoto("Nessuna macchina con questa matricola.") }
                    items(macchine, key = { "m${it.macchina.id}" }) { mc ->
                        MacchinaRiga(mc) { onApriCliente(mc.cliente.id) }
                    }
                } else {
                    item { TitoloSezione("Interventi aperti") }
                    if (aperti.isEmpty()) {
                        item { TestoVuoto("Nessun intervento aperto. Tocca + per crearne uno.") }
                    }
                    items(aperti, key = { it.intervento.id }) { d ->
                        InterventoCard(d) { onApriIntervento(d.intervento.id) }
                    }
                }
            }
        }
    }
}
