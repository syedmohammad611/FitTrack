# FitTrack

FitTrack is an Android workout logging and exercise analytics app for tracking gym progress over time. It helps users record training sessions, review history, and stay motivated with dynamic content.

## Developers

| Name | Roll Number |
|---|---|
| Maidah Nasir | 23F-0764 |
| Syed Muhammad | 23F-0559 |
| Zonia Amer | 23F-0801 |
| Zahid Khalil | 22F-3573 |

## What You Can Do in FitTrack

- Sign in with a username and enter the main app experience.
- Navigate between dashboard, workout history, and motivation screens without restarting the activity.
- View workout sessions in a vertical list powered by `RecyclerView`.
- Add, edit, and delete workout sessions that persist locally.
- Search workout history in real time (case-insensitive) and clear filters instantly.
- Browse motivational quotes fetched from a live public API.

## Core App Flow

1. `LoginActivity` collects the username.
2. Username is passed to `MainActivity` through `Intent` extra (`EXTRA_USERNAME`).
3. `MainActivity` acts as a container and switches fragments inside `R.id.fragment_container`.
4. User context is passed from activity to fragments through bundle arguments (`ARG_USERNAME`).

## Data and Functionality Overview

### Local Data (SQLite)
- Uses `SQLiteOpenHelper` for local persistence.
- Stores users, workout sessions, and exercise data with relational links.
- Supports full CRUD operations for workout sessions.
- Data remains available after app restarts.

### Remote Data (REST API)
- Uses Retrofit + Gson to fetch quote data from a public endpoint.
- Displays API responses in `RecyclerView` on the motivation screen.
- Network calls run on background threads using Kotlin coroutines.

### Search and Filtering
- Workout history supports live filtering by workout name.
- Filtering is handled in the adapter using an original list + filtered list pattern.
- A clear action resets results to the full dataset.

## Tech Stack

- **Language:** Kotlin
- **UI:** XML layouts (Android Views)
- **Navigation Pattern:** Fragment-first (activities as top-level containers)
- **Lists:** `RecyclerView` + custom `Adapter` + `ViewHolder`
- **Persistence:** SQLite via `SQLiteOpenHelper`
- **Networking:** Retrofit + Gson
- **Build:** Gradle (Kotlin DSL)

## Project Structure

```text
FitTrack/
  app/
    src/main/
      java/com/fittrack/app/
        activities/
        fragments/
        adapters/
        models/
        data/
      res/
        layout/
        drawable/
        values/
```

## Getting Started

### Prerequisites
- JDK 17
- Android Studio (latest stable recommended)
- Android SDK installed

### Build Commands

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:connectedDebugAndroidTest
.\gradlew.bat :app:lintDebug
```

## UI and Design Conventions

- Reuse existing style tokens from `app/src/main/res/values/colors.xml` and `app/src/main/res/values/dimens.xml`.
- Keep XML-based layouts consistent with existing component styling.
- Use explicit view IDs following current patterns (`tv...`, `btn...`, `et...`, `rv...`).

## Roadmap

- Expand exercise catalog and set-level logging.
- Add richer analytics for training volume and performance trends.
- Introduce optional offline caching for remote motivation content.
- Improve validation, error feedback, and test coverage.

## License

This project is developed for academic purposes.
