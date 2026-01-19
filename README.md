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
3. Sync gradle/wait for gradle to sync
4. Run the application

### Project Structure

```bash
├───data
│   ├───dao           # Data access objects which access database
│   ├───models        # Data entities
│   └───repository    # Classes to access data sources
├───screens           # UI codes for the various features
├───ui
│   └───theme
└───viewmodels        # Viewmodels with business logic (Bridge between Repo and UI)

```