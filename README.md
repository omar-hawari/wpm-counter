# WPM Counter

A simple Words Per Minute (WPM) counter built using Jetpack Compose, designed to measure typing speed and display real-time WPM & Input Accuracy calculations.

## Technologies used:
- #### Jetpack Compose: Modern toolkit for building native Android UI.
- #### Kotlin: The programming language used for Android development.
- #### Coroutines: For managing asynchronous tasks efficiently.
- #### Flows: Handling streams of data asynchronously in Kotlin.
- #### RoomDB: Local database solution for persistence and caching.
- #### JUnit: Unit testing framework to ensure code quality.

## Solution:

Entities:
- Database: RoomDB database that holds analytical data about the user's keystroke input. Along side other data such as Session and User data.
  Data models:

| **Table Name**          | **Columns**                                                                                   |
|-------------------------|-----------------------------------------------------------------------------------------------|
| **KeyStroke**            | `uuid` (String, Primary Key) – Unique ID for each keystroke                                    |
|                         | `sessionId` (String) – Session ID associated with the keystroke                                |
|                         | `keyPressTime` (Long, Nullable) – Timestamp when the key was pressed                           |
|                         | `keyReleaseTime` (Long) – Timestamp when the key was released                                  |
|                         | `keyCode` (String) – Code representing the key pressed                                         |
|                         | `phoneOrientation` (ScreenOrientation) – Device orientation during the keystroke               |
|                         | `isCorrect` (Boolean) – Whether the key pressed was correct                                    |
| **Session**              | `uuid` (String, Primary Key) – Unique ID for each session                                      |
|                         | `userId` (String) – ID of the user who participated in the session                             |
|                         | `sessionStartTime` (Long, Nullable) – Session start timestamp                                  |
|                         | `sessionEndTime` (Long, Nullable) – Session end timestamp                                      |
|                         | `wordPerMinute` (Float, Nullable) – Words-per-minute score                                     |
| **User**                 | `uuid` (String, Primary Key) – Unique ID for each user                                         |
|                         | `username` (String) – Username for the user                                                    |
|                         | `createdTime` (Long) – Account creation timestamp                                              |
|                         | `highScore` (Float, Nullable) – User's highest score (queried separately, not stored directly)  |

- `WPMCounter`: This class holds the logic for calulcaating the user WPM (Word per minute). It also handles pausing the session when the user is inactive after 2 seconds.
- `WPMRepository`: Repository interface for handling WPM-related database operation. Includes methods for user, session, and keystroke management.
- UI/ViewModel: The ViewModel serves as the bridge between the UI and the repository. It interacts with the `WPMRepository` to manage the user’s typing sessions, keystrokes, and WPM calculations.

These components interact with each other based on this graph:

![Untitled Diagram drawio](https://github.com/user-attachments/assets/eab67556-de1e-4517-9258-797287ab1deb)


The application has two screens:
- #### [UsernameScreen](app/src/main/java/com/omarhawari/wpm_counter/screens/username/UsernameScreen.kt): The user can input their username. They can also choose an existing (cached) username to start a new WPM Counter session with. The list of users also display the user's High Score if any.
- ![image](https://github.com/user-attachments/assets/5ba21ce8-f64f-45d6-81e6-6d5e69f5ae56)
- 
- #### [WPMCounterScreen](app/src/main/java/com/omarhawari/wpm_counter/screens/wpm_counter/WPMCounterScreen.kt): This screen displayes the WPM 
