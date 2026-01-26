## HomeHealth

### Project Overview
A mobile application designed to find caretakers and schedule appointments, reducing the need
to travel to care facilities and giving those with travel inconveniences accessible healthcare.

### Features

1. Appointment scheduling with certified caretakers
2. Chats with caretakers for updates
3. Sharing of location with caretakers
4. View detailed profiles of caretakers
5. Be notified of confirmed appointments and new messages

### How to run

1. Pull, Clone, or Download the project from GitHub
2. Open the project in Android Studio
3. Get a Google Maps API Key from [Google Cloud Console](https://console.cloud.google.com/)
4. Enter the package name and SHA-1 Fingerprint of the app when prompted
```
Package name: com.example.homehealth
SHA-1 Fingerprint: Your app fingerprint
```
5. Insert the API Key into the local.properties file
```
MAPS_API_KEY="YOUR_API_KEY"
```
6. Sync gradle/wait for gradle to sync
7. Run the application
8. If you encounter an authorization issue with Google Maps API and location is not loading, clean and rebuild the project before rerunning

### Project Structure

```bash
├───data
│   ├───dao           # Data access objects which access database
│   ├───models        # Data entities
│   └───repository    # Classes to access data sources
├───fragments         # Reusable UIs
├───screens           # UI codes for the various features
├───ui
│   └───theme
├───utils             # Helper functions
└───viewmodels        # Viewmodels with business logic (Bridge between Repo and UI)

```