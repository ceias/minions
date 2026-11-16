import java.util.ArrayList;
import java.util.List;

/** Deadlock detection with several instances per resource type, plus a greedy recovery suggestion. */
final class Deadlock {
    private Deadlock() {}

    static final class Result {
        List<String> steps = new ArrayList<String>();
        List<Integer> dead = new ArrayList<Integer>(), order = new ArrayList<Integer>(), kill = new ArrayList<Integer>();
    }

    /** Work = Available. A process that holds nothing counts as finished. Repeat: find Request <= Work, release its Allocation. */
    static Result detect(int[][] A, int[][] R, int[] V, List<String> pids) {
        Result res = new Result();
        int n = A.length;
        int[] work = V.clone();
        boolean[] fin = new boolean[n];
        for (int i = 0; i < n; i++) {
            fin[i] = true;
            for (int x : A[i]) if (x != 0) fin[i] = false;
            if (fin[i]) res.steps.add(pids.get(i) + " holds nothing, so it is already counted as finished.");
        }
        boolean go = true;
        while (go) {
            go = false;
            for (int i = 0; i < n; i++)
                if (!fin[i] && Util.le(R[i], work)) {
                    res.steps.add(pids.get(i) + ": request " + Util.vec(R[i]) + " <= work " + Util.vec(work) + ", so it can finish and releases " + Util.vec(A[i]) + ".");
                    work = Util.add(work, A[i]); fin[i] = true; go = true; res.order.add(i);
                }
        }
        for (int i = 0; i < n; i++) if (!fin[i]) res.dead.add(i);
        return res;
    }

    /** Terminate the biggest holder among the stuck processes until nothing is stuck. */
    static List<Integer> recovery(int[][] A0, int[][] R0, int[] V0, List<String> pids) {
        int n = A0.length, m = V0.length;
        int[][] A = new int[n][], R = new int[n][];
        for (int i = 0; i < n; i++) { A[i] = A0[i].clone(); R[i] = R0[i].clone(); }
        int[] V = V0.clone();
        List<Integer> kill = new ArrayList<Integer>();
        List<Integer> dead = detect(A, R, V, pids).dead;
        while (!dead.isEmpty()) {
            int v = dead.get(0);
            for (int i : dead) if (sum(A[i]) > sum(A[v])) v = i;
            kill.add(v);
            V = Util.add(V, A[v]);
            A[v] = new int[m]; R[v] = new int[m];
            dead = detect(A, R, V, pids).dead;
        }
        return kill;
    }

    private static int sum(int[] a) { int s = 0; for (int x : a) s += x; return s; }

    private static String names(List<String> pids, List<Integer> ids, String sep) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) sb.append(i > 0 ? sep : "").append(pids.get(ids.get(i)));
        return sb.toString();
    }

    /** Input: line 1 = Available; then "PID alloc... request..." per process. */
    static String run(List<String> ls) {
        if (ls.size() < 2) throw new IllegalArgumentException("Enter the Available line and at least one process line.");
        int[] avail = Util.ints(Util.toks(ls.get(0)), "Available", 0, null);
        int m = avail.length;
        List<String> pids = new ArrayList<String>();
        List<int[]> alloc = new ArrayList<int[]>(), req = new ArrayList<int[]>();
        for (int k = 1; k < ls.size(); k++) {
            String ln = ls.get(k);
            List<String> t = Util.toks(ln);
            if (t.size() != 2 * m + 1) throw new IllegalArgumentException("'" + ln + "': need PID + " + m + " Allocation + " + m + " Request values");
            int[] a = Util.ints(t.subList(1, m + 1), t.get(0) + " Allocation", 0, null);
            int[] r = Util.ints(t.subList(m + 1, t.size()), t.get(0) + " Request", 0, null);
            if (pids.contains(t.get(0))) throw new IllegalArgumentException("Duplicate PID " + t.get(0));
            pids.add(t.get(0)); alloc.add(a); req.add(r);
        }
        String[] h = new String[1 + 2 * m];
        h[0] = "PID";
        for (int j = 0; j < m; j++) { h[1 + j] = "Alloc" + j; h[1 + m + j] = "Req" + j; }
        List<String[]> rows = new ArrayList<String[]>();
        for (int i = 0; i < pids.size(); i++) {
            List<Object> c = new ArrayList<Object>();
            c.add(pids.get(i));
            for (int x : alloc.get(i)) c.add(x);
            for (int x : req.get(i)) c.add(x);
            rows.add(Util.row(c.toArray()));
        }
        int[][] A = alloc.toArray(new int[0][]), R = req.toArray(new int[0][]);
        Result r = detect(A, R, avail, pids);
        StringBuilder sb = new StringBuilder(Util.table(h, rows));
        sb.append("\nWork = Available = ").append(Util.vec(avail)).append('\n');
        for (String s : r.steps) sb.append(s).append('\n');
        if (r.dead.isEmpty()) {
            sb.append("\nNO DEADLOCK.").append(r.order.isEmpty() ? " Nothing is waiting." : " Every process can finish: " + names(pids, r.order, " -> "))
              .append('\n');
        } else {
            sb.append("\nDEADLOCK detected. Stuck processes: ").append(names(pids, r.dead, ", ")).append('\n');
            sb.append("Suggested recovery: terminate ").append(names(pids, recovery(A, R, avail, pids), ", then "))
              .append(" to release its resources so the rest can finish.\n");
        }
        return sb.toString();
    }
}
