# Worked examples and presentation questions

[Back to guide](README.md)

## CPU: FCFS

Input:

```text
P1 0 5
P2 1 3
```

P1 arrives first and runs0–5. P2 arrived at1 but waits until5, then runs5–8. For P1, finish5, turnaround5−0=5, waiting5−5=0, response0−0=0. For P2, finish8, turnaround8−1=7, waiting7−3=4, response5−1=4. Average waiting=(0+4)/2=2; turnaround=(5+7)/2=6. CPU is busy8/8=100% and throughput2/8=0.25 processes/time unit.

Explain the code path: Cpu.parse → Cpu.sim (rank arrival, decrement rem once per tick) → metrics → report. SJF ranks original burst; SRTF ranks remaining burst each tick; RR rotates after its quantum. MLQ keeps fixed foreground/background classes; MLFQ changes queue level when a full quantum is used. This implementation uses two finite MLFQ quanta and a final FCFS level.

## Banker: a safe order

```text
1
P0 1 2
P1 1 1
```

One resource type. P0 holds1, Max2, Need1. P1 holds1, Max1, Need0. Work starts1. The implemented scan can finish P0 first because Need1<=Work1, then release Allocation1, making Work2. P1 then finishes. Safe sequence P0→P1 exists. Other valid sequences may exist too; a safe sequence is not necessarily unique.

A request must be <=Need and <=Available. Those comparisons alone are insufficient: the tentative state must also remain safe. The algorithm clones matrices, tentatively grants, checks safety, and reports the decision.

## Deadlock: two blocked holders

```text
0 0
P0 1 0 0 1
P1 0 1 1 0
```

P0 holds resourceA and requestsB; P1 holdsB and requestsA. Available is zero for both. Neither request fits Work, so no completion can be simulated and both remain stuck. This detects a current deadlock. Banker instead considers maximum future demand and can report unsafe before current deadlock actually exists.

## Paging

Page size1024; page-table entry `1 2`; logical address1500.

- Page =1500/1024 using integer division =1.
- Offset =1500%1024 =476.
- Page1 maps to frame2.
- Physical =2×1024+476 =2524.

A missing page produces PAGE FAULT, not a physical address. This module demonstrates mapping; page replacement is a separate module.

## Replacement: FIFO, two frames

References `1 2 1 3 2`:

| Reference | Frames after access | Result |
|---|---|---|
| 1 | [1, empty] | fault |
| 2 | [1, 2] | fault |
| 1 | [1, 2] | hit; FIFO load order is unchanged |
| 3 | [3, 2] | fault; evict oldest-loaded1 |
| 2 | [3, 2] | hit |

Total3 faults,2 hits. LRU differs because hits update last-use time; OPT looks forward in the provided reference string and is a benchmark rather than an online predictor. NRU uses2R+M classes; CLOCK gives R=1 pages another chance. The code clones frame arrays after every access so old history does not change when a later frame is replaced.

## Segmentation

Table `0 100 50`: segment0 has base100 and limit50. `0:49` is valid and translates to149. `0:50` traps because offset must be strictly less than50. Unknown segment IDs also trap. The displayed trap is a simulated result, not a real hardware exception.

## Fixed-block fragmentation

Blocks `100 200`; processes `60 150` using First Fit. P1 takes100, wasting40 inside its assigned partition. P2 takes200, wasting50. Total internal waste90; neither block can accept a second process in this fixed-partition model.

With free blocks `50 50` and waiting processes `80 200`, aggregate free memory100 is enough for80 but no individual block fits. Only80 belongs in that fragmentation explanation. Process200 also exceeds total free capacity and is a capacity-shortage case. The code does not compact or split blocks.

## Disk: serve the current head first

Head50, requests `50 40`, LOOK Down. Service50 immediately with zero movement, then40 with movement10. The corrected order is50→40, not40→50. With head0, requests0/99/50, C-LOOK Down: serve0, wrap to99, then descend to50; total99+49=148. Wrap travel counts in this simulator.

SCAN goes to a physical endpoint before reversing; LOOK reverses at the last request. Output traversal can include an endpoint that was not itself a requested I/O.

## Multiprogramming

```text
A 50 2 0
B 50 2 0
```

Total memory100, quantum1. Both arrive and fit, so both are admitted at0. CPU slices are A0–1, B1–2, A2–3 (A finishes), B3–4 (B finishes). CPU busy4/elapsed4=100%. Peak resident programs2. Memory is100 for the first3 time units and50 for the last1: area350; average memory utilization350/(4×100)=87.5%.

This is concurrent residency with time-sharing on one simulated CPU, not simultaneous execution on multiple cores. Admission is checked at scheduling boundaries. Long totals avoid negative times for accepted large bursts.

## Explain the GUI and tests

- Why use a background Task? To keep the JavaFX application thread available for input/layout while an algorithm computes.
- Why snapshot inputs first? A submitted run should use the data at submission, not later edits.
- Why bind both visible and managed? One controls drawing and the other controls reserved layout space.
- Why use long in Banker/Multi? Individual accepted ints can produce sums beyond int range.
- What does passing a smoke test prove? The selected path ran and produced output; it does not prove every number is correct.
- What do known-answer checks add? They compare specific output values with manually/reference-derived expected answers.
- Which code is presentation-critical? Input parsing, the chosen algorithm branch, one result formula, validation, and the actual engine-to-GUI call path.
- What should not be claimed? That the chatbot calls an AI service, that simulated traps are real hardware faults, that the root Swing JAR is the JavaFX launcher, or that test counts guarantee every possible input is correct.
