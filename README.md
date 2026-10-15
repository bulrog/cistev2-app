# ciste 2.0

## What is it?

An open-source android application to create/play on your mobile treasure hunts where people have to find checkpoints in the real world and solve puzzles.

If you look for high-res graphics, amazing soundtracks, videos, animations... you can turn around but if you want to unleash your creativity without coding skills then you came to the right place!

If you want to add new capabilities to this game just clone this repo. It allows to build the ciste 2.0 application.

This was built with android studio 4.2.1 and tested on a samsung s7, a71 and a redmi 9 (as of March 2022).

Since October 2026 the project targets Android 16 (API 36) and was tested on a Fairphone 4 (Android 15) and on an Android 15 emulator.

## reporting an issue

In case of issue, just look if it is a know issue or create a new one [here](https://github.com/bulrog/cistev2-app/issues)

If the application crashes you should get such screen:

![](crash_example.jpg)

Providing such picture will help to debug and if big error good to get the top like on this picture and also the "caused by" in the text.

## deleting some game or/and an account

In case you want some games you have created or your account to be deleted, please contact the developer of the game. The account and data will be removed in maximum 90 days.


## developer area 

### Build requirements

- JDK 17 (in Android Studio: Settings -> Build Tools -> Gradle -> Gradle JDK; do not use a JDK newer than 21)
- Gradle 8.11.1 (wrapper) and Android Gradle Plugin 8.10.1
- Android SDK platform 36 (`compileSdk` and `targetSdk` are 36, `minSdk` is 33)

### Native libraries (openCV)

The openCV native libraries are committed in `app/src/main/jnilibs/`, only for the ABIs `arm64-v8a` and `armeabi-v7a` (see `abiFilters` in `app/build.gradle`). x86/x86_64 were dropped because the prebuilt openCV 4.5.2 libs for those ABIs are not 16 KB aligned, which Google Play requires for 64-bit libs.

No NDK is needed to build the project.

`libc++_shared.so` comes from NDK r27c (16 KB aligned). To refresh it, copy from a recent NDK:

- `<ndk>/toolchains/llvm/prebuilt/<host>/sysroot/usr/lib/aarch64-linux-android/libc++_shared.so` to `jnilibs/arm64-v8a/`
- `<ndk>/toolchains/llvm/prebuilt/<host>/sysroot/usr/lib/arm-linux-androideabi/libc++_shared.so` to `jnilibs/armeabi-v7a/`

`libopencv_java4.so` for `arm64-v8a` is already 64 KB aligned. If you add x86_64 back, you need an openCV build (4.11 or later, or a rebuild) that is 16 KB aligned.

When starting in debug mode the console will show:

    D/check: openCV ok

### How to setup download of data from firestore:

Create a project in firebase and generate as mentioned the key to access the project and add it at the root of the app folder.

Then change the security rule for the storage:

    rules_version = '2';
    service firebase.storage {
      match /b/{bucket}/o {
        match /{allPaths=**} {
        allow read;
        }
        
        match /{userId}/{allPaths=**} {
        allow read;
        allow write: if request.auth.uid == userId;
        }
      }
    }
    
 And in firestore DB, add a collection games and set the permission as follow:
 
    rules_version = '2';
    service cloud.firestore {
      match /databases/{database}/documents {
        match /games/{gameId} {
          allow read;
          allow write :if request.auth != null;
        }
      }
    }
    
### How to use openCV 4 in android sdk (already done for this project)

Based on this article: https://learnopencv.com/image-classification-with-opencv-for-android/

Made a new project with an empty activity

Download openCV4Android: https://opencv.org/releases/ (version 4.5.2)

Then import is as a module (File-> New-> Import module and name it openCV)
Then open the build.gradle of openCV and change:

    apply plugin: 'com.android.application'
    
To:

    apply plugin: 'com.android.library'

Also comment the 'applicationId' line of openCV so it is not seen as an app.

Then in project structure, choose the dependencies tab and add openCV as a module dependency to the app.

### how to publish on google play

You of course need a developer account on google play.

Then you need to create a signed bundle (not an APK) with android studio and the first time you need to create a keystore and a key so google play can see that next releases are all signed by the same entity. See more details [here](https://developer.android.com/studio/publish/app-signing#generate-key).

Before building the bundle:

- Increase `versionCode` (and `versionName`) in `app/build.gradle`.
- Google Play requires a recent target API level (API 36 since August 2026) and 16 KB page size support for native libs (see above).

The signed bundle is generated in `app/release/app-release.aab`. Upload it first as an internal test release in the Play Console, check the report (errors must be fixed, warnings about missing deobfuscation file / native debug symbols can be ignored), then promote it to production. Never commit the keystore or its passwords.
