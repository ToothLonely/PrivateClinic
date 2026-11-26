# Частный приём

Клиенты для Android, iOS и веба используют общий каркас Compose Multiplatform. На главных экранах доступны переходы между каталогом, записью и профилем. Сервер Ktor отвечает `200 OK` на `GET /health`.

| Модуль | Назначение |
| --- | --- |
| `androidApp` | Android-приложение и стартовая Activity |
| `iosApp` | Xcode-проект и SwiftUI-оболочка |
| `webApp` | Точка входа Kotlin/Wasm и сборка веб-клиента |
| `shared/ui` | Общие экраны, навигация, состояние, тема и Kotlin-фреймворк для iOS |
| `shared/network` | Общий клиент API на Ktor и kotlinx.serialization |
| `shared/storage` | Локальная база Room/SQLite для Android и iOS |
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

## Адрес API

- **Android.** Передайте абсолютный URL через `-PclinicApiBaseUrl=https://api.example.org/` при сборке. Для локального сервера в Android Emulator можно использовать `-PclinicApiBaseUrl=http://10.0.2.2:8080/` в debug-сборке.
- **iOS.** Задайте `CLINIC_API_BASE_URL` в настройках сборки Xcode или передайте `CLINIC_API_BASE_URL=https://api.example.org/` в `xcodebuild`.
- **Веб.** Задайте `content` у метатега `clinic-api-base-url` в [index.html](webApp/src/wasmJsMain/resources/index.html) абсолютным или относительным URL. Пустое значение использует текущий origin. Для отдельного origin разрешите его в CORS на сервере.

Префикс пути в адресе сохраняется. Сеть подключается при запросе из функции, которой нужен API. Открытие экранов каркаса и жест обновления на заглушках работают без сервера.

## Быстрые проверки каркаса

```sh
./gradlew :shared:network:testAndroidHostTest :shared:ui:testAndroidHostTest :server:test
```

Тесты проверяют настройки URL, десериализацию ответа, ошибки HTTP и транспорта, навигацию и пороги жеста. Для ручной проверки откройте «Профиль» → «Личные данные» в компактном окне и потяните экран вниз от верхней границы. До 80 dp двигается заголовок, затем появляется индикатор. Порог обновления — 130 dp. При ширине от 680 dp заголовок остаётся рядом со стрелкой.
