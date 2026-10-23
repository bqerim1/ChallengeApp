# ChallengeApp

An Android app that fetches a form from a JSON endpoint and shows its page, section and question
hierarchy. It works offline and handles poor connections.

Built with Kotlin, Jetpack Compose, Hilt, Room, Retrofit, Coil and Navigation Compose.

## Features

- **Hierarchy:** pages, nested sections and questions, with font size and indent by depth.
- **Text questions:** shown directly.
- **Image questions:** a thumbnail that opens a full-size view with the title.
- **Choice questions:** selectable chips that respect `multiple_selection`.
- **Offline support:** the form is stored in Room, and images are cached on disk.
- **Network failures:** retries with backoff, an error screen or snackbar with Retry, and pull to
  refresh.
- **Languages:** English, German, Albanian, Spanish and French.

## Setup

Requirements: Android Studio with AGP 9.4 support, Android SDK 37, and a device on API 24 or newer.

```bash
git clone https://github.com/bqerim1/ChallengeApp.git
cd ChallengeApp
./gradlew installDebug
```

Or open the project in Android Studio and run the `app` configuration.

## Tests

```bash
./gradlew testDebugUnitTest          # unit tests
./gradlew connectedDebugAndroidTest  # Room and Compose UI tests, needs a device
```

Unit tests cover the mappers, the repository's retry and error handling, and the ViewModel's
selection and error state. Instrumented tests cover the Room DAO and the form and image screens.

## Architecture

A single module, layered by package:

| Package | Contents |
|---|---|
| `domain` | Form models and the `FormRepository` interface, with no Android dependencies |
| `data` | Retrofit API, Room database, and `FormRepositoryImpl` |
| `ui` | Compose screens, `FormViewModel` and navigation |
| `di` | Hilt modules |

The UI always reads from Room. `refresh()` downloads the form and replaces the saved copy in one
transaction; if it fails, the saved copy stays on screen.

Room stores the form as two tables: `form_item`, where each row points to its parent, and
`response`, which holds choice responses.

## Design decisions

- **No use-case classes:** they would only pass calls through to the repository.
- **Flat rows in a `LazyColumn`:** long forms stay lazy, and a tap redraws only its own row.
- **Selections stay in the ViewModel:** they survive rotation but aren't saved.
- **Two user-facing errors:** "offline", which the user can fix, and "something went wrong",
  which they can't.

## Possible improvements

- Retry automatically when the connection comes back.
- Save selected responses in Room.
- Split into feature modules as the app grows.
