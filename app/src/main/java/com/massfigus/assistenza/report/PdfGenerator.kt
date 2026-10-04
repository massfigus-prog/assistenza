package com.massfigus.assistenza.report

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.StaticLayout
import android.text.TextPaint
import com.massfigus.assistenza.data.Tecnico
import com.massfigus.assistenza.model.InterventoDettaglio
import com.massfigus.assistenza.model.StatoIntervento
import com.massfigus.assistenza.util.Formati
import java.io.File
import java.io.FileOutputStream
import kotlin.math.min

private const val LARGHEZZA = 595 // A4 in punti
private const val ALTEZZA = 842
private const val MARGINE = 40f
private const val COLONNA_VALORI = 120f

private val BLU = Color.rgb(23, 57, 79)
private val AMBRA = Color.rgb(242, 169, 0)
private val GRIGIO = Color.rgb(110, 116, 122)
private val SFONDO = Color.rgb(238, 242, 245)

private fun riempi(colore: Int) = Paint().apply { color = colore; style = Paint.Style.FILL }

private fun testo(dimensione: Float, colore: Int, grassetto: Boolean = false) =
    TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = dimensione
        color = colore
        typeface = Typeface.create(Typeface.DEFAULT, if (grassetto) Typeface.BOLD else Typeface.NORMAL)
    }

/** Crea il report PDF di un intervento in formato A4. */
object PdfGenerator {

    fun genera(context: Context, d: InterventoDettaglio, tecnico: Tecnico): File {
        val documento = PdfDocument()
        val w = Scrittore(documento)

        // Intestazione
        w.canvas.drawRect(0f, 0f, LARGHEZZA.toFloat(), 92f, riempi(BLU))
        w.canvas.drawRect(0f, 92f, LARGHEZZA.toFloat(), 96f, riempi(AMBRA))
        w.canvas.drawText("Rapporto di intervento", MARGINE, 48f, testo(22f, Color.WHITE, true))
        w.canvas.drawText(
            "N. ${d.intervento.id} del ${Formati.data(d.intervento.data)}",
            MARGINE, 72f, testo(11f, Color.WHITE)
        )
        if (tecnico.azienda.isNotBlank()) {
            w.canvas.drawText(
                tecnico.azienda, LARGHEZZA - MARGINE, 48f,
                testo(13f, Color.WHITE, true).apply { textAlign = Paint.Align.RIGHT }
            )
        }
        w.y = 130f

        if (listOf(tecnico.nome, tecnico.telefono, tecnico.email).any { it.isNotBlank() }) {
            w.sezione("Tecnico")
            w.campo("Nome", tecnico.nome)
            w.campo("Telefono", tecnico.telefono)
            w.campo("Email", tecnico.email)
        }

        w.sezione("Cliente")
        w.campo("Nome", d.cliente.nome)
        w.campo("Indirizzo", d.cliente.indirizzo)
        w.campo("Telefono", d.cliente.telefono)
        w.campo("Email", d.cliente.email)

        w.sezione("Macchina")
        w.campo("Modello", d.macchina.modello)
        w.campo("Matricola", d.macchina.matricola)

        w.sezione("Intervento")
        w.campo("Data", Formati.data(d.intervento.data))
        w.campo("Stato", if (d.intervento.stato == StatoIntervento.CHIUSO) "Chiuso" else "Aperto")

        w.sezione("Lavoro svolto")
        w.paragrafo(d.intervento.descrizione.ifBlank { "Nessuna descrizione inserita." }, MARGINE, LARGHEZZA - 2 * MARGINE)

        // Riquadro costo
        w.spazio(60f)
        w.y += 10f
        w.canvas.drawRect(MARGINE, w.y, LARGHEZZA - MARGINE, w.y + 38f, riempi(SFONDO))
        w.canvas.drawRect(MARGINE, w.y, MARGINE + 4f, w.y + 38f, riempi(AMBRA))
        w.canvas.drawText("Costo intervento", MARGINE + 16f, w.y + 24f, testo(12f, BLU, true))
        w.canvas.drawText(
            Formati.euro(d.intervento.costo), LARGHEZZA - MARGINE - 14f, w.y + 25f,
            testo(15f, BLU, true).apply { textAlign = Paint.Align.RIGHT }
        )
        w.y += 70f

        // Firma
        w.spazio(150f)
        w.canvas.drawText("Firma del cliente", MARGINE, w.y, testo(10f, GRIGIO))
        if (tecnico.nome.isNotBlank()) {
            w.canvas.drawText(
                "Il tecnico", LARGHEZZA - MARGINE, w.y,
                testo(10f, GRIGIO).apply { textAlign = Paint.Align.RIGHT }
            )
        }
        w.y += 8f
        val firma = d.intervento.firmaPath
            ?.takeIf { File(it).exists() }
            ?.let { BitmapFactory.decodeFile(it) }
        if (firma != null) {
            val scala = min(220f / firma.width, 90f / firma.height)
            val dest = RectF(MARGINE, w.y, MARGINE + firma.width * scala, w.y + firma.height * scala)
            w.canvas.drawBitmap(firma, null, dest, null)
            w.y += 96f
        } else {
            w.canvas.drawText("Non firmato", MARGINE, w.y + 40f, testo(11f, GRIGIO))
            w.y += 60f
        }
        val linea = Paint().apply { color = GRIGIO; strokeWidth = 0.8f }
        w.canvas.drawLine(MARGINE, w.y, MARGINE + 220f, w.y, linea)
        w.canvas.drawText(d.cliente.nome, MARGINE, w.y + 14f, testo(10f, Color.BLACK))
        if (tecnico.nome.isNotBlank()) {
            w.canvas.drawLine(LARGHEZZA - MARGINE - 180f, w.y, LARGHEZZA - MARGINE, w.y, linea)
            w.canvas.drawText(
                tecnico.nome, LARGHEZZA - MARGINE, w.y + 14f,
                testo(10f, Color.BLACK).apply { textAlign = Paint.Align.RIGHT }
            )
        }

        w.chiudiPagina()

        val cartella = File(context.cacheDir, "reports").apply { mkdirs() }
        val file = File(cartella, "Report_Intervento_${d.intervento.id}.pdf")
        FileOutputStream(file).use { documento.writeTo(it) }
        documento.close()
        return file
    }

