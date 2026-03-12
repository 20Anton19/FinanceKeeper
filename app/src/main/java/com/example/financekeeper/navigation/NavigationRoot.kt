package com.example.financekeeper.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.financekeeper.auth.presentation.LoginScreen
import com.example.financekeeper.auth.presentation.RegisterScreen
import kotlinx.serialization.Serializable

@Serializable
data object RegisterScreenNav: NavKey

@Serializable
data object LoginScreenNav: NavKey

@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(RegisterScreenNav)
    NavDisplay(
        backStack = backStack,
        entryProvider = { key ->
            when(key) {
                is RegisterScreenNav -> {
                    NavEntry(key) {
                        RegisterScreen(
                            onNavigateToLogin = {
                                backStack.add(LoginScreenNav)
                            },
                            onRegisterSuccess = {
                                backStack.add(LoginScreenNav) // ВРЕМЕННО
                            }
                        )
                    }
                }
                is LoginScreenNav -> {
                    NavEntry(key){
                        LoginScreen(
                            onNavigateToRegister = {
                                backStack.add(RegisterScreenNav)
                            },
                            onLoginSuccess = {
                                backStack.add(RegisterScreenNav) // ВРЕМЕННО
                            }
                        )
                    }
                }
                else -> throw RuntimeException("Invalid NavKey.")
            }
        },
    )
}