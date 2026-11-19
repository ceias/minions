# JavaFX validation

- Maven Wrapper 3.9.9: dependency download, compilation and test execution verified on Linux with Java 21.
- JavaFX 21.0.2 scene launched with the test-only Monocle software renderer. A 1600×1200 virtual screen accommodates the application window.
- `ConversationTest`: 5 tests passed, 0 failures/errors/skips. Covers every module's real result path; commands and input draft preservation; invalid-input recovery and subsequent execution. The fourth test executes the 31 audit algorithm regressions. The fifth verifies the advanced CPU shortcut, hidden layout/dropdown/settings, input preservation, MLFQ execution after revealing, and safe fallback when hiding again.
- Original `Tests`: 137/137 passed against the Maven-compiled shared engines.
- `RubricTests`: 138/138 passed against the Maven-compiled shared engines.
- Rendered JavaFX scene inspected; `javafx/preview.png` is a snapshot of the running scene.
- Windows launch script reviewed but not executed on Windows. Native Windows file chooser, clipboard, window controls and local Antigravity installation require local verification.

The JavaFX interface reuses existing algorithms directly. The root MiniOS.jar remains the previous Swing executable. The JavaFX version runs with the Maven Wrapper; it is not packaged as a standalone Windows executable.
