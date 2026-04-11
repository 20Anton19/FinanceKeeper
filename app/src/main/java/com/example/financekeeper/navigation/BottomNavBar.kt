package com.example.financekeeper.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

enum class BottomNavItem { Transactions, Analytics, Categories, Profile }

@Composable
fun BottomNavBar(
    current: BottomNavItem,
    onTransactions: () -> Unit,
    onAnalytics: () -> Unit,
    onCategories: () -> Unit,
    onProfile: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = current == BottomNavItem.Transactions,
            onClick = onTransactions,
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
            label = { Text("Операции") }
        )
        NavigationBarItem(
            selected = current == BottomNavItem.Categories,
            onClick = onCategories,
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
            label = { Text("Категории") }
        )
        NavigationBarItem(
            selected = current == BottomNavItem.Analytics,
            onClick = onAnalytics,
            icon = { Icon(Icons.Default.Star, contentDescription = null) },
            label = { Text("Аналитика") }
        )
        NavigationBarItem(
            selected = current == BottomNavItem.Profile,
            onClick = onProfile,
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
            label = { Text("Профиль") }
        )
    }
}