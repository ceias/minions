import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Page replacement from the slides: FIFO, Optimal, NRU, Second Chance, CLOCK, NFU, LRU, Random, Working Set.
 * Pages are stored in fixed frames; a new page takes the victim's frame. New pages load with R = 1.
 */
final class Pages {
    private Pages() {}

    static final String[] IDS = {"fifo", "opt", "nru", "sc", "clock", "nfu", "lru", "rand", "ws"};
    static final String[] NAMES = {"FIFO", "Optimal (OPT)", "Not Recently Used (NRU)", "Second Chance", "CLOCK",
        "Not Frequently Used (NFU)", "Least Recently Used (LRU)", "Random", "Working Set"};

    static String idOf(String name) {
        for (int i = 0; i < NAMES.length; i++) if (NAMES[i].equals(name)) return IDS[i];
        return name;
    }

    static String nameOf(String id) {
        for (int i = 0; i < IDS.length; i++) if (IDS[i].equals(id)) return NAMES[i];
        return id;
    }

    static final class Ref {
        final String p;
        final boolean w;   // a "w" after the page marks a write (sets the modified bit, used by NRU)
        Ref(String p, boolean w) { this.p = p; this.w = w; }
        String label() { return p + (w ? "w" : ""); }
    }

    static final class Step {
        String ref;
        boolean hit;
        int k;
        String[] fr;
        int[] R, M, cnt;
        int hand;
    }

    static final class Out {
        List<Step> steps = new ArrayList<Step>();
        int faults, hits, total, F;
    }

    static List<Ref> parseRefs(String s) {
        List<Ref> out = new ArrayList<Ref>();
        for (String x : s.trim().split("[\\s,]+")) {
            if (x.isEmpty()) continue;
            boolean w = x.length() > 1 && (x.endsWith("w") || x.endsWith("W"));
            out.add(new Ref(w ? x.substring(0, x.length() - 1) : x, w));
        }
        return out;
    }

    /** Small seeded generator (same sequence as the web version), so Random is repeatable. */
    private static final class Rng {
        int seed;
        Rng(int seed) { this.seed = seed; }
        double next() {
            seed += 0x6D2B79F5;
            int t = (seed ^ (seed >>> 15)) * (1 | seed);
            t = (t + ((t ^ (t >>> 7)) * (61 | t))) ^ t;
            return ((t ^ (t >>> 14)) & 0xFFFFFFFFL) / 4294967296.0;
        }
    }

    static Out sim(String algo, List<Ref> refs, int F, int tick, int delta, int seed) {
        final String[] fr = new String[F];
        final int[] R = new int[F], M = new int[F], ld = new int[F], lu = new int[F], cnt = new int[F];
        LinkedList<Integer> order = new LinkedList<Integer>();
        Rng rng = new Rng(seed);
        Out out = new Out();
        out.F = F; out.total = refs.size();
        int hand = 0;
        for (int t = 0; t < refs.size(); t++) {
            Ref r = refs.get(t);
            if (algo.equals("nru") && t > 0 && t % tick == 0) Arrays.fill(R, 0);   // clock tick: clear all R bits
            int k = indexOf(fr, r.p);
            boolean hit = k >= 0;
            if (hit) {
                R[k] = 1; if (r.w) M[k] = 1; lu[k] = t;
            } else {
                out.faults++;
                int free = indexOf(fr, null);
                if (free >= 0) k = free;
                else {
                    List<Integer> all = new ArrayList<Integer>();
                    for (int i = 0; i < F; i++) all.add(i);
                    if (algo.equals("fifo")) k = oldest(all, ld);
                    else if (algo.equals("lru")) k = least(all, lu);
                    else if (algo.equals("opt")) {
                        int[] nx = new int[F];
                        int mx = -1;
                        for (int i = 0; i < F; i++) {
                            nx[i] = Integer.MAX_VALUE;
                            for (int j = t + 1; j < refs.size(); j++) if (refs.get(j).p.equals(fr[i])) { nx[i] = j - t; break; }
                            mx = Math.max(mx, nx[i]);
                        }
                        List<Integer> c = new ArrayList<Integer>();
                        for (int i = 0; i < F; i++) if (nx[i] == mx) c.add(i);
                        k = oldest(c, ld);
                    } else if (algo.equals("nru")) {
                        int mn = Integer.MAX_VALUE;
                        for (int i = 0; i < F; i++) mn = Math.min(mn, 2 * R[i] + M[i]);
                        List<Integer> c = new ArrayList<Integer>();
                        for (int i = 0; i < F; i++) if (2 * R[i] + M[i] == mn) c.add(i);
                        k = oldest(c, ld);
                    } else if (algo.equals("nfu")) {
                        int mn = Integer.MAX_VALUE;
                        for (int i = 0; i < F; i++) mn = Math.min(mn, cnt[i]);
                        List<Integer> c = new ArrayList<Integer>();
                        for (int i = 0; i < F; i++) if (cnt[i] == mn) c.add(i);
                        k = oldest(c, ld);
                    } else if (algo.equals("sc")) {
                        for (;;) {
                            int f = order.getFirst();
                            if (R[f] != 0) { R[f] = 0; order.addLast(order.removeFirst()); }   // second chance
                            else { k = f; order.removeFirst(); break; }
                        }
                    } else if (algo.equals("clock")) {
                        while (R[hand] != 0) { R[hand] = 0; hand = (hand + 1) % F; }
                        k = hand;
                    } else if (algo.equals("rand")) k = (int) Math.floor(rng.next() * F);
                    else {   // working set: evict a page outside the pages used in the last "delta" references
                        Set<String> W = new HashSet<String>();
                        for (int j = Math.max(0, t - delta + 1); j <= t; j++) W.add(refs.get(j).p);
                        List<Integer> outside = new ArrayList<Integer>();
                        for (int i = 0; i < F; i++) if (!W.contains(fr[i])) outside.add(i);
                        k = least(outside.isEmpty() ? all : outside, lu);   // all in the set: fall back to LRU
                    }
                }
                fr[k] = r.p; R[k] = 1; M[k] = r.w ? 1 : 0; ld[k] = t; lu[k] = t; cnt[k] = 0;
                if (algo.equals("sc")) order.addLast(k);
                if (algo.equals("clock")) hand = (k + 1) % F;
            }
            if (algo.equals("nfu") && (t + 1) % tick == 0)   // clock tick: counter += R, then R = 0
                for (int i = 0; i < F; i++) if (fr[i] != null) { cnt[i] += R[i]; R[i] = 0; }
            Step s = new Step();
            s.ref = r.label(); s.hit = hit; s.k = k; s.fr = fr.clone(); s.R = R.clone(); s.M = M.clone(); s.cnt = cnt.clone(); s.hand = hand;
            out.steps.add(s);
        }
        out.hits = out.total - out.faults;
        return out;
    }

