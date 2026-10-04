package com.massfigus.assistenza.report

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.massfigus.assistenza.model.InterventoDettaglio
import java.io.File

object ShareUtils {

    private fun uri(context: Context, file: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

    /** Apre il PDF con un visualizzatore installato sul telefono. */
    fun apriPdf(context: Context, file: File) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri(context, file), "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                context,
                "Report creato. Nessuna app per aprire i PDF: usa \"Invia report\".",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /** Apre il menu di condivisione (Gmail, WhatsApp, ecc.) con il PDF allegato. */
    fun condividiPdf(context: Context, file: File, d: InterventoDettaglio) {
        val pdfUri = uri(context, file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, pdfUri)
            putExtra(Intent.EXTRA_SUBJECT, "Rapporto di intervento n. ${d.intervento.id}")
            putExtra(
                Intent.EXTRA_TEXT,
                "Gentile ${d.cliente.nome},\nin allegato il rapporto dell'intervento sulla macchina " +
                    "${d.macchina.modello} (matricola ${d.macchina.matricola})."
            )
            if (d.cliente.email.isNotBlank()) {
                putExtra(Intent.EXTRA_EMAIL, arrayOf(d.cliente.email))
            }
            clipData = ClipData.newRawUri("report", pdfUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Invia report"))
    }
}
