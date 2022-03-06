# How to use openCV 4 in android sdk

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

To build you need then to install NDK (AndroidStudio-> preferences-> android SDK-> SDK tools-> NDK side by side) (1GB download starts)

Then add in the build.gradle of the app (version matches the one installed):

    ndkVersion '22.1.7171670'

Copy the OpenCV-android-sdk/sdk/native/libs in app/src/main/jnilibs

Then take from ndk folder the shared lib and put it in the same folder (merge with previous copy) (where Android installed it: ~/Library/Android/sdk/ndk/<version>/sources/cxx-stl/llvm-libc++/libs/)

When starting in debug mode the console will show:

    D/check: openCV ok

#How to setup download of data from firestore:

Create a project in firebase and generate as mentioned the key to access the project and add it at the root of this project.

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