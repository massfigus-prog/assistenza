@file:OptIn(ExperimentalMaterial3Api::class)

package com.massfigus.assistenza.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.massfigus.assistenza.model.Cliente
import com.massfigus.assistenza.model.Intervento
import com.massfigus.assistenza.model.InterventoDettaglio
import com.massfigus.assistenza.model.Macchina
import com.massfigus.assistenza.model.MacchinaConCliente
import com.massfigus.assistenza.model.StatoIntervento
import com.massfigus.assistenza.ui.theme.AssistenzaTheme
import com.massfigus.assistenza.util.Formati

@Composable
fun BarraSuperiore(
    titolo: String,
    onBack: (() -> Unit)? = null,
    azioni: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = { Text(titolo, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                }
            }
        },
        actions = azioni,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
fun TitoloSezione(testo: String, modifier: Modifier = Modifier) {
    Text(
        testo,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(top = 12.dp, bottom = 4.dp)
    )
}

@Composable
fun TestoVuoto(testo: String) {
    Text(
        testo,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun RigaInfo(icona: ImageVector, testo: String) {
    if (testo.isBlank()) return
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
        Icon(icona, contentDescription = null, modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(8.dp))
        Text(testo, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun StatoChip(stato: StatoIntervento) {
    val aperto = stato == StatoIntervento.APERTO
    Surface(
        color = if (aperto) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer,
        contentColor = if (aperto) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimaryContainer,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            if (aperto) "Aperto" else "Chiuso",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun InterventoCard(d: InterventoDettaglio, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    d.cliente.nome,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                StatoChip(d.intervento.stato)
            }
            Text(
                "${d.macchina.modello}  ·  matr. ${d.macchina.matricola}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (d.intervento.descrizione.isNotBlank()) {
                Text(
                    d.intervento.descrizione,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                Formati.data(d.intervento.data),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ClienteRiga(c: Cliente, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        ListItem(
            headlineContent = { Text(c.nome) },
            supportingContent = {
                val info = listOf(c.telefono, c.indirizzo).filter { it.isNotBlank() }.joinToString("  ·  ")
                if (info.isNotEmpty()) Text(info, maxLines = 1, overflow = TextOverflow.Ellipsis)
            },
            leadingContent = { Icon(Icons.Filled.Person, contentDescription = null) },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}

@Composable
fun MacchinaRiga(mc: MacchinaConCliente, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        ListItem(
            headlineContent = { Text(mc.macchina.modello) },
            supportingContent = { Text("Matricola ${mc.macchina.matricola}  ·  ${mc.cliente.nome}") },
            leadingContent = { Icon(Icons.Filled.Build, contentDescription = null) },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AnteprimaInterventoCard() {
    AssistenzaTheme {
        InterventoCard(
            d = InterventoDettaglio(
                intervento = Intervento(
                    id = 1, macchinaId = 1,
                    descrizione = "Sostituzione cinghia di trasmissione e pulizia filtri."
                ),
                macchina = Macchina(id = 1, clienteId = 1, modello = "Lavatrice industriale LX-200", matricola = "SN-48213"),
                cliente = Cliente(id = 1, nome = "Bar Centrale Iglesias")
            ),
            onClick = {}
        )
    }
}
