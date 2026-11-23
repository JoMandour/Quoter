# Quoter

A distraction-free desktop quote library built with JavaFX and SQLite, styled with a classic newsprint broadsheet aesthetic.

## Features

- **Broadsheet Editorial UI:** Warm newsprint cream palette (`#F4F1EA`), Georgia serif typography, and minimalist hairline borders designed for comfortable reading.
- **Instant Search & Filtering:** Filter saved quotes in real time by quote text, book title, author, or category.
- **Zero-Config Local Storage:** Automatically initializes and persists your library in an embedded SQLite database (`~/.quoter/Quotes.db`)—no external database server required.
- **Standalone Windows Installer:** Bundled with its own custom Java runtime via `jpackage` and WiX Toolset so end users can install and run it without installing Java.

## Tech Stack

- **Language:** Java 27
- **UI Framework:** JavaFX 26 (FXML + Custom CSS)
- **Database:** SQLite (`sqlite-jdbc`)
- **Build & Packaging:** Maven, `jpackage`, WiX Toolset v3.14

## Installation (Windows)

1. Go to the [**Releases**](../../releases) page and download **`Quoter-1.0.0.exe`**.
2. Run the installer. *(If Windows SmartScreen displays a warning for an unrecognized app, click **More info** → **Run anyway**.)*
3. Launch **Quoter** from your Desktop or Start Menu shortcut.

## Building from Source

### Prerequisites
- JDK 27 (with `jpackage` on your `PATH`)
- Apache Maven 3.9+
- WiX Toolset v3.14 *(only required if building the `.exe` installer)*

### Run Locally
```bash
git clone [https://github.com/JoMandour/quotes-library.git](https://github.com/JoMandour/quotes-library.git)
cd quotes-library
mvn clean javafx:run
