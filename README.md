# Soundboard-G

A desktop soundboard application built with JavaFX that allows audio device selection, audio loopback from microphone to speakers, and playback of audio files with an interactive GUI.

<img width="600" height="401" alt="image" src="https://github.com/user-attachments/assets/fb0616c9-e450-4bc6-a159-b7c8ef7a2730" />



## Features
- Lists all audio mixers available on your system.
- Select and toggle audio loopback between microphone and speakers.
- Supports playback of audio files via selected output device.
- Uses JavaFX FXML layouts for UI and CSS for styling.

## Technologies and Requirements
- Java 21 (tested with OpenJDK 21)
- JavaFX 21 (controls and FXML modules)
- Java Sound API for audio capture and playback
- Compatible with Linux systems where JavaFX and Java Sound API are available

## Setup and Installation
Install required Java environment:

`sudo apt install openjdk-21-jdk openjfx`

Compile project source files:
`(javac --module-path /usr/share/openjfx/lib --add-modules javafx.controls,javafx.fxml src/application/*.java)`

Place (config.properties) in the project root directory (Soundboard/).

Run the application from the project root directory:
`(java --module-path /usr/share/openjfx/lib --add-modules javafx.controls,javafx.fxml -cp src application.Main)`

## Usage Guide
On launch, the available audio devices populate in a dropdown.
Choose a device and toggle loopback to start or stop audio routing from mic to speakers.
Play audio files using the interface.
Switch between scenes with provided buttons.
Configuration persists between sessions.

## Notes
Ensure your Java and JavaFX versions match for smooth running.
(config.properties) file must contain valid indices for audio devices.
The application requires access to audio hardware and related permissions.

## License
MIT License

