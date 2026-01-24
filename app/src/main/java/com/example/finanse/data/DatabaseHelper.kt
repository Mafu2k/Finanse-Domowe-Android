package com.example.finanse.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.finanse.data.entity.*

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "FinanseDB"
        private const val DB_VERSION = 2

        @Volatile
        private var INSTANCE: DatabaseHelper? = null

        fun getDatabase(context: Context): DatabaseHelper {
            return INSTANCE ?: synchronized(this) {
                val instance = DatabaseHelper(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Users table
        db.execSQL("""
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                pin TEXT,
                is_biometric_enabled INTEGER DEFAULT 0
            )
        """)

        // Accounts table
        db.execSQL("""
            CREATE TABLE accounts (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                name TEXT NOT NULL,
                balance REAL NOT NULL,
                currency TEXT NOT NULL,
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
            )
        """)

        // Transactions table
        db.execSQL("""
            CREATE TABLE transakcje (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                kwota REAL NOT NULL,
                typ TEXT NOT NULL,
                data INTEGER NOT NULL,
                opis TEXT NOT NULL,
                kategoria_id INTEGER NOT NULL,
                konto_id INTEGER NOT NULL,
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                FOREIGN KEY (kategoria_id) REFERENCES kategorie(id),
                FOREIGN KEY (konto_id) REFERENCES accounts(id)
            )
        """)

        // Categories table
        db.execSQL("""
            CREATE TABLE kategorie (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER DEFAULT 0,
                name TEXT NOT NULL,
                icon TEXT,
                color TEXT
            )
        """)

        // Budget table
        db.execSQL("""
            CREATE TABLE budzet (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                kategoria_id INTEGER NOT NULL,
                miesiac TEXT NOT NULL,
                plan REAL NOT NULL,
                fakty REAL NOT NULL,
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                FOREIGN KEY (kategoria_id) REFERENCES kategorie(id)
            )
        """)

        // Goals table
        db.execSQL("""
            CREATE TABLE cele (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                name TEXT NOT NULL,
                cel REAL NOT NULL,
                aktual REAL NOT NULL,
                termin TEXT NOT NULL,
                priorytet TEXT NOT NULL,
                kategoria_id INTEGER DEFAULT 0,
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
            )
        """)

        // Recurring transactions table
        db.execSQL("""
            CREATE TABLE cykliczne (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                kwota REAL NOT NULL,
                typ TEXT NOT NULL,
                opis TEXT NOT NULL,
                kategoria_id INTEGER NOT NULL,
                konto_id INTEGER NOT NULL,
                czestotliwosc TEXT NOT NULL,
                nastepna_data INTEGER NOT NULL,
                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
            )
        """)

        // Currencies table
        db.execSQL("""
            CREATE TABLE waluty (
                code TEXT PRIMARY KEY,
                rate REAL NOT NULL,
                name TEXT NOT NULL
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS users")
        db.execSQL("DROP TABLE IF EXISTS accounts")
        db.execSQL("DROP TABLE IF EXISTS transakcje")
        db.execSQL("DROP TABLE IF EXISTS kategorie")
        db.execSQL("DROP TABLE IF EXISTS budzet")
        db.execSQL("DROP TABLE IF EXISTS cele")
        db.execSQL("DROP TABLE IF EXISTS cykliczne")
        db.execSQL("DROP TABLE IF EXISTS waluty")
        onCreate(db)
    }

    // User operations
    fun insertUser(user: User): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", user.name)
            put("pin", user.pin)
            put("is_biometric_enabled", if (user.isBiometricEnabled) 1 else 0)
        }
        return db.insert("users", null, values)
    }

    fun getAllUsers(): List<User> {
        val db = readableDatabase
        val users = mutableListOf<User>()
        val cursor = db.query("users", null, null, null, null, null, null)

        cursor.use {
            while (it.moveToNext()) {
                users.add(cursorToUser(it))
            }
        }
        return users
    }

    fun getUserById(id: Int): User? {
        val db = readableDatabase
        val cursor = db.query("users", null, "id = ?", arrayOf(id.toString()), null, null, null)

        cursor.use {
            if (it.moveToFirst()) {
                return cursorToUser(it)
            }
        }
        return null
    }

    // Account operations
    fun insertAccount(account: Account): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("user_id", account.userId)
            put("name", account.name)
            put("balance", account.balance)
            put("currency", account.currency)
        }
        return db.insert("accounts", null, values)
    }

    fun updateAccount(account: Account): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("user_id", account.userId)
            put("name", account.name)
            put("balance", account.balance)
            put("currency", account.currency)
        }
        return db.update("accounts", values, "id = ?", arrayOf(account.id.toString()))
    }

    fun deleteAccount(account: Account): Int {
        val db = writableDatabase
        return db.delete("accounts", "id = ?", arrayOf(account.id.toString()))
    }

    fun getAllAccounts(userId: Int): List<Account> {
        val db = readableDatabase
        val accounts = mutableListOf<Account>()
        val cursor = db.query("accounts", null, "user_id = ?", arrayOf(userId.toString()), null, null, null)

        cursor.use {
            while (it.moveToNext()) {
                accounts.add(cursorToAccount(it))
            }
        }
        return accounts
    }

    fun getAccountById(id: Int): Account? {
        val db = readableDatabase
        val cursor = db.query("accounts", null, "id = ?", arrayOf(id.toString()), null, null, null)

        cursor.use {
            if (it.moveToFirst()) {
                return cursorToAccount(it)
            }
        }
        return null
    }

    // Transaction operations
    fun insertTransaction(transaction: Transaction): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("user_id", transaction.userId)
            put("kwota", transaction.kwota)
            put("typ", transaction.typ)
            put("data", transaction.data)
            put("opis", transaction.opis)
            put("kategoria_id", transaction.kategoriaId)
            put("konto_id", transaction.kontoId)
        }
        return db.insert("transakcje", null, values)
    }

    fun updateTransaction(transaction: Transaction): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("user_id", transaction.userId)
            put("kwota", transaction.kwota)
            put("typ", transaction.typ)
            put("data", transaction.data)
            put("opis", transaction.opis)
            put("kategoria_id", transaction.kategoriaId)
            put("konto_id", transaction.kontoId)
        }
        return db.update("transakcje", values, "id = ?", arrayOf(transaction.id.toString()))
    }

    fun deleteTransaction(transaction: Transaction): Int {
        val db = writableDatabase
        return db.delete("transakcje", "id = ?", arrayOf(transaction.id.toString()))
    }

    fun getAllTransactions(userId: Int): List<Transaction> {
        val db = readableDatabase
        val transactions = mutableListOf<Transaction>()
        val cursor = db.query("transakcje", null, "user_id = ?", arrayOf(userId.toString()), null, null, "data DESC")

        cursor.use {
            while (it.moveToNext()) {
                transactions.add(cursorToTransaction(it))
            }
        }
        return transactions
    }

    fun searchTransactions(userId: Int, query: String): List<Transaction> {
        val db = readableDatabase
        val transactions = mutableListOf<Transaction>()
        val cursor = db.query(
            "transakcje",
            null,
            "user_id = ? AND (opis LIKE ? OR kwota LIKE ?)",
            arrayOf(userId.toString(), "%$query%", "%$query%"),
            null, null, "data DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                transactions.add(cursorToTransaction(it))
            }
        }
        return transactions
    }

    // Category operations
    fun insertCategory(category: Category): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("user_id", category.userId)
            put("name", category.name)
            put("icon", category.icon)
            put("color", category.color)
        }
        return db.insert("kategorie", null, values)
    }

    fun getAllCategories(userId: Int): List<Category> {
        val db = readableDatabase
        val categories = mutableListOf<Category>()
        val cursor = db.query("kategorie", null, "user_id = ? OR user_id = 0", arrayOf(userId.toString()), null, null, null)

        cursor.use {
            while (it.moveToNext()) {
                categories.add(cursorToCategory(it))
            }
        }
        return categories
    }

    fun getCategoryById(id: Int): Category? {
        val db = readableDatabase
        val cursor = db.query("kategorie", null, "id = ?", arrayOf(id.toString()), null, null, null)

        cursor.use {
            if (it.moveToFirst()) {
                return cursorToCategory(it)
            }
        }
        return null
    }

    // Budget operations
    fun insertBudget(budget: Budget): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("user_id", budget.userId)
            put("kategoria_id", budget.kategoriaId)
            put("miesiac", budget.miesiac)
            put("plan", budget.plan)
            put("fakty", budget.fakty)
        }
        return db.insert("budzet", null, values)
    }

    fun updateBudget(budget: Budget): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("user_id", budget.userId)
            put("kategoria_id", budget.kategoriaId)
            put("miesiac", budget.miesiac)
            put("plan", budget.plan)
            put("fakty", budget.fakty)
        }
        return db.update("budzet", values, "id = ?", arrayOf(budget.id.toString()))
    }

    fun getAllBudgets(userId: Int): List<Budget> {
        val db = readableDatabase
        val budgets = mutableListOf<Budget>()
        val cursor = db.query("budzet", null, "user_id = ?", arrayOf(userId.toString()), null, null, null)

        cursor.use {
            while (it.moveToNext()) {
                budgets.add(cursorToBudget(it))
            }
        }
        return budgets
    }

    // Goal operations
    fun insertGoal(goal: Goal): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("user_id", goal.userId)
            put("name", goal.name)
            put("cel", goal.cel)
            put("aktual", goal.aktual)
            put("termin", goal.termin)
            put("priorytet", goal.priorytet)
            put("kategoria_id", goal.kategoriaId)
        }
        return db.insert("cele", null, values)
    }

    fun updateGoal(goal: Goal): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("user_id", goal.userId)
            put("name", goal.name)
            put("cel", goal.cel)
            put("aktual", goal.aktual)
            put("termin", goal.termin)
            put("priorytet", goal.priorytet)
            put("kategoria_id", goal.kategoriaId)
        }
        return db.update("cele", values, "id = ?", arrayOf(goal.id.toString()))
    }

    fun deleteGoal(goal: Goal): Int {
        val db = writableDatabase
        return db.delete("cele", "id = ?", arrayOf(goal.id.toString()))
    }

    fun getAllGoals(userId: Int): List<Goal> {
        val db = readableDatabase
        val goals = mutableListOf<Goal>()
        val cursor = db.query("cele", null, "user_id = ?", arrayOf(userId.toString()), null, null, null)

        cursor.use {
            while (it.moveToNext()) {
                goals.add(cursorToGoal(it))
            }
        }
        return goals
    }

    // Recurring transaction operations
    fun insertRecurringTransaction(recurring: RecurringTransaction): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("user_id", recurring.userId)
            put("kwota", recurring.kwota)
            put("typ", recurring.typ)
            put("opis", recurring.opis)
            put("kategoria_id", recurring.kategoriaId)
            put("konto_id", recurring.kontoId)
            put("czestotliwosc", recurring.czestotliwosc)
            put("nastepna_data", recurring.nastepnaData)
        }
        return db.insert("cykliczne", null, values)
    }

    fun getAllRecurringTransactions(userId: Int): List<RecurringTransaction> {
        val db = readableDatabase
        val recurring = mutableListOf<RecurringTransaction>()
        val cursor = db.query("cykliczne", null, "user_id = ?", arrayOf(userId.toString()), null, null, null)

        cursor.use {
            while (it.moveToNext()) {
                recurring.add(cursorToRecurringTransaction(it))
            }
        }
        return recurring
    }

    // Currency operations
    fun insertCurrency(currency: Currency): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("code", currency.code)
            put("rate", currency.rate)
            put("name", currency.name)
        }
        return db.insertWithOnConflict("waluty", null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getAllCurrencies(): List<Currency> {
        val db = readableDatabase
        val currencies = mutableListOf<Currency>()
        val cursor = db.query("waluty", null, null, null, null, null, null)

        cursor.use {
            while (it.moveToNext()) {
                currencies.add(cursorToCurrency(it))
            }
        }
        return currencies
    }

    fun getCurrencyByCode(code: String): Currency? {
        val db = readableDatabase
        val cursor = db.query("waluty", null, "code = ?", arrayOf(code), null, null, null)

        cursor.use {
            if (it.moveToFirst()) {
                return cursorToCurrency(it)
            }
        }
        return null
    }

    // Helper functions to convert Cursor to entities
    private fun cursorToUser(cursor: Cursor): User {
        return User(
            id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
            name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
            pin = cursor.getString(cursor.getColumnIndexOrThrow("pin")),
            isBiometricEnabled = cursor.getInt(cursor.getColumnIndexOrThrow("is_biometric_enabled")) == 1
        )
    }

    private fun cursorToAccount(cursor: Cursor): Account {
        return Account(
            id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
            userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
            name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
            balance = cursor.getDouble(cursor.getColumnIndexOrThrow("balance")),
            currency = cursor.getString(cursor.getColumnIndexOrThrow("currency"))
        )
    }

    private fun cursorToTransaction(cursor: Cursor): Transaction {
        return Transaction(
            id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
            userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
            kwota = cursor.getDouble(cursor.getColumnIndexOrThrow("kwota")),
            typ = cursor.getString(cursor.getColumnIndexOrThrow("typ")),
            data = cursor.getLong(cursor.getColumnIndexOrThrow("data")),
            opis = cursor.getString(cursor.getColumnIndexOrThrow("opis")),
            kategoriaId = cursor.getInt(cursor.getColumnIndexOrThrow("kategoria_id")),
            kontoId = cursor.getInt(cursor.getColumnIndexOrThrow("konto_id"))
        )
    }

    private fun cursorToCategory(cursor: Cursor): Category {
        return Category(
            id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
            userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
            name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
            icon = cursor.getString(cursor.getColumnIndexOrThrow("icon")),
            color = cursor.getString(cursor.getColumnIndexOrThrow("color"))
        )
    }

    private fun cursorToBudget(cursor: Cursor): Budget {
        return Budget(
            id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
            userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
            kategoriaId = cursor.getInt(cursor.getColumnIndexOrThrow("kategoria_id")),
            miesiac = cursor.getString(cursor.getColumnIndexOrThrow("miesiac")),
            plan = cursor.getDouble(cursor.getColumnIndexOrThrow("plan")),
            fakty = cursor.getDouble(cursor.getColumnIndexOrThrow("fakty"))
        )
    }

    private fun cursorToGoal(cursor: Cursor): Goal {
        return Goal(
            id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
            userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
            name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
            cel = cursor.getDouble(cursor.getColumnIndexOrThrow("cel")),
            aktual = cursor.getDouble(cursor.getColumnIndexOrThrow("aktual")),
            termin = cursor.getString(cursor.getColumnIndexOrThrow("termin")),
            priorytet = cursor.getString(cursor.getColumnIndexOrThrow("priorytet")),
            kategoriaId = cursor.getInt(cursor.getColumnIndexOrThrow("kategoria_id"))
        )
    }

    private fun cursorToRecurringTransaction(cursor: Cursor): RecurringTransaction {
        return RecurringTransaction(
            id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
            userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
            kwota = cursor.getDouble(cursor.getColumnIndexOrThrow("kwota")),
            typ = cursor.getString(cursor.getColumnIndexOrThrow("typ")),
            opis = cursor.getString(cursor.getColumnIndexOrThrow("opis")),
            kategoriaId = cursor.getInt(cursor.getColumnIndexOrThrow("kategoria_id")),
            kontoId = cursor.getInt(cursor.getColumnIndexOrThrow("konto_id")),
            czestotliwosc = cursor.getString(cursor.getColumnIndexOrThrow("czestotliwosc")),
            nastepnaData = cursor.getLong(cursor.getColumnIndexOrThrow("nastepna_data"))
        )
    }

    private fun cursorToCurrency(cursor: Cursor): Currency {
        return Currency(
            code = cursor.getString(cursor.getColumnIndexOrThrow("code")),
            rate = cursor.getDouble(cursor.getColumnIndexOrThrow("rate")),
            name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
        )
    }
}
