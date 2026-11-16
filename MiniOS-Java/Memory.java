import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Paging (address translation), segmentation (base/limit) and fragmentation (first / best / worst fit). */
final class Memory {
    private Memory() {}

    /** Page table lines "page frame". Physical = frame * page size + offset. */
    static String paging(List<String> lines, String pageSize, String addrText) {
        int ps = Util.one(pageSize, "Page size", 1);
        Map<Integer, Integer> pt = new LinkedHashMap<Integer, Integer>();
        for (String ln : lines) {
            int[] pf = Util.ints(Util.toks(ln), "Page table '" + ln + "'", 0, 2);
            if (pt.containsKey(pf[0])) throw new IllegalArgumentException("Page " + pf[0] + " appears twice in the page table");
            pt.put(pf[0], pf[1]);
        }
        if (pt.isEmpty()) throw new IllegalArgumentException("Enter at least one page table entry.");
        int[] addrs = Util.ints(Util.toks(addrText.replace(',', ' ')), "Addresses", 0, null);
        if (addrs.length == 0) throw new IllegalArgumentException("Enter at least one logical address.");
        List<String[]> rows = new ArrayList<String[]>();
        for (int a : addrs) {
            int p = a / ps, off = a % ps;
            rows.add(pt.containsKey(p) ? Util.row(a, p, off, pt.get(p), (long) pt.get(p) * ps + off) : Util.row(a, p, off, "-", "PAGE FAULT"));
        }
        return Util.table(new String[]{"Logical", "Page", "Offset", "Frame", "Physical"}, rows)
            + "\nFormula: physical = frame * page_size + offset; page = address // page_size\n";
    }

    /** Segment table lines "segment base limit"; addresses written seg:offset. Valid if offset < limit. */
    static String segmentation(List<String> lines, String addrText) {
        Map<Integer, int[]> seg = new LinkedHashMap<Integer, int[]>();
        for (String ln : lines) {
            int[] v = Util.ints(Util.toks(ln), "Segment '" + ln + "'", 0, 3);
            if (v[2] == 0) throw new IllegalArgumentException("Segment " + v[0] + ": limit must be at least 1");
            if (seg.containsKey(v[0])) throw new IllegalArgumentException("Segment " + v[0] + " defined twice");
            seg.put(v[0], new int[]{v[1], v[2]});
        }
        if (seg.isEmpty()) throw new IllegalArgumentException("Enter at least one segment.");
        List<String[]> rows = new ArrayList<String[]>();
        for (String tok : Util.toks(addrText.replace(',', ' '))) {
            int colons = tok.length() - tok.replace(":", "").length();
            if (colons != 1) throw new IllegalArgumentException("Address '" + tok + "' must look like seg:offset");
            String[] parts = tok.split(":", -1);
            int[] so = Util.ints(java.util.Arrays.asList(parts), "Address " + tok, 0, null);
            int s = so[0], off = so[1];
            if (!seg.containsKey(s)) rows.add(Util.row(tok, s, off, "-", "TRAP: no such segment"));
            else if (off >= seg.get(s)[1]) rows.add(Util.row(tok, s, off, seg.get(s)[1], "TRAP: offset beyond limit"));
            else rows.add(Util.row(tok, s, off, seg.get(s)[1], (long) seg.get(s)[0] + off));
        }
        if (rows.isEmpty()) throw new IllegalArgumentException("Enter at least one address.");
        return Util.table(new String[]{"Address", "Seg", "Offset", "Limit", "Physical"}, rows)
            + "\nRule: valid if offset < limit; physical = base + offset\n";
    }

    /** Line 1 = block sizes, line 2 = process sizes. Each block holds at most one process. */
    static String fragmentation(List<String> lines, String strat) {
        if (lines.size() != 2) throw new IllegalArgumentException("Enter exactly 2 lines: block sizes, then process sizes.");
        int[] blocks = Util.ints(Util.toks(lines.get(0)), "Blocks", 1, null);
        int[] procs = Util.ints(Util.toks(lines.get(1)), "Processes", 1, null);
        List<Integer> free = new ArrayList<Integer>();
        for (int i = 0; i < blocks.length; i++) free.add(i);
        List<String[]> rows = new ArrayList<String[]>();
        List<Integer> waiting = new ArrayList<Integer>();
        long internal = 0;
        for (int i = 0; i < procs.length; i++) {
            int sz = procs[i];
            List<Integer> c = new ArrayList<Integer>();
            for (int b : free) if (blocks[b] >= sz) c.add(b);
            if (c.isEmpty()) { rows.add(Util.row("P" + (i + 1), sz, "-", "NOT ALLOCATED", "-")); waiting.add(sz); continue; }
            int b = c.get(0);
            for (int x : c) {
                if (strat.equals("Best Fit") && blocks[x] < blocks[b]) b = x;
                if (strat.equals("Worst Fit") && blocks[x] > blocks[b]) b = x;
            }
            free.remove(Integer.valueOf(b));
            internal += blocks[b] - sz;
            rows.add(Util.row("P" + (i + 1), sz, "B" + (b + 1) + " (" + blocks[b] + ")", "allocated", blocks[b] - sz));
        }
        long ext = 0;
        StringBuilder fl = new StringBuilder();
        for (int b : free) { ext += blocks[b]; fl.append(fl.length() > 0 ? ", " : "").append("B").append(b + 1).append("(").append(blocks[b]).append(")"); }
        List<Integer> fragmented = new ArrayList<Integer>();
        for (int w : waiting) if (w <= ext) fragmented.add(w);
        StringBuilder sb = new StringBuilder(Util.table(new String[]{"Process", "Size", "Block", "Status", "Internal frag"}, rows));
        sb.append("\nTotal internal fragmentation: ").append(internal).append("\nFree (unused) blocks: ")
          .append(fl.length() == 0 ? "none" : fl.toString()).append("\nTotal free memory: ").append(ext).append('\n');
        if (!fragmented.isEmpty())
            sb.append("EXTERNAL FRAGMENTATION: enough total free memory exists but no single block fits process size(s) ").append(Util.list(fragmented)).append(".\n");
        else sb.append("No external fragmentation problem detected.\n");
        return sb.toString();
    }
}
