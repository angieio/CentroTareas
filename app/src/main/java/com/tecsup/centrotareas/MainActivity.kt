package com.tecsup.centrotareas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tecsup.centrotareas.data.TareasRepositoryLocal
import com.tecsup.centrotareas.ui.PantallaTareas
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.centrotareas.ui.theme.CentroTareasTheme
import com.tecsup.centrotareas.viewmodel.TareasViewModel
import com.tecsup.centrotareas.viewmodel.TareasViewModelFactory

class MainActivity : ComponentActivity() {

    private val factory by lazy {
        TareasViewModelFactory(TareasRepositoryLocal())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CentroTareasTheme {
                val tareasViewModel: TareasViewModel = viewModel(
                    factory = factory
                )

                PantallaTareas(viewModel = tareasViewModel)
            }
        }
    }
}