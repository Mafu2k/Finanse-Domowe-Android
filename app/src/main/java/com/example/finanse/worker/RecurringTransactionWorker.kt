package com.example.finanse.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.finanse.data.AppDatabase
import com.example.finanse.data.entity.Transaction
import com.example.finanse.data.repository.FinanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecurringTransactionWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val database = AppDatabase.getDatabase(applicationContext)
            val repository = FinanceRepository(database)

            // Pobierz wszystkich użytkowników i sprawdź ich transakcje cykliczne
            val users = repository.getAllUsers()

            users.forEach { user ->
                val recurringTransactions = repository.getAllRecurringTransactions(user.id)
                val currentTime = System.currentTimeMillis()

                recurringTransactions.forEach { recurring ->
                    // Sprawdź czy nadszedł czas na wykonanie transakcji
                    if (recurring.nastepnaData <= currentTime) {
                        // Dodaj nową transakcję
                        val transaction = Transaction(
                            userId = recurring.userId,
                            kwota = recurring.kwota,
                            typ = recurring.typ,
                            opis = recurring.opis,
                            kategoriaId = recurring.kategoriaId,
                            kontoId = recurring.kontoId,
                            data = currentTime
                        )
                        repository.insertTransaction(transaction)

                        // Zaktualizuj saldo konta
                        val account = repository.getAccountById(recurring.kontoId)
                        account?.let {
                            val newBalance = if (recurring.typ == "Przychód") {
                                it.balance + recurring.kwota
                            } else {
                                it.balance - recurring.kwota
                            }
                            repository.updateAccount(it.copy(balance = newBalance))
                        }

                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
