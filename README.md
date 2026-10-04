# Finanse Domowe

Aplikacja na Androida do pilnowania domowego budżetu. Można w niej prowadzić kilka profili
domowników i kont, zapisywać przychody i wydatki, ustawiać cele oszczędnościowe oraz płatności
cykliczne. Na wykresach widać saldo w czasie i strukturę wydatków. Przy okazji aplikacja
pobiera aktualne kursy walut z API NBP.

Całość jest napisana w Kotlinie z Jetpack Compose i trzyma dane lokalnie w Room.

## Uruchomienie

Najprościej otworzyć projekt w Android Studio i odpalić na emulatorze albo telefonie.
Z terminala:

```bash
./gradlew assembleDebug
```

## Architektura

Klasyczne MVVM. Ekrany w Compose (`ui/`, `MainActivity.kt`) czytają stan z `FinanceViewModel`,
a ten korzysta z `FinanceRepository`. Repozytorium opakowuje DAO z Rooma (`data/dao`, `data/entity`).
Transakcje cykliczne księguje w tle `RecurringTransactionWorker` (WorkManager). Kursy walut
przychodzą przez Retrofit z `api.nbp.pl` (`api/NbpApi.kt`).

Wykresy to MPAndroidChart osadzony w Compose przez `AndroidView`.

## Do poprawy

- Baza używa `fallbackToDestructiveMigration()`, więc zmiana schematu kasuje dane.
  Zanim aplikacja trafi do kogokolwiek, trzeba to zastąpić prawdziwymi migracjami.
- `MainActivity.kt` i `Screens.kt` są za duże. Ekrany warto rozbić na osobne pliki.
- Brakuje testów (przynajmniej dla repozytorium i ViewModelu).

## Licencja

MIT
