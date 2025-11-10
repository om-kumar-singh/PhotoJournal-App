# Firebase Setup Checklist

Follow these steps to configure Firebase for the Photo Library app:

## Step 1: Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project" or select an existing project
3. Enter project name: "Photo Library" (or your preferred name)
4. Enable/disable Google Analytics (optional)
5. Click "Create project"

## Step 2: Add Android App to Firebase

1. In Firebase Console, click "Add app" and select Android
2. Enter package name: `com.example.photolibrary`
3. Enter app nickname: "Photo Library" (optional)
4. Enter SHA-1 (optional, for now you can skip)
5. Click "Register app"
6. Download `google-services.json`
7. Place `google-services.json` in the `app/` directory of your project

## Step 3: Enable Authentication

1. In Firebase Console, go to **Authentication**
2. Click "Get started"
3. Go to "Sign-in method" tab
4. Enable **Email/Password** provider
5. Click "Save"

## Step 4: Enable Realtime Database

1. In Firebase Console, go to **Realtime Database**
2. Click "Create Database" (or use existing database if already created)
3. Choose location (select closest to your users)
   - Recommended: us-central1, asia-south1, or europe-west1
4. Start in **test mode** (for development) or **production mode** (for production)
5. Click "Enable"
6. **Important**: Note your database URL
   - Your database URL: `https://photo-library-347ce-default-rtdb.firebaseio.com/`
   - This URL is already configured in the app code (FirebaseHelper.java)

### Set Database Rules

Go to **Realtime Database** > **Rules** and paste:

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

Click "Publish"

### Verify Database Connection

After setting up:
1. The app is configured to use: `https://photo-library-347ce-default-rtdb.firebaseio.com/`
2. Database rules are set to require authentication
3. Users can only read/write their own user data
4. Photos can be read by any authenticated user, but only the owner can write/update/delete

## Step 5: Enable Cloud Storage

1. In Firebase Console, go to **Storage**
2. Click "Get started"
3. Start in **test mode** (for development)
4. Choose storage location (same as database)
5. Click "Done"

### Set Storage Rules

Go to **Storage** > **Rules** and paste:

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

Click "Publish"

## Step 6: Verify Setup

1. Make sure `google-services.json` is in `app/` directory
2. Sync Gradle files in Android Studio
3. Build the project
4. Run the app

## Security Notes

- **Test mode** rules allow read/write access to anyone for 30 days
- For production, update rules to be more restrictive
- Never commit `google-services.json` to public repositories (it's in .gitignore)

## Troubleshooting

### Build Error: "File google-services.json is missing"
- Make sure you've downloaded and placed `google-services.json` in the `app/` directory
- Verify the package name matches: `com.example.photolibrary`

### Authentication Error
- Verify Email/Password is enabled in Firebase Console
- Check internet connection
- Verify Firebase project is correctly configured

### Storage/Database Error
- Verify Storage and Realtime Database are enabled
- Check security rules are published
- Verify user is authenticated before uploading
- **Database Connection Issues**:
  - Verify the database URL in `FirebaseHelper.java` matches your Firebase Console
  - Check that the database is enabled in Firebase Console
  - Verify internet connection
  - Check Firebase Console > Realtime Database > Data tab to see if data is being written
  - Ensure database rules allow authenticated users to read/write

## Next Steps

After Firebase is configured:
1. Build and run the app
2. Test sign up functionality
3. Test login functionality
4. Test photo upload functionality

