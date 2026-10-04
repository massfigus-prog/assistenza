package com.massfigus.assistenza

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.massfigus.assistenza.ui.AppNavigation
import com.massfigus.assistenza.ui.AppViewModel
import com.massfigus.assistenza.ui.theme.AssistenzaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AssistenzaTheme {
                val vm: AppViewModel = viewModel()
                AppNavigation(vm)
            }
        }
    }
}
