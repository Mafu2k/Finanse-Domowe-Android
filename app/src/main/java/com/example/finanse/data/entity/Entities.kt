package com.example.finanse.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val pin: String?,
    @ColumnInfo(name = "is_biometric_enabled")
    val isBiometricEnabled: Boolean = false
)

@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "user_id")
    val userId: Int,
    val name: String,
    val balance: Double,
    val currency: String
)

@Entity(tableName = "transakcje")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "user_id")
    val userId: Int,
    val kwota: Double,
    val typ: String,
    val data: Long,
    val opis: String,
    @ColumnInfo(name = "kategoria_id")
    val kategoriaId: Int,
    @ColumnInfo(name = "konto_id")
    val kontoId: Int
)

@Entity(tableName = "kategorie")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "user_id")
    val userId: Int = 0,
    val name: String,
    val icon: String? = null,
    val color: String? = null
)

@Entity(tableName = "budzet")
data class Budget(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "user_id")
    val userId: Int,
    @ColumnInfo(name = "kategoria_id")
    val kategoriaId: Int,
    val miesiac: String,
    val plan: Double,
    val fakty: Double
)

@Entity(tableName = "cele")
data class Goal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "user_id")
    val userId: Int,
    val name: String,
    val cel: Double,
    val aktual: Double,
    val termin: String,
    val priorytet: String,
    @ColumnInfo(name = "kategoria_id")
    val kategoriaId: Int = 0
)

@Entity(tableName = "cykliczne")
data class RecurringTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "user_id")
    val userId: Int,
    val kwota: Double,
    val typ: String,
    val opis: String,
    @ColumnInfo(name = "kategoria_id")
    val kategoriaId: Int,
    @ColumnInfo(name = "konto_id")
    val kontoId: Int,
    val czestotliwosc: String,
    @ColumnInfo(name = "nastepna_data")
    val nastepnaData: Long
)

@Entity(tableName = "waluty")
data class Currency(
    @PrimaryKey
    val code: String,
    val rate: Double,
    val name: String
)
