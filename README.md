# Частный приём

Клиенты для Android, iOS и веба открывают общий экран Compose Multiplatform. Сервер Ktor отвечает `200 OK` на `GET /health`.

| Модуль | Назначение |
| --- | --- |
| `androidApp` | Android-приложение и стартовая Activity |
| `iosApp` | Xcode-проект и SwiftUI-оболочка |
| `webApp` | Точка входа Kotlin/Wasm и сборка веб-клиента |
| `shared/ui` | Общий экран и Kotlin-фреймворк для iOS |
| `server` | Приложение Ktor на JVM |
| `build-logic` | Gradle convention plugins для платформ и Compose |

## Инструменты

- JDK 21 с компилятором `javac` и Gradle Wrapper 9.3.1. Kotlin и KMP 2.4.21, Compose Multiplatform 1.12.2, AGP 9.1.1 и зависимости зафиксированы в [каталоге версий](gradle/libs.versions.toml).
- Для Android нужны SDK Platform 37.0 (`platforms;android-37.0`), Build Tools 36.0.0 и Platform-Tools. Укажите путь к SDK в `ANDROID_HOME` или через `sdk.dir` в локальном `local.properties`. Приложение поддерживает Android API 24 и выше.
- Для веба нужен браузер с поддержкой WasmGC. Gradle скачивает Node.js и инструменты сборки при первом запуске.
- Для iOS нужен Mac с Apple Silicon, Xcode 26.4 с iOS SDK и установленный iOS Simulator. Минимальная версия iOS — 16. Для сборки из Xcode задайте `JAVA_HOME` с JDK 21 или зарегистрируйте JDK так, чтобы его находил `/usr/libexec/java_home -v 21`.

## Сборка и запуск

Команды выполняются из корня репозитория. На Ubuntu можно собрать Android, веб и сервер одной командой.

```sh
./gradlew :androidApp:assembleDebug :webApp:wasmJsBrowserDistribution :server:build
```

**Android.** APK находится в `androidApp/build/outputs/apk/debug/androidApp-debug.apk`. Подключите устройство или запустите эмулятор. Для команды `adb` добавьте каталог `platform-tools` из Android SDK в `PATH`.

```sh
./gradlew :androidApp:installDebug
adb shell am start -n ru.privateclinic.app/ru.privateclinic.android.MainActivity
```

**Веб.** Готовые файлы находятся в `webApp/build/dist/wasmJs/productionExecutable`. Для локального запуска откройте адрес, который выведет webpack.

```sh
./gradlew :webApp:wasmJsBrowserDevelopmentRun
```

**Сервер.** Пока Gradle держит сервер запущенным, выполните HTTP-проверку в другом терминале.

```sh
./gradlew :server:run
```

```sh
curl --fail --show-error http://127.0.0.1:8080/health
```

**iOS.** Откройте `iosApp/ClinicApp.xcodeproj` в Xcode, выберите схему `ClinicApp` и запустите её в симуляторе. Xcode сам собирает фреймворк `ClinicUI` через Gradle. Сборка для симулятора из терминала выполняется командой ниже.

```sh
xcodebuild -project iosApp/ClinicApp.xcodeproj -scheme ClinicApp \
  -configuration Debug -destination 'generic/platform=iOS Simulator' \
  -derivedDataPath iosApp/build CODE_SIGNING_ALLOWED=NO build
```
