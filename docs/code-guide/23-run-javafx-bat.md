# run-javafx.bat — line-by-line explanation

[Guide index](README.md) · [Exact source snapshot](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/run-javafx.bat)

Path: `MiniOS-Java/run-javafx.bat`. Source revision: `dba2d32`.

Build/launch configuration. This file selects tools and working directories; the OS calculations remain in the Java engines.

Each row preserves one physical source line. Dense lines can contain several statements. Blank lines and closing delimiters are included so numbering matches your editor.

## Commands / configuration

| Line | Original code | Explanation |
|---:|---|---|
| [L1](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/run-javafx.bat#L1) | <code>@echo off</code> | Turn off command echoing so the Windows console shows results rather than every script line. |
| [L2](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/run-javafx.bat#L2) | <code>cd /d &quot;%~dp0javafx&quot;</code> | Change to the script&#x27;s own project directory (or its javafx subdirectory), so launching from another working folder still finds the files. |
| [L3](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/run-javafx.bat#L3) | <code>call mvnw.cmd javafx:run</code> | Use the included Maven Wrapper to resolve JavaFX dependencies, compile and launch MiniOSFX. Windows call returns control to the batch script afterward. |
