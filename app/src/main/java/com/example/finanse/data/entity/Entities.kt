package com.example.finanse.data.entity

data class User(
    val id: Int = 0,
    val name: String,
    val pin: String?,
    val isBiometricEnabled: Boolean = false
)

data class Account(
    val id: Int = 0,
    val userId: Int,
    val name: String,
    val balance: Double,
    val currency: String
)

data class Transaction(
    val id: Int = 0,
    val userId: Int,
    val kwota: Double,
    val typ: String,
    val data: Long,
    val opis: String,
    val kategoriaId: Int,
    val kontoId: Int
)

data class Category(
    val id: Int = 0,
    val userId: Int = 0,
    val name: String,
    val icon: String? = null,
    val color: String? = null
)

data class Budget(
    val id: Int = 0,
    val userId: Int,
    val kategoriaId: Int,
    val miesiac: String,
    val plan: Double,
    val fakty: Double
)

data class Goal(
    val id: Int = 0,
    val userId: Int,
    val name: String,
    val cel: Double,
    val aktual: Double,
    val termin: String,
    val priorytet: String,
    val kategoriaId: Int = 0
)

data class RecurringTransaction(
    val id: Int = 0,
    val userId: Int,
    val kwota: Double,
    val typ: String,
    val opis: String,
    val kategoriaId: Int,
    val kontoId: Int,
    val czestotliwosc: String,
    val nastepnaData: Long
)

data class Currency(
    val code: String,
    val rate: Double,
    val name: String
)
