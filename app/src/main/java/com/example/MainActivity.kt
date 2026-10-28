package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.data.KeyboardPreferences
import com.example.data.model.KeyboardTheme
import com.example.ui.MainScreen
import com.example.ui.NavTab
import com.example.ui.theme.CrystalKeyboardTheme
import com.example.util.MessageScheduler

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val preferences = KeyboardPreferences.getInstance(this)

        val targetTemplateId = intent?.getStringExtra(MessageScheduler.EXTRA_TEMPLATE_ID)
        val isPlannerAction = intent?.action == "OPEN_PLANNER" || intent?.action == MessageScheduler.ACTION_OPEN_MESSAGE_READY

        val initialTab = if (isPlannerAction) NavTab.PLANNER else NavTab.HOME

        setContent {
            val settings by preferences.settings.collectAsState()
            val theme = KeyboardTheme.getById(settings.themeId)
            val isDark = theme.isDark

            CrystalKeyboardTheme(darkTheme = isDark) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        preferences = preferences,
                        initialTab = initialTab,
                        initialMessageReadyTemplateId = targetTemplateId
                    )
                }
            }
        }
    }
}
