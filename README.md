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

1. **Splash/Welcome Screen**: Entry point with sign up button
2. **Sign Up Screen**: User registration with email, password, and full name
3. **Login Screen**: User authentication
4. **Upload Screen**: Photo upload with description

## Prerequisites

- Android Studio (latest version)
- Minimum SDK: 24 (Android 7.0)
- Target SDK: 34 (Android 14)
- Firebase project with Authentication, Storage, and Realtime Database enabled

## Setup Instructions

### 1. Firebase Configuration

1. Create a new Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Add an Android app to your Firebase project:
   - Package name: `com.example.photolibrary`
   - App nickname: Photo Library (optional)
3. Download the `google-services.json` file
4. Place the `google-services.json` file in the `app/` directory of your project

### 2. Enable Firebase Services

In the Firebase Console, enable the following services:

#### Authentication
- Go to Authentication > Sign-in method
- Enable "Email/Password" provider

#### Realtime Database
- Go to Realtime Database
- Create database in test mode (or production mode with proper rules)
- Copy the database URL

#### Storage
- Go to Storage
- Get started and set up Cloud Storage
- Set security rules (you can start with test mode for development)

### 3. Database Rules

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
```
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

### 4. Build and Run

1. Open the project in Android Studio
2. Sync Gradle files
3. Build the project
4. Run on an emulator or physical device

## Project Structure

```
app/
├── java/com/example/photolibrary/
│   ├── MainActivity.java          # Splash/Welcome screen
│   ├── SignUpActivity.java        # User registration
│   ├── LoginActivity.java         # User authentication
│   ├── UploadActivity.java        # Photo upload
│   └── models/
│       └── Photo.java             # Photo model class
├── res/
│   ├── layout/
│   │   ├── activity_main.xml      # Splash screen layout
│   │   ├── activity_signup.xml    # Sign up screen layout
│   │   ├── activity_login.xml     # Login screen layout
│   │   └── activity_upload.xml    # Upload screen layout
│   ├── values/
│   │   ├── colors.xml             # Color resources
│   │   ├── strings.xml            # String resources
│   │   └── themes.xml             # App themes
│   └── xml/
│       ├── backup_rules.xml       # Backup configuration
│       └── data_extraction_rules.xml
└── AndroidManifest.xml            # App manifest
```

## Dependencies

- AndroidX AppCompat
- Material Components
- Firebase Authentication
- Firebase Realtime Database
- Firebase Storage

## Usage

1. **Sign Up**: Create a new account with email, password, and full name
2. **Log In**: Sign in with your credentials
3. **Upload Photo**: 
   - Click "Choose Image" to select a photo from gallery
   - Enter a description
   - Click "SAVE" to upload and save the photo

## Color Scheme

- Background (Dark Blue): `#0D47A1`
- Button (Red): `#F44336`
- Text: White
- Light Gray: `#E0E0E0`

## Notes

- Make sure to add the `google-services.json` file before building
- Configure Firebase Security Rules properly for production
- The app requires internet connection for Firebase services
- Image selection requires storage permissions (handled automatically on Android 13+)

## License

This project is created for educational purposes.

