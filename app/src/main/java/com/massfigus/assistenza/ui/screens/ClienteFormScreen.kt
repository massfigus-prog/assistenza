package com.massfigus.assistenza.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.massfigus.assistenza.model.Cliente
import com.massfigus.assistenza.ui.AppViewModel
import com.massfigus.assistenza.ui.components.BarraSuperiore

@Composable
fun ClienteFormScreen(
    vm: AppViewModel,
    clienteId: Long,
    onBack: () -> Unit,
    onSalvato: (Long) -> Unit
) {
    var nome by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var indirizzo by rememberSaveable { mutableStateOf("") }
    var caricato by rememberSaveable { mutableStateOf(false) }
    var errore by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(clienteId) {
        if (clienteId > 0 && !caricato) {
            vm.clienteOnce(clienteId)?.let {
                nome = it.nome; telefono = it.telefono; email = it.email; indirizzo = it.indirizzo
            }
        }
        caricato = true
    }

    Scaffold(
        topBar = { BarraSuperiore(if (clienteId > 0) "Modifica cliente" else "Nuovo cliente", onBack) }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it; errore = false },
                label = { Text("Nome o ragione sociale") },
                isError = errore,
                supportingText = if (errore) { { Text("Inserisci il nome del cliente") } } else null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Telefono") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = indirizzo,
                onValueChange = { indirizzo = it },
                label = { Text("Indirizzo") },
                minLines = 2,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    if (nome.isBlank()) {
                        errore = true
                    } else {
                        vm.salvaCliente(
                            Cliente(
                                id = clienteId,
                                nome = nome.trim(),
                                telefono = telefono.trim(),
                                email = email.trim(),
                                indirizzo = indirizzo.trim()
                            ),
                            onSalvato
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Filled.Check, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Salva cliente")
            }
        }
    }
}
