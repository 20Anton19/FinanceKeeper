package com.example.financekeeper.transaction.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFormScreen(
    id: Long?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: TransactionFormViewModel = koinViewModel()
) {
    val formState by viewModel.formState.collectAsState()
    val transaction by viewModel.transaction.collectAsState()
    val categories by viewModel.categories.collectAsState()

    LaunchedEffect(id) {
        if (id != null) viewModel.loadTransaction(id)
    }

    LaunchedEffect(formState) {
        if (formState is TransactionFormState.Saved) {
            viewModel.resetState()
            onSaved()
        }
    }

    var type by remember(transaction) { mutableStateOf(transaction?.type ?: false) }
    var amount by remember(transaction) { mutableStateOf(transaction?.amount?.toString() ?: "") }
    var date by remember(transaction) { mutableStateOf(transaction?.date ?: LocalDate.now()) }
    var selectedCategoryId by remember(transaction) { mutableStateOf(transaction?.categoryId) }
    var categoryExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (id == null) "Новая операция" else "Редактировать") },
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
            // Тип
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = !type,
                    onClick = { type = false },
                    label = { Text("Расход") }
                )
                FilterChip(
                    selected = type,
                    onClick = { type = true },
                    label = { Text("Доход") }
                )
            }

            Spacer(Modifier.height(12.dp))

            // Сумма
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Сумма") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            // Категория
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it }
            ) {
                OutlinedTextField(
                    value = categories.find { it.id == selectedCategoryId }?.name ?: "Выберите категорию",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Категория") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.name) },
                            onClick = {
                                selectedCategoryId = cat.id
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Дата
            OutlinedTextField(
                value = date.toString(),
                onValueChange = { runCatching { date = LocalDate.parse(it) } },
                label = { Text("Дата (гггг-мм-дд)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (formState is TransactionFormState.Error) {
                Spacer(Modifier.height(8.dp))
                Text(
                    (formState as TransactionFormState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull()
                    val catId = selectedCategoryId
                    if (amt == null || catId == null) {
                        return@Button
                    }
                    viewModel.save(id, amt, catId, date as LocalDate, type)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = formState !is TransactionFormState.Loading
            ) {
                if (formState is TransactionFormState.Loading)
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                else Text(if (id == null) "Сохранить" else "Сохранить изменения")
            }
        }
    }
}