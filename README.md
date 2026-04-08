# FitTrack

FitTrack is an Android-based workout logging and exercise analytics application that allows users to manually record gym sessions. Users can select exercises, enter reps and weights for each set, and track complete workout history using local storage. The app is designed to calculate training volume, track total session time, record Personal Records (PRs), and visualize progress through charts and analytics.

---

## Developers

| Name | Roll Number |
|---|---|
| Maidah Nasir | 23F-0764 |
| Syed Muhammad | 23F-0559 |
| Zonia Amer | 23F-0801 |
| Zahid Khalil | 22F-3573 |

---

## Project Overview

FitTrack is developed as a modular Android application following fragment-first UI design and assignment-driven Android architecture practices. The project focuses on:

- Converting static XML interfaces into functional app screens
- Building smooth navigation using Activities and Fragments
- Passing data strictly via `Intent Extras` and `Bundles`
- Displaying dynamic list data using `RecyclerView` with custom `Adapter` and `ViewHolder`
- Implementing search/filter operations for better usability

---

## Objectives

- Build a data-driven fitness tracking app
- Enable seamless inter-screen communication between Activities and Fragments
- Maintain clean folder structure and separation of concerns
- Support scalable development for future analytics and persistence features

---

## Implemented / Target Features

### 1) User Login and Navigation
- User enters username on login screen
- Username is passed to main container via `Intent` extra
- Main screen loads fragments without restarting activity

### 2) Dashboard and Workout Insights
- Dashboard fragment displays user-facing workout summary widgets
- Designed for extension into real-time training metrics

### 3) Workout History List
- Vertical workout list implemented using `RecyclerView`
- Custom row item displays workout metadata (date, duration, volume, workout title)
- Adapter + ViewHolder architecture used for clean and reusable binding

### 4) Fragment Transactions (F4)
- App switches between fragments (e.g., Dashboard and History) inside `MainActivity`
- No activity restart; navigation is handled through fragment replacement

### 5) Search / Filter (F5)
- Live search filters workout sessions by workout name
- Case-insensitive matching
- Clear action restores complete list instantly

### 6) Analytics Scope (Project Vision)
- Training volume calculations
- Session duration tracking
- Personal Record (PR) tracking
- Progress visualization with charts

> Note: Some analytics features may be in-progress depending on current milestone.

---

## Architecture

### Tech Stack
- **Language:** Kotlin
- **UI:** XML layouts (Android Views, no Compose)
- **Architecture Style:** Fragment-first, Activity as container/navigation coordinator
- **List Rendering:** RecyclerView + Adapter + ViewHolder
- **Data Passing:** Intent Extras + Bundles
- **Build System:** Gradle (Kotlin DSL)
- **Min SDK / Target SDK:** As defined in `app/build.gradle.kts`

### Current High-Level Flow
1. `LoginActivity` receives username input  
2. Username sent to `MainActivity` using `EXTRA_USERNAME`  
3. `MainActivity` opens fragments in `fragment_container`  
4. Username passed to fragments through bundle arguments (`ARG_USERNAME`)  
5. History screen renders workout sessions and applies search filter in real time

---

## Folder Structure

```text
FitTrack/
  app/
    src/main/
      java/com/fittrack/app/
        activities/
          LoginActivity.kt
          MainActivity.kt
        fragments/
          DashboardFragment.kt
          HistoryFragment.kt
          WorkoutDetailFragment.kt
        adapters/
          WorkoutSessionAdapter.kt
        models/
          WorkoutSession.kt
      res/
        layout/
        drawable/
        values/
```



### Prerequisites
- JDK 17
- Android Studio (latest stable recommended)
- Android SDK installed


### Other Useful Commands

```powershell
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:connectedDebugAndroidTest
.\gradlew.bat :app:lintDebug
```

---

## UI and Design Conventions

- Reuse existing tokens from:
  - `res/values/colors.xml`
  - `res/values/dimens.xml`
- Follow existing ID naming style (`tv...`, `btn...`, `et...`, `rv...`)
- Keep layouts XML-based and consistent with current visual system

---

## Future Enhancements

- Integrate Room database for persistent workout storage
- Add exercise catalog and set-level logging
- Implement PR detection logic and trend analytics
- Introduce charts for weekly/monthly progression
- Add edit/delete session functionality
- Add input validation and richer UX feedback

---


## License

This project is developed for academic purposes as part of an Android development assignment.
