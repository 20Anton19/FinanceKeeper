package com.example.financekeeper.transaction.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.financekeeper.category.presentation.CategoryListState
import com.example.financekeeper.category.presentation.CategoryViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    id: Long,
    onBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    viewModel: TransactionViewModel = koinViewModel(),
    categoryViewModel: CategoryViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val categoryState by categoryViewModel.listState.collectAsState()

    val item = remember(state) {
        (state as? TransactionListState.Success)?.items?.find { it.id == id }
    }

    val detailVm: TransactionFormViewModel = koinViewModel()
    val detail by detailVm.transaction.collectAsState()

    LaunchedEffect(id) {
        if (item == null) detailVm.loadTransaction(id)
    }

    val tx = item ?: detail

    val category = remember(tx, categoryState) {
        val cats = (categoryState as? CategoryListState.Success)?.items
        cats?.find { it.id == tx?.categoryId }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Операция") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    if (tx != null) {
                        TextButton(onClick = { onNavigateToEdit(id) }) { Text("Изменить") }
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (tx == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Text(
                        text = "${if (tx.type) "+" else "−"}${tx.amount} ₽",
                        style = MaterialTheme.typography.displaySmall,
                        color = if (tx.type) Color(0xFF3B6D11) else MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = if (tx.type) "Доход" else "Расход",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(Modifier.height(24.dp))
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                Modifier.fillMaxWidth(),
                                Arrangement.SpaceBetween,
                                Alignment.CenterVertically
                            ) {
                                Text(
                                    "Дата",
                                    color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                                )
                                Text(tx.date.toString())
                            }
                            HorizontalDivider(Modifier.padding(vertical = 8.dp))
                            Row(
                                Modifier.fillMaxWidth(),
                                Arrangement.SpaceBetween,
                                Alignment.CenterVertically
                            ) {
                                Text(
                                    "Категория",
                                    color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                                )
                                if (category != null) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = Color(category.color),
                                            modifier = Modifier.size(12.dp)
                                        ) {}
                                        Text(category.name)
                                    }
                                } else {
                                    Text("ID: ${tx.categoryId}")
                                }
                            }
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    OutlinedButton(
                        onClick = { viewModel.deleteTransaction(id, onBack) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) { Text("Удалить операцию") }
                }
            }
        }
    }
}