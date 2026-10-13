package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.ClassmateAiApp
import com.example.ui.theme.ClassmateAiTheme
import com.example.ui.viewmodel.StudentViewModel
import com.example.ui.viewmodel.StudentViewModelFactory

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val studentViewModel: StudentViewModel = viewModel(
        factory = StudentViewModelFactory(application)
      )
      val currentThemeMode by studentViewModel.themeMode.collectAsState()
      val currentThemePalette by studentViewModel.themePalette.collectAsState()

      ClassmateAiTheme(
        themeMode = currentThemeMode,
        themePalette = currentThemePalette
      ) {
        ClassmateAiApp(viewModel = studentViewModel)
      }
    }
  }
}
