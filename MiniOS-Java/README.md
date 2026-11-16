# Mini OS - Java edition

The Mini OS simulators as a Java desktop program (Swing). Plain Java 8 or newer, no libraries.

## Run it
- **Double-click `MiniOS.jar`**, or run `java -jar MiniOS.jar` (needs Java 8+ installed).
- Windows: `run.bat`   Mac / Linux: `./run.sh`
- From source: `javac -d out *.java` then `java -cp out MiniOS`

## What is inside (pick a module on the left)
| Module | What it does |
|---|---|
| CPU Scheduling | FCFS (FIFO), SJF, SRTF, Priority, Preemptive priority, Round robin, Multilevel queue, Multilevel feedback queue. Gantt chart, start / finish / turnaround / waiting / response, averages, utilization, throughput, optional aging. The Priority column only shows for the priority algorithms. |
| Page Replacement | FIFO, Optimal, NRU, Second Chance, CLOCK, NFU, LRU, Random, Working Set. Frame grid with faults and hits. |
| Deadlock Detection | Finds stuck processes, writes the steps, suggests which process to terminate. |
| Banker's Algorithm | Need matrix, safe sequence, and a resource-request test. |
| Paging, Segmentation, Fragmentation | Address translation, base/limit traps, first / best / worst fit. |
| Disk Scheduling | FCFS, SSTF, SCAN, C-SCAN, LOOK, C-LOOK with a comparison table. |
| Multiprogramming | Jobs share memory and the CPU in round robin slices. |

Every module has **Small**, **Large** and **Edge** sample buttons, **Run (Ctrl+Enter)**, and **Copy / Save** for the results.
Bad input gives a clear message instead of a crash.

## Tests
    javac -d out *.java
    java -cp out Tests

137 checks: the reviewer's CPU examples, the slides' page-fault counts and the CLOCK figure, textbook disk totals,
the output of the original Python OS Simulator (`testdata/cases.txt`), agreement with the web version, and every sample button on every module.

## Files
    MiniOS.java   window (Swing)            Modules.java  inputs, options and samples per module
    Cpu.java      CPU scheduling            Pages.java    page replacement
    Banker.java   Banker's algorithm        Deadlock.java deadlock detection
    Memory.java   paging, segmentation, fragmentation
    Disk.java     disk scheduling           Multi.java    multiprogramming
    Util.java     input checks and text tables
    Samples.java  large sample inputs       Tests.java    known-answer tests

## Rubric-integrated edition

Start with [Antigravity setup](ANTIGRAVITY-SETUP.md), then read [rubric coverage and presentation guide](RUBRIC-COVERAGE.md). The app has a **Help** button for usage instructions; the rubric remains a development reference in the documentation. Build with JDK 21: `sh build.sh` (macOS/Linux), or `cmd /c build.bat` (Windows). These scripts run 137 reference checks, 138 rubric checks and 31 audit regression checks before packaging the JAR. The former frame cap and silent CPU cutoff have been removed.

## New: JavaFX frog chat interface

For the conversational interface using your supplied pixel artwork, follow [JavaFX / Antigravity setup](javafx/README.md). Requires **JDK 21**; the Maven Wrapper downloads Maven automatically. From `MiniOS-Java/javafx` on Windows, run `.\mvnw.cmd test`, then `.\mvnw.cmd javafx:run`. On macOS/Linux use `sh ./mvnw test`, then `sh ./mvnw javafx:run`. The root JAR and original build/run scripts still launch Swing.
