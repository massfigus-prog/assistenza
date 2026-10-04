package com.massfigus.assistenza.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "macchine",
    foreignKeys = [
        ForeignKey(
            entity = Cliente::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("clienteId"), Index("matricola")]
)
data class Macchina(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clienteId: Long,
    val modello: String,
    val matricola: String
)
