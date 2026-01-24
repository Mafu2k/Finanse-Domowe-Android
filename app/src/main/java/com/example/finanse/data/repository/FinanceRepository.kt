package com.example.finanse.data.repository

import com.example.finanse.data.AppDatabase
import com.example.finanse.data.entity.Account
import com.example.finanse.data.entity.Budget
import com.example.finanse.data.entity.Category
import com.example.finanse.data.entity.Currency
import com.example.finanse.data.entity.Goal
import com.example.finanse.data.entity.RecurringTransaction
import com.example.finanse.data.entity.Transaction
import com.example.finanse.data.entity.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FinanceRepository(private val database: AppDatabase) {

    private val userDao = database.userDao()
    private val accountDao = database.accountDao()
    private val transactionDao = database.transactionDao()
    private val categoryDao = database.categoryDao()
    private val budgetDao = database.budgetDao()
    private val goalDao = database.goalDao()
    private val recurringTransactionDao = database.recurringTransactionDao()
    private val currencyDao = database.currencyDao()

    suspend fun getAllTransactions(userId: Int): List<Transaction> = withContext(Dispatchers.IO) {
        transactionDao.getAllByUserId(userId)
    }

    suspend fun getAllAccounts(userId: Int): List<Account> = withContext(Dispatchers.IO) {
        accountDao.getAllByUserId(userId)
    }

    suspend fun getAllCategories(userId: Int): List<Category> = withContext(Dispatchers.IO) {
        categoryDao.getAllByUserId(userId)
    }

    suspend fun getAllBudgets(userId: Int): List<Budget> = withContext(Dispatchers.IO) {
        budgetDao.getAllByUserId(userId)
    }

    suspend fun getAllGoals(userId: Int): List<Goal> = withContext(Dispatchers.IO) {
        goalDao.getAllByUserId(userId)
    }

    suspend fun getAllUsers(): List<User> = withContext(Dispatchers.IO) {
        userDao.getAll()
    }

    suspend fun getAllCurrencies(): List<Currency> = withContext(Dispatchers.IO) {
        currencyDao.getAll()
    }

    suspend fun insertUser(user: User): Long = withContext(Dispatchers.IO) {
        userDao.insert(user)
    }

    suspend fun getUserById(id: Int): User? = withContext(Dispatchers.IO) {
        userDao.getById(id)
    }

    suspend fun insertTransaction(transaction: Transaction) = withContext(Dispatchers.IO) {
        transactionDao.insert(transaction)
    }

    suspend fun updateTransaction(transaction: Transaction) = withContext(Dispatchers.IO) {
        transactionDao.update(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) = withContext(Dispatchers.IO) {
        transactionDao.delete(transaction)
    }

    suspend fun insertAccount(account: Account) = withContext(Dispatchers.IO) {
        accountDao.insert(account)
    }

    suspend fun updateAccount(account: Account) = withContext(Dispatchers.IO) {
        accountDao.update(account)
    }

    suspend fun deleteAccount(account: Account) = withContext(Dispatchers.IO) {
        accountDao.delete(account)
    }

    suspend fun insertGoal(goal: Goal) = withContext(Dispatchers.IO) {
        goalDao.insert(goal)
    }

    suspend fun updateGoal(goal: Goal) = withContext(Dispatchers.IO) {
        goalDao.update(goal)
    }

    suspend fun deleteGoal(goal: Goal) = withContext(Dispatchers.IO) {
        goalDao.delete(goal)
    }

    suspend fun insertBudget(budget: Budget) = withContext(Dispatchers.IO) {
        budgetDao.insert(budget)
    }

    suspend fun updateBudget(budget: Budget) = withContext(Dispatchers.IO) {
        budgetDao.update(budget)
    }

    suspend fun insertCategory(category: Category) = withContext(Dispatchers.IO) {
        categoryDao.insert(category)
    }

    suspend fun getAccountById(id: Int): Account? = withContext(Dispatchers.IO) {
        accountDao.getById(id)
    }

    suspend fun getCategoryById(id: Int): Category? = withContext(Dispatchers.IO) {
        categoryDao.getById(id)
    }

    suspend fun searchTransactions(userId: Int, query: String): List<Transaction> = withContext(Dispatchers.IO) {
        transactionDao.searchByUserId(userId, "%$query%")
    }

    suspend fun insertRecurringTransaction(recurring: RecurringTransaction) = withContext(Dispatchers.IO) {
        recurringTransactionDao.insert(recurring)
    }

    suspend fun getAllRecurringTransactions(userId: Int): List<RecurringTransaction> = withContext(Dispatchers.IO) {
        recurringTransactionDao.getAllByUserId(userId)
    }

    suspend fun insertCurrency(currency: Currency) = withContext(Dispatchers.IO) {
        currencyDao.insert(currency)
    }

    suspend fun getCurrencyByCode(code: String): Currency? = withContext(Dispatchers.IO) {
        currencyDao.getByCode(code)
    }
}
