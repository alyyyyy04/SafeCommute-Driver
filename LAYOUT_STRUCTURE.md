# Where the layouts are

This app uses **Jetpack Compose** (no XML layout files). Each screen is in its **own file**.

## Screen layout files (one file per screen)

| Screen          | File |
|-----------------|------|
| **Login**       | `app/src/main/java/com/example/safecommute_driver/ui/screens/LoginScreen.kt` |
| **Home**        | `app/src/main/java/com/example/safecommute_driver/ui/screens/DriverHomeScreen.kt` |
| **Profile**     | `app/src/main/java/com/example/safecommute_driver/ui/screens/DriverProfileScreen.kt` |
| **Inbox**       | `app/src/main/java/com/example/safecommute_driver/ui/screens/DriverInboxScreen.kt` |
| **Submit Report** | `app/src/main/java/com/example/safecommute_driver/ui/screens/SubmitReportScreen.kt` |
| **Emergency SOS** | `app/src/main/java/com/example/safecommute_driver/ui/screens/EmergencySosModal.kt` |

## Shared components

| Component   | File |
|------------|------|
| App bar    | `app/src/main/java/com/example/safecommute_driver/ui/components/AppBar.kt` |
| Bottom nav | `app/src/main/java/com/example/safecommute_driver/ui/components/BottomNav.kt` |

## Navigation (chooses which screen to show)

- `app/src/main/java/com/example/safecommute_driver/ui/navigation/AppNavigation.kt`

## Entry point

- `app/src/main/java/com/example/safecommute_driver/MainActivity.kt` — only sets theme and calls `AppNavigation`.
