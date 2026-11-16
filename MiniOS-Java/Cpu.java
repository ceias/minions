import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * CPU scheduling, following the "CPU Scheduling Reviewer".
 * Algorithms: fcfs (FIFO), sjf, srtf, pri, prip, rr, mlq, mlfq.
 * The simulation moves one time unit (tick) at a time. Ties are broken by arrival order.
 */
final class Cpu {
    private Cpu() {}

    static final String[] IDS = {"fcfs", "sjf", "srtf", "pri", "prip", "rr", "mlq", "mlfq"};
    static final String[] NAMES = {
        "First come, first served (FIFO)", "Shortest job first (non-preemptive)",
        "Shortest remaining time first (preemptive SJF)", "Priority (non-preemptive)", "Priority (preemptive)",
        "Round robin", "Multilevel queue", "Multilevel feedback queue"};

    static String idOf(String name) {
        for (int i = 0; i < NAMES.length; i++) if (NAMES[i].equals(name)) return IDS[i];
        return name;
    }

    static String nameOf(String id) {
        for (int i = 0; i < IDS.length; i++) if (IDS[i].equals(id)) return NAMES[i];
        return id;
    }

    /** One process: id, arrival, burst, priority (lower = first), queue (MLQ: 0 foreground, 1 background). */
    static final class Proc {
        final String id;
        final int a, b, p, q;
        Proc(String id, int a, int b, int p, int q) { this.id = id; this.a = a; this.b = b; this.p = p; this.q = q; }
    }

    static final class Result {
        int[] tl, ql, ct, first;   // tl[t] = process running at tick t (-1 = idle)
        boolean[] sl;              // true when a new Gantt block starts at tick t
    }

    static final class Seg {
        final int p, s, e, q;
        Seg(int p, int s, int e, int q) { this.p = p; this.s = s; this.e = e; this.q = q; }
    }

    private static final int INF = Integer.MAX_VALUE;

    static Result sim(List<Proc> P, String algo, int q, int age, int[] quanta) {
        final int n = P.size();
        final int[] rem = new int[n], ct = new int[n], first = new int[n], waited = new int[n], lvl = new int[n];
        for (int i = 0; i < n; i++) { rem[i] = P.get(i).b; first[i] = -1; }
        final boolean[] seen = new boolean[n];
        List<Integer> tl = new ArrayList<Integer>(), ql = new ArrayList<Integer>();
        List<Boolean> sl = new ArrayList<Boolean>();
        List<LinkedList<Integer>> Qs = new ArrayList<LinkedList<Integer>>();
        for (int i = 0; i < 3; i++) Qs.add(new LinkedList<Integer>());
        boolean queued = algo.equals("rr") || algo.equals("mlq") || algo.equals("mlfq");
        boolean mlq = algo.equals("mlq"), mlfq = algo.equals("mlfq");
        int t = 0, done = 0, cur = -1, used = 0, prev = -2;
        boolean ns = false;

        while (done < n) {
            // arrivals
            for (int i = 0; i < n; i++)
                if (!seen[i] && P.get(i).a <= t) { seen[i] = true; if (queued) Qs.get(mlq && P.get(i).q != 0 ? 1 : 0).add(i); }
            if (!queued) {
                boolean pre = algo.equals("srtf") || algo.equals("prip");
                if (cur >= 0 && rem[cur] == 0) cur = -1;
                if (cur < 0 || pre) {
                    int best = -1;
                    for (int i = 0; i < n; i++) {
                        if (rem[i] <= 0 || P.get(i).a > t) continue;
                        if (best < 0 || key(algo, i, P, rem, waited, age) < key(algo, best, P, rem, waited, age)
                                || (key(algo, i, P, rem, waited, age) == key(algo, best, P, rem, waited, age) && P.get(i).a < P.get(best).a))
                            best = i;
                    }
                    if (best >= 0 && (cur < 0 || key(algo, best, P, rem, waited, age) < key(algo, cur, P, rem, waited, age))) cur = best;
                }
            } else {
                if (cur >= 0) {                                   // a higher queue preempts a lower one
                    int L = level(algo, cur, P, lvl);
                    boolean higher = false;
                    for (int k = 0; k < L; k++) if (!Qs.get(k).isEmpty()) higher = true;
                    if (higher) { Qs.get(L).addFirst(cur); cur = -1; }
                }
                if (cur < 0) {
                    for (int k = 0; k < 3; k++)
                        if (!Qs.get(k).isEmpty()) { cur = Qs.get(k).removeFirst(); used = 0; ns = true; break; }
                }
            }
            if (cur < 0) { tl.add(-1); sl.add(false); ql.add(-1); prev = -1; t++; continue; }   // CPU idle
            if (first[cur] < 0) first[cur] = t;
            tl.add(cur); sl.add(ns || cur != prev); ql.add(mlq || mlfq ? level(algo, cur, P, lvl) : 0);
            ns = false; prev = cur;
            rem[cur]--; used++; t++;
            for (int i = 0; i < n; i++) if (i != cur && rem[i] > 0 && P.get(i).a < t) waited[i]++;
            if (rem[cur] == 0) { ct[cur] = t; done++; }
            if (queued) {
                for (int i = 0; i < n; i++)
                    if (!seen[i] && P.get(i).a <= t) { seen[i] = true; Qs.get(mlq && P.get(i).q != 0 ? 1 : 0).add(i); }
                if (rem[cur] == 0) cur = -1;
                else {
                    int L = level(algo, cur, P, lvl);
                    int Q = mlfq ? (L < 2 ? quanta[L] : INF) : (L == 0 ? q : INF);
                    if (used >= Q) {
                        if (mlfq) lvl[cur] = Math.min(2, L + 1);
                        Qs.get(level(algo, cur, P, lvl)).add(cur);
                        cur = -1;
                    }
                }
            }
        }
        Result r = new Result();
        r.tl = toArray(tl); r.ql = toArray(ql); r.ct = ct; r.first = first;
        r.sl = new boolean[sl.size()];
        for (int i = 0; i < r.sl.length; i++) r.sl[i] = sl.get(i);
        return r;
    }

