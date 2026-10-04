package com.example.finanse.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.finanse.data.entity.Transaction
import com.example.finanse.viewmodel.FinanceViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(viewModel: FinanceViewModel) {
    val transactions by viewModel.allTransactions.collectAsState()
    val accounts by viewModel.allAccounts.collectAsState()
    val goals by viewModel.allGoals.collectAsState()
    val users by viewModel.allUsers.collectAsState()
    val currentUserId by viewModel.currentUserId.collectAsState()
    
    val currentUser = remember(users, currentUserId) { users.find { it.id == currentUserId } }
    val df = remember { DecimalFormat("#,##0.00") }
    
    val totalBalance = remember(accounts) { accounts.sumOf { it.balance } }
    val totalInGoals = remember(goals) { goals.sumOf { it.aktual } }
    val availableBalance = totalBalance
    
    val totalExpenses = remember(transactions) { transactions.filter { it.typ == "Wydatek" }.sumOf { it.kwota } }
    val totalIncome = remember(transactions) { transactions.filter { it.typ == "Przychód" }.sumOf { it.kwota } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Witaj,", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                    Text(currentUser?.name ?: "Użytkowniku", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF1B5E20), Color(0xFF4CAF50))
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        Text("Dostępne Środki", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                        Text("${df.format(availableBalance)} PLN", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("W celach: ${df.format(totalInGoals)} PLN", color = Color.White.copy(alpha = 0.9f), fontSize = 16.sp)
                        Text("Suma: ${df.format(availableBalance + totalInGoals)} PLN", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                    }
                    Icon(
                        Icons.Default.Wallet,
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.TopEnd).size(40.dp),
                        tint = Color.White.copy(alpha = 0.3f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (accounts.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Dodaj swoje pierwsze konto w zakładce 'Zarządzaj', aby móc dodawać transakcje!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard("Wydatki", df.format(totalExpenses), "Suma", Color(0xFFE57373), Modifier.weight(1f))
                StatCard("Dochody", df.format(totalIncome), "Suma", Color(0xFF81C784), Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val budgetProgress = if (totalIncome > 0) (totalExpenses / totalIncome * 100).toInt().coerceIn(0, 100) else 0
                StatCard("Zużycie", "$budgetProgress%", "Dochodu", Color(0xFFFFB74D), Modifier.weight(1f))
                
                val totalGoalTarget = goals.sumOf { it.cel }
                val goalsProgress = if (totalGoalTarget > 0) (goals.sumOf { it.aktual } / totalGoalTarget * 100).toInt().coerceIn(0, 100) else 0
                StatCard("Cele", "$goalsProgress%", "Realizacja", Color(0xFF64B5F6), Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Text("Ostatnie Transakcje", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(12.dp))
            if (transactions.isEmpty()) {
                Text("Brak transakcji", modifier = Modifier.padding(vertical = 16.dp), color = Color.Gray)
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(transactions.take(5)) { transaction ->
                        TransactionCarouselItem(transaction)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        item {
            Text("Trend Salda (Ostatnie)", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(12.dp))
            BalanceLineChart(transactions)
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Text("Struktura Wydatków", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(12.dp))
            BudgetPieChart(transactions, viewModel)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun BalanceLineChart(transactions: List<Transaction>) {
    Card(
        modifier = Modifier.fillMaxWidth().height(200.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        AndroidView<LineChart>(
            factory = { context ->
                LineChart(context).apply {
                    description.isEnabled = false
                    setTouchEnabled(true)
                    isDragEnabled = true
                    setScaleEnabled(false)
                    setPinchZoom(false)
                    setDrawGridBackground(false)

                    xAxis.apply {
                        position = XAxis.XAxisPosition.BOTTOM
                        setDrawGridLines(false)
                        granularity = 1f
                    }

                    axisLeft.apply {
                        setDrawGridLines(true)
                        gridColor = android.graphics.Color.LTGRAY
                    }

                    axisRight.isEnabled = false
                    legend.isEnabled = true
                    animateX(1000)
                }
            },
            modifier = Modifier.fillMaxSize().padding(8.dp),
            update = { chart ->
                try {
                    if (transactions.isEmpty()) {
                        chart.clear()
                        chart.invalidate()
                        return@AndroidView
                    }

                    // Calculate cumulative balance over time
                    val sortedTransactions = transactions.sortedBy { it.data }
                    var currentBalance = 0.0
                    val entries = mutableListOf<Entry>()
                    
                    sortedTransactions.forEachIndexed { index, transaction ->
                        when (transaction.typ) {
                            "Przychód" -> currentBalance += transaction.kwota
                            "Wydatek", "Cel" -> currentBalance -= transaction.kwota
                        }
                        entries.add(Entry(index.toFloat(), currentBalance.toFloat()))
                    }

                    if (entries.isEmpty()) {
                        chart.clear()
                        chart.invalidate()
                        return@AndroidView
                    }

                    val dataSet = LineDataSet(entries, "Saldo (PLN)").apply {
                        color = android.graphics.Color.parseColor("#4CAF50")
                        setCircleColor(android.graphics.Color.parseColor("#4CAF50"))
                        lineWidth = 2f
                        circleRadius = 4f
                        setDrawCircleHole(false)
                        valueTextSize = 10f
                        setDrawFilled(true)
                        fillColor = android.graphics.Color.parseColor("#81C784")
                        mode = LineDataSet.Mode.CUBIC_BEZIER
                    }

                    chart.data = LineData(dataSet)
                    chart.invalidate()
                } catch (e: Exception) {
                    android.util.Log.e("BalanceLineChart", "Error updating chart", e)
                }
            }
        )
    }
}

@Composable
fun StatCard(label: String, value: String, trend: String, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Column {
                Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(trend, fontSize = 11.sp, color = color)
            }
        }
    }
}

@Composable
fun TransactionCarouselItem(transaction: Transaction) {
    Card(
        modifier = Modifier.width(160.dp).height(100.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Text(transaction.opis, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
            Text(
                "${if (transaction.typ == "Wydatek") "-" else "+"}${transaction.kwota} PLN",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (transaction.typ == "Wydatek") Color(0xFFE57373) else Color(0xFF81C784)
            )
        }
    }
}

@Composable
fun BudgetPieChart(transactions: List<Transaction>, viewModel: FinanceViewModel) {
    val categories by viewModel.allCategories.collectAsState()
    Card(
        modifier = Modifier.fillMaxWidth().height(250.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        AndroidView<PieChart>(
            factory = { context ->
                PieChart(context).apply {
                    description.isEnabled = false
                    isDrawHoleEnabled = true
                    setHoleColor(android.graphics.Color.TRANSPARENT)
                    legend.isEnabled = true
                    setEntryLabelColor(android.graphics.Color.BLACK)
                    setEntryLabelTextSize(12f)
                    animateY(1400)
                }
            },
            modifier = Modifier.fillMaxSize().padding(16.dp),
            update = { chart ->
                try {
                    val expenses = transactions.filter { it.typ == "Wydatek" || it.typ == "Cel" }
                    if (expenses.isEmpty()) {
                        chart.clear()
                        chart.invalidate()
                        return@AndroidView
                    }

                    val expensesByCategory = expenses.groupBy { it.kategoriaId }
                    val entries = expensesByCategory.map { (catId, transList) ->
                        val categoryName = categories.find { it.id == catId }?.name ?: "Inne"
                        PieEntry(transList.sumOf { it.kwota }.toFloat(), categoryName)
                    }.filter { it.value > 0f }

                    if (entries.isEmpty()) {
                        chart.clear()
                        chart.invalidate()
                        return@AndroidView
                    }
                    
                    val dataSet = PieDataSet(entries, "Wydatki").apply {
                        colors = ColorTemplate.MATERIAL_COLORS.toList()
                        valueTextSize = 14f
                        sliceSpace = 3f
                    }
                    chart.data = PieData(dataSet)
                    chart.invalidate()
                } catch (e: Exception) {
                    android.util.Log.e("BudgetPieChart", "Error updating chart", e)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsListScreen(viewModel: FinanceViewModel) {
    val transactions by viewModel.allTransactions.collectAsState()
    val categories by viewModel.allCategories.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedTypeFilter by remember { mutableStateOf("Wszystkie") }
    var selectedCategoryId by remember { mutableIntStateOf(-1) } // -1 for all
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)),
            placeholder = { Text("Szukaj transakcji...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(selected = selectedTypeFilter == "Wszystkie", onClick = { selectedTypeFilter = "Wszystkie" }, label = { Text("Wszystkie") })
            }
            item {
                FilterChip(selected = selectedTypeFilter == "Wydatek", onClick = { selectedTypeFilter = "Wydatek" }, label = { Text("Wydatki") })
            }
            item {
                FilterChip(selected = selectedTypeFilter == "Przychód", onClick = { selectedTypeFilter = "Przychód" }, label = { Text("Dochody") })
            }
            item {
                FilterChip(selected = selectedTypeFilter == "Cel", onClick = { selectedTypeFilter = "Cel" }, label = { Text("Na cel") })
            }
        }
        
        ScrollableTabRow(
            selectedTabIndex = if (selectedCategoryId == -1) 0 else (categories.indexOfFirst { it.id == selectedCategoryId } + 1).coerceAtLeast(0),
            edgePadding = 0.dp,
            divider = {}
        ) {
            Tab(
                selected = selectedCategoryId == -1,
                onClick = { selectedCategoryId = -1 },
                text = { Text("Wszystkie") }
            )
            categories.forEach { category ->
                Tab(
                    selected = selectedCategoryId == category.id,
                    onClick = { selectedCategoryId = category.id },
                    text = { Text(category.name) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val filteredTransactions = transactions.filter {
            (searchQuery.isBlank() || it.opis.contains(searchQuery, ignoreCase = true)) &&
            (selectedTypeFilter == "Wszystkie" || it.typ == selectedTypeFilter) &&
            (selectedCategoryId == -1 || it.kategoriaId == selectedCategoryId)
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(filteredTransactions) { transaction ->
                TransactionListItem(transaction, categories.find { it.id == transaction.kategoriaId })
            }
        }
    }
}

@Composable
fun TransactionListItem(transaction: Transaction, category: com.example.finanse.data.entity.Category?) {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).background(
                        when(transaction.typ) {
                            "Wydatek" -> Color(0xFFFFEBEE)
                            "Przychód" -> Color(0xFFE8F5E9)
                            else -> Color(0xFFE3F2FD) // For "Cel"
                        },
                        CircleShape
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        when(transaction.typ) {
                            "Wydatek" -> Icons.Default.ArrowDownward
                            "Przychód" -> Icons.Default.ArrowUpward
                            else -> Icons.Default.Star
                        },
                        contentDescription = null,
                        tint = when(transaction.typ) {
                            "Wydatek" -> Color.Red
                            "Przychód" -> Color.Green
                            else -> Color.Blue
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(transaction.opis, fontWeight = FontWeight.Bold)
                    Text("${category?.name ?: "Inne"} • ${sdf.format(Date(transaction.data))}", fontSize = 12.sp, color = Color.Gray)
                }
            }
            Text(
                "${if (transaction.typ == "Wydatek" || transaction.typ == "Cel") "-" else "+"}${transaction.kwota} PLN",
                fontWeight = FontWeight.Bold,
                color = when(transaction.typ) {
                    "Wydatek" -> Color.Red
                    "Przychód" -> Color.Green
                    else -> Color.Blue
                }
            )
        }
    }
}

@Composable
fun ChartsScreen(viewModel: FinanceViewModel) {
    val transactions by viewModel.allTransactions.collectAsState()
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text("Analiza Finansowa", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        }

        item {
            Text("Trend Salda", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(12.dp))
            BalanceLineChart(transactions)
        }

        item {
            Text("Podział Wydatków", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(12.dp))
            BudgetPieChart(transactions, viewModel)
        }
    }
}

@Composable
fun GoalsListScreen(viewModel: FinanceViewModel) {
    val goals by viewModel.allGoals.collectAsState()
    val categories by viewModel.allCategories.collectAsState()
    val accounts by viewModel.allAccounts.collectAsState()
    
    var showAddGoal by remember { mutableStateOf(false) }
    var goalName by remember { mutableStateOf("") }
    var goalAmount by remember { mutableStateOf("") }
    var goalDeadline by remember { mutableStateOf("") }
    var goalPriority by remember { mutableStateOf("Medium") }
    var selectedCategoryId by remember { mutableIntStateOf(0) }

    var showDepositDialog by remember { mutableStateOf<com.example.finanse.data.entity.Goal?>(null) }
    var depositAmount by remember { mutableStateOf("") }
    var selectedAccountId by remember { mutableIntStateOf(0) }

    LaunchedEffect(categories) {
        if (selectedCategoryId == 0 && categories.isNotEmpty()) selectedCategoryId = categories.first().id
    }
    
    LaunchedEffect(accounts) {
        if (selectedAccountId == 0 && accounts.isNotEmpty()) selectedAccountId = accounts.first().id
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Twoje Cele", fontWeight = FontWeight.Bold, fontSize = 24.sp)
            Button(onClick = { showAddGoal = true }) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Dodaj")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(goals) { goal ->
                GoalItem(goal, onDeposit = { showDepositDialog = goal })
            }
        }

        if (showAddGoal) {
            AlertDialog(
                onDismissRequest = { showAddGoal = false },
                title = { Text("Nowy Cel") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = goalName,
                            onValueChange = { goalName = it },
                            label = { Text("Nazwa celu") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = goalAmount,
                            onValueChange = { goalAmount = it },
                            label = { Text("Kwota docelowa") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text("Kategoria", style = MaterialTheme.typography.bodySmall)
                        if (categories.isNotEmpty()) {
                            ScrollableTabRow(
                                selectedTabIndex = categories.indexOfFirst { it.id == selectedCategoryId }.coerceAtLeast(0),
                                edgePadding = 0.dp,
                                divider = {}
                            ) {
                                categories.forEach { category ->
                                    Tab(
                                        selected = selectedCategoryId == category.id,
                                        onClick = { selectedCategoryId = category.id },
                                        text = { Text(category.name) }
                                    )
                                }
                            }
                        } else {
                            Text("Brak kategorii", style = MaterialTheme.typography.bodySmall)
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = goalDeadline,
                            onValueChange = { goalDeadline = it },
                            label = { Text("Termin (np. 2025-12-31)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Priorytet:", fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(selected = goalPriority == "High", onClick = { goalPriority = "High" }, label = { Text("High") })
                            FilterChip(selected = goalPriority == "Medium", onClick = { goalPriority = "Medium" }, label = { Text("Medium") })
                            FilterChip(selected = goalPriority == "Low", onClick = { goalPriority = "Low" }, label = { Text("Low") })
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (goalName.isNotBlank() && goalAmount.isNotBlank()) {
                                viewModel.addGoal(
                                    name = goalName,
                                    cel = goalAmount.toDoubleOrNull() ?: 0.0,
                                    aktual = 0.0,
                                    termin = goalDeadline.ifBlank { "Brak" },
                                    priorytet = goalPriority,
                                    kategoriaId = selectedCategoryId
                                )
                                showAddGoal = false
                                goalName = ""
                                goalAmount = ""
                                goalDeadline = ""
                                goalPriority = "Medium"
                            }
                        }
                    ) { Text("Dodaj") }
                },
                dismissButton = {
                    TextButton(onClick = { showAddGoal = false }) { Text("Anuluj") }
                }
            )
        }
        
        if (showDepositDialog != null) {
            AlertDialog(
                onDismissRequest = { showDepositDialog = null },
                title = { Text("Wpłać na cel: ${showDepositDialog?.name}") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = depositAmount,
                            onValueChange = { depositAmount = it },
                            label = { Text("Kwota wpłaty") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Z konta:", style = MaterialTheme.typography.bodySmall)
                        if (accounts.isNotEmpty()) {
                            ScrollableTabRow(
                                selectedTabIndex = accounts.indexOfFirst { it.id == selectedAccountId }.coerceAtLeast(0),
                                edgePadding = 0.dp,
                                divider = {}
                            ) {
                                accounts.forEach { account ->
                                    Tab(
                                        selected = selectedAccountId == account.id,
                                        onClick = { selectedAccountId = account.id },
                                        text = { Text(account.name) }
                                    )
                                }
                            }
                        } else {
                            Text("Brak kont", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val amount = depositAmount.toDoubleOrNull() ?: 0.0
                        if (amount > 0 && selectedAccountId != 0) {
                            viewModel.transferToGoal(showDepositDialog!!, amount, selectedAccountId)
                            showDepositDialog = null
                            depositAmount = ""
                        }
                    }) { Text("Wpłać") }
                },
                dismissButton = {
                    TextButton(onClick = { showDepositDialog = null }) { Text("Anuluj") }
                }
            )
        }
    }
}

@Composable
fun GoalItem(goal: com.example.finanse.data.entity.Goal, onDeposit: () -> Unit) {
    val progress = if (goal.cel > 0) (goal.aktual / goal.cel).toFloat().coerceIn(0f, 1f) else 0f
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(goal.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Termin: ${goal.termin}", fontSize = 12.sp, color = Color.Gray)
                }
                Surface(
                    color = when(goal.priorytet) {
                        "High" -> Color(0xFFFFEBEE)
                        "Medium" -> Color(0xFFFFF3E0)
                        else -> Color(0xFFE8F5E9)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        goal.priorytet,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        color = when(goal.priorytet) {
                            "High" -> Color.Red
                            "Medium" -> Color(0xFFFFA500) // Orange
                            else -> Color.Green
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = Color(0xFF4CAF50),
                trackColor = Color(0xFFE0E0E0)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${goal.aktual} / ${goal.cel} PLN", fontWeight = FontWeight.SemiBold)
                Text("${(progress * 100).toInt()}%", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onDeposit, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Text("Wpłać środki")
            }
        }
    }
}