    private static int indexOf(String[] a, String x) {
        for (int i = 0; i < a.length; i++) if (x == null ? a[i] == null : x.equals(a[i])) return i;
        return -1;
    }

    private static int oldest(List<Integer> c, int[] ld) { return least(c, ld); }

    private static int least(List<Integer> c, int[] v) {
        int best = c.get(0);
        for (int i : c) if (v[i] < v[best]) best = i;
        return best;
    }

    /** Final state "page:R,page:R" plus the hand position, used by the tests. */
    static String finalState(Out o) {
        Step l = o.steps.get(o.steps.size() - 1);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < l.fr.length; i++) sb.append(i > 0 ? "," : "").append(l.fr[i]).append(':').append(l.R[i]);
        return sb.append(" h").append(l.hand).toString();
    }

    static String report(String algo, String refText, int F, int nruTick, int nfuTick, int delta, int seed) {
        List<Ref> refs = parseRefs(refText);
        if (refs.isEmpty()) throw new IllegalArgumentException("Enter a reference string.");
        for (Ref r : refs) if (!r.p.matches("[\\w-]+") || r.p.matches("-\\d+")) throw new IllegalArgumentException("Reference '" + r.p + "' must be a letter or number.");
        Out o = sim(algo, refs, F, algo.equals("nfu") ? nfuTick : nruTick, delta, seed);
        int w = 3;
        for (Step s : o.steps) { w = Math.max(w, s.ref.length() + 2); for (String f : s.fr) if (f != null) w = Math.max(w, f.length() + 2); }
        StringBuilder sb = new StringBuilder(nameOf(algo)).append(" with ").append(F).append(" frames\n\n");
        sb.append(Util.pad("Reference", 10));
        for (Step s : o.steps) sb.append(Util.pad(s.ref, w));
        sb.append('\n');
        for (int i = 0; i < F; i++) {
            sb.append(Util.pad("Frame " + (i + 1), 10));
            for (Step s : o.steps) sb.append(Util.pad(s.fr[i] == null ? "" : s.fr[i] + (!s.hit && s.k == i ? "*" : ""), w));
            sb.append('\n');
        }
        sb.append(Util.pad("Result", 10));
        for (Step s : o.steps) sb.append(Util.pad(s.hit ? "H" : "F", w));
        sb.append("\n\nF = page fault, H = hit, * = the page that was just loaded\n");
        sb.append("Page faults: ").append(o.faults).append(" of ").append(o.total).append(" references (")
          .append(Util.fixed(100.0 * o.faults / o.total, 0)).append("%)\n");
        sb.append("Hits: ").append(o.hits).append(" (").append(Util.fixed(100.0 * o.hits / o.total, 0)).append("%)\n");
        if (algo.equals("sc") || algo.equals("clock")) {
            Step l = o.steps.get(o.steps.size() - 1);
            sb.append("\nFinal frames (page:R bit").append(algo.equals("clock") ? ", [hand] = next frame to check" : "").append("): ");
            for (int i = 0; i < F; i++) sb.append(i > 0 ? "  " : "").append(l.fr[i]).append(':').append(l.R[i]).append(algo.equals("clock") && l.hand == i ? "[hand]" : "");
            sb.append('\n');
        }
        return sb.toString();
    }
}