    private static int level(String algo, int i, List<Proc> P, int[] lvl) {
        if (algo.equals("mlfq")) return lvl[i];
        if (algo.equals("mlq")) return P.get(i).q != 0 ? 1 : 0;
        return 0;
    }

    private static int key(String algo, int i, List<Proc> P, int[] rem, int[] waited, int age) {
        switch (algo) {
            case "fcfs": return P.get(i).a;
            case "sjf": return P.get(i).b;
            case "srtf": return rem[i];
            default: return Math.max(0, P.get(i).p - (age > 0 ? waited[i] / age : 0));   // pri / prip, with aging
        }
    }

    private static int[] toArray(List<Integer> l) {
        int[] a = new int[l.size()];
        for (int i = 0; i < a.length; i++) a[i] = l.get(i);
        return a;
    }

    /** Gantt blocks: a new block starts when the process changes or a new time slice is dispatched. */
    static List<Seg> segs(Result r) {
        List<Seg> out = new ArrayList<Seg>();
        for (int t = 0; t < r.tl.length; t++) {
            Seg l = out.isEmpty() ? null : out.get(out.size() - 1);
            if (l != null && l.p == r.tl[t] && !r.sl[t]) out.set(out.size() - 1, new Seg(l.p, l.s, t + 1, l.q));
            else out.add(new Seg(r.tl[t], t, t + 1, r.ql[t]));
        }
        return out;
    }

    static String gantt(List<Proc> P, Result r, boolean tag) {
        StringBuilder sb = new StringBuilder();
        for (Seg s : segs(r))
            sb.append('|').append(s.p < 0 ? "idle" : P.get(s.p).id + (tag ? "(Q" + s.q + ")" : "")).append(' ').append(s.s).append('-').append(s.e);
        return sb.append('|').toString();
    }

    /** Gantt as "P1 0-24 | P2 24-27" (idle blocks skipped), handy for checking against worked examples. */
    static String ganttPlain(List<Proc> P, Result r) {
        StringBuilder sb = new StringBuilder();
        for (Seg s : segs(r)) {
            if (s.p < 0) continue;
            if (sb.length() > 0) sb.append(" | ");
            sb.append(P.get(s.p).id).append(' ').append(s.s).append('-').append(s.e);
        }
        return sb.toString();
    }

    static final class Metrics {
        int[] st, ft, tat, wt, rt;
        double avgWT, avgTAT, avgRT, util, thru;
        int total;
    }

