package org.example.project.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.domain.Breach

@Composable
fun BreachListScreen(viewModel: BreachListViewModel) {
    val breaches by viewModel.breaches.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.loadBreaches()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            itemsIndexed(
                items = breaches,
                key = { _, breach -> breach.name } // Assuming 'name' is a unique identifier
            ) { index, breach ->
                BreachItem(breach)

                // Load more items when reaching the end of the list
                if (index == breaches.size - 1 && !isLoading) {
                    LaunchedEffect(key1 = Unit) {
                        viewModel.loadBreaches()
                    }
                }
            }

            item {
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }

        error?.let { errorMessage ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = errorMessage, color = MaterialTheme.colors.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.retryLoading() }) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}

@Composable
fun BreachItem(breach: Breach) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            // Placeholder for logo
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(MaterialTheme.colors.primaryVariant)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = breach.title, style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold)
                Text(text = "Domain: ${breach.domain}")
                Text(text = "Breach Date: ${breach.breachDate}")
                Text(text = "Affected Accounts: ${breach.pwnCount}")
                Text(text = "Data Classes: ${breach.dataClasses.joinToString(", ")}")
                Text(text = "Verified: ${if (breach.isVerified) "Yes" else "No"}")
            }
        }
    }
}