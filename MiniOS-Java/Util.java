import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Shared helpers: input validation, text tables and number formatting. */
final class Util {
    private Util() {}

    /** Convert tokens to ints; throws IllegalArgumentException with a clear message if invalid. */
    static int[] ints(List<String> tokens, String name, Integer lo, Integer count) {
        if (count != null && tokens.size() != count)
            throw new IllegalArgumentException(name + ": expected " + count + " values, got " + tokens.size());
        int[] out = new int[tokens.size()];
        for (int i = 0; i < out.length; i++) {
            String s = tokens.get(i);
            if (!s.matches("[+-]?\\d+")) throw new IllegalArgumentException(name + ": '" + s + "' is not a whole number");
            long v;
            try {
                v = Long.parseLong(s);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(name + ": '" + s + "' is too large");
            }
            if (v > 1000000000L || v < -1000000000L) throw new IllegalArgumentException(name + ": '" + s + "' is too large");
            if (lo != null && v < lo) throw new IllegalArgumentException(name + ": " + v + " must be >= " + lo);
            out[i] = (int) v;
        }
        return out;
    }

    static int[] ints(List<String> tokens, String name, int lo) { return ints(tokens, name, lo, null); }

    static int one(String text, String name, int lo) {
        return ints(Collections.singletonList(text.trim()), name, lo, null)[0];
    }

    static List<String> toks(String s) {
        s = s.trim();
        return s.isEmpty() ? new ArrayList<String>() : new ArrayList<String>(Arrays.asList(s.split("\\s+")));
    }

    /** Non-empty lines that do not start with '#'. */
    static List<String> lineList(String text) {
        List<String> out = new ArrayList<String>();
        for (String l : text.split("\\r?\\n")) if (!l.trim().isEmpty() && !l.trim().startsWith("#")) out.add(l.trim());
        return out;
    }

    static String pad(String s, int w) {
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < w) sb.append(' ');
        return sb.toString();
    }

    static String dashes(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append('-');
        return sb.toString();
    }

    /** Aligned plain-text table. */
    static String table(String[] h, List<String[]> rows) {
        int[] w = new int[h.length];
        for (int i = 0; i < h.length; i++) {
            w[i] = h[i].length();
            for (String[] r : rows) w[i] = Math.max(w[i], r[i].length());
        }
        StringBuilder sb = new StringBuilder();
        sb.append(line(h, w)).append('\n');
        for (int i = 0; i < w.length; i++) sb.append(i > 0 ? "-+-" : "").append(dashes(w[i]));
        sb.append('\n');
        for (String[] r : rows) sb.append(line(r, w)).append('\n');
        return sb.toString();
    }

    private static String line(String[] r, int[] w) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < r.length; i++) sb.append(i > 0 ? " | " : "").append(pad(r[i], w[i]));
        return sb.toString();
    }

    static String[] row(Object... cells) {
        String[] r = new String[cells.length];
        for (int i = 0; i < r.length; i++) r[i] = String.valueOf(cells[i]);
        return r;
    }

    /** Fixed decimals, rounding the exact value half-to-even (same as Python's format). */
    static String fixed(double x, int digits) {
        return new BigDecimal(x).setScale(digits, RoundingMode.HALF_EVEN).toPlainString();
    }

    static String list(List<Integer> a) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < a.size(); i++) sb.append(i > 0 ? ", " : "").append(a.get(i));
        return sb.append("]").toString();
    }

    static String vec(int[] a) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < a.length; i++) sb.append(i > 0 ? ", " : "").append(a[i]);
        return sb.append("]").toString();
    }

    static boolean le(int[] a, int[] b) {
        for (int i = 0; i < a.length; i++) if (a[i] > b[i]) return false;
        return true;
    }

    static int[] add(int[] a, int[] b) {
        int[] r = new int[a.length];
        for (int i = 0; i < r.length; i++) r[i] = a[i] + b[i];
        return r;
    }
}
