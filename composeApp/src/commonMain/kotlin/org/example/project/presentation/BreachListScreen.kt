package org.example.project.presentation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.delay
import org.example.project.domain.Breach

@Composable
fun BreachListScreen(viewModel: BreachListViewModel) {
    val breaches by viewModel.breaches.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    var selectedBreach by remember { mutableStateOf<Breach?>(null) }
    var showInitialLoading by remember { mutableStateOf(true) }
    var isSearchVisible by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        viewModel.loadBreaches()
        delay(2000) // Show loading animation for 2 seconds
        showInitialLoading = false
    }

    val customColors = lightColors(
        primary = Color(0xFF3F51B5),
        primaryVariant = Color(0xFF303F9F),
        secondary = Color(0xFFFF4081)
    )

    MaterialTheme(colors = customColors) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1A237E),
                            Color(0xFF3949AB)
                        )
                    )
                )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Header(
                    isSearchVisible = isSearchVisible,
                    onSearchClick = { isSearchVisible = !isSearchVisible },
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.setSearchQuery(it) }
                )
                FilterOptions(
                    currentFilter = filterState,
                    onFilterSelected = { viewModel.setFilterState(it) }
                )
                AnimatedVisibility(
                    visible = showInitialLoading,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    CoolLoadingAnimation()
                }
                AnimatedVisibility(
                    visible = !showInitialLoading,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        LazyColumn {
                            items(
                                items = breaches,
                                key = { breach -> breach.name }
                            ) { breach ->
                                FloatingBreachItem(
                                    breach = breach,
                                    onClick = { selectedBreach = breach }
                                )
                            }

                            item {
                                if (isLoading && breaches.isNotEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(color = Color.White)
                                    }
                                }
                            }
                        }

                        error?.let { errorMessage ->
                            ErrorMessage(
                                message = errorMessage,
                                onRetry = { viewModel.retryLoading() }
                            )
                        }
                    }
                }
            }
        }
    }

    selectedBreach?.let { breach ->
        BreachDetailDialog(
            breach = breach,
            onDismiss = { selectedBreach = null }
        )
    }
}

@Composable
fun FilterOptions(
    currentFilter: BreachListViewModel.FilterState,
    onFilterSelected: (BreachListViewModel.FilterState) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A237E))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        FilterChip(
            selected = currentFilter == BreachListViewModel.FilterState.ALL,
            onClick = { onFilterSelected(BreachListViewModel.FilterState.ALL) },
            label = { Text("All") }
        )
        FilterChip(
            selected = currentFilter == BreachListViewModel.FilterState.VERIFIED,
            onClick = { onFilterSelected(BreachListViewModel.FilterState.VERIFIED) },
            label = { Text("Verified") }
        )
        FilterChip(
            selected = currentFilter == BreachListViewModel.FilterState.NOT_VERIFIED,
            onClick = { onFilterSelected(BreachListViewModel.FilterState.NOT_VERIFIED) },
            label = { Text("Not Verified") }
        )
    }
}

@Composable
fun FilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.padding(4.dp),
        elevation = 8.dp,
        shape = RoundedCornerShape(16.dp),
        color = if (selected) Color(0xFFFF4081) else Color.White
    ) {
        Box(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            CompositionLocalProvider(
                LocalContentColor provides if (selected) Color.White else Color(0xFF1A237E)
            ) {
                label()
            }
        }
    }
}

@Composable
fun Header(
    isSearchVisible: Boolean,
    onSearchClick: () -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A237E))
            .padding(16.dp)
    ) {
        if (isSearchVisible) {
            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(8.dp)),
                placeholder = { Text("Search breaches...") },
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    cursorColor = Color(0xFF3F51B5),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = { /* Perform search */ }
                )
            )
        } else {
            Text(
                text = "Data Breach Tracker",
                style = MaterialTheme.typography.h5,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        IconButton(
            onClick = onSearchClick,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.White
            )
        }
    }
}

@Composable
fun FloatingBreachItem(breach: Breach, onClick: () -> Unit) {
    val offsetY by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .offset(y = offsetY.dp)
            .clickable(onClick = onClick)
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .animateContentSize(),
        elevation = 4.dp,
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color.White.copy(alpha = 0.9f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            KamelImage(
                resource = asyncPainterResource(data = breach.logoPath),
                contentDescription = "Breach Logo",
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = breach.title,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E)
                )
                Text(
                    text = "Affected Accounts: ${breach.pwnCount}",
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray
                )
                Text(
                    text = "Breach Date: ${breach.breachDate}",
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun CoolLoadingAnimation() {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(100.dp)
                .scale(scale),
            color = Color.White,
            strokeWidth = 8.dp
        )
    }
}

@Composable
fun ErrorMessage(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(16.dp),
            elevation = 4.dp,
            backgroundColor = Color.White.copy(alpha = 0.9f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Error",
                    tint = Color.Red,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.body1,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF3F51B5))
                ) {
                    Text("Retry", color = Color.White)
                }
            }
        }
    }
}