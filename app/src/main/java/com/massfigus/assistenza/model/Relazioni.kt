package com.massfigus.assistenza.model

import androidx.room.Embedded

/** Una macchina insieme al cliente a cui appartiene. */
data class MacchinaConCliente(
    @Embedded val macchina: Macchina,
    @Embedded(prefix = "c_") val cliente: Cliente
)

/** Un intervento con tutti i dati di macchina e cliente collegati. */
data class InterventoDettaglio(
    @Embedded val intervento: Intervento,
    @Embedded(prefix = "m_") val macchina: Macchina,
    @Embedded(prefix = "c_") val cliente: Cliente
)
