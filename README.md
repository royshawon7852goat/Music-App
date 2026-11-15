# Pulse – Premium Native Android Music Player

> **Your Music. Your Rhythm.**

Pulse is a fully functional, premium-quality native Android music player crafted specifically with a deep-black AMOLED aesthetic inspired by Nothing OS and tailored for devices like the Nothing Phone (2a). It delivers a commercial-grade offline audio listening experience without requiring an internet connection or external services.

---

## 🎵 Features Overview

### 1. Visual & Brand Identity
- **AMOLED Deep Black Interface**: Pure `#090909` background and `#1D1D1D` cards for maximum power efficiency on OLED/AMOLED screens.
- **Nothing OS Inspired Styling**: Minimalist typography, subtle geometry, signature Pulse Red accents (`#D71920`), and animated pulse indicators.
- **Dynamic Theming**: Dark mode enabled by default with light theme fallback and custom accent support.
- **Custom Adaptive Icon**: Minimalist white waveform geometry with red accent dot.

### 2. Music Library & MediaStore Scanner
- **Zero Internet Required**: Automatically scans local device storage via Android `MediaStore`.
- **Supported Formats**: MP3, FLAC, WAV, AAC, M4A, OGG, Opus.
- **Organized Sections**:
  - All Songs
  - Albums
  - Artists
  - Folders
  - Recently Added
  - Recently Played
  - Favorites
- **Multi-criteria Sorting**: Title (A-Z, Z-A), Artist, Album, Date Added, Duration.
- **Instant Search**: Real-time filtering across songs, artists, albums, and playlists with search history.
- **Album Artwork**: Resolves embedded and system artwork via `content://media/external/audio/albumart/` with fallback placeholders.

### 3. Audio Engine & Playback
- **Media3 ExoPlayer**: High-performance audio engine with audio focus handling, headset disconnection detection (`ACTION_AUDIO_BECOMING_NOISY`), and call interruption resilience.
- **Controls**: Play, Pause, Next, Previous (smart seek back if >3s), Seekbar with scrub preview.
- **Modes**: Shuffle (preserving original queue) and Repeat (Off, All, One).
- **Persistent Mini-Player**: Floats above bottom navigation with live progress bar and instant controls.
- **Fullscreen Now Playing**:
  - Full-resolution artwork with subtle breathing animation when playing.
  - Interactive seek bar.
  - Quick action row for Favorites, Lyrics, Queue, and Equalizer.

### 4. Background Playback & MediaSession
- **MediaSessionService**: Standard Android Media3 foreground service.
- **Notification Controls**: Track title, artist, album art, previous, play/pause, and next.
- **Lock-screen Controls**: Fully integrated with Android system lock-screen media controls.
- **Background Persistence**: Music continues playing seamlessly when the app is minimized, screen is locked, or other apps are opened.

### 5. Equalizer & Audio Effects
- **Hardware Audio Effects**: Direct integration with Android `android.media.audiofx.Equalizer` and `android.media.audiofx.BassBoost` tied to ExoPlayer's `audioSessionId`.
- **Presets**: Normal, Classical, Dance, Flat, Folk, Heavy Metal, Hip Hop, Jazz, Pop, Rock.
- **Custom Band Tuning**: Adjust 5 frequency bands (-15dB to +15dB) with real-time feedback.
- **Bass Boost**: Dedicated bass strength slider (0–100%).
- **Reset Option**: Instantly revert to flat neutral tuning.

### 6. Sleep Timer
- **Options**: 5, 10, 15, 30, 45, 60 minutes, or custom duration.
- **Smart Fade-out**: Automatically attenuates audio volume over the final 30 seconds before pausing.
- **Active Countdown**: Live remaining time display with instant cancel option.
- **Background Execution**: Continues counting down even when the device is locked.

### 7. Synchronized Lyrics (.lrc)
- **Local LRC Support**: Automatically searches and parses `.lrc` files in the song's directory matching the song name.
- **Timestamp Synchronization**: Real-time auto-scrolling to active lines with tap-to-seek functionality.
- **Graceful Fallback**: Informs user when lyrics are unavailable without crashing.

