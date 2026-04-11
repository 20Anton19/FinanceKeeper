package com.example.financekeeper.user.presentation


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.passwordState.collectAsState()
    var old by remember { mutableStateOf("") }
    var new by remember { mutableStateOf("") }

    LaunchedEffect(state) {
        if (state is PasswordState.Saved) {
            viewModel.resetPasswordState()
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Сменить пароль") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
        ) {
            OutlinedTextField(
                value = old, onValueChange = { old = it },
                label = { Text("Текущий пароль") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = new, onValueChange = { new = it },
                label = { Text("Новый пароль") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            if (state is PasswordState.Error) {
                Spacer(Modifier.height(8.dp))
                Text((state as PasswordState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.weight(1f))
            Button(
                onClick = { viewModel.changePassword(old, new) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state !is PasswordState.Loading
            ) {
                if (state is PasswordState.Loading)
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                else Text("Сохранить")
            }
        }
    }
}