package com.example.finanse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.finanse.data.entity.*
import com.example.finanse.data.repository.FinanceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FinanceViewModel(private val repository: FinanceRepository) : ViewModel() {

    private val _currentUserId = MutableStateFlow<Int?>(null)
    val currentUserId: StateFlow<Int?> = _currentUserId.asStateFlow()

    private val _allUsers = MutableStateFlow<List<User>>(emptyList())
    val allUsers: StateFlow<List<User>> = _allUsers.asStateFlow()

    private val _allTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val allTransactions: StateFlow<List<Transaction>> = _allTransactions.asStateFlow()

    private val _allAccounts = MutableStateFlow<List<Account>>(emptyList())
    val allAccounts: StateFlow<List<Account>> = _allAccounts.asStateFlow()

    private val _allCategories = MutableStateFlow<List<Category>>(emptyList())
    val allCategories: StateFlow<List<Category>> = _allCategories.asStateFlow()

    private val _allBudgets = MutableStateFlow<List<Budget>>(emptyList())
    val allBudgets: StateFlow<List<Budget>> = _allBudgets.asStateFlow()

    private val _allGoals = MutableStateFlow<List<Goal>>(emptyList())
    val allGoals: StateFlow<List<Goal>> = _allGoals.asStateFlow()

    init {
        loadUsers()
        viewModelScope.launch {
            _currentUserId.collect { userId ->
                if (userId != null) {
                    loadData(userId)
                } else {
                    clearData()
                }
            }
        }
    }

    private fun loadUsers() = viewModelScope.launch {
        _allUsers.value = repository.getAllUsers()
    }

    private fun loadData(userId: Int) = viewModelScope.launch {
        _allTransactions.value = repository.getAllTransactions(userId)
        _allAccounts.value = repository.getAllAccounts(userId)
        _allCategories.value = repository.getAllCategories(userId)
        _allBudgets.value = repository.getAllBudgets(userId)
        _allGoals.value = repository.getAllGoals(userId)
    }

    private fun clearData() {
        _allTransactions.value = emptyList()
        _allAccounts.value = emptyList()
        _allCategories.value = emptyList()
        _allBudgets.value = emptyList()
        _allGoals.value = emptyList()
    }

    fun refreshData() {
        loadUsers()
        _currentUserId.value?.let { loadData(it) }
    }

    fun selectUser(userId: Int?) {
        _currentUserId.value = userId
    }

    fun addUser(name: String) = viewModelScope.launch {
        val userId = repository.insertUser(User(name = name, pin = null)).toInt()
        seedDefaultCategories(userId)
        loadUsers()
    }

    private suspend fun seedDefaultCategories(userId: Int) {
        val defaultCategories = listOf(
            Category(userId = userId, name = "Jedzenie", icon = "restaurant", color = "#FF5722"),
            Category(userId = userId, name = "Transport", icon = "directions_car", color = "#2196F3"),
            Category(userId = userId, name = "Rozrywka", icon = "movie", color = "#E91E63"),
            Category(userId = userId, name = "Zdrowie", icon = "medical_services", color = "#4CAF50"),
            Category(userId = userId, name = "Dom", icon = "home", color = "#795548"),
            Category(userId = userId, name = "Inne", icon = "category", color = "#9E9E9E"),
            Category(userId = userId, name = "Przychód", icon = "attach_money", color = "#4CAF50")
        )
        defaultCategories.forEach { repository.insertCategory(it) }
    }

    fun addTransaction(kwota: Double, typ: String, opis: String, kategoriaId: Int, kontoId: Int, data: Long = System.currentTimeMillis()) = viewModelScope.launch {
        val userId = _currentUserId.value ?: return@launch
        val transaction = Transaction(userId = userId, kwota = kwota, typ = typ, opis = opis, kategoriaId = kategoriaId, kontoId = kontoId, data = data)
        repository.insertTransaction(transaction)

        // Update account balance
        val account = repository.getAccountById(kontoId)
        account?.let {
            val newBalance = if (typ == "Przychód") it.balance + kwota else it.balance - kwota
            repository.updateAccount(it.copy(balance = newBalance))
        }
        refreshData()
    }

    fun deleteTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.deleteTransaction(transaction)
        // Revert account balance
        val account = repository.getAccountById(transaction.kontoId)
        account?.let {
            val newBalance = if (transaction.typ == "Przychód") it.balance - transaction.kwota else it.balance + transaction.kwota
            repository.updateAccount(it.copy(balance = newBalance))
        }
        refreshData()
    }

    fun addAccount(name: String, balance: Double, currency: String) = viewModelScope.launch {
        val userId = _currentUserId.value ?: return@launch
        repository.insertAccount(Account(userId = userId, name = name, balance = balance, currency = currency))
        refreshData()
    }

    fun addGoal(name: String, cel: Double, aktual: Double, termin: String, priorytet: String, kategoriaId: Int = 0) = viewModelScope.launch {
        val userId = _currentUserId.value ?: return@launch
        repository.insertGoal(Goal(userId = userId, name = name, cel = cel, aktual = aktual, termin = termin, priorytet = priorytet, kategoriaId = kategoriaId))
        refreshData()
    }

    fun updateGoal(goal: Goal) = viewModelScope.launch {
        repository.updateGoal(goal)
        refreshData()
    }

    fun deleteGoal(goal: Goal) = viewModelScope.launch {
        repository.deleteGoal(goal)
        refreshData()
    }

    fun transferToGoal(goal: Goal, amount: Double, fromAccountId: Int) = viewModelScope.launch {
        val userId = _currentUserId.value ?: return@launch
        val account = repository.getAccountById(fromAccountId)
        if (account != null && account.balance >= amount) {
            repository.updateAccount(account.copy(balance = account.balance - amount))
            repository.updateGoal(goal.copy(aktual = goal.aktual + amount))

            val transaction = Transaction(
                userId = userId,
                kwota = amount,
                typ = "Cel",
                opis = "Transfer do celu: ${goal.name}",
                kategoriaId = goal.kategoriaId,
                kontoId = fromAccountId,
                data = System.currentTimeMillis()
            )
            repository.insertTransaction(transaction)
            refreshData()
        }
    }
}

class FinanceViewModelFactory(private val repository: FinanceRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FinanceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
