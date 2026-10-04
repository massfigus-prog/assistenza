package com.massfigus.assistenza.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class StatoIntervento { APERTO, CHIUSO }

@Entity(
    tableName = "interventi",
    foreignKeys = [
        ForeignKey(
            entity = Macchina::class,
            parentColumns = ["id"],
            childColumns = ["macchinaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("macchinaId")]
)
data class Intervento(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val macchinaId: Long,
    val data: Long = System.currentTimeMillis(),
    val descrizione: String = "",
    val costo: Double = 0.0,
    val stato: StatoIntervento = StatoIntervento.APERTO,
    /** Percorso del file PNG con la firma del cliente (null se non firmato). */
    val firmaPath: String? = null
)
