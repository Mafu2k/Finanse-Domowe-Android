package com.example.finanse.data.repository

import com.example.finanse.data.DatabaseHelper
import com.example.finanse.data.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FinanceRepository(private val dbHelper: DatabaseHelper) {

    suspend fun getAllTransactions(userId: Int): List<Transaction> = withContext(Dispatchers.IO) {
        dbHelper.getAllTransactions(userId)
    }

    suspend fun getAllAccounts(userId: Int): List<Account> = withContext(Dispatchers.IO) {
        dbHelper.getAllAccounts(userId)
    }

    suspend fun getAllCategories(userId: Int): List<Category> = withContext(Dispatchers.IO) {
        dbHelper.getAllCategories(userId)
    }

    suspend fun getAllBudgets(userId: Int): List<Budget> = withContext(Dispatchers.IO) {
        dbHelper.getAllBudgets(userId)
    }

    suspend fun getAllGoals(userId: Int): List<Goal> = withContext(Dispatchers.IO) {
        dbHelper.getAllGoals(userId)
    }

    suspend fun getAllUsers(): List<User> = withContext(Dispatchers.IO) {
        dbHelper.getAllUsers()
    }

    suspend fun getAllCurrencies(): List<Currency> = withContext(Dispatchers.IO) {
        dbHelper.getAllCurrencies()
    }

    suspend fun insertUser(user: User): Long = withContext(Dispatchers.IO) {
        dbHelper.insertUser(user)
    }

    suspend fun getUserById(id: Int): User? = withContext(Dispatchers.IO) {
        dbHelper.getUserById(id)
    }

    suspend fun insertTransaction(transaction: Transaction) = withContext(Dispatchers.IO) {
        dbHelper.insertTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: Transaction) = withContext(Dispatchers.IO) {
        dbHelper.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) = withContext(Dispatchers.IO) {
        dbHelper.deleteTransaction(transaction)
    }

    suspend fun insertAccount(account: Account) = withContext(Dispatchers.IO) {
        dbHelper.insertAccount(account)
    }

    suspend fun updateAccount(account: Account) = withContext(Dispatchers.IO) {
        dbHelper.updateAccount(account)
    }

    suspend fun deleteAccount(account: Account) = withContext(Dispatchers.IO) {
        dbHelper.deleteAccount(account)
    }

    suspend fun insertGoal(goal: Goal) = withContext(Dispatchers.IO) {
        dbHelper.insertGoal(goal)
    }

    suspend fun updateGoal(goal: Goal) = withContext(Dispatchers.IO) {
        dbHelper.updateGoal(goal)
    }

    suspend fun deleteGoal(goal: Goal) = withContext(Dispatchers.IO) {
        dbHelper.deleteGoal(goal)
    }

    suspend fun insertBudget(budget: Budget) = withContext(Dispatchers.IO) {
        dbHelper.insertBudget(budget)
    }

    suspend fun updateBudget(budget: Budget) = withContext(Dispatchers.IO) {
        dbHelper.updateBudget(budget)
    }

    suspend fun insertCategory(category: Category) = withContext(Dispatchers.IO) {
        dbHelper.insertCategory(category)
    }

    suspend fun getAccountById(id: Int): Account? = withContext(Dispatchers.IO) {
        dbHelper.getAccountById(id)
    }

    suspend fun getCategoryById(id: Int): Category? = withContext(Dispatchers.IO) {
        dbHelper.getCategoryById(id)
    }

    suspend fun searchTransactions(userId: Int, query: String): List<Transaction> = withContext(Dispatchers.IO) {
        dbHelper.searchTransactions(userId, query)
    }

    suspend fun insertRecurringTransaction(recurring: RecurringTransaction) = withContext(Dispatchers.IO) {
        dbHelper.insertRecurringTransaction(recurring)
    }

    suspend fun getAllRecurringTransactions(userId: Int): List<RecurringTransaction> = withContext(Dispatchers.IO) {
        dbHelper.getAllRecurringTransactions(userId)
    }

    suspend fun insertCurrency(currency: Currency) = withContext(Dispatchers.IO) {
        dbHelper.insertCurrency(currency)
    }

    suspend fun getCurrencyByCode(code: String): Currency? = withContext(Dispatchers.IO) {
        dbHelper.getCurrencyByCode(code)
    }
}
