package com.massfigus.assistenza.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clienti")
data class Cliente(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val telefono: String = "",
    val email: String = "",
    val indirizzo: String = ""
)
