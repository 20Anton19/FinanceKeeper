package com.example.financekeeper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.financekeeper.ui.theme.FinanceKeeperTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinanceKeeperTheme {
                Greeting()
            }
        }
    }
}

@Composable
fun Greeting(
    viewModel: MainActivityViewModel = koinViewModel()
) {
    val message by viewModel.text.collectAsStateWithLifecycle()
    Box(
        modifier = Modifier
            .fillMaxSize(),
    ){
        Text(
            text = message,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}