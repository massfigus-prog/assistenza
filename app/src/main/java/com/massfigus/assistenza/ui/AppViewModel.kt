package com.massfigus.assistenza.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.massfigus.assistenza.data.AppDatabase
import com.massfigus.assistenza.data.Repository
import com.massfigus.assistenza.data.Tecnico
import com.massfigus.assistenza.data.TecnicoPrefs
import com.massfigus.assistenza.model.Cliente
import com.massfigus.assistenza.model.Intervento
import com.massfigus.assistenza.model.InterventoDettaglio
import com.massfigus.assistenza.model.Macchina
import com.massfigus.assistenza.model.MacchinaConCliente
import com.massfigus.assistenza.model.StatoIntervento
import com.massfigus.assistenza.report.FirmaStorage
import com.massfigus.assistenza.report.PdfGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = Repository(AppDatabase.get(application))
    private val prefs = TecnicoPrefs(application)

    // ---------- Ricerca globale ----------
    private val _ricerca = MutableStateFlow("")
    val ricerca: StateFlow<String> = _ricerca.asStateFlow()
    fun aggiornaRicerca(testo: String) { _ricerca.value = testo }

    val clientiTrovati: StateFlow<List<Cliente>> = _ricerca
        .flatMapLatest { t -> if (t.isBlank()) flowOf(emptyList()) else repo.cercaClienti(t.trim()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val macchineTrovate: StateFlow<List<MacchinaConCliente>> = _ricerca
        .flatMapLatest { t -> if (t.isBlank()) flowOf(emptyList()) else repo.cercaMacchine(t.trim()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val interventiAperti: StateFlow<List<InterventoDettaglio>> = repo.interventiAperti()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val tutteLeMacchine: StateFlow<List<MacchinaConCliente>> = repo.tutteLeMacchine()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // ---------- Clienti ----------
    fun cliente(id: Long): Flow<Cliente?> = repo.cliente(id)
    suspend fun clienteOnce(id: Long): Cliente? = repo.clienteOnce(id)

    fun salvaCliente(c: Cliente, onFatto: (Long) -> Unit) {
        viewModelScope.launch {
            val id = if (c.id == 0L) repo.inserisciCliente(c) else { repo.aggiornaCliente(c); c.id }
            onFatto(id)
        }
    }

    fun eliminaCliente(c: Cliente, onFatto: () -> Unit) {
        viewModelScope.launch { repo.eliminaCliente(c); onFatto() }
    }

    // ---------- Macchine ----------
    fun macchineDi(clienteId: Long): Flow<List<Macchina>> = repo.macchineDi(clienteId)

    fun aggiungiMacchina(clienteId: Long, modello: String, matricola: String) {
        viewModelScope.launch {
            repo.inserisciMacchina(
                Macchina(clienteId = clienteId, modello = modello.trim(), matricola = matricola.trim())
            )
        }
    }

    fun eliminaMacchina(m: Macchina) {
        viewModelScope.launch { repo.eliminaMacchina(m) }
    }

    // ---------- Interventi ----------
    fun intervento(id: Long): Flow<InterventoDettaglio?> = repo.intervento(id)
    fun interventiDi(clienteId: Long): Flow<List<InterventoDettaglio>> = repo.interventiDi(clienteId)

    fun nuovoIntervento(macchinaId: Long, onCreato: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repo.inserisciIntervento(Intervento(macchinaId = macchinaId))
            onCreato(id)
        }
    }

    fun salvaIntervento(i: Intervento, onFatto: () -> Unit = {}) {
        viewModelScope.launch { repo.aggiornaIntervento(i); onFatto() }
    }

    fun eliminaIntervento(i: Intervento, onFatto: () -> Unit) {
        viewModelScope.launch {
            i.firmaPath?.let { File(it).delete() }
            repo.eliminaIntervento(i)
            onFatto()
        }
    }

    /** Salva l'immagine della firma e imposta lo stato su "Chiuso". */
    fun chiudiConFirma(id: Long, firma: Bitmap, onFatto: () -> Unit) {
        viewModelScope.launch {
            val dettaglio = repo.interventoOnce(id) ?: return@launch
            val percorso = withContext(Dispatchers.IO) {
                FirmaStorage.salva(getApplication<Application>(), id, firma)
            }
            repo.aggiornaIntervento(
                dettaglio.intervento.copy(stato = StatoIntervento.CHIUSO, firmaPath = percorso)
            )
            onFatto()
        }
    }

    /** Salva eventuali modifiche e crea il PDF. Restituisce null in caso di errore. */
    fun generaReport(
        id: Long,
        modifiche: Intervento?,
        onPronto: (File?, InterventoDettaglio?) -> Unit
    ) {
        viewModelScope.launch {
            if (modifiche != null) repo.aggiornaIntervento(modifiche)
            val dettaglio = repo.interventoOnce(id)
            val file = if (dettaglio == null) null else withContext(Dispatchers.IO) {
                try {
                    PdfGenerator.genera(getApplication<Application>(), dettaglio, prefs.leggi())
                } catch (e: Exception) {
                    null
                }
            }
            onPronto(file, dettaglio)
        }
    }

    // ---------- Dati tecnico ----------
    fun tecnico(): Tecnico = prefs.leggi()
    fun salvaTecnico(t: Tecnico) = prefs.salva(t)
}
