package com.example.financekeeper.category.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToTransactions: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    viewModel: CategoryViewModel = koinViewModel()
) {
    val state by viewModel.listState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Категории") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        },
        bottomBar = {
            BottomNavBar(
                current = BottomNavItem.Categories,
                onTransactions = onNavigateToTransactions,
                onCategories = {},
                onAnalytics = onNavigateToAnalytics,
                onProfile = onNavigateToProfile
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is CategoryListState.Loading ->
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is CategoryListState.Error ->
                    Text(s.message, color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center))
                is CategoryListState.Success -> {
                    if (s.items.isEmpty()) {
                        Text("Нет категорий", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(s.items, key = { it.id }) { cat ->
                                ListItem(
                                    headlineContent = { Text(cat.name) },
                                    leadingContent = {
                                        Surface(
                                            shape = CircleShape,
                                            color = Color(cat.color),
                                            modifier = Modifier.size(16.dp)
                                        ) {}
                                    },
                                    modifier = Modifier.clickable { onNavigateToEdit(cat.id) }
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}