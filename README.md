# RM News Feed

Android news reader built for the RM case study. The app fetches US headlines from NewsAPI, supports keyword searches and pagination, and opens the original article in a browser.

## Features

- Headline cards with images, source and publication date.
- Server-side keyword search with a debounce and cancellation of previous requests.
- Pagination in batches of 10, pull-to-refresh, and retry actions.
- Article details with the full headline, image, description, source and date; a fixed **Read Full Article** button opens the publisher's URL.
- Light and dark themes that follow the device setting.
- Animated search visibility and a shared header that transitions between the feed and detail.
- Firebase Analytics for basic interactions.

## Setup

1. Open the repository in Android Studio. Use its bundled JDK as the Gradle JDK.
2. Install the Android SDK required by `app/build.gradle.kts` (compile SDK 37.1). The app runs on Android 8.0 / API 26 or later and targets API 36.
3. Obtain a key from [NewsAPI](https://newsapi.org/register). Add this entry to the root `local.properties`, keeping Android Studio's existing `sdk.dir` entry:

   ```properties
   NEWS_API_KEY=your_news_api_key
   ```

   Do not surround the value with quotes. `local.properties` is ignored by Git.
4. The repository includes `app/google-services.json` for the demo Firebase project. To collect events in your own project, register an Android app with package `com.sartori.rmnews` and replace that file with your Firebase configuration.
5. Sync Gradle and run the `app` configuration on a device or emulator with internet access.

The NewsAPI key is compiled into `BuildConfig` for this exercise. Keeping it outside Git avoids publishing it in source control, but does not make a key embedded in an APK secret. A production integration would need a different credential strategy.

## Build and tests

From the repository root, with `JAVA_HOME` pointing to a compatible JDK:

```sh
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
```

On macOS with Android Studio installed in the default location:

```sh
JAVA_HOME='/Applications/Android Studio.app/Contents/jbr/Contents/Home' ./gradlew :app:assembleDebug :app:testDebugUnitTest
```

The debug APK is signed with the local debug key and can be installed for evaluation. Gradle normally exports it to `app/build/outputs/apk/debug/app-debug.apk`; Android Studio builds may also place it under `app/build/intermediates/apk/debug/app-debug.apk`.

```sh
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Unit tests cover repository mapping and network-failure propagation, initial load/retry, and preserving content during refresh and refresh failure. These tests use fakes and do not call the live API.

## Architecture

The app uses MVVM with unidirectional state updates:

```text
Compose UI → ViewModel → NewsRepository → Retrofit / NewsAPI
                   StateFlow → Compose UI
```

- `ui/`: Compose screens/components, navigation and the ViewModel. Sealed UI states represent loading, content and errors; refresh and pagination flags preserve visible content during subsequent requests.
- `domain/`: Article/page models and the repository interface.
- `data/`: Retrofit API, nullable response DTOs and repository implementation. The repository maps usable articles into domain models and formats dates for display.
- `di/`: Hilt bindings and network/Firebase providers.
- `analytics/`: An analytics interface and Firebase implementation that centralizes event names and parameters.

Hilt supplies dependencies; Retrofit and Gson handle networking and JSON; Coil loads images. Navigation Compose uses serializable routes. Detail navigation carries a small article snapshot, so it does not need another network request.

The domain layer intentionally has no use cases: the current operations are simple repository calls. Pagination is implemented directly in the ViewModel to keep the scope small. Articles use their URL as an identity because the API does not supply an article ID; appended pages are deduplicated by URL.

## Analytics

| Event | Trigger |
| --- | --- |
| `search` | A non-empty search passes the debounce; includes `search_term`. |
| `select_content` | A user selects an article; includes its title and content type. |
| `refresh_news` | A user requests a refresh. |
| `open_full_article` | A user taps the external article button. This measures the click, not a successful page load or completed reading. |

To inspect events in Firebase Analytics DebugView on a connected test device:

```sh
adb shell setprop debug.firebase.analytics.app com.sartori.rmnews
```

Open the app and interact with it. Disable Analytics debug mode afterward:

```sh
adb shell setprop debug.firebase.analytics.app .none.
```

## Scope and limitations

- Search queries the US `top-headlines` endpoint; it is not a search across NewsAPI's complete article archive.
- News availability, result totals and request limits depend on NewsAPI. Articles missing a title or URL are skipped; repeated URLs are deduplicated, so displayed counts can differ from the server total.
- The detail displays the API's description, not the full article body. The original publisher link provides the complete article.
- Feed content is held in memory; there is no persistent offline feed, favorites storage or background synchronization.
- Request failures use a simple error/retry experience rather than separate screens for every HTTP error.
- Firebase Analytics is integrated; no A/B testing or Remote Config experiment is included.

Implementation choices and tradeoffs are documented here for the case-study walkthrough.
