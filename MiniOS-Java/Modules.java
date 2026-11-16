import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** The simulator modules: what each one asks for, its sample inputs, and how it runs. */
final class Modules {
    private Modules() {}

    interface Runner { String run(List<String> lines, Map<String, String> o); }

    static final class Field {
        final String label, def;
        final String[] choices;   // null = free text
        Field(String label, String def, String[] choices) { this.label = label; this.def = def; this.choices = choices; }
    }

    static final class Sample {
        final String text;
        final Map<String, String> opts = new LinkedHashMap<String, String>();
        Sample(String text, String... kv) {
            this.text = text;
            for (int i = 0; i + 1 < kv.length; i += 2) opts.put(kv[i], kv[i + 1]);
        }
    }

    static final class Module {
        final String name, hint, how, inputLabel;
        final Field[] fields;
        final Sample small, edge, large;
        final Runner run;
        Module(String name, String hint, String how, String inputLabel, Field[] fields, Sample small, Sample edge, Sample large, Runner run) {
            this.name = name; this.hint = hint; this.how = how; this.inputLabel = inputLabel;
            this.fields = fields; this.small = small; this.edge = edge; this.large = large; this.run = run;
        }
    }

    private static Field f(String l, String d) { return new Field(l, d, null); }
    private static Field f(String l, String d, String[] c) { return new Field(l, d, c); }

    static final String QUANTUM = "Quantum (round robin / MLQ)", AGING = "Aging every N ticks (0 = off)", Q0 = "MLFQ Q0 quantum", Q1 = "MLFQ Q1 quantum";
    static final String NRU_TICK = "NRU clock tick (references)", NFU_TICK = "NFU clock tick (references)", WINDOW = "Working set window", SEED = "Random seed";

