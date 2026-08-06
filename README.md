# LinguaLearn Pro

Windows Vista Aero–styled language learning app, ported from the HTML mockup into a native **Android** project (Kotlin + Jetpack Compose).

> Swift / Xcode is for iOS. Android uses **Kotlin** in **Android Studio**.

## What you get

- Adaptive shell that mirrors the mockup: **sidebar + widgets** on tablets, **drawer + inline widgets** on phones
- All mockup destinations: languages, daily lesson / dialogue practice, vocabulary, conversation, listening, writing, AI tutor, Instagram, Google tools, profile, preferences, practice, challenges
- Live analog clock, calendar, and daily progress widgets
- Interactive bits (likes, comments, follow buttons, flip cards, AI chat replies, offline phrasebook translate)

## Open in Android Studio

1. Install [Android Studio](https://developer.android.com/studio) if you don't have it.
2. **File → Open** and choose this folder (`languagelearning`).
3. Wait for Gradle sync to finish.
4. Pick an emulator (a Pixel AVD is already on this machine) or a USB device, then press **Run**.

## Build / run from the terminal

```bash
# JDK 17+ required (JDK 21 works)
export JAVA_HOME=$(/usr/libexec/java_home -v 21)

./gradlew :app:assembleDebug
./gradlew :app:installDebug
adb shell am start -n com.lingualearn.pro/.MainActivity
```

The debug APK lands at:

```
app/build/outputs/apk/debug/app-debug.apk
```

## Project layout

```
app/src/main/java/com/lingualearn/pro/
├── MainActivity.kt          # entry point
├── data/SampleContent.kt    # lesson / social / course data
└── ui/
    ├── AppShell.kt          # title bar, sidebar/drawer, toolbar, status bar
    ├── Destination.kt       # navigation destinations
    ├── components/Aero.kt   # glass cards, buttons, background
    ├── screens/             # every screen from the mockup
    ├── theme/Theme.kt       # Vista colour tokens
    └── widgets/Widgets.kt   # calendar, clock, progress, photo
```

## Notes

- The mockup used remote CDN images; avatars are drawn as initials, and the wallpaper is a Compose gradient so the app works fully offline.
- The AI tutor and Google Translate cards use canned / phrasebook replies — no API keys required.
- Min SDK 24, target / compile SDK 35.
- Frosted glass panels use [Haze](https://github.com/chrisbanes/haze) for real backdrop blur over the Vista wallpaper.
