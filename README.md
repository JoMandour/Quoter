## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Build a Windows installer

`jpackage` is included with the JDK, not the Java runtime. This project requires JDK 27. In PowerShell, point `JAVA_HOME` to your JDK and add its `bin` folder to `PATH`:

```powershell
$env:JAVA_HOME = 'C:\Path\To\jdk-27'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
jpackage --version
```

For the current PowerShell session, if `JAVA_HOME` is already set correctly, only run:

```powershell
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
```

Rebuild the JAR after changing the application sources. Then copy the newly built JAR and dependencies into the jpackage input folder before creating the installer:

```powershell
mvn package
New-Item -ItemType Directory -Force target\jpackage-input\libs | Out-Null
Copy-Item -Force target\quotes-library-1.0-SNAPSHOT.jar target\jpackage-input\
Copy-Item -Force target\libs\*.jar target\jpackage-input\libs\
jpackage --type exe --name Quoter --app-version 1.0.2 --vendor "JoMandour" --icon src\main\resources\icon.ico --input target\jpackage-input --main-jar quotes-library-1.0-SNAPSHOT.jar --main-class Launcher --dest target\dist --win-dir-chooser --win-menu --win-shortcut
```

`Quoter-1.0.2.exe` is the installer. After installing, launch **Quoter** from the Start menu or desktop shortcut.
The app window uses `src\main\resources\icon.png`; the installer and Windows app launcher use `src\main\resources\icon.ico`.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).
