# Android Build & Gradle Troubleshooting Guide

This document breaks down every build issue encountered in this project, the exact error messages, why they happened, what was done wrong (or what caused them), how each was resolved, and how to avoid them in future Android projects.

---

## Table of Contents
1. [Error 1: Kotlin Plugin Collision with AGP 9 Built-in Kotlin](#error-1-kotlin-plugin-collision-with-agp-9-built-in-kotlin)
2. [Error 2: KSP Version Incompatibility with Kotlin 2.2.x](#error-2-ksp-version-incompatibility-with-kotlin-22x)
3. [Error 3: KSP Source Sets Disallowed in AGP Built-in Kotlin](#error-3-ksp-source-sets-disallowed-in-agp-built-in-kotlin)
4. [Error 4: Missing `jlink.exe` / JRE vs JDK Toolchain Conflict](#error-4-missing-jlinkexe--jre-vs-jdk-toolchain-conflict)
5. [Error 5: Resource Linking Failure with `<adaptive-icon>` & `minSdk`](#error-5-resource-linking-failure-with-adaptive-icon--minsdk)
6. [Error 6: Unresolved References to Material 3 in `:app`](#error-6-unresolved-references-to-material-3-in-app)
7. [Error 7: Runtime Crash `ContactDatabase_Impl does not exist` (Room 3 vs Room 2 Mismatch)](#error-7-runtime-crash-contactdatabase_impl-does-not-exist-room-3-vs-room-2-mismatch)
8. [Summary: Golden Rules to Avoid These in Future Projects](#summary-golden-rules-to-avoid-these-in-future-projects)

---

## Error 1: Kotlin Plugin Collision with AGP 9 Built-in Kotlin

### The Error Message
```text
Build file 'C:\Users\HP\AndroidStudioProjects\MyApplication\app\build.gradle.kts' line: 1
Error resolving plugin [id: 'org.jetbrains.kotlin.android', version: '2.1.10', apply: false]
> The request for this plugin could not be satisfied because the plugin is already on the classpath with an unknown version, so compatibility cannot be checked.
```
Followed by:
```text
An exception occurred applying plugin request [id: 'org.jetbrains.kotlin.android', version: '2.1.10']
> Failed to apply plugin 'org.jetbrains.kotlin.android'.
   > Cannot add extension with name 'kotlin', as there is an extension already registered with that name.
```

### What Went Wrong
* **What you did:** Applied the traditional `org.jetbrains.kotlin.android` plugin in `build.gradle.kts` alongside Android Gradle Plugin (AGP) version `9.3.2`.
* **Root Cause:** Starting with **AGP 9.0+**, Android has **"Built-in Kotlin"** enabled by default. AGP internally manages Kotlin compilation and registers the `kotlin` extension. When you explicitly apply `org.jetbrains.kotlin.android`, Gradle tries to register the `kotlin` extension a second time, resulting in a duplicate extension crash (`Cannot add extension with name 'kotlin'`).

### How It Was Fixed
Removed `org.jetbrains.kotlin.android` from the `plugins {}` block in the module build files:
```kotlin
// Before (Conflicted)
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android) // ❌ Do not apply with AGP 9+
    alias(libs.plugins.kotlin.compose)
}

// After (Working)
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose) // ✅ Only keep compose plugin if using Compose
}
```

### How to Avoid It
* With AGP 9.0 and newer, **do not** apply `org.jetbrains.kotlin.android`. AGP automatically compiles Kotlin files.

---

## Error 2: KSP Version Incompatibility with Kotlin 2.2.x

### The Error Message
```text
> Configure project :roomdatabase
ksp-2.1.10-1.0.30 is too old for kotlin-2.2.10. Please upgrade ksp or downgrade kotlin-gradle-plugin to 2.1.10.
...
Cannot invoke "com.android.build.gradle.api.BaseVariant.getSourceFolders(com.android.build.gradle.api.SourceKind)" because the return value of "org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmAndroidCompilation.getAndroidVariant()" is null
```
Followed by a failed guess:
```text
Plugin [id: 'com.google.devtools.ksp', version: '2.2.10-1.0.31'] was not found in any of the following sources:
- Plugin Repositories (could not resolve plugin artifact 'com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:2.2.10-1.0.31')
```

### What Went Wrong
* **What you did:** AGP 9.3.2 bundles Kotlin `2.2.10`, but the project was configured with KSP `2.1.10-1.0.30`.
* **Root Cause:** Unlike normal Gradle plugins, KSP (Kotlin Symbol Processing) is tightly coupled to the exact Kotlin compiler version. A KSP binary compiled for Kotlin 2.1.x cannot run inside Kotlin 2.2.x. Furthermore, guessing version numbers (`1.0.31`) fails because KSP updated its release increments to `2.0.x` for Kotlin 2.2 releases.

### How It Was Fixed
Looked up the actual published artifact on Maven Central and updated `gradle/libs.versions.toml`:
```toml
[versions]
kotlin = "2.2.10"
ksp = "2.2.10-2.0.2" # ✅ Exact matching published release
```

### How to Avoid It
* Always match KSP to the active Kotlin version.
* Check [Maven Central](https://mvnrepository.com/artifact/com.google.devtools.ksp/com.google.devtools.ksp.gradle.plugin) or the [KSP GitHub Releases page](https://github.com/google/ksp/releases) rather than guessing patch suffixes.

---

## Error 3: KSP Source Sets Disallowed in AGP Built-in Kotlin

### The Error Message
```text
Using kotlin.sourceSets DSL to add Kotlin sources is not allowed with built-in Kotlin.
Kotlin source set 'debug' contains: [C:\Users\HP\AndroidStudioProjects\MyApplication\roomdatabase\build\generated\ksp\debug\kotlin, C:\Users\HP\AndroidStudioProjects\MyApplication\roomdatabase\build\generated\ksp\debug\java]
Solution: Use android.sourceSets DSL instead.
For more information, see https://developer.android.com/r/tools/built-in-kotlin
To suppress this error, set android.disallowKotlinSourceSets=false in gradle.properties.
```

### What Went Wrong
* **Root Cause:** AGP 9's Built-in Kotlin enforces that all source paths must be registered through `android.sourceSets`, not `kotlin.sourceSets`. Older/interim KSP versions still register their generated source folders (`build/generated/ksp/debug/kotlin`) via the legacy `kotlin.sourceSets` API.

### How It Was Fixed
Added the compatibility suppression flags in `gradle.properties`:
```properties
# KSP registers generated sources via kotlin.sourceSets which is normally
# disallowed with AGP 9.x built-in Kotlin. Allow it until KSP has native support.
android.disallowKotlinSourceSets=false
android.sync.suppressAgpWarnings=UNSUPPORTED_PROJECT_OPTION_USE
```

### How to Avoid It
* Whenever you see `Using kotlin.sourceSets DSL to add Kotlin sources is not allowed with built-in Kotlin`, set `android.disallowKotlinSourceSets=false` in `gradle.properties`.
* Alternatively, update to KSP 2.3+ once your module transitions to independent KSP versioning.

---

## Error 4: Missing `jlink.exe` / JRE vs JDK Toolchain Conflict

### The Error Message
```text
Execution failed for JdkImageTransform: C:\Users\HP\AppData\Local\Android\Sdk\platforms\android-36.1\core-for-system-modules.jar.
> jlink executable C:\Users\HP\.antigravity-ide\extensions\redhat.java-1.55.0-win32-x64\jre\21.0.11-win32-x86_64\bin\jlink.exe does not exist.
```

### What Went Wrong
* **Root Cause:** Gradle automatically searches your machine for installed Java runtimes. It picked up a lightweight **JRE** installed by an IDE extension (`redhat.java`). Because a JRE only contains runtime binaries and **omits JDK tools like `jlink.exe`**, AGP's task (`JdkImageTransform`) crashed when attempting to link the Android core system modules.

### How It Was Fixed
Explicitly forced Gradle to use Android Studio's bundled, full **JetBrains Runtime JDK (JBR)** in `gradle.properties`:
```properties
org.gradle.java.home=C:/Program Files/Android/Android Studio/jbr
```
And stopped lingering Gradle daemons (`gradlew --stop`) so the daemon restarted with the correct JDK.

### How to Avoid It
* Do not rely on Gradle's auto-detected JVM if you have IDE extensions, JREs, or multiple Java versions installed.
* Always explicitly pin `org.gradle.java.home` in `gradle.properties` to Android Studio's JBR (`<Android Studio>/jbr`), which is guaranteed to be a complete JDK with `jlink` and `javac`.

---

## Error 5: Resource Linking Failure with `<adaptive-icon>` & `minSdk`

### The Error Message
```text
Execution failed for task ':app:processDebugResources'
> Android resource linking failed
  com.example.myapplication.app-main-41:/mipmap-anydpi/ic_launcher.xml: error: <adaptive-icon> elements require a sdk version of at least 26.
  com.example.myapplication.app-main-41:/mipmap-anydpi/ic_launcher_round.xml: error: <adaptive-icon> elements require a sdk version of at least 26.
  error: failed linking file resources.
```

### What Went Wrong
* **What you did:** In `app/build.gradle.kts`, `minSdk` was set to `24`, but adaptive launcher icons were placed in the folder `res/mipmap-anydpi/` without an API qualifier.
* **Root Cause:** Adaptive icons (`<adaptive-icon>`) were introduced in Android 8.0 (API 26). When `minSdk` is lower than 26 (e.g. 24) and the resource folder is not qualified as `mipmap-anydpi-v26`, the Android AAPT resource linker throws an error because devices running API 24 or 25 cannot parse `<adaptive-icon>`.

### How It Was Fixed
Updated `minSdk` to `26` in `app/build.gradle.kts`:
```kotlin
defaultConfig {
    applicationId = "com.example.myapplication"
    minSdk = 26  // ✅ Changed from 24 to 26
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"
}
```

### How to Avoid It
* Either set `minSdk = 26` (standard for modern Android apps), OR
* Move adaptive icon XML files into `res/mipmap-anydpi-v26/` and provide legacy PNG/bitmap icons in `res/mipmap-<density>/` for older Android versions.

---

## Error 6: Unresolved References to Material 3 in `:app`

### The Error Message
```text
e: file:///.../MainActivity.kt: Unresolved reference 'foundation'.
e: file:///.../MainActivity.kt: Unresolved reference 'material3'.
e: file:///.../MainActivity.kt: Unresolved reference 'Scaffold'.
e: file:///.../Theme.kt: Unresolved reference 'MaterialTheme'.
e: file:///.../Type.kt: Unresolved reference 'Typography'.
```

### What Went Wrong
* **What you did:** In `app/build.gradle.kts`, Compose was enabled (`compose = true`), and the code used `Scaffold`, `Text`, `MaterialTheme`, etc., but the Material3 library was not declared under `dependencies`.
* **Root Cause:** The `:roomdatabase` module had `implementation(libs.androidx.compose.material3)`, but the `:app` module omitted it.

### How It Was Fixed
Added the Material 3 dependency to `app/build.gradle.kts`:
```kotlin
dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3) // ✅ Added
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    ...
}
```

### How to Avoid It
* If code in a module imports `androidx.compose.material3.*`, ensure `libs.androidx.compose.material3` is in that specific module's `build.gradle.kts`.

---

## Error 7: Runtime Crash `ContactDatabase_Impl does not exist` (Room 3 vs Room 2 Mismatch)

### The Error Message
```text
FATAL EXCEPTION: main
Process: com.example.roomdatabase, PID: 18825
java.lang.RuntimeException: Cannot find implementation for com.example.roomdatabase.data.ContactDatabase. ContactDatabase_Impl does not exist. Is Room annotation processor correctly configured?
	at androidx.room3.util.KClassUtil__KClassUtil_jvmAndAndroidKt.findAndInstantiateDatabaseImpl(KClassUtil.jvmAndAndroid.kt:47)
	at androidx.room3.RoomDatabase$Builder.build(RoomDatabase.android.kt:1256)
	at com.example.roomdatabase.RoomMainActivity.db_delegate$lambda$0(RoomMainActivity.kt:25)
```

### What Went Wrong
* **What you did:**
  1. Your code (`Contact.kt`, `ContactDAO.kt`, `ContactDatabase.kt`) used the new **Room 3** package: `androidx.room3.*`.
  2. In `build.gradle.kts`, you declared `ksp(libs.androidx.room.compiler)` which resolved to **`androidx.room:room-compiler:2.6.1`** (Room 2).
  3. In `Contact.kt`, you had `@Entity(tableName = "contact_table")`, but in `ContactDAO.kt` the queries selected from `contact` (`SELECT * FROM contact`).
* **Root Causes:**
  1. **Compiler Mismatch:** Room 2's compiler looks for `@androidx.room.Database`. It ignores `@androidx.room3.Database`. Because Room 2 ignored your Room 3 annotations, KSP never generated `ContactDatabase_Impl.kt`. The project built without errors, but crashed at runtime when `Room.databaseBuilder(...).build()` looked for `ContactDatabase_Impl`.
  2. **Table Name Discrepancy:** When Room 3's compiler ran, it caught that `ContactDAO` queried table `contact`, but the `@Entity` specified `tableName = "contact_table"`.

### How It Was Fixed
1. **Added `androidx.room3:room3-compiler` in `gradle/libs.versions.toml`:**
   ```toml
   [libraries]
   androidx-room3-compiler = { group = "androidx.room3", name = "room3-compiler", version.ref = "room3Runtime" }
   ```
2. **Updated `roomdatabase/build.gradle.kts` to use Room 3's compiler:**
   ```kotlin
   dependencies {
       implementation(libs.androidx.room3.common.jvm)
       implementation(libs.androidx.room3.runtime)
       ksp(libs.androidx.room3.compiler) // ✅ Switched from Room 2 to Room 3 compiler
   }
   ```
3. **Aligned table name in `Contact.kt`:**
   ```kotlin
   @Entity(tableName = "contact") // ✅ Matches queries in ContactDAO
   data class Contact(...)
   ```
4. **Bypassed Reflection by Providing the `factory` Lambda in `RoomMainActivity.kt`:**
   Room 3 uses a `factory` lambda parameter in `databaseBuilder` to instantiate the implementation directly at compile-time instead of relying on runtime reflection (`findAndInstantiateDatabaseImpl`):
   ```kotlin
   private val db by lazy {
       Room.databaseBuilder(
           context = applicationContext,
           name = "contact.db",
           factory = { ContactDatabase_Impl() } // ✅ Direct compile-time instantiation, no reflection failure!
       ).build()
   }
   ```

### How to Avoid It
* **Always match Room runtime and compiler artifacts:** If using `androidx.room3:*`, always use `ksp("androidx.room3:room3-compiler:*")`. Never pair Room 3 runtime with Room 2 compiler.
* Ensure the `@Entity` table name matches the table name in `@Query("SELECT * FROM ...")`.

---

## Summary: Golden Rules to Avoid These in Future Projects

| Aspect | Best Practice |
| :--- | :--- |
| **AGP 9+ & Kotlin** | Never apply `org.jetbrains.kotlin.android`. AGP 9 handles Kotlin natively. |
| **KSP** | Match the exact KSP release to your Kotlin version (check Maven Central for `<kotlin-version>-<ksp-release>`). |
| **KSP & AGP 9** | Add `android.disallowKotlinSourceSets=false` to `gradle.properties`. |
| **Gradle JDK** | Always set `org.gradle.java.home=C:/Program Files/Android/Android Studio/jbr` to prevent missing toolchain errors (like `jlink`). |
| **Icons & SDK** | Keep `minSdk >= 26` or use `mipmap-anydpi-v26` folder qualifiers for adaptive icons. |
| **Room 3 vs Room 2** | If importing `androidx.room3.*`, use `androidx.room3:room3-compiler`. Never mix Room 3 runtime with Room 2 compiler. |
| **Room Entities & DAOs** | Ensure `@Entity(tableName = "...")` exactly matches `@Query("SELECT * FROM ...")`. |
| **Dependencies** | Always declare dependencies (`material3`, `room`, etc.) in the module where they are imported. |
