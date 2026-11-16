# Antigravity IDE: step-by-step setup

This is a Java Swing desktop application. No Maven, Gradle, web server, API key or third-party application library is required. Use Antigravity's terminal; Java editor/debug extensions are optional and their availability depends on your IDE version.

1. Install a **JDK 21** (a full Java Development Kit, not only a JRE), such as Microsoft OpenJDK or Eclipse Temurin. Enable the installer's PATH/JAVA_HOME options if available. Restart Antigravity afterward.
2. Extract `MiniOS-Java-Rubric.zip`. Keep the entire `MiniOS-Java` folder, including `testdata` and all `.java` files.
3. In Antigravity, use **Open Folder** and select `MiniOS-Java` itself.
4. Open the integrated terminal in that folder. Check:
   ```text
   java -version
   javac -version
   jar --version
   ```
   All three must work. The build scripts support JDK 8+; JDK 21 is recommended. The generated application targets Java 8 bytecode.
5. Build, test and create the runnable JAR:
   - Windows PowerShell: `cmd /c build.bat`
   - Windows Command Prompt: `build.bat`
   - macOS/Linux: `sh build.sh`
   The scripts stop if compilation or any test suite fails. Expected summaries: `137/137 tests passed`, `138/138 rubric checks passed`, and `Audit regressions: 31 passed, 0 failed`.
6. Launch the desktop window:
   ```text
   java -jar MiniOS.jar
   ```
   Alternatively, Windows: `cmd /c run.bat`; macOS/Linux: `sh run.sh`.
7. Select **CPU Scheduling**, click **Small**, then **Run**. Select other algorithms and run again. Repeat for each required module. **Large** and **Edge** provide larger and boundary examples; the samples are editable.
8. Click **Help** for operating instructions. For development and assessment planning, read `RUBRIC-COVERAGE.md` for assessment mapping, known answers and the presentation plan.
9. Save results with each module's **Save** button. Save test evidence from the terminal:
   ```text
   java -Djava.awt.headless=true -cp out Tests > original-test-results.txt
   java -Djava.awt.headless=true -cp out RubricTests > rubric-test-results.txt
   ```
10. Submit the source files, rebuilt `MiniOS.jar`, `testdata`, documentation and test evidence. Add your group/member names to your report. Prepare screenshots and rehearse the ten-minute demo.

## Optional Java editor support

If Antigravity offers a Java language/debug extension, install it and select the installed JDK as its Java runtime. Open `MiniOS.java` and use its Run/Debug action if offered. Terminal commands above remain the supported fallback; no IDE-specific extension or generated project wizard is needed. Do not run `Tests.java` to start the GUI.

## Troubleshooting

- **javac or jar not found:** install the full JDK and put its `bin` directory on PATH. Restart the IDE. `java` alone only proves a runtime exists.
- **Wrong Java version:** check `where java` / `where javac` on Windows, or `which java` / `which javac` on macOS/Linux. Both should point to the intended JDK.
- **Could not find MiniOS.jar:** run from the project folder and build first.
- **testdata/cases.txt missing:** open the extracted project root and retain `testdata`; the tests use that relative path.
- **HeadlessException / no DISPLAY:** Swing needs a local graphical desktop. Run the GUI on your own computer. Headless tests can run on a remote terminal.
- **Results look unchanged after editing:** rerun the build before launching the JAR.
- **Large simulations take time:** CPU scheduling stores each tick; page replacement stores frame snapshots. Dataset size is bounded by available memory and computation, not a fixed number of rows. Use moderate bursts for large process-count demos.
- **Windows SmartScreen or blocked download:** use your system's normal trusted download/unblock process; the application is an unsigned student JAR.

## JavaFX conversation interface

The new frog/bunny interface has a separate [JavaFX setup guide](javafx/README.md). Install JDK 21, open `MiniOS-Java/javafx`, then run `.\mvnw.cmd test` and `.\mvnw.cmd javafx:run` on Windows. The included wrapper downloads Maven automatically. The instructions above and `MiniOS.jar` refer to the original Swing interface.
