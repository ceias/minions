import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Multiprogramming: jobs share memory and the CPU (round robin time slices). A job is admitted when it has arrived and memory is free. */
final class Multi {
    private Multi() {}

    private static final class Job {
        final String id;
        final int m, b, a;
        Job(String id, int m, int b, int a) { this.id = id; this.m = m; this.b = b; this.a = a; }
    }

    static String run(List<String> lines, String totalText, String quantumText) {
        int total = Util.one(totalText, "Total memory", 1), q = Util.one(quantumText, "Quantum", 1);
        List<Job> wait = new ArrayList<Job>();
        Set<String> seen = new HashSet<String>();
        for (String ln : lines) {
            List<String> t = Util.toks(ln);
            if (t.size() != 4) throw new IllegalArgumentException("'" + ln + "': use PID memory burst arrival");
            int[] v = Util.ints(t.subList(1, 4), "Program " + t.get(0), 0, null);
            if (v[0] < 1 || v[1] < 1) throw new IllegalArgumentException(t.get(0) + ": memory and burst must be at least 1");
            if (v[0] > total) throw new IllegalArgumentException(t.get(0) + " needs " + v[0] + " but total memory is only " + total);
            if (!seen.add(t.get(0))) throw new IllegalArgumentException("Duplicate PID " + t.get(0));
            wait.add(new Job(t.get(0), v[0], v[1], v[2]));
        }
        if (wait.isEmpty()) throw new IllegalArgumentException("Enter at least one program.");
        wait.sort(new Comparator<Job>() { public int compare(Job x, Job y) { return Integer.compare(x.a, y.a); } });
        int t = 0, busy = 0, peak = 0, mem = 0;
        long area = 0;
        List<String> ready = new ArrayList<String>(), log = new ArrayList<String>();
        Map<String, Integer> rem = new HashMap<String, Integer>(), memof = new HashMap<String, Integer>();
        while (!wait.isEmpty() || !ready.isEmpty()) {
            for (Job j : new ArrayList<Job>(wait))
                if (j.a <= t && mem + j.m <= total) {
                    wait.remove(j); ready.add(j.id); mem += j.m; rem.put(j.id, j.b); memof.put(j.id, j.m);
                    log.add(String.format("t=%-4d ADMIT  %s  memory in use %d/%d", t, j.id, mem, total));
                }
            peak = Math.max(peak, ready.size());
            if (ready.isEmpty()) { t++; continue; }
            String p = ready.remove(0);
            int run = Math.min(q, rem.get(p));
            log.add(String.format("t=%-4d RUN    %s  for %d  (degree of multiprogramming %d)", t, p, run, ready.size() + 1));
            t += run; busy += run; area += (long) mem * run; rem.put(p, rem.get(p) - run);
            if (rem.get(p) == 0) { mem -= memof.get(p); log.add(String.format("t=%-4d FINISH %s  memory in use %d/%d", t, p, mem, total)); }
            else ready.add(p);
        }
        return String.join("\n", log) + "\n\nTotal time: " + t + "\nCPU utilization: " + Util.fixed(100.0 * busy / t, 1) + "%\n"
            + "Average memory utilization: " + Util.fixed(100.0 * area / ((double) t * total), 1) + "%\n"
            + "Maximum programs in memory at once: " + peak + "\n";
    }
}
