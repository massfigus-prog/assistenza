package com.massfigus.assistenza.ui.screens

import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.massfigus.assistenza.model.InterventoDettaglio
import com.massfigus.assistenza.model.StatoIntervento
import com.massfigus.assistenza.report.ShareUtils
import com.massfigus.assistenza.ui.AppViewModel
import com.massfigus.assistenza.ui.components.BarraSuperiore
import com.massfigus.assistenza.ui.components.RigaInfo
import com.massfigus.assistenza.ui.components.StatoChip
import com.massfigus.assistenza.util.Formati
import java.io.File

@Composable
fun DettaglioInterventoScreen(
    vm: AppViewModel,
    interventoId: Long,
    onBack: () -> Unit,
    onFirma: () -> Unit
) {
    val context = LocalContext.current
    val dettaglio by remember(interventoId) { vm.intervento(interventoId) }.collectAsState(initial = null)

    var descrizione by rememberSaveable { mutableStateOf("") }
    var costoTesto by rememberSaveable { mutableStateOf("") }
    var caricato by rememberSaveable { mutableStateOf(false) }
    var inElaborazione by remember { mutableStateOf(false) }
    var confermaElimina by remember { mutableStateOf(false) }

    LaunchedEffect(dettaglio) {
        val d = dettaglio
        if (d != null && !caricato) {
            descrizione = d.intervento.descrizione
            costoTesto = if (d.intervento.costo == 0.0) "" else Formati.numero(d.intervento.costo)
            caricato = true
        }
    }

    fun avviso(testo: String) = Toast.makeText(context, testo, Toast.LENGTH_SHORT).show()

    Scaffold(
        topBar = {
            BarraSuperiore("Intervento n. $interventoId", onBack, azioni = {
                IconButton(onClick = { confermaElimina = true }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Elimina intervento")
                }
            })
        }
    ) { padding ->
        val d = dettaglio
        if (d == null) {
            Box(
                Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else {
            val chiuso = d.intervento.stato == StatoIntervento.CHIUSO
            val modificato = d.intervento.copy(
                descrizione = descrizione.trim(),
                costo = Formati.leggiCosto(costoTesto)
            )

            /** Salva (se aperto), genera il PDF e poi esegue l'azione richiesta. */
            fun conReport(azione: (File, InterventoDettaglio) -> Unit) {
                inElaborazione = true
                vm.generaReport(interventoId, if (chiuso) null else modificato) { file, aggiornato ->
                    inElaborazione = false
                    if (file != null && aggiornato != null) azione(file, aggiornato)
                    else avviso("Impossibile creare il report, riprova.")
                }
            }

            Column(
                Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        Formati.data(d.intervento.data),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    StatoChip(d.intervento.stato)
                }

                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Cliente", style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(d.cliente.nome, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        RigaInfo(Icons.Filled.Phone, d.cliente.telefono)
                        RigaInfo(Icons.Filled.Email, d.cliente.email)
                        RigaInfo(Icons.Filled.Place, d.cliente.indirizzo)
                        Spacer(Modifier.height(12.dp))
                        Text("Macchina", style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(d.macchina.modello, style = MaterialTheme.typography.titleMedium)
                        Text("Matricola ${d.macchina.matricola}", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                OutlinedTextField(
                    value = descrizione,
                    onValueChange = { descrizione = it },
                    label = { Text("Lavoro svolto") },
                    placeholder = { Text("Descrivi il guasto, i ricambi sostituiti e le prove fatte") },
                    minLines = 6,
                    readOnly = chiuso,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = costoTesto,
                    onValueChange = { nuovo ->
                        if (nuovo.all { it.isDigit() || it == ',' || it == '.' }) costoTesto = nuovo
                    },
                    label = { Text("Costo intervento") },
                    prefix = { Text("€ ") },
                    singleLine = true,
                    readOnly = chiuso,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                if (!chiuso) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { vm.salvaIntervento(modificato) { avviso("Intervento salvato") } },
                            modifier = Modifier.weight(1f)
                        ) { Text("Salva") }
                        Button(
                            onClick = { vm.salvaIntervento(modificato) { onFirma() } },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.Create, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Chiudi e firma")
                        }
                    }
                } else {
                    val firma = remember(d.intervento.firmaPath) {
                        d.intervento.firmaPath
                            ?.takeIf { File(it).exists() }
                            ?.let { BitmapFactory.decodeFile(it)?.asImageBitmap() }
                    }
                    Text("Firma del cliente", style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (firma != null) {
                        Image(
                            bitmap = firma,
                            contentDescription = "Firma del cliente",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .background(Color.White)
                        )
                    } else {
                        Text("Firma non disponibile")
                    }
                }

                HorizontalDivider(Modifier.padding(vertical = 4.dp))

                Button(
                    onClick = { conReport { file, _ -> ShareUtils.apriPdf(context, file) } },
                    enabled = !inElaborazione,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(Icons.Filled.List, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Genera report PDF")
                }
                FilledTonalButton(
                    onClick = { conReport { file, agg -> ShareUtils.condividiPdf(context, file, agg) } },
                    enabled = !inElaborazione,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(Icons.Filled.Share, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Invia report")
                }
                if (inElaborazione) LinearProgressIndicator(Modifier.fillMaxWidth())
            }
        }
    }

    if (confermaElimina) {
        AlertDialog(
            onDismissRequest = { confermaElimina = false },
            title = { Text("Eliminare l'intervento?") },
            text = { Text("L'operazione non si può annullare.") },
            confirmButton = {
                TextButton(onClick = {
                    confermaElimina = false
                    dettaglio?.let { vm.eliminaIntervento(it.intervento, onBack) }
                }) { Text("Elimina") }
            },
            dismissButton = { TextButton(onClick = { confermaElimina = false }) { Text("Annulla") } }
        )
    }
}
