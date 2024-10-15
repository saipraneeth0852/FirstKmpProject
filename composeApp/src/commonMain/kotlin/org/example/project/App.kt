package org.example.project

import androidx.compose.runtime.Composable
import org.example.project.data.BreachApiImpl
import org.example.project.data.BreachRepository
import org.example.project.domain.GetBreachesUseCase
import org.example.project.presentation.BreachListScreen
import org.example.project.presentation.BreachListViewModel

@Composable
fun App() {

    val api = BreachApiImpl()
    val repository = BreachRepository(api)
    val getBreachesUseCase = GetBreachesUseCase(repository)
    val viewModel = BreachListViewModel(getBreachesUseCase)

    BreachListScreen(viewModel)
}