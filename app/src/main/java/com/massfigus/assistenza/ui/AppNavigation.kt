package com.massfigus.assistenza.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.massfigus.assistenza.ui.screens.ClienteFormScreen
import com.massfigus.assistenza.ui.screens.DettaglioClienteScreen
import com.massfigus.assistenza.ui.screens.DettaglioInterventoScreen
import com.massfigus.assistenza.ui.screens.FirmaScreen
import com.massfigus.assistenza.ui.screens.HomeScreen
import com.massfigus.assistenza.ui.screens.ImpostazioniScreen
import com.massfigus.assistenza.ui.screens.SelezionaMacchinaScreen

private val argId = listOf(navArgument("id") { type = NavType.LongType })

@Composable
fun AppNavigation(vm: AppViewModel) {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = "home") {

        composable("home") {
            HomeScreen(
                vm = vm,
                onApriCliente = { nav.navigate("cliente/$it") },
                onApriIntervento = { nav.navigate("intervento/$it") },
                onNuovoCliente = { nav.navigate("cliente_form/0") },
                onNuovoIntervento = { nav.navigate("seleziona_macchina") },
                onImpostazioni = { nav.navigate("impostazioni") }
            )
        }

        composable("cliente_form/{id}", arguments = argId) { entry ->
            val id = entry.arguments?.getLong("id") ?: 0L
            ClienteFormScreen(
                vm = vm,
                clienteId = id,
                onBack = { nav.popBackStack() },
                onSalvato = { nuovoId ->
                    if (id == 0L) {
                        nav.navigate("cliente/$nuovoId") {
                            popUpTo("cliente_form/{id}") { inclusive = true }
                        }
                    } else {
                        nav.popBackStack()
                    }
                }
            )
        }

        composable("cliente/{id}", arguments = argId) { entry ->
            val id = entry.arguments?.getLong("id") ?: 0L
            DettaglioClienteScreen(
                vm = vm,
                clienteId = id,
                onBack = { nav.popBackStack() },
                onModifica = { nav.navigate("cliente_form/$id") },
                onNuovoIntervento = { macchinaId ->
                    vm.nuovoIntervento(macchinaId) { nav.navigate("intervento/$it") }
                },
                onApriIntervento = { nav.navigate("intervento/$it") }
            )
        }

        composable("seleziona_macchina") {
            SelezionaMacchinaScreen(
                vm = vm,
                onBack = { nav.popBackStack() },
                onSelezionata = { macchinaId ->
                    vm.nuovoIntervento(macchinaId) { interventoId ->
                        nav.navigate("intervento/$interventoId") {
                            popUpTo("seleziona_macchina") { inclusive = true }
                        }
                    }
                },
                onNuovoCliente = { nav.navigate("cliente_form/0") }
            )
        }

        composable("intervento/{id}", arguments = argId) { entry ->
            val id = entry.arguments?.getLong("id") ?: 0L
            DettaglioInterventoScreen(
                vm = vm,
                interventoId = id,
                onBack = { nav.popBackStack() },
                onFirma = { nav.navigate("firma/$id") }
            )
        }

        composable("firma/{id}", arguments = argId) { entry ->
            val id = entry.arguments?.getLong("id") ?: 0L
            FirmaScreen(
                vm = vm,
                interventoId = id,
                onBack = { nav.popBackStack() },
                onFirmato = { nav.popBackStack() }
            )
        }

        composable("impostazioni") {
            ImpostazioniScreen(vm = vm, onBack = { nav.popBackStack() })
        }
    }
}
