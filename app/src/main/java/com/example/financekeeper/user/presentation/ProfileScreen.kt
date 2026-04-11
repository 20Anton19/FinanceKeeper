package com.example.financekeeper.user.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financekeeper.navigation.BottomNavBar
import com.example.financekeeper.navigation.BottomNavItem
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToChangePassword: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.profileState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Профиль") }) },
        bottomBar = {
            BottomNavBar(
                current = BottomNavItem.Profile,
                onTransactions = onNavigateToTransactions,
                onCategories = onNavigateToCategories,
                onAnalytics = onNavigateToAnalytics,
                onProfile = {}
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is ProfileState.Loading ->
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is ProfileState.Error ->
                    Text(s.message, color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center))
                is ProfileState.Success -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(16.dp))
                        Text(s.data.login, style = MaterialTheme.typography.headlineSmall)
                        Text("В системе с ${s.data.createdAt}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Spacer(Modifier.height(24.dp))
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                    Text("Операций")
                                    Text("${s.data.transactionCount}")
                                }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                    Text("Категорий")
                                    Text("${s.data.categoryCount}")
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        OutlinedButton(
                            onClick = onNavigateToChangePassword,
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Сменить пароль") }
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = onLogout,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) { Text("Выйти из аккаунта") }
                    }
                }
            }
        }
    }
}