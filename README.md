# QuizScan Android App
An Android Kotlin quiz application built for the QR-based quiz practical.

## Features
- Firebase Authentication with Google Sign-In
- Google Code Scanner using `GmsBarcodeScanning.getClient(context).startScan()`
- QR payload format: `category=9;difficulty=easy`
- Manual category and difficulty selection
- Open Trivia DB categories and 10 multiple-choice questions
- Four shuffled answers, HTML entity decoding, correct/incorrect colour feedback
- Question progress indicator, score result, and Play Again
- Room local score history shown with RecyclerView

## Setup
1. Open this repository in Android Studio (JDK 17, Android SDK 36).
2. Sync Gradle.
3. For Google Sign-In, create a Firebase project, add Android package `com.quizscan.app`, enable Google provider, add your debug SHA-1/SHA-256, download `google-services.json`, and place it in `app/google-services.json`.
4. Run on an Android device/emulator with internet access.

The Firebase configuration file is deliberately ignored by Git. Trivia and local history do not require Firebase configuration; Google login does.

## APIs
- Categories: https://opentdb.com/api_category.php
- Questions: https://opentdb.com/api.php?amount=10&category=9&difficulty=easy&type=multiple

## QR examples
`category=9;difficulty=easy`
`category=18;difficulty=medium`
`category=21;difficulty=hard`

Score history is stored locally on the device using Room.
