package com.massfigus.assistenza.report

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import androidx.compose.ui.geometry.Offset
import java.io.File
import java.io.FileOutputStream

object FirmaStorage {

    /** Disegna i tratti della firma su una bitmap bianca. */
    fun creaBitmap(larghezza: Int, altezza: Int, tratti: List<List<Offset>>, spessore: Float): Bitmap {
        val bitmap = Bitmap.createBitmap(larghezza, altezza, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = spessore
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }
        val punto = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.BLACK }
        tratti.forEach { tratto ->
            if (tratto.size == 1) {
                canvas.drawCircle(tratto[0].x, tratto[0].y, spessore / 2, punto)
            } else if (tratto.size > 1) {
                val path = Path()
                path.moveTo(tratto[0].x, tratto[0].y)
                for (i in 1 until tratto.size) path.lineTo(tratto[i].x, tratto[i].y)
                canvas.drawPath(path, paint)
            }
        }
        return bitmap
    }

    /** Salva la firma come PNG nella memoria interna dell'app e restituisce il percorso. */
    fun salva(context: Context, interventoId: Long, bitmap: Bitmap): String {
        val cartella = File(context.filesDir, "firme").apply { mkdirs() }
        val file = File(cartella, "firma_intervento_$interventoId.png")
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        return file.absolutePath
    }
}
