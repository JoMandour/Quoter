# Quoter

A small desktop quote library built with **JavaFX** and **SQLite**. Browse, search,
filter, add, and delete quotes in a paper-and-ink styled interface.

## Features

- Card-based quote library with live text search and category filtering
- Add new quotes with book, author, and category
- Delete quotes with a confirmation prompt
- Categories are suggested from values already in the database
- Quotes are stored locally in SQLite

## Requirements

- **JDK 27** (the project targets Java 27)
- **Maven 3.9+**

Check your setup:

```powershell
mvn -v
```

`mvn -v` should report Java 27.

## Run the app

```powershell
mvn javafx:run
```

## Build and test

```powershell
mvn clean package   # compile, run tests, build the JAR
mvn test            # run the unit tests only
```

The executable JAR is written to `target/quotes-library-1.0-SNAPSHOT.jar`.

## Project structure

```
src/
├── main/
│   ├── java/com/quoter/
│   │   ├── Launcher.java          # JVM entry point (non-JavaFX launcher)
│   │   ├── QuoterApp.java         # JavaFX application / window setup
│   │   ├── AppContext.java        # shared QuoteDao instance for FXML controllers
│   │   ├── model/Quote.java       # immutable quote record
│   │   ├── dao/QuoteDao.java      # SQLite CRUD operations
│   │   ├── db/Database.java       # connection handling and DB path
│   │   ├── db/DataAccessException.java
│   │   └── ui/                    # FXML controllers and dialogs
│   └── resources/                 # FXML views, styles.css, icons
└── test/java/com/quoter/dao/      # JUnit 5 tests against a temporary database
```

## Database

The app stores quotes in:

```
%USERPROFILE%\.quoter\Quotes.db
```

The data layer is created automatically on first launch. Older versions used a
`Bookshelf` table; on startup any existing rows are migrated into the new
`quotes` table and the legacy table is removed.

## Build a Windows installer

`jpackage` ships with the JDK, not the Java runtime, so a full JDK is required.

```powershell
$env:JAVA_HOME = 'C:\Path\To\jdk-27'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
jpackage --version
```

Rebuild the JAR, copy it and its dependencies into the jpackage input folder,
then create the installer:

```powershell
mvn clean package
New-Item -ItemType Directory -Force target\jpackage-input\libs | Out-Null
Copy-Item -Force target\quotes-library-1.0-SNAPSHOT.jar target\jpackage-input\
Copy-Item -Force target\libs\*.jar target\jpackage-input\libs\
jpackage --type exe --name Quoter --app-version 1.0.2 --vendor "JoMandour" `
  --icon src\main\resources\icon.ico --input target\jpackage-input `
  --main-jar quotes-library-1.0-SNAPSHOT.jar --main-class com.quoter.Launcher `
  --java-options "--enable-native-access=ALL-UNNAMED,javafx.graphics" `
  --dest target\dist --win-dir-chooser --win-menu --win-shortcut
```

`Quoter-1.0.2.exe` is the installer. After installing, launch **Quoter** from the
Start menu or desktop shortcut.

The app window uses `src\main\resources\icon.png`; the installer and Windows app
launcher use `src\main\resources\icon.ico`.
