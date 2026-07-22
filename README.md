# Finanse Domowe

Aplikacja na Androida do zarządzania budżetem domowym — rejestrowanie przychodów
i wydatków, podgląd bieżącego salda oraz obsługa transakcji cyklicznych. Napisana
w Kotlinie z użyciem Jetpack Compose, z lokalną bazą Room i pobieraniem kursów walut
z API NBP.

## Funkcjonalności

- Dodawanie przychodów i wydatków
- Podgląd salda i historii transakcji
- Transakcje cykliczne obsługiwane w tle (WorkManager)
- Kursy walut pobierane z API Narodowego Banku Polskiego (NBP)
- Trwałe przechowywanie danych lokalnie (Room)
- Interfejs w Jetpack Compose z obsługą motywu jasnego i ciemnego

## Stack

- Kotlin
- Jetpack Compose (UI deklaratywne)
- Room (lokalna baza danych, DAO + encje)
- Architektura MVVM (ViewModel + repozytorium)
- WorkManager (transakcje cykliczne w tle)
- API NBP (kursy walut)
- Gradle (Kotlin DSL)

## Architektura

```
com.example.finanse/
├── MainActivity.kt
├── api/            # NbpApi — kursy walut z API NBP
├── data/
│   ├── AppDatabase.kt, DatabaseHelper.kt
│   ├── dao/        # Daos.kt — zapytania Room
│   ├── entity/     # Entities.kt — encje bazodanowe
│   └── repository/ # FinanceRepository
├── viewmodel/      # FinanceViewModel
├── worker/         # RecurringTransactionWorker — transakcje cykliczne
└── ui/             # Screens.kt + theme (kolory, typografia)
```

## Uruchomienie

Wymagania: Android Studio (aktualna wersja) oraz SDK Androida.

Otwórz projekt w Android Studio i uruchom na emulatorze lub urządzeniu przyciskiem
**Run**. Alternatywnie z konsoli:

```bash
./gradlew assembleDebug
```

## Autor

Łukasz Janicki

## Licencja

MIT — szczegóły w pliku [LICENSE](LICENSE).
