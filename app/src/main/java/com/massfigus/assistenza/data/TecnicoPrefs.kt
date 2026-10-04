package com.massfigus.assistenza.data

import android.content.Context

/** Dati del tecnico che compaiono nell'intestazione del report PDF. */
data class Tecnico(
    val nome: String = "",
    val azienda: String = "",
    val telefono: String = "",
    val email: String = ""
)

class TecnicoPrefs(context: Context) {
    private val sp = context.getSharedPreferences("tecnico", Context.MODE_PRIVATE)

    fun leggi() = Tecnico(
        nome = sp.getString("nome", "") ?: "",
        azienda = sp.getString("azienda", "") ?: "",
        telefono = sp.getString("telefono", "") ?: "",
        email = sp.getString("email", "") ?: ""
    )

    fun salva(t: Tecnico) {
        sp.edit()
            .putString("nome", t.nome)
            .putString("azienda", t.azienda)
            .putString("telefono", t.telefono)
            .putString("email", t.email)
            .apply()
    }
}
