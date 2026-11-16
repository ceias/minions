# Mini OS: JavaFX conversation interface

A JavaFX 21 desktop interface using the supplied frog/bunny sprite sheet, with local command routing and real results from the existing Java engines. It is not an AI chatbot and needs no API key. The artwork is used through ImageView viewports, preserving the original sheet. The layout follows the supplied reference with cream/rose panels and grouped algorithm buttons; a system monospace font is used because no font file was supplied.

## Antigravity on Windows

1. Install **JDK 21**. For example, in PowerShell: `winget install --id Microsoft.OpenJDK.21 --exact --source winget`. Enable PATH and JAVA_HOME in the installer if needed.
2. Download the updated repository ZIP from GitHub and extract it to a new folder. The included **Maven Wrapper** downloads Maven automatically; a separate Maven installation is optional.
3. Completely close and reopen Antigravity so it reads the updated PATH.
4. Open the extracted repository folder in Antigravity. In its terminal, verify:
   ```powershell
   java -version
   javac -version
   ```
   Both must report JDK 21. If needed, point JAVA_HOME to the installed JDK directory and add its `bin` directory to PATH, then restart the IDE.
5. If your terminal is in `minions-main`, run:
   ```powershell
   cd .\MiniOS-Java\javafx
   .\mvnw.cmd test
   .\mvnw.cmd javafx:run
   ```
   If you opened `MiniOS-Java` instead, use `cd .\javafx`. `Get-ChildItem` lists the current folder contents. Run commands separately; don't append backslashes.
6. The frog chat window opens. Choose **SRTF**, edit the process data, then click **Run simulation**. Results include the existing text Gantt chart and per-process metrics.

From the `MiniOS-Java` folder you can also use `cmd /c run-javafx.bat`. On macOS/Linux, install JDK 21 and run `sh run-javafx.sh` from that folder.

The first run requires Internet access to Maven Central to download JavaFX and build plugins. No separate JavaFX SDK installation or manually configured module path is needed. `mvn package` builds a dependency-based JAR; it is not a standalone executable. Use `.\mvnw.cmd javafx:run` on Windows or `sh ./mvnw javafx:run` on macOS/Linux. If Maven is already installed, `mvn javafx:run` also works. The existing root `MiniOS.jar` continues to launch the Swing version.

## Controls

- Algorithm buttons open editable cards with all original options and variable-length input.
- Small, Large and Edge buttons load the existing sample datasets. A new module starts with its Small sample, clearly labeled. Your edits persist when switching modules.
- **Run simulation** uses the actual module runner on a background thread. One simulation runs at a time. Large tick-based workloads still consume memory and CPU.
- Results appear in a scrollable monospace area with Copy/Save controls.
- Enter or Send routes commands: `srtf`, `run srtf`, `run lru`, `banker`, `deadlock`, `paging`, `segmentation`, `fragmentation`, `disk`, `multiprogramming`, `run`, `help`, `home`, `clear`.
- `run <algorithm>` executes the data currently in that algorithm's module card. If not previously opened, it executes the visible Small sample. `run` executes the active card.
- Help (`?`) explains module inputs and available commands. Home hides the current editor and shows guidance. Clear removes chat history but keeps module input drafts.
- Title bar controls minimize, maximize/restore and close; drag its background to move the window. Maximize/restore provides window sizing for this custom undecorated frame.

## Validation

`sh ./mvnw test` (Windows: `.\mvnw.cmd test`) launches the actual JavaFX scene using the test-only Monocle software renderer. It exercises every module's result path, command routing, persistent inputs and recovery after invalid input, plus the 31 audit algorithm regressions. These tests do not validate Windows-specific clipboard/file picker integration. On a normal run Maven uses your operating system's native JavaFX renderer, not Monocle.

The original suites are unchanged and remain available from the parent folder:

```text
javac -d out *.java
java -Djava.awt.headless=true -cp out Tests
java -Djava.awt.headless=true -cp out RubricTests
java -Djava.awt.headless=true -cp out AuditRegressionTests
```

## Project structure

- `src/main/java/MiniOSFX.java`: JavaFX scene, input cards, command routing and background execution.
- `src/main/resources/mini-os.css`: UI styling.
- `src/main/resources/assets/ui-sheet.png`: original supplied artwork.
- `src/test/java/ConversationTest.java`: JavaFX flow tests.
- `pom.xml`: JavaFX and test dependencies. Maven compiles the parent engine sources directly, without duplicating them.

The archive's Claude/HTML/JavaScript instructions were treated as design reference. This implementation uses JavaFX and the existing Java algorithms.
