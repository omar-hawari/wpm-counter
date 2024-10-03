package com.omarhawari.wpm_counter.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.omarhawari.wpm_counter.screens.username.UsernameScreen
import com.omarhawari.wpm_counter.screens.username.UsernameViewModel
import com.omarhawari.wpm_counter.screens.wpm_counter.ARG_SESSION_ID
import com.omarhawari.wpm_counter.screens.wpm_counter.WPMCounterScreen
import com.omarhawari.wpm_counter.screens.wpm_counter.WPMCounterViewModel
import com.omarhawari.wpm_counter.ui.theme.WPMCounterTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WPMCounterTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val navController = rememberNavController()

                    WPMNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun WPMNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    usernameViewModel: UsernameViewModel? = null,
    wpmCounterViewModel: WPMCounterViewModel? = null
) {
    NavHost(
        navController = navController,
        startDestination = "username",
        modifier = modifier
    ) {
        composable(ROUTE_USERNAME) {
            if (usernameViewModel != null)
                UsernameScreen(navController = navController, viewModel = usernameViewModel)
            else
                UsernameScreen(navController = navController)
        }
        composable("${ROUTE_WPM_COUNTER}/{${ARG_SESSION_ID}}") { navBackStackEntry ->
            val sessionId = navBackStackEntry.arguments?.getString(ARG_SESSION_ID)

            sessionId?.let {
                if (wpmCounterViewModel != null)
                    WPMCounterScreen(sessionId = sessionId, viewModel = wpmCounterViewModel)
                else
                    WPMCounterScreen(sessionId = sessionId)
            }
        }
    }
}

const val ROUTE_USERNAME = "username"
const val ROUTE_WPM_COUNTER = "wpm_counter"