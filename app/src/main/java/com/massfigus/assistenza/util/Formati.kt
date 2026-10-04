package com.massfigus.assistenza.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Formati {
    private val locale = Locale.ITALY

    fun data(millis: Long): String =
        SimpleDateFormat("dd/MM/yyyy HH:mm", locale).format(Date(millis))

    fun euro(valore: Double): String =
        NumberFormat.getCurrencyInstance(locale).format(valore)

    fun numero(valore: Double): String = String.format(locale, "%.2f", valore)

    fun leggiCosto(testo: String): Double =
        testo.trim().replace(',', '.').toDoubleOrNull() ?: 0.0
}
