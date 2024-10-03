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

The application has two screens:
- #### [UsernameScreen](app/src/main/java/com/omarhawari/wpm_counter/screens/username/UsernameScreen.kt): The user can input their username. They can also choose an existing (cached) username to start a new WPM Counter session with. The list of users also display the user's High Score if any.
- ![image](https://github.com/user-attachments/assets/5ba21ce8-f64f-45d6-81e6-6d5e69f5ae56)
- #### [WPMCounterScreen](app/src/main/java/com/omarhawari/wpm_counter/screens/wpm_counter/WPMCounterScreen.kt): This screen displayes 
