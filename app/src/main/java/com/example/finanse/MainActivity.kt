package com.example.finanse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.finanse.data.AppDatabase
import com.example.finanse.data.entity.User
import com.example.finanse.data.repository.FinanceRepository
import com.example.finanse.ui.ChartsScreen
import com.example.finanse.ui.DashboardScreen
import com.example.finanse.ui.GoalsListScreen
import com.example.finanse.ui.TransactionsListScreen
import com.example.finanse.viewmodel.FinanceViewModel
import com.example.finanse.viewmodel.FinanceViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(this)
        val repository = FinanceRepository(database)
        val viewModelFactory = FinanceViewModelFactory(repository)

        setContent {
            com.example.finanse.ui.theme.FinanseTheme {
                val viewModel: FinanceViewModel = viewModel(factory = viewModelFactory)
                MainScreen(viewModel)
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: FinanceViewModel) {
    val currentUserId by viewModel.currentUserId.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()

    if (currentUserId == null) {
        WelcomeScreen(
            users = allUsers,
            onSelect = { viewModel.selectUser(it.id) },
            onCreate = { viewModel.addUser(it) }
        )
    } else {
        AppNavigation(viewModel)
    }
}

@Composable
fun AppNavigation(viewModel: FinanceViewModel) {
    val navController = rememberNavController()
    var selectedItem by remember { mutableIntStateOf(0) }
    var showAddSheet by remember { mutableStateOf(false) }

    val items = listOf(
        NavigationItem("Home", Icons.Default.Home, "home"),
        NavigationItem("Transakcje", Icons.AutoMirrored.Filled.List, "transactions"),
        NavigationItem("Wykresy", Icons.Default.Info, "charts"),
        NavigationItem("Cele", Icons.Default.Star, "goals"),
        NavigationItem("Zarządzaj", Icons.Default.Settings, "premium")
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSheet = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Dodaj")
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") { DashboardScreen(viewModel) }
            composable("transactions") { TransactionsListScreen(viewModel) }
            composable("charts") { ChartsScreen(viewModel) }
            composable("goals") { GoalsListScreen(viewModel) }
            composable("premium") { PremiumScreen(viewModel) }
        }

        if (showAddSheet) {
            AddTransactionSheet(
                viewModel = viewModel,
                onDismiss = { showAddSheet = false },
                onAdd = { kwota, typ, opis, kategoriaId, kontoId ->
                    viewModel.addTransaction(kwota, typ, opis, kategoriaId, kontoId)
                    showAddSheet = false
                }
            )
        }
    }
}

@Composable
fun WelcomeScreen(
    users: List<User>,
    onSelect: (User) -> Unit,
    onCreate: (String) -> Unit
) {
    var newUserName by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1B5E20), Color(0xFF4CAF50))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))
            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.2f)
            ) {
                Icon(
                    Icons.Default.Wallet,
                    contentDescription = null,
                    modifier = Modifier.padding(20.dp).fillMaxSize(),
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Mój Portfel",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                "Twoje centrum dowodzenia finansami",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(48.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        "Wybierz profil",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (users.isEmpty()) {
                        Column(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.PeopleOutline, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Brak aktywnych profili.\nUtwórz swój pierwszy profil, aby zacząć!",
                                textAlign = TextAlign.Center,
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(users) { user ->
                                ProfileItem(user, onClick = { onSelect(user) })
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showCreateDialog = true },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Nowy Profil", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Utwórz Profil") },
            text = {
                OutlinedTextField(
                    value = newUserName,
                    onValueChange = { newUserName = it },
                    label = { Text("Imię lub Nazwa") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (newUserName.isNotBlank()) {
                        onCreate(newUserName)
                        showCreateDialog = false
                        newUserName = ""
                    }
                }) { Text("Utwórz") }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) { Text("Anuluj") }
            }
        )
    }
}

