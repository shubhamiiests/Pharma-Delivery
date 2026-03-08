package com.pharmadelivery.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.pharmadelivery.data.models.UserRole
import com.pharmadelivery.ui.theme.PharmaDeliveryTheme
import com.pharmadelivery.utils.NetworkState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PharmaDeliveryTheme {
                val mainVm: MainViewModel = hiltViewModel()
                val appState by mainVm.appState.collectAsState()
                val networkState by mainVm.networkState.collectAsState()

                // Keep splash screen showing while we check session
                splashScreen.setKeepOnScreenCondition {
                    appState is AppState.Loading
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    when (appState) {
                        AppState.Loading -> { /* splash is covering it */ }

                        AppState.Unauthenticated -> {
                            PharmaNavHost(
                                startDestination = Route.Onboarding.path,
                                isOffline = networkState is NetworkState.Unavailable,
                                onLogin = { role -> mainVm.loginAs(role) },
                                onLogout = { mainVm.logout() }
                            )
                        }

                        is AppState.Authenticated -> {
                            val user = (appState as AppState.Authenticated).user
                            PharmaNavHost(
                                startDestination = when (user.role) {
                                    UserRole.CUSTOMER -> Route.CustomerHome.path
                                    UserRole.PHARMACY -> Route.PharmacyDashboard.path
                                    UserRole.RIDER -> Route.RiderDashboard.path
                                    UserRole.ADMIN -> Route.AdminDashboard.path
                                },
                                isOffline = networkState is NetworkState.Unavailable,
                                currentUser = user,
                                onLogin = { role -> mainVm.loginAs(role) },
                                onLogout = { mainVm.logout() }
                            )
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}
