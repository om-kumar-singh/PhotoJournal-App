# Photo Library Android App

A modern and minimal Photo Library app built with Java and XML for Android, featuring Firebase integration for authentication, storage, and database.

## Features

- **User Authentication**: Sign up and log in with email and password
- **Photo Upload**: Upload photos from gallery with descriptions
- **Firebase Integration**:
  - Firebase Authentication for user management
  - Firebase Storage for image storage
  - Firebase Realtime Database for photo metadata
- **Modern UI**: Clean, minimalist design with Material Components

## Screens

1. **Splash/Welcome Screen**
2. **Sign Up Screen**
3. **Login Screen**
4. **Upload Screen**

## Prerequisites

- Android Studio (latest version)
- Minimum SDK: 24 (Android 7.0)
- Target SDK: 34 (Android 14)
- Firebase project with Authentication, Storage, and Realtime Database enabled

## Setup Instructions

### 1. Firebase Configuration

1. Create a Firebase project at https://console.firebase.google.com/
2. Add your Android app (`com.example.photolibrary`)
3. Download `google-services.json`
4. Place it inside `/app` folder

### 2. Enable Firebase Services

- **Authentication** → Enable *Email/Password*
- **Realtime Database** → Create database (test mode for dev)
- **Storage** → Set up Cloud Storage

### 3. Firebase Rules

#### Realtime Database Rules
```json
{
  "rules": {
    "users": {
      "$userId": {
        ".read": "$userId === auth.uid",
        ".write": "$userId === auth.uid"
      }
    },
    "photos": {
      "$photoId": {
        ".read": "auth != null",
        ".write": "auth != null && data.child('userId').val() === auth.uid"
      }
    }
  }
}
```

#### Storage Rules
```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /photos/{userId}/{fileName} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

## Project Structure
```
app/
├── java/com/example/photolibrary/
│   ├── MainActivity.java
│   ├── SignUpActivity.java
│   ├── LoginActivity.java
│   ├── UploadActivity.java
│   └── models/
│       └── Photo.java
├── res/
│   ├── layout/
│   ├── values/
│   └── xml/
└── AndroidManifest.xml
```

## Dependencies

Add these dependencies to your `app/build.gradle` file:

```gradle
dependencies {
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.9.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    
    // Firebase
    implementation platform('com.google.firebase:firebase-bom:32.3.1')
    implementation 'com.google.firebase:firebase-auth'
    implementation 'com.google.firebase:firebase-database'
    implementation 'com.google.firebase:firebase-storage'
    
    // Image loading
    implementation 'com.github.bumptech.glide:glide:4.15.1'
}
```

## Usage

1. **Sign up** with email and password
2. **Log in** with your credentials
3. **Upload** a photo with description
4. **View** your photo library

## Color Scheme

| Element | Color |
|---------|-------|
| Background | #0D47A1 |
| Button | #F44336 |
| Text | White |
| Light Gray | #E0E0E0 |

## Contributors

- **Prince Singh**  
  GitHub: [https://github.com/Ps5510038](https://github.com/Ps5510038)

## License

MIT License

Copyright (c) 2025 [https://github.com/om-kumar-singh](https://github.com/om-kumar-singh)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

## Release Description

**PhotoJournal App – Initial Release v1.0**  
Includes Firebase-powered authentication, photo upload feature with metadata storage, and clean Material UI.
