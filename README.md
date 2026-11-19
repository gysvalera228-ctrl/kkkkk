# Dataminde

Dataminde is an Android visual diagram editor prototype built with Java and C++17.

## Current prototype
- Dark infinite-style canvas with pan and pinch zoom.
- Three example nodes and curved connections.
- Add-node button.
- JNI bridge loading a native C++ library.
- GitHub Actions workflow for Debug APK builds.

## Build
The project uses:
- JDK 17
- Gradle 8.9
- Android Gradle Plugin 8.7.3
- Android SDK 35
- Android NDK 27.2.12479018
- CMake 3.22.1

Run:

```bash
gradle :app:assembleDebug
```

APK output:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## GitHub Actions
Push the repository to GitHub. The workflow in `.github/workflows/android.yml` builds a Debug APK and uploads it as an artifact.

Create a tag such as `v0.1.0` to additionally build Release APK and AAB.

## Notes
This is the Foundation prototype, not the complete Dataminde 1.0 specification. Node editing, connection creation, project persistence, undo/redo and export are planned next.