    /** Gestisce la posizione verticale e il passaggio a una nuova pagina. */
    private class Scrittore(private val documento: PdfDocument) {
        private var numeroPagina = 0
        private lateinit var pagina: PdfDocument.Page
        lateinit var canvas: Canvas
        var y = MARGINE

        private val pSezione = testo(12f, BLU, true)
        private val pEtichetta = testo(10f, GRIGIO)
        private val pTesto = testo(11f, Color.BLACK)
        private val pPiccolo = testo(8f, GRIGIO)
        private val pLinea = Paint().apply { color = AMBRA; strokeWidth = 1.2f }

        init { nuovaPagina() }

        private fun nuovaPagina() {
            numeroPagina++
            val info = PdfDocument.PageInfo.Builder(LARGHEZZA, ALTEZZA, numeroPagina).create()
            pagina = documento.startPage(info)
            canvas = pagina.canvas
            y = MARGINE + 10f
        }

        fun chiudiPagina() {
            canvas.drawText(
                "Documento generato il ${Formati.data(System.currentTimeMillis())}",
                MARGINE, ALTEZZA - 24f, pPiccolo
            )
            canvas.drawText(
                "Pagina $numeroPagina", LARGHEZZA - MARGINE, ALTEZZA - 24f,
                testo(8f, GRIGIO).apply { textAlign = Paint.Align.RIGHT }
            )
            documento.finishPage(pagina)
        }

        /** Se non c'è abbastanza spazio, passa alla pagina successiva. */
        fun spazio(altezza: Float) {
            if (y + altezza > ALTEZZA - 60f) {
                chiudiPagina()
                nuovaPagina()
            }
        }

        fun sezione(titolo: String) {
            spazio(44f)
            y += 8f
            canvas.drawText(titolo, MARGINE, y, pSezione)
            y += 6f
            canvas.drawLine(MARGINE, y, MARGINE + 36f, y, pLinea)
            y += 18f
        }

        fun campo(etichetta: String, valore: String) {
            if (valore.isBlank()) return
            spazio(16f)
            canvas.drawText(etichetta, MARGINE, y, pEtichetta)
            paragrafo(valore, MARGINE + COLONNA_VALORI, LARGHEZZA - 2 * MARGINE - COLONNA_VALORI)
            y += 3f
        }

        /** Scrive un testo andando a capo automaticamente, anche su più pagine. */
        fun paragrafo(testo: String, x: Float, larghezza: Float) {
            val layout = StaticLayout.Builder
                .obtain(testo, 0, testo.length, pTesto, larghezza.toInt())
                .build()
            val altezzaRiga = pTesto.fontSpacing
            for (i in 0 until layout.lineCount) {
                spazio(altezzaRiga)
                val riga = testo.substring(layout.getLineStart(i), layout.getLineEnd(i)).trimEnd()
                canvas.drawText(riga, x, y, pTesto)
                y += altezzaRiga
            }
        }
    }
}
