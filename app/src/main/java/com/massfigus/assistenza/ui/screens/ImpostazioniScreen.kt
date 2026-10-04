package com.massfigus.assistenza.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.massfigus.assistenza.data.Tecnico
import com.massfigus.assistenza.ui.AppViewModel
import com.massfigus.assistenza.ui.components.BarraSuperiore

@Composable
fun ImpostazioniScreen(vm: AppViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val iniziale = remember { vm.tecnico() }
    var nome by remember { mutableStateOf(iniziale.nome) }
    var azienda by remember { mutableStateOf(iniziale.azienda) }
    var telefono by remember { mutableStateOf(iniziale.telefono) }
    var email by remember { mutableStateOf(iniziale.email) }

    Scaffold(topBar = { BarraSuperiore("Dati del tecnico", onBack) }) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Questi dati compaiono nell'intestazione di ogni report PDF.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedTextField(
                nome, { nome = it }, label = { Text("Nome e cognome") }, singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                azienda, { azienda = it }, label = { Text("Azienda") }, singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                telefono, { telefono = it }, label = { Text("Telefono") }, singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                email, { email = it }, label = { Text("Email") }, singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    vm.salvaTecnico(Tecnico(nome.trim(), azienda.trim(), telefono.trim(), email.trim()))
                    Toast.makeText(context, "Dati salvati", Toast.LENGTH_SHORT).show()
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) { Text("Salva dati") }
        }
    }
}
