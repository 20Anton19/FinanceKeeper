package com.example.financekeeper.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.financekeeper.analytics.presentation.AnalyticsScreen
import com.example.financekeeper.auth.presentation.LoginScreen
import com.example.financekeeper.auth.presentation.RegisterScreen
import com.example.financekeeper.category.presentation.CategoryFormScreen
import com.example.financekeeper.category.presentation.CategoryListScreen
import com.example.financekeeper.transaction.presentation.TransactionDetailScreen
import com.example.financekeeper.transaction.presentation.TransactionFormScreen
import com.example.financekeeper.transaction.presentation.TransactionListScreen
import com.example.financekeeper.user.presentation.ProfileScreen
import com.example.financekeeper.user.presentation.ChangePasswordScreen
import kotlinx.serialization.Serializable

@Serializable data object RegisterScreenNav : NavKey
@Serializable data object LoginScreenNav : NavKey
@Serializable data object TransactionListScreenNav : NavKey
@Serializable data object CategoryListScreenNav : NavKey
@Serializable data object ProfileScreenNav : NavKey
@Serializable data object ChangePasswordScreenNav : NavKey
@Serializable data class TransactionDetailScreenNav(val id: Long) : NavKey
@Serializable data class TransactionFormScreenNav(val id: Long? = null) : NavKey
@Serializable data class CategoryFormScreenNav(val id: Long? = null) : NavKey

@Serializable data object AnalyticsScreenNav : NavKey

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationRoot(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(RegisterScreenNav)

    NavDisplay(
        backStack = backStack,
        entryProvider = { key ->
            when (key) {
                is RegisterScreenNav -> NavEntry(key) {
                    RegisterScreen(
                        onNavigateToLogin = { backStack.add(LoginScreenNav) },
                        onRegisterSuccess = { backStack.add(TransactionListScreenNav) }
                    )
                }
                is LoginScreenNav -> NavEntry(key) {
                    LoginScreen(
                        onNavigateToRegister = { backStack.add(RegisterScreenNav) },
                        onLoginSuccess = { backStack.add(TransactionListScreenNav) }
                    )
                }
                is TransactionListScreenNav -> NavEntry(key) {
                    TransactionListScreen(
                        onNavigateToDetail = { id -> backStack.add(TransactionDetailScreenNav(id)) },
                        onNavigateToCreate = { backStack.add(TransactionFormScreenNav()) },
                        onNavigateToCategories = { backStack.add(CategoryListScreenNav) },
                        onNavigateToAnalytics = { backStack.add(AnalyticsScreenNav) },
                        onNavigateToProfile = { backStack.add(ProfileScreenNav) }
                    )
                }
                is TransactionDetailScreenNav -> NavEntry(key) {
                    TransactionDetailScreen(
                        id = key.id,
                        onBack = { backStack.removeLastOrNull() },
                        onNavigateToEdit = { id -> backStack.add(TransactionFormScreenNav(id)) }
                    )
                }
                is TransactionFormScreenNav -> NavEntry(key) {
                    TransactionFormScreen(
                        id = key.id,
                        onBack = { backStack.removeLastOrNull() },
                        onSaved = { backStack.removeLastOrNull() }
                    )
                }
                is CategoryListScreenNav -> NavEntry(key) {
                    CategoryListScreen(
                        onNavigateToCreate = { backStack.add(CategoryFormScreenNav()) },
                        onNavigateToEdit = { id -> backStack.add(CategoryFormScreenNav(id)) },
                        onNavigateToTransactions = { backStack.add(TransactionListScreenNav) },
                        onNavigateToAnalytics = { backStack.add(AnalyticsScreenNav) },
                        onNavigateToProfile = { backStack.add(ProfileScreenNav) }
                    )
                }
                is CategoryFormScreenNav -> NavEntry(key) {
                    CategoryFormScreen(
                        id = key.id,
                        onBack = { backStack.removeLastOrNull() },
                        onSaved = { backStack.removeLastOrNull() }
                    )
                }
                is ProfileScreenNav -> NavEntry(key) {
                    ProfileScreen(
                        onNavigateToChangePassword = { backStack.add(ChangePasswordScreenNav) },
                        onLogout = {
                            backStack.clear()
                            backStack.add(LoginScreenNav)
                        },
                        onNavigateToTransactions = { backStack.add(TransactionListScreenNav) },
                        onNavigateToAnalytics = { backStack.add(AnalyticsScreenNav) },
                        onNavigateToCategories = { backStack.add(CategoryListScreenNav) }
                    )
                }
                is ChangePasswordScreenNav -> NavEntry(key) {
                    ChangePasswordScreen(onBack = { backStack.removeLastOrNull() })
                }
                is AnalyticsScreenNav -> NavEntry(key) {
                    AnalyticsScreen(
                        onNavigateToTransactions = { backStack.add(TransactionListScreenNav) },
                        onNavigateToCategories = { backStack.add(CategoryListScreenNav) },
                        onNavigateToAnalytics = { backStack.add(AnalyticsScreenNav) },
                        onNavigateToProfile = { backStack.add(ProfileScreenNav) }
                    )
                }
                else -> throw RuntimeException("Invalid NavKey.")
            }
        }
    )
}