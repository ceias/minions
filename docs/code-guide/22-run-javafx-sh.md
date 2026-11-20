# run-javafx.sh — line-by-line explanation

[Guide index](README.md) · [Exact source snapshot](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/run-javafx.sh)

Path: `MiniOS-Java/run-javafx.sh`. Source revision: `dba2d32`.

Build/launch configuration. This file selects tools and working directories; the OS calculations remain in the Java engines.

Each row preserves one physical source line. Dense lines can contain several statements. Blank lines and closing delimiters are included so numbering matches your editor.

## Commands / configuration

| Line | Original code | Explanation |
|---:|---|---|
| [L1](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/run-javafx.sh#L1) | <code>#!/bin/sh</code> | Comment, interpreter header or Apache license text; not an OS simulation step. |
| [L2](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/run-javafx.sh#L2) | <code>set -eu</code> | Stop on a failed command (-e) or an unset variable (-u), avoiding packaging after a failed test. |
| [L3](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/run-javafx.sh#L3) | <code>cd &quot;$(dirname &quot;$0&quot;)/javafx&quot;</code> | Change to the script&#x27;s own project directory (or its javafx subdirectory), so launching from another working folder still finds the files. |
| [L4](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/run-javafx.sh#L4) | <code>sh ./mvnw javafx:run</code> | Use the included Maven Wrapper to resolve JavaFX dependencies, compile and launch MiniOSFX. Windows call returns control to the batch script afterward. |
