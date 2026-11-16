# Rubric integration and submission guide

Based on the supplied Operating System Rubrics PDF. This guide maps implementation to assessment criteria; it does not assign a grade. Instructor-only evaluation and submission directions in the PDF are treated as reference material. Confirm the full list of algorithms taught in your class: the PDF gives examples rather than an exhaustive syllabus.

## A. Implementation — 40 points

| Required module | Points | Implementation and visible evidence |
|---|---:|---|
| CPU scheduling | 6 | `Cpu.java`: FCFS, SJF, SRTF, Priority, Preemptive Priority, Round Robin, MLQ, MLFQ. Gantt chart, per-process waiting/turnaround/response, averages, utilization and throughput. |
| Banker's algorithm | 5 | `Banker.java`: Need matrix, safe/unsafe result, safe sequence, optional resource-request decision. Processes are rows; resource count comes from Available, with matching Allocation/Max vectors. |
| Paging | 4 | `Memory.java`: variable page/frame table, page size, logical addresses, physical translations and missing-page faults. |
| Page replacement | 6 | `Pages.java`: FIFO, Optimal, NRU, Second Chance, CLOCK, NFU, LRU, Random, Working Set. Frame history, hits and faults. |
| Segmentation | 4 | `Memory.java`: segment/base/limit entries; translation or trap for invalid segment/offset. |
| Fragmentation | 4 | `Memory.java`: First/Best/Worst Fit for fixed partitions; internal waste and free-block fragmentation condition. Each block holds at most one process; this is not a variable-partition compaction simulator. |
| Disk scheduling | 6 | `Disk.java`: FCFS, SSTF, SCAN, C-SCAN, LOOK, C-LOOK; traversal and total head movement, comparison table. Boundary/wrap travel counts toward movement. |
| Multiprogramming | 5 | `Multi.java`: arrival-based memory admission, CPU round robin, event log, CPU/memory utilization and peak programs resident. Admission occurs at scheduling boundaries. |

Deadlock Detection is an additional module, separate from the eight required areas.

## B. GUI and flexible input — 10 points

`MiniOS.java` and `Modules.java` provide navigation, editable input, algorithm selectors, Small/Large/Edge examples, Copy/Save and validation messages. Counts are inferred from variable-length input; users do not need to type a separate count. No fixed item-count restriction is imposed. The former 20-frame restriction was removed and CPU simulation no longer silently truncates at 100,000 ticks. Numeric values use Java integer-backed structures and input bounds; memory/CPU limits still apply. CPU simulation is tick-based and very large runs can take time. The in-app Rubric / Demo Guide explains assessment and presentation expectations.

## C. Accuracy and testing — 15 points

Run both test suites from this folder. `Tests` has 137 existing known-answer, reference-output, validation and sample-panel checks. `RubricTests` has 138 additional checks covering every algorithm choice with Small/Large/Edge samples, blank inputs, more than 20 frames, negative numeric pages, a CPU burst beyond the old cutoff, indented comments, and manual answers. Sample-size checks establish execution, while known-answer checks verify specific outputs; they are not exhaustive proofs of every algorithm.

Manual examples for demonstration:

| Module | Input / settings | Expected result |
|---|---|---|
| CPU FCFS | P1 0 24; P2 0 3; P3 0 3 (separate lines) | P1 0–24, P2 24–27, P3 27–30; average wait 17, turnaround 27 |
| Banker | Small sample, request P1 1 0 2 | Safe P1 → P3 → P4 → P0 → P2; request granted |
| Paging | table `1 2`, page size 1024, address 1500 | page 1, offset 476, physical 2524 |
| Replacement | references `1 2 3 1`, 64 frames | 3 faults, 1 hit for every supplied algorithm |
| Segmentation | table `0 100 50`, addresses `0:49, 0:50` | physical 149; then out-of-range trap |
| Fragmentation | blocks `100`; process `60` | internal waste 40 |
| Disk FCFS | Small sample, head 53 | total movement 640 cylinders |
| Multiprogramming | A 50 2 0; B 50 2 0, memory 100, quantum 1 | total time 4, CPU utilization 100%, peak residency 2 |

Use Small then Large on every module and save representative output. Existing larger samples range from tens of jobs/segments to hundreds of page references. Test summaries are included under `evidence/`. Headless tests instantiate module panels and execute their input/output path; they do not verify native window appearance, clipboard, file dialogs or your local IDE installation.

## D. Source and runnable submission — 10 points

Include all `.java` files, `MiniOS.jar`, `testdata/cases.txt`, build/run scripts, this guide, `ANTIGRAVITY-SETUP.md`, and current test evidence. Run the build script after every code change to refresh the JAR. Java Swing is included with Java; no external application libraries are needed.

## E. Presentation — 15 points

- 0:00–1:00: project purpose, group names, Java/Swing and eight modules.
- 1:00–3:00: navigation, editable inputs, samples, results and error handling.
- 3:00–7:00: briefly demonstrate all eight areas. Use Small examples, show at least one Large input and one Edge error/trap.
- 7:00–9:00: explain waiting/turnaround, Need/safety, address formulas and an algorithm comparison. Explain removing fixed limits as one implementation challenge.
- 9:00–10:00: questions and conclusion.

## F. Conceptual understanding — 10 points

Be prepared to explain:

- Turnaround = finish − arrival; waiting = turnaround − burst; response = first start − arrival.
- A safe Banker state has a possible completion order. An unsafe state is not proof that deadlock has already occurred.
- Paging uses fixed-size pages and frames; segmentation uses base/limit pairs. Offset must be strictly less than segment limit.
- FIFO uses load order, LRU uses past references, Optimal uses future references and serves as a benchmark.
- Internal fragmentation is waste inside allocated fixed blocks; external fragmentation concerns separated free areas.
- SCAN travels to a disk boundary, LOOK reverses at the last request; circular algorithms include wrap travel here.
- Multiprogramming permits multiple jobs in memory; a single simulated CPU still executes one job at a time.

## Before submission

Record group/member names; confirm the class algorithm list; rerun both test suites; launch the GUI locally; save Small/Large outputs and screenshots; verify the rebuilt JAR runs; rehearse the demo. Presentation performance and independent understanding require your own preparation.