    static List<Module> all() {
        List<Module> m = new ArrayList<Module>();

        m.add(new Module("CPU Scheduling",
            "One process per line: PID arrival burst [priority] [queue]. Priority (lower = first) is only used by the two priority algorithms. "
            + "Queue (0 = foreground, 1 = background) is only used by the multilevel queue.",
            "Turnaround = Finish - Arrival; Waiting = Turnaround - Burst (non-preemptive: Start - Arrival); Response = Start - Arrival.",
            "Processes (PID arrival burst [priority] [queue])",
            new Field[]{f("Algorithm", Cpu.NAMES[0], Cpu.NAMES), f(QUANTUM, "2"), f(AGING, "0"), f(Q0, "8"), f(Q1, "16")},
            new Sample("P1 0 5 2\nP2 1 3 1\nP3 2 8 4\nP4 3 6 3"),
            new Sample("P1 0 5 1\nP2 0 5 1\nP3 10 2 1"),   // ties + idle gap
            new Sample(Samples.CPU_LARGE),
            new Runner() {
                public String run(List<String> lines, Map<String, String> o) {
                    List<Cpu.Proc> P = Cpu.parse(lines);
                    int q = Util.one(o.get(QUANTUM), "Quantum", 1), age = Util.one(o.get(AGING), "Aging", 0);
                    int[] quanta = {Util.one(o.get(Q0), "Q0 quantum", 1), Util.one(o.get(Q1), "Q1 quantum", 1)};
                    return Cpu.report(P, Cpu.idOf(o.get("Algorithm")), q, age, quanta);
                }
            }));

        m.add(new Module("Page Replacement",
            "Enter the page reference string (numbers or letters separated by spaces or commas; add a w after a page for a write) and the number of frames.",
            "A reference not in memory is a FAULT. FIFO evicts the oldest, OPT the page used farthest ahead, LRU the least recently used, NRU the lowest class (2R + M), "
            + "Second Chance and CLOCK skip pages with R = 1, NFU the smallest counter, Random any page, Working Set a page outside the last N references.",
            "Reference string",
            new Field[]{f("Algorithm", Pages.NAMES[0], Pages.NAMES), f("Frames", "3"), f(NRU_TICK, "4"), f(NFU_TICK, "1"), f(WINDOW, "4"), f(SEED, "1")},
            new Sample("7 0 1 2 0 3 0 4 2 3 0 3 2 1 2 0 1 7 0 1", "Frames", "3"),
            new Sample("1 1 1 1 1", "Frames", "1"),
            new Sample(Samples.PAGE_LARGE, "Frames", Samples.PAGE_LARGE_FRAMES),
            new Runner() {
                public String run(List<String> lines, Map<String, String> o) {
                    int F = Util.one(o.get("Frames"), "Frames", 1);
                    return Pages.report(Pages.idOf(o.get("Algorithm")), String.join(" ", lines), F, Util.one(o.get(NRU_TICK), "NRU clock tick", 1),
                        Util.one(o.get(NFU_TICK), "NFU clock tick", 1), Util.one(o.get(WINDOW), "Working set window", 1), Util.one(o.get(SEED), "Random seed", 1));
                }
            }));

        m.add(new Module("Deadlock Detection",
            "Line 1: Available resources (one number per resource type). Then one line per process: PID Allocation... Request... (what it holds, then what it is still waiting for).",
            "Work = Available. A process that holds nothing is finished. Repeat: find a process whose Request <= Work, assume it finishes and add its Allocation to Work. Any process left over is deadlocked.",
            "Available, then processes (PID alloc... request...)",
            new Field[0],
            new Sample("0 0 0\nP0 0 1 0 0 0 0\nP1 2 0 0 2 0 2\nP2 3 0 3 0 0 1\nP3 2 1 1 1 0 0\nP4 0 0 2 0 0 2"),
            new Sample("0 0\nP0 1 0 1 1\nP1 0 1 1 1"),
            new Sample("0 1 0\nP0 0 1 0 0 0 0\nP1 2 0 0 2 0 2\nP2 3 0 3 0 0 0\nP3 2 1 1 1 0 0\nP4 0 0 2 0 0 2"),
            new Runner() { public String run(List<String> lines, Map<String, String> o) { return Deadlock.run(lines); } }));

        m.add(new Module("Banker's Algorithm",
            "Line 1: Available resources (one number per resource type). Then one line per process: PID Allocation... Max... (e.g. 3 resource types -> PID a a a m m m).",
            "Need = Max - Allocation. Safe if every process can finish in some order using Work += Allocation.",
            "Available, then processes (PID alloc... max...)",
            new Field[]{f("Request (optional, e.g. P1 1 0 2)", "")},
            new Sample("3 3 2\nP0 0 1 0 7 5 3\nP1 2 0 0 3 2 2\nP2 3 0 2 9 0 2\nP3 2 1 1 2 2 2\nP4 0 0 2 4 3 3", "Request (optional, e.g. P1 1 0 2)", "P1 1 0 2"),
            new Sample("0 0\nP0 1 0 2 1\nP1 0 1 1 2", "Request (optional, e.g. P1 1 0 2)", ""),   // unsafe state
            new Sample(Samples.BANKER_LARGE, "Request (optional, e.g. P1 1 0 2)", ""),
            new Runner() { public String run(List<String> lines, Map<String, String> o) { return Banker.run(lines, o.get("Request (optional, e.g. P1 1 0 2)")); } }));

        m.add(new Module("Paging",
            "Page table: one line per entry, \"page frame\". Enter the page size and the logical addresses (comma separated).",
            "page = address // page size; offset = address % page size; physical = frame * size + offset.",
            "Page table (page frame)",
            new Field[]{f("Page size", "1024"), f("Addresses", "")},
            new Sample("0 5\n1 2\n2 7\n3 0", "Page size", "1024", "Addresses", "100, 1500, 3000, 4095, 5000"),
            new Sample("0 3\n1 1", "Page size", "4", "Addresses", "0, 3, 4, 7, 8, 100"),
            new Sample(Samples.PAGING_LARGE, "Page size", Samples.PAGING_LARGE_SIZE, "Addresses", Samples.PAGING_LARGE_ADDR),
            new Runner() { public String run(List<String> lines, Map<String, String> o) { return Memory.paging(lines, o.get("Page size"), o.get("Addresses")); } }));

        m.add(new Module("Segmentation",
            "Segment table: one line per segment, \"segment base limit\". Write addresses as seg:offset (comma separated).",
            "Valid if offset < limit; physical = base + offset. Otherwise the hardware traps.",
            "Segment table (segment base limit)",
            new Field[]{f("Addresses", "")},
            new Sample("0 1400 1000\n1 6300 400\n2 4300 400\n3 3200 1100\n4 4700 1000", "Addresses", "0:430, 1:10, 2:500, 3:400, 4:1222, 7:5"),
            new Sample("0 100 50", "Addresses", "0:0, 0:49, 0:50, 1:0"),
            new Sample(Samples.SEG_LARGE, "Addresses", Samples.SEG_LARGE_ADDR),
            new Runner() { public String run(List<String> lines, Map<String, String> o) { return Memory.segmentation(lines, o.get("Addresses")); } }));

        m.add(new Module("Fragmentation",
            "Line 1: memory block sizes. Line 2: process sizes (each block holds at most one process).",
            "Internal = block size - process size (wasted inside a block). External = enough free memory exists but no single block is big enough.",
            "Block sizes (line 1), process sizes (line 2)",
            new Field[]{f("Strategy", "First Fit", new String[]{"First Fit", "Best Fit", "Worst Fit"})},
            new Sample("100 500 200 300 600\n212 417 112 426"),
            new Sample("50 50 50\n120"),
            new Sample(Samples.FRAG_LARGE),
            new Runner() { public String run(List<String> lines, Map<String, String> o) { return Memory.fragmentation(lines, o.get("Strategy")); } }));

        m.add(new Module("Disk Scheduling",
            "Enter the disk request queue (cylinder numbers, spaces or commas), the head position and the disk size.",
            "Total movement = sum of |next cylinder - current cylinder|. Lower is better.",
            "Requests (cylinders)",
            new Field[]{f("Head", "53"), f("Disk size", "200"), f("Direction", "Up", new String[]{"Up", "Down"}), f("Algorithm", "SCAN", Disk.ALGOS)},
            new Sample("98 183 37 122 14 124 65 67", "Head", "53", "Disk size", "200"),
            new Sample("0 99 50", "Head", "0", "Disk size", "100", "Direction", "Up"),
            new Sample(Samples.DISK_LARGE, "Head", Samples.DISK_LARGE_HEAD, "Disk size", Samples.DISK_LARGE_SIZE),
            new Runner() { public String run(List<String> lines, Map<String, String> o) { return Disk.run(lines, o.get("Head"), o.get("Disk size"), o.get("Direction"), o.get("Algorithm")); } }));

        m.add(new Module("Multiprogramming",
            "One program per line: PID memory burst arrival. Programs wait in a job queue until memory is free.",
            "A job is admitted only if memory is free; admitted jobs share the CPU in round robin slices.",
            "Programs (PID memory burst arrival)",
            new Field[]{f("Total memory", "100"), f("Quantum", "3")},
            new Sample("J1 40 8 0\nJ2 30 5 1\nJ3 50 6 2\nJ4 20 4 3", "Total memory", "100"),
            new Sample("J1 100 3 0\nJ2 100 3 0", "Total memory", "100"),
            new Sample(Samples.MP_LARGE, "Total memory", Samples.MP_LARGE_MEM),
            new Runner() { public String run(List<String> lines, Map<String, String> o) { return Multi.run(lines, o.get("Total memory"), o.get("Quantum")); } }));
        return m;
    }
}
