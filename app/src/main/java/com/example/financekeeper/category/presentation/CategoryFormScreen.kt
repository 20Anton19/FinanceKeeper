package com.example.financekeeper.category.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

private val PRESET_COLORS = listOf(
    0xFF185FA5.toInt(), 0xFF3B6D11.toInt(), 0xFFA32D2D.toInt(),
    0xFF854F0B.toInt(), 0xFF534AB7.toInt(), 0xFF993556.toInt(),
    0xFF0F6E56.toInt(), 0xFF888780.toInt()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFormScreen(
    id: Long?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: CategoryViewModel = koinViewModel()
) {
    val listState by viewModel.listState.collectAsState()
    val formState by viewModel.formState.collectAsState()

    val existing = remember(listState) {
        if (id == null) null
        else (listState as? CategoryListState.Success)?.items?.find { it.id == id }
    }

    var name by remember(existing) { mutableStateOf(existing?.name ?: "") }
    var selectedColor by remember(existing) {
        mutableStateOf(existing?.color ?: PRESET_COLORS.first())
    }

    LaunchedEffect(formState) {
        if (formState is CategoryFormState.Saved) {
            viewModel.resetFormState()
            viewModel.loadAll()
            onSaved()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (id == null) "Новая категория" else "Редактировать") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Название") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))
            Text("Цвет", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PRESET_COLORS.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(color))
                            .then(
                                if (selectedColor == color)
                                    Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                else Modifier
                            )
                            .clickable { selectedColor = color }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Предпросмотр", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = Color(selectedColor),
                    modifier = Modifier.size(16.dp)) {}
                Spacer(Modifier.width(8.dp))
                Text(name.ifBlank { "Название категории" },
                    color = if (name.isBlank()) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    else MaterialTheme.colorScheme.onSurface)
            }

            if (formState is CategoryFormState.Error) {
                Spacer(Modifier.height(8.dp))
                Text((formState as CategoryFormState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { viewModel.save(id, name, selectedColor) },
                modifier = Modifier.fillMaxWidth(),
                enabled = formState !is CategoryFormState.Loading
            ) {
                if (formState is CategoryFormState.Loading)
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                else Text(if (id == null) "Создать" else "Сохранить")
            }

            if (id != null) {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { viewModel.delete(id) { onBack() } },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("Удалить категорию") }
            }
        }
    }
}