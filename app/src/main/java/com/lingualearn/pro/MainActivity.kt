package com.lingualearn.pro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.lingualearn.pro.ui.LinguaLearnApp
import com.lingualearn.pro.ui.theme.LinguaLearnTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        AppCheckSetup.install()
        setContent {
            LinguaLearnTheme {
                LinguaLearnApp()
            }
        }
    }
}
