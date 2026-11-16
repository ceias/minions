import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Disk scheduling: FCFS, SSTF, SCAN, C-SCAN, LOOK, C-LOOK. */
final class Disk {
    private Disk() {}

    static final String[] ALGOS = {"FCFS", "SSTF", "SCAN", "C-SCAN", "LOOK", "C-LOOK"};

    static List<Integer> order(int[] reqs, int head, String algo, int size, boolean up) {
        List<Integer> o = new ArrayList<Integer>();
        if (algo.equals("FCFS")) { for (int r : reqs) o.add(r); return o; }
        if (algo.equals("SSTF")) {
            List<Integer> left = new ArrayList<Integer>();
            for (int r : reqs) left.add(r);
            int h = head;
            while (!left.isEmpty()) {
                int bi = 0;
                for (int i = 1; i < left.size(); i++) if (Math.abs(left.get(i) - h) < Math.abs(left.get(bi) - h)) bi = i;
                int x = left.remove(bi);
                o.add(x); h = x;
            }
            return o;
        }
        int[] r = reqs.clone();
        Arrays.sort(r);
        List<Integer> lo = new ArrayList<Integer>(), hi = new ArrayList<Integer>();
        for (int x : r) { if (x < head) lo.add(x); else hi.add(x); }
        Collections.reverse(lo);   // lo: nearest first
        List<Integer> loR = new ArrayList<Integer>(lo), hiR = new ArrayList<Integer>(hi);
        Collections.reverse(loR); Collections.reverse(hiR);
        if (algo.equals("SCAN")) {
            if (up) { o.addAll(hi); o.add(size - 1); o.addAll(lo); } else { o.addAll(lo); o.add(0); o.addAll(hi); }
        } else if (algo.equals("C-SCAN")) {
            if (up) { o.addAll(hi); o.add(size - 1); o.add(0); o.addAll(loR); } else { o.addAll(lo); o.add(0); o.add(size - 1); o.addAll(hiR); }
        } else if (algo.equals("LOOK")) {
            if (up) { o.addAll(hi); o.addAll(lo); } else { o.addAll(lo); o.addAll(hi); }
        } else {   // C-LOOK
            if (up) { o.addAll(hi); o.addAll(loR); } else { o.addAll(lo); o.addAll(hiR); }
        }
        return o;
    }

    static long movement(int head, List<Integer> o) {
        long total = 0;
        for (int x : o) { total += Math.abs(x - head); head = x; }
        return total;
    }

    static String run(List<String> lines, String headText, String sizeText, String direction, String algo) {
        int head = Util.one(headText, "Head", 0), size = Util.one(sizeText, "Disk size", 1);
        int[] reqs = Util.ints(Util.toks(String.join(" ", lines).replace(',', ' ')), "Requests", 0, null);
        if (reqs.length == 0) throw new IllegalArgumentException("Enter at least one request.");
        int max = 0;
        for (int r : reqs) max = Math.max(max, r);
        if (head >= size || max >= size) throw new IllegalArgumentException("Head and requests must be less than the disk size (cylinders 0..size-1).");
        boolean up = direction.equals("Up");
        List<Integer> o = order(reqs, head, algo, size, up);
        StringBuilder sb = new StringBuilder(algo + ", head starts at " + head + "\nService order: " + head);
        for (int x : o) sb.append(" -> ").append(x);
        sb.append("\nTotal head movement: ").append(movement(head, o)).append(" cylinders\n\nComparison:\n");
        List<String[]> rows = new ArrayList<String[]>();
        for (String a : ALGOS) rows.add(Util.row(a, movement(head, order(reqs, head, a, size, up))));
        return sb.append(Util.table(new String[]{"Algorithm", "Total movement"}, rows)).toString();
    }
}
