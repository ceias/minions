# Multi.java — line-by-line explanation

[Guide index](README.md) · [Exact source snapshot](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java)

Path: `MiniOS-Java/Multi.java`. Source revision: `dba2d32`.

This simulator admits arrived programs when enough memory is available, and shares one CPU by round-robin slices. Admissions occur at scheduling boundaries. Memory is a capacity counter rather than a block/segment model. Simulation time and busy time use long; memory-time area is a double used only for percentage calculation. This avoids the audit's integer overflows.

Each row preserves one physical source line. Dense lines can contain several statements. Blank lines and closing delimiters are included so numbering matches your editor.

## Imports and class declaration

| Line | Original code | Explanation |
|---:|---|---|
| [L1](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L1) | <code>import java.util.ArrayList;</code> | Import java.util.ArrayList: makes a growable list implementation available without writing its full package name at every use. |
| [L2](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L2) | <code>import java.util.Comparator;</code> | Import java.util.Comparator: makes custom sorting comparisons available without writing its full package name at every use. |
| [L3](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L3) | <code>import java.util.HashMap;</code> | Import java.util.HashMap: makes a key/value hash map available without writing its full package name at every use. |
| [L4](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L4) | <code>import java.util.HashSet;</code> | Import java.util.HashSet: makes a set for unique PIDs/pages available without writing its full package name at every use. |
| [L5](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L5) | <code>import java.util.List;</code> | Import java.util.List: makes the list interface used for variable-length data available without writing its full package name at every use. |
| [L6](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L6) | <code>import java.util.Map;</code> | Import java.util.Map: makes key/value lookup interfaces available without writing its full package name at every use. |
| [L7](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L7) | <code>import java.util.Set;</code> | Import java.util.Set: makes the set interface available without writing its full package name at every use. |
| [L8](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L8) | <code>&nbsp;</code> | Blank line separating related statements; it has no runtime effect. |
| [L9](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L9) | <code>/** Multiprogramming: jobs share memory and the CPU (round robin time slices). A job is admitted when it has arrived and memory is free. */</code> | Developer comment/documentation; explains intent and is not executed. |
| [L10](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L10) | <code>final class Multi {</code> | Declare the class/record container shown here. Its fields and methods define the state and behavior explained below. |
| [L11](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L11) | <code>    private Multi() {}</code> | Private empty constructor: prevent instantiation of this static utility/data class. |
| [L12](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L12) | <code>&nbsp;</code> | Blank line separating related statements; it has no runtime effect. |
| [L13](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L13) | <code>    private static final class Job {</code> | Declare the class/record container shown here. Its fields and methods define the state and behavior explained below. |
| [L14](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L14) | <code>        final String id;</code> | Store each job&#x27;s PID. |
## Job record

| Line | Original code | Explanation |
|---:|---|---|
| [L15](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L15) | <code>        final int m, b, a;</code> | m = memory demand; b = burst; a = arrival. |
| [L16](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L16) | <code>        Job(String id, int m, int b, int a) { this.id = id; this.m = m; this.b = b; this.a = a; }</code> | Initialize PID, memory requirement, burst and arrival in the Job constructor. |
| [L17](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L17) | <code>    }</code> | Close the enclosing block/call/array. Match the opening delimiter above; no independent algorithm step is added. |
| [L18](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L18) | <code>&nbsp;</code> | Blank line separating related statements; it has no runtime effect. |
## Input validation

| Line | Original code | Explanation |
|---:|---|---|
| [L19](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L19) | <code>    static String run(List&lt;String&gt; lines, String totalText, String quantumText) {</code> | Declare the method shown, its parameter/return types and visibility. Its body executes only when called. |
| [L20](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L20) | <code>        int total = Util.one(totalText, &quot;Total memory&quot;, 1), q = Util.one(quantumText, &quot;Quantum&quot;, 1);</code> | Require positive total memory and quantum. |
| [L21](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L21) | <code>        List&lt;Job&gt; wait = new ArrayList&lt;Job&gt;();</code> | Declare the named typed variable/field and, if an initializer is present, evaluate it now. final prevents reassignment of that reference/value; a referenced collection may still be mutable. |
| [L22](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L22) | <code>        Set&lt;String&gt; seen = new HashSet&lt;String&gt;();</code> | Declare the named typed variable/field and, if an initializer is present, evaluate it now. final prevents reassignment of that reference/value; a referenced collection may still be mutable. |
| [L23](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L23) | <code>        for (String ln : lines) {</code> | Loop over the indicated indexes/items. The body performs the next operation for each eligible item; an inline if filters items and an inline add/update is executed inside the loop. |
| [L24](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L24) | <code>            List&lt;String&gt; t = Util.toks(ln);</code> | Declare the named typed variable/field and, if an initializer is present, evaluate it now. final prevents reassignment of that reference/value; a referenced collection may still be mutable. |
| [L25](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L25) | <code>            if (t.size() != 4) throw new IllegalArgumentException(&quot;&#x27;&quot; + ln + &quot;&#x27;: use PID memory burst arrival&quot;);</code> | Require exactly PID memory burst arrival on each row. |
| [L26](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L26) | <code>            int[] v = Util.ints(t.subList(1, 4), &quot;Program &quot; + t.get(0), 0, null);</code> | Parse the three numeric attributes as nonnegative integers. |
| [L27](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L27) | <code>            if (v[0] &lt; 1 &#124;&#124; v[1] &lt; 1) throw new IllegalArgumentException(t.get(0) + &quot;: memory and burst must be at least 1&quot;);</code> | Require strictly positive memory and burst. |
| [L28](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L28) | <code>            if (v[0] &gt; total) throw new IllegalArgumentException(t.get(0) + &quot; needs &quot; + v[0] + &quot; but total memory is only &quot; + total);</code> | Reject a job that could never fit total memory. |
| [L29](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L29) | <code>            if (!seen.add(t.get(0))) throw new IllegalArgumentException(&quot;Duplicate PID &quot; + t.get(0));</code> | Reject duplicate PIDs. |
| [L30](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L30) | <code>            wait.add(new Job(t.get(0), v[0], v[1], v[2]));</code> | Append the validated job to the waiting list. |
| [L31](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L31) | <code>        }</code> | Close the enclosing block/call/array. Match the opening delimiter above; no independent algorithm step is added. |
| [L32](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L32) | <code>        if (wait.isEmpty()) throw new IllegalArgumentException(&quot;Enter at least one program.&quot;);</code> | Require at least one program. |
## Simulation state

| Line | Original code | Explanation |
|---:|---|---|
| [L33](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L33) | <code>        wait.sort(new Comparator&lt;Job&gt;() { public int compare(Job x, Job y) { return Integer.compare(x.a, y.a); } });</code> | Sort by arrival; Java&#x27;s stable list sort preserves input order for tied arrivals. |
| [L34](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L34) | <code>        long t = 0, busy = 0;</code> | Use long simulated elapsed and CPU busy times so their totals can exceed 2,147,483,647. |
| [L35](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L35) | <code>        int peak = 0, mem = 0;</code> | Track peak resident jobs and current memory usage. |
| [L36](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L36) | <code>        // Memory-time area may exceed even long for many accepted long jobs.</code> | Developer comment/documentation; explains intent and is not executed. |
| [L37](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L37) | <code>        double area = 0;</code> | Accumulate memory*duration in double to avoid overflow for very large aggregate workloads. |
| [L38](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L38) | <code>        List&lt;String&gt; ready = new ArrayList&lt;String&gt;(), log = new ArrayList&lt;String&gt;();</code> | ready is the resident RR queue; log contains displayed state changes. |
| [L39](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L39) | <code>        Map&lt;String, Integer&gt; rem = new HashMap&lt;String, Integer&gt;(), memof = new HashMap&lt;String, Integer&gt;();</code> | Map PIDs to remaining burst and their reserved memory. |
## Admit and dispatch

| Line | Original code | Explanation |
|---:|---|---|
| [L40](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L40) | <code>        while (!wait.isEmpty() &#124;&#124; !ready.isEmpty()) {</code> | Keep simulating while either waiting or resident programs remain. |
| [L41](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L41) | <code>            for (Job j : new ArrayList&lt;Job&gt;(wait))</code> | Iterate a snapshot so admission may safely remove jobs from wait. |
| [L42](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L42) | <code>                if (j.a &lt;= t &amp;&amp; mem + j.m &lt;= total) {</code> | A job may enter only after arrival and only if its memory fits remaining capacity. |
| [L43](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L43) | <code>                    wait.remove(j); ready.add(j.id); mem += j.m; rem.put(j.id, j.b); memof.put(j.id, j.m);</code> | Move it to ready, reserve memory and initialize remaining burst. |
| [L44](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L44) | <code>                    log.add(String.format(&quot;t=%-4d ADMIT  %s  memory in use %d/%d&quot;, t, j.id, mem, total));</code> | Log admission time and total memory in use. |
| [L45](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L45) | <code>                }</code> | Close the enclosing block/call/array. Match the opening delimiter above; no independent algorithm step is added. |
| [L46](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L46) | <code>            peak = Math.max(peak, ready.size());</code> | Update the maximum number of resident programs. |
| [L47](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L47) | <code>            if (ready.isEmpty()) {</code> | With no ready job, no program occupies the CPU. |
| [L48](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L48) | <code>                // With no resident jobs, jump directly to the next arrival.</code> | Developer comment/documentation; explains intent and is not executed. |
| [L49](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L49) | <code>                t = wait.get(0).a;</code> | Jump directly to the earliest waiting arrival instead of looping over each idle tick. |
| [L50](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L50) | <code>                continue;</code> | Skip to the next iteration of the surrounding loop. |
| [L51](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L51) | <code>            }</code> | Close the enclosing block/call/array. Match the opening delimiter above; no independent algorithm step is added. |
| [L52](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L52) | <code>            String p = ready.remove(0);</code> | Remove the front RR job for execution. |
| [L53](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L53) | <code>            int run = Math.min(q, rem.get(p));</code> | Run for the smaller of its quantum and remaining burst. |
| [L54](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L54) | <code>            log.add(String.format(&quot;t=%-4d RUN    %s  for %d  (degree of multiprogramming %d)&quot;, t, p, run, ready.size() + 1));</code> | Log CPU dispatch and the current degree of multiprogramming. |
| [L55](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L55) | <code>            t += run; busy += run; area += (double) mem * run; rem.put(p, rem.get(p) - run);</code> | Advance time and busy duration; accumulate occupied-memory area; subtract the executed burst. |
| [L56](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L56) | <code>            if (rem.get(p) == 0) { mem -= memof.get(p); log.add(String.format(&quot;t=%-4d FINISH %s  memory in use %d/%d&quot;, t, p, mem, total)); }</code> | A finished job releases its memory and logs completion. |
| [L57](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L57) | <code>            else ready.add(p);</code> | An unfinished job goes to the back of the RR queue. |
| [L58](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L58) | <code>        }</code> | Close the enclosing block/call/array. Match the opening delimiter above; no independent algorithm step is added. |
| [L59](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L59) | <code>        return String.join(&quot;\n&quot;, log) + &quot;\n\nTotal time: &quot; + t + &quot;\nCPU utilization: &quot; + Util.fixed(100.0 * busy / t, 1) + &quot;%\n&quot;</code> | Report elapsed time and busy/elapsed CPU utilization. |
## Utilization report

| Line | Original code | Explanation |
|---:|---|---|
| [L60](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L60) | <code>            + &quot;Average memory utilization: &quot; + Util.fixed(100.0 * area / ((double) t * total), 1) + &quot;%\n&quot;</code> | Divide memory-time area by elapsed*totalMemory for time-weighted average utilization. |
| [L61](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L61) | <code>            + &quot;Maximum programs in memory at once: &quot; + peak + &quot;\n&quot;;</code> | Report maximum simultaneously resident programs. |
| [L62](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L62) | <code>    }</code> | Close the enclosing block/call/array. Match the opening delimiter above; no independent algorithm step is added. |
| [L63](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/Multi.java#L63) | <code>}</code> | Close the enclosing block/call/array. Match the opening delimiter above; no independent algorithm step is added. |
