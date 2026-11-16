import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Known-answer tests. Run from this folder:
 *   javac -d out *.java
 *   java -cp out Tests
 * Checks the reviewer's CPU examples, the slides' page-fault counts, textbook answers, the output of the original
 * Python OS Simulator (testdata/cases.txt) and that every button on every module works.
 */
public class Tests {
    static int pass = 0, fail = 0;

    static void check(String name, Object got, Object want) {
        boolean ok = String.valueOf(got).equals(String.valueOf(want));
        if (ok) pass++; else fail++;
        System.out.println((ok ? "PASS  " : "FAIL  ") + name);
        if (!ok) System.out.println("      got  " + got + "\n      want " + want);
    }

    static List<Cpu.Proc> procs(String... lines) { return Cpu.parse(Arrays.asList(lines)); }
    static final int[] QUANTA = {8, 16};

    /** gantt | avg waiting | avg turnaround */
    static String cpu(String algo, int q, int age, String... lines) {
        List<Cpu.Proc> P = procs(lines);
        Cpu.Result r = Cpu.sim(P, algo, q, age, QUANTA);
        Cpu.Metrics m = Cpu.metrics(P, r);
        return Cpu.ganttPlain(P, r) + " | " + Util.fixed(m.avgWT, 2) + " | " + Util.fixed(m.avgTAT, 2);
    }

    static String finish(String algo, int q, String... lines) {
        List<Cpu.Proc> P = procs(lines);
        return Arrays.toString(Cpu.sim(P, algo, q, 0, QUANTA).ct);
    }

    static String page(String algo, String refs, int F) {
        Pages.Out o = Pages.sim(algo, Pages.parseRefs(refs), F, algo.equals("nfu") ? 1 : 4, 4, 1);
        return o.faults + " " + Pages.finalState(o);
    }

