# build.sh — line-by-line explanation

[Guide index](README.md) · [Exact source snapshot](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/build.sh)

Path: `MiniOS-Java/build.sh`. Source revision: `dba2d32`.

Build/launch configuration. This file selects tools and working directories; the OS calculations remain in the Java engines.

Each row preserves one physical source line. Dense lines can contain several statements. Blank lines and closing delimiters are included so numbering matches your editor.

## Commands / configuration

| Line | Original code | Explanation |
|---:|---|---|
| [L1](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/build.sh#L1) | <code>#!/bin/sh</code> | Comment, interpreter header or Apache license text; not an OS simulation step. |
| [L2](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/build.sh#L2) | <code>set -eu</code> | Stop on a failed command (-e) or an unset variable (-u), avoiding packaging after a failed test. |
| [L3](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/build.sh#L3) | <code>cd &quot;$(dirname &quot;$0&quot;)&quot;</code> | Change to the script&#x27;s own project directory (or its javafx subdirectory), so launching from another working folder still finds the files. |
| [L4](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/build.sh#L4) | <code>mkdir -p out</code> | Create the compiled-class output directory if it is missing. |
| [L5](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/build.sh#L5) | <code>javac -encoding UTF-8 -source 8 -target 8 -d out ./*.java</code> | Compile the parent-folder Java sources as UTF-8 into out, targeting Java8-compatible source/bytecode for the Swing version. |
| [L6](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/build.sh#L6) | <code>java -Djava.awt.headless=true -cp out Tests</code> | Run the reference tests with no physical desktop required; a nonzero exit stops the build. |
| [L7](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/build.sh#L7) | <code>java -Djava.awt.headless=true -cp out RubricTests</code> | Run the flexible-input and module/algorithm coverage checks. |
| [L8](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/build.sh#L8) | <code>java -Djava.awt.headless=true -cp out AuditRegressionTests</code> | Run the31 audit regressions, including large-total and disk-head boundaries. |
| [L9](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/build.sh#L9) | <code>jar cfe MiniOS.jar MiniOS -C out .</code> | Package compiled classes into MiniOS.jar and set MiniOS as its Swing entry point. This is not the JavaFX launcher. |
