# GitHub Users App

This project is an Android application that fetches a list of GitHub users, displays them in a paginated list, and allows users to view detailed profiles. The app is designed with a focus on best practices in Android development, including the use of Kotlin, Room for data persistence, and Jetpack Compose for UI.




1. Code is written in Kotlin using Android Studio.
2. Data is persisted using Room.
3. UI is implemented using Jetpack Compose.
4. All media is cached on disk.
5. Unit tests are written for data processing logic, models, and Room models.
6. Screen rotation is supported.
7. MVVM pattern is used with Kotlin Coroutines.

## GitHub Users

1. The app works offline if data has been previously loaded.
2. It handles no internet scenarios and displays appropriate UI indicators.
3. Automatically retries loading data once the connection is available.
4. Displays persisted data first, then fetches new data from the backend.

## Users List

1. The GitHub users list is obtained from `https://api.github.com/users?since=0`.
2. Supports pagination using the Android Paging Library.
3. Dynamically determines page size based on the first batch of items.
4. Displays a spinner while loading data.
5. Every fourth user's avatar has inverted colors.
6. Displays a note icon if a note is saved for the user.
7. Supports local search by username and note, case-insensitive, and includes precise and partial matches.

## Profile

1. Profile info is obtained using the GitHub username from `https://api.github.com/users/username`.
2. The view includes the user's avatar as a header, followed by information fields.
3. Allows retrieval and saving of note data to the local database.



## Additional Features

1. Added shimmer effects for empty views.
2. implemented exponential backoff when retrying data loading.
3. Support dark (night) mode.

## Setup and Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/jk2pr/test.io.git
2. Open the project in Android Studio.
3. Build the project to download dependencies.

## Usage

1. Run the app on an emulator or a real device.
2. The app will fetch and display a list of GitHub users.
3. Select a user to view their profile details.

# Testing

Run unit tests:
  
     ./gradlew test
     
Run Android instrumented tests (androidTest)

     ./gradlew androidTest



