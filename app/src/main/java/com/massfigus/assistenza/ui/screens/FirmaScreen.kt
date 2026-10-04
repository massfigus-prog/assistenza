package com.massfigus.assistenza.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.massfigus.assistenza.report.FirmaStorage
import com.massfigus.assistenza.ui.AppViewModel
import com.massfigus.assistenza.ui.components.BarraSuperiore

@Composable
fun FirmaScreen(
    vm: AppViewModel,
    interventoId: Long,
    onBack: () -> Unit,
    onFirmato: () -> Unit
) {
    val context = LocalContext.current
    val dettaglio by remember(interventoId) { vm.intervento(interventoId) }.collectAsState(initial = null)

    // Ogni tratto è una lista di punti osservabile: la Canvas si ridisegna mentre si scrive
    val tratti = remember { mutableStateListOf<SnapshotStateList<Offset>>() }
    var dimensione by remember { mutableStateOf(IntSize.Zero) }
    var salvataggio by remember { mutableStateOf(false) }
    val spessore = with(LocalDensity.current) { 3.dp.toPx() }
    val forma = RoundedCornerShape(12.dp)

    Scaffold(topBar = { BarraSuperiore("Firma del cliente", onBack) }) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                dettaglio?.cliente?.nome ?: "",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                "Fai firmare il cliente nel riquadro. Confermando, l'intervento viene chiuso.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(forma)
                    .background(Color.White)
                    .border(2.dp, MaterialTheme.colorScheme.outline, forma)
            ) {
                Canvas(
                    Modifier
                        .fillMaxSize()
                        .onSizeChanged { dimensione = it }
                        .pointerInput(Unit) {
                            detectTapGestures { punto -> tratti.add(mutableStateListOf(punto)) }
                        }
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { punto -> tratti.add(mutableStateListOf(punto)) },
                                onDrag = { change, _ ->
                                    change.consume()
                                    tratti.lastOrNull()?.add(change.position)
                                }
                            )
                        }
                ) {
                    // Linea guida su cui firmare
                    val yLinea = size.height * 0.75f
                    drawLine(
                        Color(0xFFD0D5DA), Offset(24f, yLinea), Offset(size.width - 24f, yLinea),
                        strokeWidth = 2f
                    )
                    tratti.forEach { tratto ->
                        if (tratto.size == 1) {
                            drawCircle(Color.Black, spessore / 2, tratto[0])
                        } else if (tratto.size > 1) {
                            val path = Path().apply {
                                moveTo(tratto[0].x, tratto[0].y)
                                for (i in 1 until tratto.size) lineTo(tratto[i].x, tratto[i].y)
                            }
                            drawPath(
                                path, Color.Black,
                                style = Stroke(width = spessore, cap = StrokeCap.Round, join = StrokeJoin.Round)
                            )
                        }
                    }
                }
                if (tratti.isEmpty()) {
                    Text(
                        "Firma qui",
                        color = Color(0xFFB0B6BC),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { tratti.clear() },
                    enabled = tratti.isNotEmpty() && !salvataggio,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Ricomincia")
                }
                Button(
                    onClick = {
                        if (tratti.isEmpty() || dimensione == IntSize.Zero) {
                            Toast.makeText(context, "Manca la firma del cliente", Toast.LENGTH_SHORT).show()
                        } else {
                            salvataggio = true
                            val bitmap = FirmaStorage.creaBitmap(
                                dimensione.width, dimensione.height,
                                tratti.map { it.toList() }, spessore
                            )
                            vm.chiudiConFirma(interventoId, bitmap, onFirmato)
                        }
                    },
                    enabled = !salvataggio,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Chiudi intervento")
                }
            }
        }
    }
}
