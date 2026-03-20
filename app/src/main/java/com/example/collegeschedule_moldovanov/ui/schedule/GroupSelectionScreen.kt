package com.example.collegeschedule_moldovanov.ui.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.collegeschedule_moldovanov.data.repository.ScheduleRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupSelectionScreen(
    repository: ScheduleRepository,
    onGroupSelected: (String) -> Unit,
    onBack: () -> Unit
) {
    var groups by remember { mutableStateOf<List<String>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var searchText by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        try {
            groups = repository.loadGroups()
        } catch (e: Exception) {
            error = e.message
        } finally {
            loading = false
        }
    }

    val filteredGroups = remember(groups, searchText.text) {
        groups.filter { it.contains(searchText.text, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Выбор группы") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Поиск группы") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true
            )

            when {
                loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Ошибка: $error")
                    }
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(filteredGroups) { group ->
                            ListItem(
                                headlineContent = { Text(group) },
                                modifier = Modifier.clickable { onGroupSelected(group) }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}