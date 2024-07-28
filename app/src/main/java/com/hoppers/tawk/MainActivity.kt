package com.hoppers.tawk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.hoppers.tawk.components.ComposeLocalWrapper
import com.hoppers.tawk.home.screens.HomeScreen
import com.hoppers.tawk.theme.TawkTheme
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI

class MainActivity : ComponentActivity() {
    @OptIn(KoinExperimentalAPI::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            TawkTheme {
                ComposeLocalWrapper {
                    KoinAndroidContext {
                        Navigator(HomeScreen()) { navigator ->
                            SlideTransition(navigator =navigator)
                        }
                    }
                }
            }
        }

    }
}