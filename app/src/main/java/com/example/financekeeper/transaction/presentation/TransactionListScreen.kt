package com.example.financekeeper.transaction.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.financekeeper.navigation.BottomNavBar
import com.example.financekeeper.navigation.BottomNavItem
import com.example.financekeeper.transaction.data.TransactionDto
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    viewModel: TransactionViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val filter by viewModel.filter.collectAsState()
    val listState = rememberLazyListState()
    var showFilterSheet by remember { mutableStateOf(false) }

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = listState.layoutInfo.totalItemsCount
            lastVisible >= total - 3
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) viewModel.loadNextPage()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Операции") },
                actions = {
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Фильтры")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        },
        bottomBar = {
            BottomNavBar(
                current = BottomNavItem.Transactions,
                onTransactions = {},
                onCategories = onNavigateToCategories,
                onAnalytics = onNavigateToAnalytics,
                onProfile = onNavigateToProfile
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is TransactionListState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is TransactionListState.Error -> {
                    Text(
                        text = s.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is TransactionListState.Success -> {
                    if (s.items.isEmpty()) {
                        Text("Операций нет", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(s.items, key = { it.id }) { item ->
                                TransactionItem(
                                    item = item,
                                    onClick = { onNavigateToDetail(item.id) }
                                )
                            }
                            if (s.hasMore) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            current = filter,
            onApply = {
                viewModel.applyFilter(it)
                showFilterSheet = false
            },
            onDismiss = { showFilterSheet = false }
        )
    }
}

@Composable
fun TransactionItem(
    item: TransactionDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = item.date.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Text(
                text = "${if (item.type) "+" else "−"}${item.amount} ₽",
                style = MaterialTheme.typography.titleMedium,
                color = if (item.type) Color(0xFF3B6D11) else MaterialTheme.colorScheme.error
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    current: TransactionFilter,
    onApply: (TransactionFilter) -> Unit,
    onDismiss: () -> Unit
) {
    var type by remember { mutableStateOf(current.type) }
    var amountFrom by remember { mutableStateOf(current.amountFrom?.toString() ?: "") }
    var amountTo by remember { mutableStateOf(current.amountTo?.toString() ?: "") }
    var dateFrom by remember { mutableStateOf(current.dateFrom ?: "") }
    var dateTo by remember { mutableStateOf(current.dateTo ?: "") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Text("Фильтры", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))

            // Тип операции
            Text("Тип", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = type == null, onClick = { type = null }, label = { Text("Все") })
                FilterChip(selected = type == true, onClick = { type = true }, label = { Text("Доходы") })
                FilterChip(selected = type == false, onClick = { type = false }, label = { Text("Расходы") })
            }

            Spacer(Modifier.height(12.dp))

            // Сумма
            Text("Сумма", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = amountFrom,
                    onValueChange = { amountFrom = it },
                    label = { Text("От") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = amountTo,
                    onValueChange = { amountTo = it },
                    label = { Text("До") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(Modifier.height(12.dp))

            // Дата
            Text("Дата", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = dateFrom,
                    onValueChange = { dateFrom = it },
                    label = { Text("От (гггг-мм-дд)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = dateTo,
                    onValueChange = { dateTo = it },
                    label = { Text("До (гггг-мм-дд)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { onApply(TransactionFilter()) },
                    modifier = Modifier.weight(1f)
                ) { Text("Сбросить") }
                Button(
                    onClick = {
                        onApply(TransactionFilter(
                            type = type,
                            amountFrom = amountFrom.toDoubleOrNull(),
                            amountTo = amountTo.toDoubleOrNull(),
                            dateFrom = dateFrom.ifBlank { null },
                            dateTo = dateTo.ifBlank { null }
                        ))
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Применить") }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}