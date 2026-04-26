# AGENTS.md

## FitTrack quick map
- Single-module Android app (`:app`) using Kotlin + XML views (no Compose, no DI, no data layer yet).
- Entry flow is login -> dashboard: `LoginActivity` launches `MainActivity`, then `MainActivity` mounts `DashboardFragment`.
- Most screens are currently static XML prototypes under `app/src/main/res/layout/`.
- Only one fragment is wired in code today: `DashboardFragment` (`app/src/main/java/com/fittrack/app/DashboardFragment.kt`).

## Architecture and data flow (current reality)
- App start is `LoginActivity` (`AndroidManifest.xml` has launcher activity).
- Username handoff pattern:
  - Activity-to-Activity via intent extra key `"EXTRA_USERNAME"` in `LoginActivity.kt` / `MainActivity.kt`.
  - Activity-to-Fragment via fragment factory + arguments key `"ARG_USERNAME"` in `DashboardFragment.newInstance(...)`.
- `MainActivity` uses a `FrameLayout` container (`R.id.fragment_container`) in `activity_main.xml`; bottom nav is an included static layout (`layout_bottom_nav.xml`), not interactive navigation yet.
- No repositories/ViewModels/network/database are present; state is passed directly via `Intent`/`Bundle`.

## UI and resource conventions to follow
- Visual system is tokenized in `res/values/colors.xml` and `res/values/dimens.xml`; prefer existing tokens over hardcoded values.
- Reusable styles are mostly drawable-backed cards/tags/buttons (`res/drawable/bg_*.xml`) plus `Theme.FitTrack` (`res/values/themes.xml`).
- Some strings are hardcoded in layouts despite `strings.xml` existing; when editing existing screens, preserve local style unless task explicitly asks for i18n cleanup.
- IDs/naming are straightforward and explicit (`tvGreeting`, `fragment_container`, `bottom_nav_include`); keep that style.

## Build/test/debug workflows
- Use Gradle wrapper from repo root on Windows:
  - `./gradlew.bat :app:assembleDebug`
  - `./gradlew.bat :app:testDebugUnitTest`
  - `./gradlew.bat :app:connectedDebugAndroidTest`
  - `./gradlew.bat :app:lintDebug`
- If Gradle fails early with Java version errors, fix `JAVA_HOME`/PATH first (local run showed Java 1.7, which is too old for this project tooling).
- Dependency versions come from `gradle/libs.versions.toml`; plugins are applied via aliases in top-level `build.gradle.kts` and `app/build.gradle.kts`.

## Integration points and change guardrails
- External deps are minimal: AndroidX core/appcompat/activity/constraintlayout + Material Components (`app/build.gradle.kts`).
- Avoid introducing new architecture layers unless requested; align with current small-app pattern.
- When adding a new interactive screen, wire it through `MainActivity` fragment transactions and keep keys/constants consistent with existing extra/argument naming.
- Keep module boundaries as-is (`:app` only) unless there is an explicit restructuring request.

