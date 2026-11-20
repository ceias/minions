# build.bat — line-by-line explanation

[Guide index](README.md) · [Exact source snapshot](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/build.bat)

Path: `MiniOS-Java/build.bat`. Source revision: `dba2d32`.

Build/launch configuration. This file selects tools and working directories; the OS calculations remain in the Java engines.

Each row preserves one physical source line. Dense lines can contain several statements. Blank lines and closing delimiters are included so numbering matches your editor.

## Commands / configuration

| Line | Original code | Explanation |
|---:|---|---|
| [L1](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/build.bat#L1) | <code>@echo off</code> | Turn off command echoing so the Windows console shows results rather than every script line. |
| [L2](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/build.bat#L2) | <code>cd /d &quot;%~dp0&quot;</code> | Change to the script&#x27;s own project directory (or its javafx subdirectory), so launching from another working folder still finds the files. |
| [L3](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/build.bat#L3) | <code>if not exist out mkdir out</code> | Create the compiled-class output directory if it is missing. |
| [L4](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/build.bat#L4) | <code>javac -encoding UTF-8 -source 8 -target 8 -d out *.java</code> | Compile the parent-folder Java sources as UTF-8 into out, targeting Java8-compatible source/bytecode for the Swing version. |
| [L5](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/build.bat#L5) | <code>if errorlevel 1 exit /b 1</code> | Stop the Windows build with a failure exit if the preceding command failed. |
| [L6](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/build.bat#L6) | <code>java -Djava.awt.headless=true -cp out Tests</code> | Run the reference tests with no physical desktop required; a nonzero exit stops the build. |
| [L7](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/build.bat#L7) | <code>if errorlevel 1 exit /b 1</code> | Stop the Windows build with a failure exit if the preceding command failed. |
| [L8](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/build.bat#L8) | <code>java -Djava.awt.headless=true -cp out RubricTests</code> | Run the flexible-input and module/algorithm coverage checks. |
| [L9](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/build.bat#L9) | <code>if errorlevel 1 exit /b 1</code> | Stop the Windows build with a failure exit if the preceding command failed. |
| [L10](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/build.bat#L10) | <code>java -Djava.awt.headless=true -cp out AuditRegressionTests</code> | Run the31 audit regressions, including large-total and disk-head boundaries. |
| [L11](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/build.bat#L11) | <code>if errorlevel 1 exit /b 1</code> | Stop the Windows build with a failure exit if the preceding command failed. |
| [L12](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/build.bat#L12) | <code>jar cfe MiniOS.jar MiniOS -C out .</code> | Package compiled classes into MiniOS.jar and set MiniOS as its Swing entry point. This is not the JavaFX launcher. |
| [L13](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/build.bat#L13) | <code>if errorlevel 1 exit /b 1</code> | Stop the Windows build with a failure exit if the preceding command failed. |
