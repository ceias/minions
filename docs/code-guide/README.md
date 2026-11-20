# Mini OS — file-by-file, line-by-line study guide

This guide explains the actual project at source commit **dba2d32**, including the MLQ/MLFQ keyboard shortcut. It contains **28 file walkthroughs covering 2,712 physical source/configuration lines**, including all **17 Java files (2,209 lines)**. Each source row has its original line number, code and explanation. Source links are pinned to that commit, so later repository edits will not silently change the explained code.

## How to use the guide

1. Start with [Architecture and Java glossary](architecture.md).
2. Read the numbered file walkthroughs below in order. Use the original line number to find the same line in VS Code.
3. Practice the [worked algorithm examples](worked-examples.md), then explain them aloud without reading the answer.
4. For an offline reader with file selection, search and line navigation, download the repository ZIP and open **docs/code-guide/guide.html** in Chrome or Edge. It is self-contained and needs neither Java nor a server. GitHub's HTML file view displays source rather than running the reader.

Dense source lines sometimes contain several statements; their notes explain the combined operation. Blank lines, imports, comments and closing delimiters are included to preserve exact numbering. Detailed algorithm/UI notes are complemented by syntax explanations for repetitive declarations, layout setters and test assertions. Generated Maven wrapper lines are described as bootstrap operations; they are not OS simulation algorithms.

## Application and helpers

- [01 — MiniOSFX.java](01-MiniOSFX-java.md): JavaFX window, chat, commands, hidden algorithms, dynamic input, background tasks and results.
- [02 — Modules.java](02-Modules-java.md): module catalog, form fields, samples and runner callbacks.
- [03 — Util.java](03-Util-java.md): input validation, tokenization and report formatting.

## Algorithm implementations

- [04 — Cpu.java](04-Cpu-java.md): FCFS, SJF, SRTF, priority variants, RR, MLQ, MLFQ and metrics.
- [05 — Banker.java](05-Banker-java.md): Need, safety, safe sequence and requests.
- [06 — Deadlock.java](06-Deadlock-java.md): current deadlock detection and greedy recovery.
- [07 — Pages.java](07-Pages-java.md): nine replacement policies, frames, reference/modified bits and history.
- [08 — Memory.java](08-Memory-java.md): paging, segmentation and fixed-block fragmentation.
- [09 — Disk.java](09-Disk-java.md): six disk policies and head movement.
- [10 — Multi.java](10-Multi-java.md): memory admission, round-robin execution and utilization.

## Supporting code and tests

- [11 — Samples.java](11-Samples-java.md): larger sample data and settings.
- [12 — AppHelp.java](12-AppHelp-java.md): operating instructions displayed by Help.
- [13 — MiniOS.java](13-MiniOS-java.md): original Swing interface and root JAR entry point.
- [14 — Tests.java](14-Tests-java.md): known answers, reference-file parser and Swing panel tests.
- [15 — RubricTests.java](15-RubricTests-java.md): flexible input, sample combinations and boundaries.
- [16 — AuditRegressionTests.java](16-AuditRegressionTests-java.md): audit fixes and larger inputs.
- [17 — ConversationTest.java](17-ConversationTest-java.md): JavaFX threading, commands, validation and shortcut tests.

## Styling, compilation and launch

- [18 — mini-os.css](18-mini-os-css.md): JavaFX styles.
- [19 — pom.xml](19-pom-xml.md): Java21, JavaFX, JUnit, Monocle and Maven plugins.
- [20 — build.sh](20-build-sh.md) and [21 — build.bat](21-build-bat.md): compile, test and package Swing.
- [22 — run-javafx.sh](22-run-javafx-sh.md) and [23 — run-javafx.bat](23-run-javafx-bat.md): JavaFX launcher scripts.
- [24 — run.sh](24-run-sh.md) and [25 — run.bat](25-run-bat.md): launch the original Swing JAR.
- [26 — mvnw](26-mvnw.md) and [27 — mvnw.cmd](27-mvnw-cmd.md): generated Maven bootstrap scripts.
- [28 — maven-wrapper.properties](28-maven-wrapper-properties.md): pinned Maven download configuration.

## Data, documentation and binary files

- `MiniOS-Java/testdata/cases.txt` is reference-test data, not executable Java. `@@CASE` starts a case; `mod=` selects its module; `opt key=value` supplies options; `--TEXT` starts input; `--OK` starts successful expected output; `--ERR` starts an expected error; `@@END` closes the case. Tests.java explains its parser line by line. See the provenance note in [testdata/README.md](../../MiniOS-Java/testdata/README.md).
- `README.md` and the setup guides explain running the project. `RUBRIC-COVERAGE.md` maps requirements; it does not execute or award a grade. `evidence/` contains recorded test results.
- `.gitignore` excludes generated `out/`, `*.class` and `javafx/target/` files from Git.
- `MiniOS.jar` is compiled Swing bytecode packaged as a JAR. The `.java` files are its source; a JAR cannot be explained as text lines.
- `ui-sheet.png` is the supplied artwork; ImageView viewports select its avatars/icons at runtime. `preview.png` is a rendered screenshot. Images do not contain Java source lines.
- `target/` and `out/` are generated build directories; do not treat their copies as separate source implementations.

This documentation does not change simulator behavior. Studying it helps explain the code; it does not replace running examples, checking answers or preparing your own presentation.

[Guide verification](verification.md) · [Source hashes and line counts](source-manifest.json)
