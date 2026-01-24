package com.example.finanse.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.finanse.data.entity.Account
import com.example.finanse.data.entity.Budget
import com.example.finanse.data.entity.Category
import com.example.finanse.data.entity.Currency
import com.example.finanse.data.entity.Goal
import com.example.finanse.data.entity.RecurringTransaction
import com.example.finanse.data.entity.Transaction
import com.example.finanse.data.entity.User

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User): Long

    @Query("SELECT * FROM users")
    suspend fun getAll(): List<User>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): User?
}

@Dao
interface AccountDao {
    @Insert
    suspend fun insert(account: Account): Long

    @Update
    suspend fun update(account: Account): Int

    @Delete
    suspend fun delete(account: Account): Int

    @Query("SELECT * FROM accounts WHERE user_id = :userId")
    suspend fun getAllByUserId(userId: Int): List<Account>

    @Query("SELECT * FROM accounts WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Account?
}

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: Transaction): Long

    @Update
    suspend fun update(transaction: Transaction): Int

    @Delete
    suspend fun delete(transaction: Transaction): Int

    @Query("SELECT * FROM transakcje WHERE user_id = :userId ORDER BY data DESC")
    suspend fun getAllByUserId(userId: Int): List<Transaction>

    @Query(
        "SELECT * FROM transakcje " +
            "WHERE user_id = :userId AND (opis LIKE :query OR CAST(kwota AS TEXT) LIKE :query) " +
            "ORDER BY data DESC"
    )
    suspend fun searchByUserId(userId: Int, query: String): List<Transaction>
}

@Dao
interface CategoryDao {
    @Insert
    suspend fun insert(category: Category): Long

    @Query("SELECT * FROM kategorie WHERE user_id = :userId OR user_id = 0")
    suspend fun getAllByUserId(userId: Int): List<Category>

    @Query("SELECT * FROM kategorie WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Category?
}

@Dao
interface BudgetDao {
    @Insert
    suspend fun insert(budget: Budget): Long

    @Update
    suspend fun update(budget: Budget): Int

    @Query("SELECT * FROM budzet WHERE user_id = :userId")
    suspend fun getAllByUserId(userId: Int): List<Budget>
}

@Dao
interface GoalDao {
    @Insert
    suspend fun insert(goal: Goal): Long

    @Update
    suspend fun update(goal: Goal): Int

    @Delete
    suspend fun delete(goal: Goal): Int

    @Query("SELECT * FROM cele WHERE user_id = :userId")
    suspend fun getAllByUserId(userId: Int): List<Goal>
}

@Dao
interface RecurringTransactionDao {
    @Insert
    suspend fun insert(recurring: RecurringTransaction): Long

    @Query("SELECT * FROM cykliczne WHERE user_id = :userId")
    suspend fun getAllByUserId(userId: Int): List<RecurringTransaction>
}

@Dao
interface CurrencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(currency: Currency): Long

    @Query("SELECT * FROM waluty")
    suspend fun getAll(): List<Currency>

    @Query("SELECT * FROM waluty WHERE code = :code LIMIT 1")
    suspend fun getByCode(code: String): Currency?
}