    public static void main(String[] args) throws IOException {
        System.setProperty("java.awt.headless", "true");

        // ---- CPU scheduling: worked examples and practice problems from the reviewer ----
        check("FCFS 24, 3, 3 (avg waiting 17)", cpu("fcfs", 2, 0, "P1 0 24", "P2 0 3", "P3 0 3"), "P1 0-24 | P2 24-27 | P3 27-30 | 17.00 | 27.00");
        check("FCFS practice 5, 3, 8, 6 (7.25 / 12.75)", cpu("fcfs", 2, 0, "P1 0 5", "P2 0 3", "P3 0 8", "P4 0 6"), "P1 0-5 | P2 5-8 | P3 8-16 | P4 16-22 | 7.25 | 12.75");
        check("SJF slide (avg waiting 4)", cpu("sjf", 2, 0, "P1 0 7", "P2 2 4", "P3 4 1", "P4 5 4"), "P1 0-7 | P3 7-8 | P2 8-12 | P4 12-16 | 4.00 | 8.00");
        check("SRTF slide (avg waiting 3)", cpu("srtf", 2, 0, "P1 0 7", "P2 2 4", "P3 4 1", "P4 5 4"), "P1 0-2 | P2 2-4 | P3 4-5 | P2 5-7 | P4 7-11 | P1 11-16 | 3.00 | 7.00");
        check("SJF practice (6.25 / 11.75)", cpu("sjf", 2, 0, "P1 0 5", "P2 0 3", "P3 0 8", "P4 0 6"), "P2 0-3 | P1 3-8 | P4 8-14 | P3 14-22 | 6.25 | 11.75");
        check("Priority practice (avg waiting 8.2)", cpu("pri", 2, 0, "P1 0 10 3", "P2 0 1 1", "P3 0 2 4", "P4 0 1 5", "P5 0 5 2"), "P2 0-1 | P5 1-6 | P1 6-16 | P3 16-18 | P4 18-19 | 8.20 | 12.00");
        check("Round robin slide, q = 20", cpu("rr", 20, 0, "P1 0 53", "P2 0 17", "P3 0 68", "P4 0 24").split(" \\| \\d+\\.\\d\\d \\| ")[0],
            "P1 0-20 | P2 20-37 | P3 37-57 | P4 57-77 | P1 77-97 | P3 97-117 | P4 117-121 | P1 121-134 | P3 134-154 | P3 154-162");
        check("Round robin practice, q = 4 (avg waiting 5.67)", cpu("rr", 4, 0, "P1 0 24", "P2 0 3", "P3 0 3"),
            "P1 0-4 | P2 4-7 | P3 7-10 | P1 10-14 | P1 14-18 | P1 18-22 | P1 22-26 | P1 26-30 | 5.67 | 15.67");
        check("Multilevel feedback queue 8/16/FCFS", cpu("mlfq", 2, 0, "P1 0 30", "P2 0 6", "P3 5 12").split(" \\| \\d+\\.\\d\\d \\| ")[0], "P1 0-8 | P2 8-14 | P3 14-22 | P1 22-38 | P3 38-42 | P1 42-48");
        check("Multilevel queue (foreground RR over background FCFS)", cpu("mlq", 2, 0, "P1 0 5", "P2 0 6 0 1", "P3 2 3").split(" \\| \\d+\\.\\d\\d \\| ")[0], "P1 0-2 | P3 2-4 | P1 4-6 | P3 6-7 | P1 7-8 | P2 8-14");
        check("Priority without aging starves P1", cpu("pri", 2, 0, "P1 0 10 5", "P2 0 10 1", "P3 1 10 1", "P4 2 10 1").split(" \\| \\d+\\.\\d\\d \\| ")[0], "P2 0-10 | P3 10-20 | P4 20-30 | P1 30-40");
        check("Priority with aging every 3 ticks", cpu("pri", 2, 3, "P1 0 10 5", "P2 0 10 1", "P3 1 10 1", "P4 2 10 1").split(" \\| \\d+\\.\\d\\d \\| ")[0], "P2 0-10 | P3 10-20 | P1 20-30 | P4 30-40");
        String S4 = "P1 0 5 2|P2 1 3 1|P3 2 8 4|P4 3 6 3";
        String[] s4 = S4.split("\\|");
        check("OS Simulator: FCFS finish times", finish("fcfs", 2, s4), "[5, 8, 16, 22]");
        check("OS Simulator: SJF finish times", finish("sjf", 2, s4), "[5, 8, 22, 14]");
        check("OS Simulator: Priority finish times", finish("pri", 2, s4), "[5, 8, 22, 14]");
        check("OS Simulator: Round robin q=2 finish times", finish("rr", 2, s4), "[14, 11, 22, 20]");
        check("Idle gap between jobs", finish("fcfs", 1, "P1 0 2", "P2 10 2"), "[2, 12]");

        // ---- Page replacement: slides, and agreement with the web version ----
        String CLASSIC = "7 0 1 2 0 3 0 4 2 3 0 3 2 1 2 0 1 7 0 1", FIG = "3 2 3 0 8 4 2 5 0 9 8 3 2", WR = "7 0w 1 2 0 3w 0 4 2w 3 0 3 2 1 2 0 1 7 0 1";
        String[] algos = {"fifo", "opt", "nru", "sc", "clock", "nfu", "lru", "rand", "ws"};
        String[] cl = {"15 7:1,0:1,1:1 h0", "9 7:1,0:1,1:1 h0", "10 0:1,7:1,1:1 h0", "14 0:1,7:1,1:1 h0", "14 0:1,7:1,1:1 h0", "13 3:0,0:0,1:0 h0", "12 1:1,0:1,7:1 h0", "11 1:1,0:1,7:1 h0", "12 1:1,0:1,7:1 h0"};
        String[] fg = {"9 5:1,9:1,3:1,2:1,4:1 h0", "7 3:1,2:1,9:1,8:1,5:1 h0", "9 5:0,9:0,2:1,8:0,3:0 h0", "9 5:0,9:0,2:1,8:0,3:1 h0", "9 5:0,9:0,2:1,8:0,3:1 h3", "9 3:0,2:0,8:0,0:0,9:0 h0", "10 2:1,3:1,0:1,9:1,8:1 h0", "9 9:1,2:1,8:1,5:1,3:1 h0", "10 2:1,3:1,0:1,9:1,8:1 h0"};
        String[] wr = {"15 7:1,0:1,1:1 h0", "9 7:1,0:1,1:1 h0", "11 0:1,1:1,7:1 h0", "14 0:1,7:1,1:1 h0", "14 0:1,7:1,1:1 h0", "13 3:0,0:0,1:0 h0", "12 1:1,0:1,7:1 h0", "11 1:1,0:1,7:1 h0", "12 1:1,0:1,7:1 h0"};
        for (int i = 0; i < algos.length; i++) {
            check("Pages, slide string, 3 frames: " + Pages.nameOf(algos[i]), page(algos[i], CLASSIC, 3), cl[i]);
            check("Pages, 5-frame figure string: " + Pages.nameOf(algos[i]), page(algos[i], FIG, 5), fg[i]);
            check("Pages with writes (NRU uses them): " + Pages.nameOf(algos[i]), page(algos[i], WR, 3), wr[i]);
        }
        check("Slides: FIFO 15, OPT 9, LRU 12 page faults",
            page("fifo", CLASSIC, 3).split(" ")[0] + "/" + page("opt", CLASSIC, 3).split(" ")[0] + "/" + page("lru", CLASSIC, 3).split(" ")[0], "15/9/12");
        check("Slides: CLOCK figure ends 5|0 9|0 2|1 8|0* 3|1", page("clock", FIG, 5), "9 5:0,9:0,2:1,8:0,3:1 h3");

        // ---- Banker's and deadlock ----
        String bank = "3 3 2\nP0 0 1 0 7 5 3\nP1 2 0 0 3 2 2\nP2 3 0 2 9 0 2\nP3 2 1 1 2 2 2\nP4 0 0 2 4 3 3";
        String b1 = Banker.run(Util.lineList(bank), "P1 1 0 2");
        check("Banker's: classic example is safe, sequence P1 P3 P4 P0 P2", b1.contains("SYSTEM IS SAFE. Safe sequence: P1 -> P3 -> P4 -> P0 -> P2"), true);
        check("Banker's: request P1 1 0 2 is granted", b1.contains("Request P1 1 0 2: GRANTED. New safe sequence: P1 -> P3 -> P4 -> P0 -> P2"), true);
        check("Banker's: unsafe state", Banker.run(Util.lineList("0 0\nP0 1 0 2 1\nP1 0 1 1 2"), "").contains("UNSAFE"), true);
        check("Banker's: request beyond Need is rejected", rejects(new Runnable() { public void run() { Banker.run(Util.lineList("3 3 2\nP0 0 1 0 7 5 3"), "P0 9 0 0"); } }), true);
        String d1 = Deadlock.run(Util.lineList("0 0 0\nP0 0 1 0 0 0 0\nP1 2 0 0 2 0 2\nP2 3 0 3 0 0 1\nP3 2 1 1 1 0 0\nP4 0 0 2 0 0 2"));
        check("Deadlock: P1 to P4 are stuck", d1.contains("DEADLOCK detected. Stuck processes: P1, P2, P3, P4"), true);
        check("Deadlock: recovery terminates P2", d1.contains("terminate P2 to release"), true);
        String d2 = Deadlock.run(Util.lineList("0 1 0\nP0 0 1 0 0 0 0\nP1 2 0 0 2 0 2\nP2 3 0 3 0 0 0\nP3 2 1 1 1 0 0\nP4 0 0 2 0 0 2"));
        check("Deadlock: safe example finishes P0 P2 P3 P4 P1", d2.contains("NO DEADLOCK. Every process can finish: P0 -> P2 -> P3 -> P4 -> P1"), true);

        // ---- Disk scheduling: textbook totals (head 53, 200 cylinders, going up) ----
        int[] Q = {98, 183, 37, 122, 14, 124, 65, 67};
        StringBuilder got = new StringBuilder();
        for (String a : Disk.ALGOS) got.append(a).append('=').append(Disk.movement(53, Disk.order(Q, 53, a, 200, true))).append(' ');
        check("Disk total head movement, all 6 algorithms", got.toString().trim(), "FCFS=640 SSTF=236 SCAN=331 C-SCAN=382 LOOK=299 C-LOOK=322");

        // ---- Modules vs the output of the original Python program ----
        List<String> lines = Files.readAllLines(Paths.get(args.length > 0 ? args[0] : "testdata/cases.txt"), StandardCharsets.UTF_8);
        for (int i = 0; i < lines.size(); i++) {
            if (!lines.get(i).startsWith("@@CASE ")) continue;
            String title = lines.get(i).substring(7), mod = null;
            Map<String, String> opts = new LinkedHashMap<String, String>();
            List<String> text = new ArrayList<String>(), exp = new ArrayList<String>();
            boolean ok = true;
            int sec = 0;
            for (i++; !lines.get(i).equals("@@END"); i++) {
                String l = lines.get(i);
                if (l.startsWith("mod=")) mod = l.substring(4);
                else if (l.startsWith("opt ") && sec == 0) { int eq = l.indexOf('='); opts.put(l.substring(4, eq), l.substring(eq + 1)); }
                else if (l.equals("--TEXT")) sec = 1;
                else if (l.equals("--OK")) { sec = 2; ok = true; }
                else if (l.equals("--ERR")) { sec = 2; ok = false; }
                else if (sec == 1) text.add(l);
                else if (sec == 2) exp.add(l);
            }
            String want = ok ? String.join("\n", exp) + "\n" : "Invalid input: " + String.join("\n", exp);
            String res;
            try {
                res = runModule(mod, Util.lineList(String.join("\n", text)), opts);
            } catch (IllegalArgumentException e) {
                res = "Invalid input: " + e.getMessage();
            }
            check("Original Python output: " + title, res, want);
        }

        // ---- Every module, every sample button: runs without an error ----
        for (Modules.Module m : Modules.all()) {
            MiniOS.ModulePanel panel = new MiniOS.ModulePanel(m);
            Modules.Sample[] ss = {m.small, m.edge, m.large};
            String[] names = {"Small", "Edge", "Large"};
            for (int k = 0; k < 3; k++) {
                panel.load(ss[k]);
                String out = panel.execute();
                check("Window: " + m.name + " / " + names[k] + " sample runs", !out.startsWith("Invalid input") && !out.startsWith("Unexpected") && out.length() > 20, true);
            }
            panel.input.setText("not valid input at all @@");
            String bad = panel.execute();
            check("Window: " + m.name + " shows a clear message for bad input", bad.startsWith("Invalid input"), true);
        }

        System.out.println("\n" + pass + "/" + (pass + fail) + " tests passed");
        System.exit(fail == 0 ? 0 : 1);
    }

    static boolean rejects(Runnable r) {
        try { r.run(); return false; } catch (IllegalArgumentException e) { return true; }
    }

    static String runModule(String mod, List<String> L, Map<String, String> o) {
        if (mod.equals("paging")) return Memory.paging(L, o.get("Page size"), o.get("Addresses"));
        if (mod.equals("seg")) return Memory.segmentation(L, o.get("Addresses"));
        if (mod.equals("frag")) return Memory.fragmentation(L, o.get("Strategy"));
        if (mod.equals("disk")) return Disk.run(L, o.get("Head"), o.get("Disk size"), o.get("Direction"), o.get("Algorithm"));
        return Multi.run(L, o.get("Total memory"), o.get("Quantum"));
    }
}
