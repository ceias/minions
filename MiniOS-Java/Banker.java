import java.util.ArrayList;
import java.util.List;

/** Banker's algorithm: Need = Max - Allocation, safe sequence, and a resource-request test. */
final class Banker {
    private Banker() {}

    /** Safe sequence (indexes), or null if the state is unsafe. Scans the processes in order, repeatedly. */
    static List<Integer> safeSeq(int[] avail, int[][] alloc, int[][] need) {
        int n = alloc.length;
        int[] work = avail.clone();
        boolean[] done = new boolean[n];
        List<Integer> seq = new ArrayList<Integer>();
        boolean progress = true;
        while (progress) {
            progress = false;
            for (int i = 0; i < n; i++)
                if (!done[i] && Util.le(need[i], work)) { work = Util.add(work, alloc[i]); done[i] = true; seq.add(i); progress = true; }
        }
        return seq.size() == n ? seq : null;
    }

    private static String seqText(List<String> pids, List<Integer> seq) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < seq.size(); i++) sb.append(i > 0 ? " -> " : "").append(pids.get(seq.get(i)));
        return sb.toString();
    }

    /** Input: line 1 = Available; then "PID alloc... max..." per process. Optional request "P1 1 0 2". */
    static String run(List<String> ls, String req) {
        if (ls.size() < 2) throw new IllegalArgumentException("Enter the Available line and at least one process line.");
        int[] avail = Util.ints(Util.toks(ls.get(0)), "Available", 0, null);
        int m = avail.length;
        List<String> pids = new ArrayList<String>();
        List<int[]> alloc = new ArrayList<int[]>(), need = new ArrayList<int[]>();
        for (int k = 1; k < ls.size(); k++) {
            String ln = ls.get(k);
            List<String> t = Util.toks(ln);
            if (t.size() != 2 * m + 1) throw new IllegalArgumentException("'" + ln + "': need PID + " + m + " Allocation + " + m + " Max values");
            int[] a = Util.ints(t.subList(1, m + 1), t.get(0) + " Allocation", 0, null);
            int[] mx = Util.ints(t.subList(m + 1, t.size()), t.get(0) + " Max", 0, null);
            int[] nd = new int[m];
            for (int j = 0; j < m; j++) { if (a[j] > mx[j]) throw new IllegalArgumentException(t.get(0) + ": Allocation exceeds Max"); nd[j] = mx[j] - a[j]; }
            if (pids.contains(t.get(0))) throw new IllegalArgumentException("Duplicate PID " + t.get(0));
            pids.add(t.get(0)); alloc.add(a); need.add(nd);
        }
        String[] h = new String[1 + 2 * m];
        h[0] = "PID";
        for (int j = 0; j < m; j++) { h[1 + j] = "Alloc" + j; h[1 + m + j] = "Need" + j; }
        List<String[]> rows = new ArrayList<String[]>();
        for (int i = 0; i < pids.size(); i++) {
            List<Object> c = new ArrayList<Object>();
            c.add(pids.get(i));
            for (int x : alloc.get(i)) c.add(x);
            for (int x : need.get(i)) c.add(x);
            rows.add(Util.row(c.toArray()));
        }
        StringBuilder sb = new StringBuilder(Util.table(h, rows));
        int[][] A = alloc.toArray(new int[0][]), N = need.toArray(new int[0][]);
        List<Integer> seq = safeSeq(avail, A, N);
        sb.append(seq != null ? "\nSYSTEM IS SAFE. Safe sequence: " + seqText(pids, seq) + "\n"
                              : "\nSYSTEM IS UNSAFE: no safe sequence (deadlock possible).\n");
        req = req.trim();
        if (!req.isEmpty()) {
            List<String> t = Util.toks(req);
            if (!pids.contains(t.get(0))) throw new IllegalArgumentException("Request: unknown process " + t.get(0));
            int i = pids.indexOf(t.get(0));
            int[] r = Util.ints(t.subList(1, t.size()), "Request", 0, m);
            if (!Util.le(r, N[i])) throw new IllegalArgumentException("Request exceeds the process's remaining Need");
            if (!Util.le(r, avail)) return sb.append("\nRequest ").append(req).append(": ").append(t.get(0)).append(" must WAIT (not enough available resources).\n").toString();
            int[][] a2 = new int[A.length][], n2 = new int[N.length][];
            for (int x = 0; x < A.length; x++) { a2[x] = A[x].clone(); n2[x] = N[x].clone(); }
            for (int j = 0; j < m; j++) { a2[i][j] += r[j]; n2[i][j] -= r[j]; }
            int[] av2 = new int[m];
            for (int j = 0; j < m; j++) av2[j] = avail[j] - r[j];
            List<Integer> s2 = safeSeq(av2, a2, n2);
            sb.append(s2 != null ? "\nRequest " + req + ": GRANTED. New safe sequence: " + seqText(pids, s2) + "\n"
                                 : "\nRequest " + req + ": DENIED (would leave the system unsafe).\n");
        }
        return sb.toString();
    }
}