@Composable
fun ProfileItem(user: User, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = Color(0xFF4CAF50).copy(alpha = 0.1f)
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp),
                    tint = Color(0xFF2E7D32)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                user.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionSheet(viewModel: FinanceViewModel, onDismiss: () -> Unit, onAdd: (Double, String, String, Int, Int) -> Unit) {
    val categories by viewModel.allCategories.collectAsState()
    val accounts by viewModel.allAccounts.collectAsState()
    
    var kwota by remember { mutableStateOf("") }
    var opis by remember { mutableStateOf("") }
    var typ by remember { mutableStateOf("Wydatek") }
    var selectedCategoryId by remember { mutableIntStateOf(0) }
    var selectedAccountId by remember { mutableIntStateOf(0) }

    LaunchedEffect(categories, accounts) {
        if (selectedCategoryId == 0 && categories.isNotEmpty()) selectedCategoryId = categories.first().id
        if (selectedAccountId == 0 && accounts.isNotEmpty()) selectedAccountId = accounts.first().id
    }
    
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp).padding(bottom = 32.dp).fillMaxWidth()) {
            Text("Szybkie Dodawanie", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = kwota,
                onValueChange = { kwota = it },
                label = { Text("Kwota") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = opis,
                onValueChange = { opis = it },
                label = { Text("Opis") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Kategoria", style = MaterialTheme.typography.bodyMedium)
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

            Spacer(modifier = Modifier.height(16.dp))
            Text("Konto", style = MaterialTheme.typography.bodyMedium)
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

            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                FilterChip(selected = typ == "Wydatek", onClick = { typ = "Wydatek" }, label = { Text("Wydatek") })
                FilterChip(selected = typ == "Przychód", onClick = { typ = "Przychód" }, label = { Text("Przychód") })
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { 
                    val valKwota = kwota.toDoubleOrNull() ?: 0.0
                    onAdd(valKwota, typ, opis, selectedCategoryId, selectedAccountId) 
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = selectedAccountId != 0
            ) {
                Text("Dodaj Transakcję")
            }
        }
    }
}

@Composable
fun PremiumScreen(viewModel: FinanceViewModel) {
    val accounts by viewModel.allAccounts.collectAsState()
    val users by viewModel.allUsers.collectAsState()
    val currentUserId by viewModel.currentUserId.collectAsState()
    
    var showAddAccount by remember { mutableStateOf(false) }
    var accountName by remember { mutableStateOf("") }
    var accountBalance by remember { mutableStateOf("") }
    val df = java.text.DecimalFormat("#,##0.00")

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text("Twój Profil", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            val currentUser = users.find { it.id == currentUserId }
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), shape = RoundedCornerShape(16.dp)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(currentUser?.name ?: "Nieznany", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Zalogowany", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = { viewModel.selectUser(null) }) {
                        Text("Wyloguj")
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Konta", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Button(onClick = { showAddAccount = true }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Dodaj")
                }
            }
        }

        if (accounts.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Brak kont", style = MaterialTheme.typography.bodyLarge)
                        Text("Dodaj pierwsze konto, aby zacząć", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        } else {
            items(accounts) { account ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(account.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(account.currency, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                        Text(
                            "${df.format(account.balance)} ${account.currency}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = if (account.balance >= 0) Color(0xFF4CAF50) else Color.Red
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Kursy Walut NBP", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
        }

        item {
            ExchangeRatesSection()
        }

        item {
            Text("Funkcje Premium (Odblokowane)", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }

        item {
            PremiumFeatureCard(
                title = "Zaawansowane Wykresy",
                desc = "Masz pełny dostęp do wszystkich analiz i trendów.",
                icon = Icons.Default.PieChart
            )
        }
        item {
            PremiumFeatureCard(
                title = "Wiele Profili",
                desc = "Możesz tworzyć nieograniczoną liczbę profili domowników.",
                icon = Icons.Default.People
            )
        }
        item {
            PremiumFeatureCard(
                title = "Cele Oszczędnościowe",
                desc = "Planuj i realizuj swoje cele z automatycznymi wpłatami.",
                icon = Icons.Default.Star
            )
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }

    if (showAddAccount) {
        AlertDialog(
            onDismissRequest = { showAddAccount = false },
            title = { Text("Nowe Konto") },
            text = {
                Column {
                    OutlinedTextField(
                        value = accountName,
                        onValueChange = { accountName = it },
                        label = { Text("Nazwa konta") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = accountBalance,
                        onValueChange = { accountBalance = it },
                        label = { Text("Saldo początkowe") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (accountName.isNotBlank()) {
                        viewModel.addAccount(accountName, accountBalance.toDoubleOrNull() ?: 0.0, "PLN")
                        showAddAccount = false
                        accountName = ""
                        accountBalance = ""
                    }
                }) { Text("Dodaj") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddAccount = false
                    accountName = ""
                    accountBalance = ""
                }) {
                    Text("Anuluj")
                }
            }
        )
    }
}

@Composable
fun PremiumFeatureCard(title: String, desc: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold)
                Text(desc, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun ExchangeRatesSection() {
    var exchangeRates by remember { mutableStateOf<List<com.example.finanse.api.NbpRate>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var lastUpdate by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val retrofit = retrofit2.Retrofit.Builder()
                .baseUrl("https://api.nbp.pl/api/")
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .build()

            val api = retrofit.create(com.example.finanse.api.NbpApi::class.java)
            val response = api.getExchangeRates()

            if (response.isNotEmpty()) {
                exchangeRates = response[0].rates
                lastUpdate = response[0].effectiveDate
            }
            isLoading = false
        } catch (e: Exception) {
            errorMessage = "Błąd: ${e.localizedMessage ?: "Brak połączenia z internetem"}"
            isLoading = false
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AttachMoney, contentDescription = null, tint = Color(0xFF2E7D32))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Kursy walut", fontWeight = FontWeight.Bold)
                        if (lastUpdate != null) {
                            Text("Aktualizacja: $lastUpdate", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when {
                isLoading -> {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    }
                }
                errorMessage != null -> {
                    Text(
                        errorMessage ?: "",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                exchangeRates != null -> {
                    val popularCurrencies = listOf("USD", "EUR", "GBP", "CHF")
                    val filteredRates = exchangeRates!!.filter { it.code in popularCurrencies }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        filteredRates.forEach { rate ->
                            ExchangeRateItem(rate)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExchangeRateItem(rate: com.example.finanse.api.NbpRate) {
    val df = java.text.DecimalFormat("#,##0.0000")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = Color(0xFF4CAF50).copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        rate.code,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(rate.code, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(rate.currency, fontSize = 10.sp, color = Color.Gray, maxLines = 1)
            }
        }
        Text(
            "${df.format(rate.mid)} PLN",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color(0xFF2E7D32)
        )
    }
}

data class NavigationItem(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val route: String)