### 8. Playlist Management & Room Database
- **Room Database (SQLite)**: Permanent local persistence for playlists, playback history, play counts, and favorites.
- **Smart Playlists**: Pre-configured Favorites, Recently Played, Recently Added, and Most Played.
- **Custom Playlists**: Create, rename, delete playlists, and add/remove tracks.

---

## 🛠 Project Structure

```
pulse/
├── app/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── kotlin/com/pulse/music/
│       │   │   ├── PulseApplication.kt
│       │   │   ├── MainActivity.kt
│       │   │   ├── data/
│       │   │   │   ├── database/ (Entities, Daos, PulseDatabase)
│       │   │   │   ├── models/ (Song, Album, Artist, Folder, Playlist, LyricLine)
│       │   │   │   └── repository/ (MusicRepository)
│       │   │   ├── playback/
│       │   │   │   ├── MusicPlayerManager.kt
│       │   │   │   └── MusicService.kt
│       │   │   ├── ui/
│       │   │   │   ├── theme/ (Color, Theme, Type)
│       │   │   │   ├── components/ (PulseHeader, SongItem, MiniPlayer, BottomNav)
│       │   │   │   ├── home/ (HomeScreen)
│       │   │   │   ├── library/ (LibraryScreen)
│       │   │   │   ├── player/ (NowPlayingScreen)
│       │   │   │   ├── playlists/ (PlaylistsScreen)
│       │   │   │   ├── search/ (SearchScreen)
│       │   │   │   ├── equalizer/ (EqualizerScreen)
│       │   │   │   ├── sleeptimer/ (SleepTimerDialog)
│       │   │   │   └── settings/ (SettingsScreen)
│       │   │   └── utils/
│       │   │       ├── AudioScanner.kt
│       │   │       └── LrcParser.kt
│       │   └── res/ (Icons, themes, strings, XML rules)
│       └── test/ (Unit tests)
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## 🚀 How to Open, Build & Run

### Prerequisites
- **JDK**: Java 17 or higher (e.g. Microsoft OpenJDK 17).
- **Android SDK**: API 34+ with Build-Tools.
- **Gradle**: Gradle 9.1 (included via Gradle Wrapper `gradlew`).

### 1. Opening in Android Studio
1. Launch Android Studio (Iguana / Jellyfish / Koala or newer).
2. Select **Open** and browse to the project folder:
   `C:\Users\roysh\.gemini\antigravity\scratch\pulse`
3. Wait for Gradle Sync to complete.

### 2. Building via Command Line
Open a terminal in the project directory:

```bash
# Run unit tests
./gradlew test

# Build Debug APK
./gradlew assembleDebug
```

The compiled APK will be output to:
`app/build/outputs/apk/debug/app-debug.apk`

### 3. Installing on a Physical Device (e.g. Nothing Phone 2a)
1. Enable **Developer Options** and **USB Debugging** on your phone.
2. Connect your phone via USB or wireless ADB.
3. Verify connection:
   ```bash
   adb devices
   ```
4. Install the APK:
   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```
5. Launch the app from your launcher or via ADB:
   ```bash
   adb shell am start -n com.pulse.music/.MainActivity
   ```

### 4. Generating a Signed Release APK
1. In Android Studio, go to **Build > Generate Signed Bundle / APK...**
2. Choose **APK** and click **Next**.
3. Create or select a keystore file (`.jks`), enter the passwords and key alias.
4. Select build variant **release** and signature versions (V1 & V2).
5. Click **Finish**. The signed APK will be created in `app/release/`.

---

## ⚙️ Troubleshooting

- **No songs showing on first launch:**
  Make sure you grant the `READ_MEDIA_AUDIO` permission (or `READ_EXTERNAL_STORAGE` on Android 12 and below). Tap the **Scan Storage** or **Refresh** button in the Library tab.
- **Background playback stopped by battery manager:**
  On custom Android skins (including Nothing OS), ensure battery optimization for **Pulse** is set to "Unrestricted" so the background media service isn't terminated by OS memory management.
- **Missing Album Art:**
  Pulse uses standard Android MediaStore album art resolver. If a file does not have embedded ID3 album artwork or system metadata, Pulse displays a clean minimalist fallback icon.
