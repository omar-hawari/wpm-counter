# WPM Counter

A Words Per Minute (WPM) counter built using Jetpack Compose, designed to measure typing speed and display real-time WPM and Input Accuracy calculations.

## Technologies Used:
- **Jetpack Compose**: UI toolkit for building native Android apps.
- **Kotlin**: The programming language for Android development.
- **Coroutines**: Efficient management of asynchronous tasks.
- **Flows**: Handling streams of data asynchronously in Kotlin.
- **RoomDB**: Local database solution for persistence and caching.
- **JUnit**: Unit testing framework to ensure code quality.

## Solution:

### Entities:
The application uses a RoomDB database that holds analytical data about the user's keystroke input, alongside data regarding sessions and users.

#### Data Models:

| **Table Name**          | **Columns**                                                                                   |
|-------------------------|-----------------------------------------------------------------------------------------------|
| **KeyStroke**           | `uuid` (String, Primary Key) – Unique ID for each keystroke                                  |
|                         | `sessionId` (String) – Session ID associated with the keystroke                              |
|                         | `keyPressTime` (Long, Nullable) – Timestamp when the key was pressed                         |
|                         | `keyReleaseTime` (Long) – Timestamp when the key was released                                |
|                         | `keyCode` (String) – Code representing the key pressed                                       |
|                         | `phoneOrientation` (ScreenOrientation) – Device orientation during the keystroke             |
|                         | `isCorrect` (Boolean) – Whether the key pressed was correct                                  |
| **Session**             | `uuid` (String, Primary Key) – Unique ID for each session                                    |
|                         | `userId` (String) – ID of the user who participated in the session                           |
|                         | `sessionStartTime` (Long, Nullable) – Session start timestamp                                |
|                         | `sessionEndTime` (Long, Nullable) – Session end timestamp                                    |
|                         | `wordPerMinute` (Float, Nullable) – Words-per-minute score                                   |
| **User**                | `uuid` (String, Primary Key) – Unique ID for each user                                       |
|                         | `username` (String) – Username for the user                                                  |
|                         | `createdTime` (Long) – Account creation timestamp                                            |
|                         | `highScore` (Float, Nullable) – User's highest score (queried separately, not stored directly) |

### Core Components:
- **WPMCounter**: This class contains the logic for calculating the user's WPM (Words Per Minute). It also handles pausing the session when the user is inactive for 2 seconds.
- **WPMRepository**: Repository interface for managing WPM-related database operations. It includes methods for user, session, and keystroke management.
- **UI/ViewModel**: The ViewModel serves as the bridge between the UI and the repository, interacting with the `WPMRepository` to manage the user’s typing sessions, keystrokes, and WPM calculations.

These components interact with each other as shown in the diagram below:

![Architecture Diagram](https://github.com/user-attachments/assets/eab67556-de1e-4517-9258-797287ab1deb)

### Application Screens:
- #### [UsernameScreen](app/src/main/java/com/omarhawari/wpm_counter/screens/username/UsernameScreen.kt): 
  Users can input their username or choose an existing (cached) username to start a new WPM Counter session. The list of users displays each user's high score, if available.
  
  ![Username Screen](https://github.com/user-attachments/assets/5ba21ce8-f64f-45d6-81e6-6d5e69f5ae56)

- #### [WPMCounterScreen](app/src/main/java/com/omarhawari/wpm_counter/screens/wpm_counter/WPMCounterScreen.kt): 
  This screen displays the following information:
  1. Current username.
  2. Real-time WPM counter for the session.
  3. Real-time accuracy for the session.
  4. Text to be inputted, highlighting the current cursor location.
  5. Inputted text by the user, highlighting correct and incorrect inputs.

  ![WPM Counter Screen](https://github.com/user-attachments/assets/2b194838-812f-4117-9efd-2ed1c15f34e4)

## Formulas:

### Word Per Minute (WPM):
To calculate WPM, we use the following formula:
```markdown
WPM = (Total Characters Typed / 5) / (Elapsed Time in Minutes)
```

Where:
- **Total Characters Typed**: The total number of characters typed during the session.
- **5**: Assumes an average word consists of 5 characters.
- **Elapsed Time in Minutes**: The total duration of the typing session in minutes.

This formula provides a normalized WPM score based on the number of characters typed and the session's elapsed time.


### (EXTRA) Accuracy: we calculate the accuracy using this formula: 
Accuracy = Correct Inputted KeyStrokes / Total Inputted KeyStrokes

The formula can also be viewed in [KeyStrokesDao](app/src/main/java/com/omarhawari/wpm_counter/database/daos/KeyStrokeDao.kt) with this query:
```sql
SELECT
    CASE 
        WHEN COUNT(*) = 0 THEN 0 
        ELSE COUNT(*) * 1.0 / (SELECT COUNT(*) FROM $TABLE_NAME_KEYSTROKE WHERE session_id = :sessionId) 
    END AS ratio 
FROM $TABLE_NAME_KEYSTROKE 
WHERE session_id = :sessionId AND isCorrect = 1
```

## EXTRAS:
- One of the extra requirements was to detect the press and release time for each keystrokes. This is only possible for physical keyboard input, and not for soft keyboard input. So to approximate, this is how it was calculated:

```markdown
keyStroke[n].keyPresTime = keyStroke[n-1].keyReleaseTime
```


---------------------