    /** TAT = FT - AT, WT = TAT - BT (non-preemptive: WT = ST - AT), RT = ST - AT. */
    static Metrics metrics(List<Proc> P, Result r) {
        int n = P.size();
        Metrics m = new Metrics();
        m.st = r.first; m.ft = r.ct; m.tat = new int[n]; m.wt = new int[n]; m.rt = new int[n];
        double tw = 0, tt = 0, tr = 0;
        for (int i = 0; i < n; i++) {
            m.tat[i] = r.ct[i] - P.get(i).a; m.wt[i] = m.tat[i] - P.get(i).b; m.rt[i] = r.first[i] - P.get(i).a;
            tw += m.wt[i]; tt += m.tat[i]; tr += m.rt[i];
        }
        int busy = 0;
        for (int x : r.tl) if (x >= 0) busy++;
        m.total = r.tl.length;
        m.avgWT = tw / n; m.avgTAT = tt / n; m.avgRT = tr / n; m.util = (double) busy / m.total; m.thru = (double) n / m.total;
        return m;
    }

    static String report(List<Proc> P, String algo, int q, int age, int[] quanta) {
        Result r = sim(P, algo, q, age, quanta);
        Metrics m = metrics(P, r);
        boolean pc = algo.equals("pri") || algo.equals("prip"), tag = algo.equals("mlq") || algo.equals("mlfq");
        String par = algo.equals("rr") ? ", quantum " + q : algo.equals("mlq") ? ", foreground quantum " + q
            : algo.equals("mlfq") ? ", Q0 " + quanta[0] + " / Q1 " + quanta[1] + " / Q2 FCFS" : (pc && age > 0 ? ", aging every " + age + " ticks" : "");
        StringBuilder sb = new StringBuilder(nameOf(algo)).append(par).append("\n\nGantt chart:\n").append(gantt(P, r, tag)).append("\n\n");
        List<String> h = new ArrayList<String>();
        h.add("Process"); h.add("Arrival"); h.add("Burst"); if (pc) h.add("Priority");
        h.add("Start"); h.add("Finish"); h.add("Turnaround"); h.add("Waiting"); h.add("Response");
        List<String[]> rows = new ArrayList<String[]>();
        for (int i = 0; i < P.size(); i++) {
            List<Object> c = new ArrayList<Object>();
            c.add(P.get(i).id); c.add(P.get(i).a); c.add(P.get(i).b); if (pc) c.add(P.get(i).p);
            c.add(m.st[i]); c.add(m.ft[i]); c.add(m.tat[i]); c.add(m.wt[i]); c.add(m.rt[i]);
            rows.add(Util.row(c.toArray()));
        }
        sb.append(Util.table(h.toArray(new String[0]), rows));
        sb.append("\nAverage waiting time    = ").append(Util.fixed(m.avgWT, 2)).append('\n');
        sb.append("Average turnaround time = ").append(Util.fixed(m.avgTAT, 2)).append('\n');
        sb.append("Average response time   = ").append(Util.fixed(m.avgRT, 2)).append('\n');
        sb.append("CPU utilization         = ").append(Util.fixed(m.util * 100, 0)).append("%\n");
        sb.append("Throughput              = ").append(Util.fixed(m.thru, 2)).append(" processes per time unit\n");
        if (algo.equals("mlq")) sb.append("\nForeground (Q0) runs round robin, background (Q1) runs FCFS. Q0 always goes first.\n");
        if (algo.equals("mlfq")) sb.append("\nA job drops one queue when it uses up its quantum. A higher queue preempts a lower one.\n");
        return sb.toString();
    }

    /** Parse lines "PID arrival burst [priority] [queue]". */
    static List<Proc> parse(List<String> lines) {
        List<Proc> out = new ArrayList<Proc>();
        java.util.Set<String> seen = new java.util.HashSet<String>();
        for (String ln : lines) {
            List<String> t = Util.toks(ln);
            if (t.size() < 3 || t.size() > 5) throw new IllegalArgumentException("Bad line '" + ln + "': use PID arrival burst [priority] [queue 0 or 1]");
            int[] v = Util.ints(t.subList(1, t.size()), "Process " + t.get(0), 0, null);
            if (v[1] == 0) throw new IllegalArgumentException(t.get(0) + ": burst time must be at least 1");
            if (v.length == 4 && v[3] > 1) throw new IllegalArgumentException(t.get(0) + ": queue must be 0 (foreground) or 1 (background)");
            if (!seen.add(t.get(0))) throw new IllegalArgumentException("Duplicate PID " + t.get(0));
            out.add(new Proc(t.get(0), v[0], v[1], v.length > 2 ? v[2] : 0, v.length > 3 ? v[3] : 0));
        }
        if (out.isEmpty()) throw new IllegalArgumentException("Enter at least one process.");
        return out;
    }
}
