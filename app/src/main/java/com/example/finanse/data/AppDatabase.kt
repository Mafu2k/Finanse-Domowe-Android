package com.example.finanse.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.finanse.data.dao.AccountDao
import com.example.finanse.data.dao.BudgetDao
import com.example.finanse.data.dao.CategoryDao
import com.example.finanse.data.dao.CurrencyDao
import com.example.finanse.data.dao.GoalDao
import com.example.finanse.data.dao.RecurringTransactionDao
import com.example.finanse.data.dao.TransactionDao
import com.example.finanse.data.dao.UserDao
import com.example.finanse.data.entity.Account
import com.example.finanse.data.entity.Budget
import com.example.finanse.data.entity.Category
import com.example.finanse.data.entity.Currency
import com.example.finanse.data.entity.Goal
import com.example.finanse.data.entity.RecurringTransaction
import com.example.finanse.data.entity.Transaction
import com.example.finanse.data.entity.User

@Database(
    entities = [
        User::class,
        Account::class,
        Transaction::class,
        Category::class,
        Budget::class,
        Goal::class,
        RecurringTransaction::class,
        Currency::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun goalDao(): GoalDao
    abstract fun recurringTransactionDao(): RecurringTransactionDao
    abstract fun currencyDao(): CurrencyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "FinanseDB"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
