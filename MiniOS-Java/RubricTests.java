import java.util.*;

/** Rubric regressions: all algorithm selectors, scalable input and validation. */
public class RubricTests {
    private static int checks;
    static void check(String label, boolean ok) {
        if (!ok) throw new AssertionError(label);
        checks++;
        System.out.println("PASS  " + label);
    }
    public static void main(String[] args) {
        for (Modules.Module m : Modules.all()) {
            for (Modules.Sample sample : new Modules.Sample[]{m.small, m.large, m.edge}) {
                Map<String, String> opts = new LinkedHashMap<String, String>();
                for (Modules.Field f : m.fields) opts.put(f.label, f.def);
                opts.putAll(sample.opts);
                for (Modules.Field f : m.fields) if (f.choices != null) {
                    for (String choice : f.choices) {
                        opts.put(f.label, choice);
                        check(m.name + " / " + choice + " / " + Util.lineList(sample.text).size() + " input lines",
                            m.run.run(Util.lineList(sample.text), opts).length() > 20);
                    }
                }
                check(m.name + " sample", m.run.run(Util.lineList(sample.text), opts).length() > 20);
            }
            MiniOS.ModulePanel panel = new MiniOS.ModulePanel(m);
            panel.load(m.small);
            panel.input.setText("");
            check(m.name + " rejects blank input", panel.execute().startsWith("Invalid input"));
        }
        check("CPU completes beyond former 100,000-tick cutoff",
            Cpu.sim(Cpu.parse(Arrays.asList("P1 0 100001")), "fcfs", 2, 0, new int[]{8,16}).ct[0] == 100001);
        for (String a : Pages.IDS) {
            Pages.Out out = Pages.sim(a, Pages.parseRefs("1 2 3 1"), 64, 4, 4, 1);
            check(a + " known answer with 64 frames", out.faults == 3 && out.hits == 1);
        }
        for (Modules.Module m : Modules.all()) if (m.name.equals("Page Replacement")) {
            MiniOS.ModulePanel panel = new MiniOS.ModulePanel(m);
            panel.load(m.small);
            ((javax.swing.JTextField)panel.comps.get("Frames")).setText("64");
            check("GUI accepts more than 20 frames", panel.execute().contains("64 frames"));
            panel.input.setText("-1 2");
            check("Negative numeric pages rejected", panel.execute().startsWith("Invalid input"));
        }
        check("Indented comments ignored", Util.lineList("  # note\nP1 0 1").size() == 1);
        check("Paging manual answer 1500 -> 2524", Memory.paging(Arrays.asList("1 2"), "1024", "1500").contains("2524"));
        check("Segment last valid byte", Memory.segmentation(Arrays.asList("0 100 50"), "0:49").contains("149"));
        check("Segment limit traps", Memory.segmentation(Arrays.asList("0 100 50"), "0:50").contains("TRAP"));
        check("Fixed partition internal waste 100-60=40", Memory.fragmentation(Arrays.asList("100", "60"), "First Fit").contains("Total internal fragmentation: 40"));
        check("Multiprogramming two equal jobs fully utilize CPU", Multi.run(Arrays.asList("A 50 2 0", "B 50 2 0"), "100", "1").contains("CPU utilization: 100.0%"));
        System.out.println("\n" + checks + "/" + checks + " rubric checks passed");
    }
}
