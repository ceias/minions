# RubricTests.java — line-by-line explanation

[Guide index](README.md) · [Exact source snapshot](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java)

Path: `MiniOS-Java/RubricTests.java`. Source revision: `dba2d32`.

This runner verifies flexible input and the additional module/algorithm combinations. It is test code, not an in-app rubric feature. Some checks are smoke tests that assert output exists; other checks have explicit numerical answers. It throws AssertionError immediately on a failed check, giving a nonzero process exit.

Each row preserves one physical source line. Dense lines can contain several statements. Blank lines and closing delimiters are included so numbering matches your editor.

## Imports and class declaration

| Line | Original code | Explanation |
|---:|---|---|
| [L1](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L1) | <code>import java.util.*;</code> | Import java.util.*: makes the types/helpers in this package available without writing its full package name at every use. |
| [L2](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L2) | <code>&nbsp;</code> | Blank line separating related statements; it has no runtime effect. |
| [L3](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L3) | <code>/** Rubric regressions: all algorithm selectors, scalable input and validation. */</code> | Developer comment/documentation; explains intent and is not executed. |
| [L4](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L4) | <code>public class RubricTests {</code> | Declare the class/record container shown here. Its fields and methods define the state and behavior explained below. |
| [L5](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L5) | <code>    private static int checks;</code> | Declare the named typed variable/field and, if an initializer is present, evaluate it now. final prevents reassignment of that reference/value; a referenced collection may still be mutable. |
## Fail-fast assertion

| Line | Original code | Explanation |
|---:|---|---|
| [L6](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L6) | <code>    static void check(String label, boolean ok) {</code> | Declare the method shown, its parameter/return types and visibility. Its body executes only when called. |
| [L7](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L7) | <code>        if (!ok) throw new AssertionError(label);</code> | Fail immediately with the check label if the condition is false. |
| [L8](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L8) | <code>        checks++;</code> | Only increment after a passing check. |
| [L9](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L9) | <code>        System.out.println(&quot;PASS  &quot; + label);</code> | Print test progress/summary to the terminal; this is diagnostic output rather than a simulator GUI result. |
| [L10](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L10) | <code>    }</code> | Close the enclosing block/call/array. Match the opening delimiter above; no independent algorithm step is added. |
## All module/sample combinations

| Line | Original code | Explanation |
|---:|---|---|
| [L11](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L11) | <code>    public static void main(String[] args) {</code> | Declare the method shown, its parameter/return types and visibility. Its body executes only when called. |
| [L12](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L12) | <code>        for (Modules.Module m : Modules.all()) {</code> | Visit every module definition, including extra deadlock detection. |
| [L13](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L13) | <code>            for (Modules.Sample sample : new Modules.Sample[]{m.small, m.large, m.edge}) {</code> | Exercise Small, Large and Edge data for each module. |
| [L14](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L14) | <code>                Map&lt;String, String&gt; opts = new LinkedHashMap&lt;String, String&gt;();</code> | Declare the named typed variable/field and, if an initializer is present, evaluate it now. final prevents reassignment of that reference/value; a referenced collection may still be mutable. |
| [L15](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L15) | <code>                for (Modules.Field f : m.fields) opts.put(f.label, f.def);</code> | Start with all field defaults. |
| [L16](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L16) | <code>                opts.putAll(sample.opts);</code> | Apply that sample&#x27;s option overrides. |
| [L17](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L17) | <code>                for (Modules.Field f : m.fields) if (f.choices != null) {</code> | Find every field with selectable choices. |
| [L18](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L18) | <code>                    for (String choice : f.choices) {</code> | Try every choice in that field. |
| [L19](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L19) | <code>                        opts.put(f.label, choice);</code> | Change the named option to that choice. |
| [L20](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L20) | <code>                        check(m.name + &quot; / &quot; + choice + &quot; / &quot; + Util.lineList(sample.text).size() + &quot; input lines&quot;,</code> | Label the smoke check with module, choice and input-row count. |
| [L21](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L21) | <code>                            m.run.run(Util.lineList(sample.text), opts).length() &gt; 20);</code> | Require nontrivial output; this verifies execution, not exact arithmetic. |
| [L22](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L22) | <code>                    }</code> | Close the enclosing block/call/array. Match the opening delimiter above; no independent algorithm step is added. |
| [L23](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L23) | <code>                }</code> | Close the enclosing block/call/array. Match the opening delimiter above; no independent algorithm step is added. |
| [L24](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L24) | <code>                check(m.name + &quot; sample&quot;, m.run.run(Util.lineList(sample.text), opts).length() &gt; 20);</code> | Also execute the final options combination after the choices loop. |
| [L25](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L25) | <code>            }</code> | Close the enclosing block/call/array. Match the opening delimiter above; no independent algorithm step is added. |
## Blank input

| Line | Original code | Explanation |
|---:|---|---|
| [L26](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L26) | <code>            MiniOS.ModulePanel panel = new MiniOS.ModulePanel(m);</code> | Build a Swing panel to test its validation path. |
| [L27](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L27) | <code>            panel.load(m.small);</code> | Invoke the shown method/constructor with these arguments. The method&#x27;s walkthrough explains its behavior; this call belongs to Blank input. |
| [L28](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L28) | <code>            panel.input.setText(&quot;&quot;);</code> | Clear input completely. |
| [L29](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L29) | <code>            check(m.name + &quot; rejects blank input&quot;, panel.execute().startsWith(&quot;Invalid input&quot;));</code> | Require blank input to produce Invalid input. |
| [L30](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L30) | <code>        }</code> | Close the enclosing block/call/array. Match the opening delimiter above; no independent algorithm step is added. |
## Previously fixed limits

| Line | Original code | Explanation |
|---:|---|---|
| [L31](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L31) | <code>        check(&quot;CPU completes beyond former 100,000-tick cutoff&quot;,</code> | Verify CPU completes beyond former 100,000-tick cutoff. The remaining arguments supply actual/expected values or a boolean condition; the check helper records or throws on failure. |
| [L32](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L32) | <code>            Cpu.sim(Cpu.parse(Arrays.asList(&quot;P1 0 100001&quot;)), &quot;fcfs&quot;, 2, 0, new int[]{8,16}).ct[0] == 100001);</code> | A100,001-tick CPU burst must actually finish, proving the old cutoff is removed. |
| [L33](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L33) | <code>        for (String a : Pages.IDS) {</code> | Loop over the indicated indexes/items. The body performs the next operation for each eligible item; an inline if filters items and an inline add/update is executed inside the loop. |
| [L34](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L34) | <code>            Pages.Out out = Pages.sim(a, Pages.parseRefs(&quot;1 2 3 1&quot;), 64, 4, 4, 1);</code> | With64 frames, all four references use available frames rather than replacement. |
| [L35](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L35) | <code>            check(a + &quot; known answer with 64 frames&quot;, out.faults == 3 &amp;&amp; out.hits == 1);</code> | Expect3 distinct page faults and1 repeated-reference hit. |
| [L36](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L36) | <code>        }</code> | Close the enclosing block/call/array. Match the opening delimiter above; no independent algorithm step is added. |
| [L37](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L37) | <code>        for (Modules.Module m : Modules.all()) if (m.name.equals(&quot;Page Replacement&quot;)) {</code> | Loop over the indicated indexes/items. The body performs the next operation for each eligible item; an inline if filters items and an inline add/update is executed inside the loop. |
| [L38](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L38) | <code>            MiniOS.ModulePanel panel = new MiniOS.ModulePanel(m);</code> | Update the indicated local/object state with the expression on this line. These changes are used by subsequent statements in this section. |
| [L39](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L39) | <code>            panel.load(m.small);</code> | Invoke the shown method/constructor with these arguments. The method&#x27;s walkthrough explains its behavior; this call belongs to Previously fixed limits. |
| [L40](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L40) | <code>            ((javax.swing.JTextField)panel.comps.get(&quot;Frames&quot;)).setText(&quot;64&quot;);</code> | Enter64 via the actual Frames UI control. |
| [L41](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L41) | <code>            check(&quot;GUI accepts more than 20 frames&quot;, panel.execute().contains(&quot;64 frames&quot;));</code> | Verify the former20-frame cap no longer blocks UI execution. |
| [L42](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L42) | <code>            panel.input.setText(&quot;-1 2&quot;);</code> | Use a negative numeric page as invalid input. |
| [L43](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L43) | <code>            check(&quot;Negative numeric pages rejected&quot;, panel.execute().startsWith(&quot;Invalid input&quot;));</code> | Verify Negative numeric pages rejected. The remaining arguments supply actual/expected values or a boolean condition; the check helper records or throws on failure. |
| [L44](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L44) | <code>        }</code> | Close the enclosing block/call/array. Match the opening delimiter above; no independent algorithm step is added. |
## Known-answer boundaries

| Line | Original code | Explanation |
|---:|---|---|
| [L45](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L45) | <code>        check(&quot;Indented comments ignored&quot;, Util.lineList(&quot;  # note\nP1 0 1&quot;).size() == 1);</code> | Verify Indented comments ignored. The remaining arguments supply actual/expected values or a boolean condition; the check helper records or throws on failure. |
| [L46](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L46) | <code>        check(&quot;Paging manual answer 1500 -&gt; 2524&quot;, Memory.paging(Arrays.asList(&quot;1 2&quot;), &quot;1024&quot;, &quot;1500&quot;).contains(&quot;2524&quot;));</code> | Manual translation:1500/1024 gives page1, offset476; frame2 gives2524. |
| [L47](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L47) | <code>        check(&quot;Segment last valid byte&quot;, Memory.segmentation(Arrays.asList(&quot;0 100 50&quot;), &quot;0:49&quot;).contains(&quot;149&quot;));</code> | Offset49 is the last valid byte for segment limit50; base100 gives149. |
| [L48](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L48) | <code>        check(&quot;Segment limit traps&quot;, Memory.segmentation(Arrays.asList(&quot;0 100 50&quot;), &quot;0:50&quot;).contains(&quot;TRAP&quot;));</code> | Offset50 equals the exclusive limit and must trap. |
| [L49](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L49) | <code>        check(&quot;Fixed partition internal waste 100-60=40&quot;, Memory.fragmentation(Arrays.asList(&quot;100&quot;, &quot;60&quot;), &quot;First Fit&quot;).contains(&quot;Total internal fragmentation: 40&quot;));</code> | Allocating60 into fixed block100 wastes40 internally. |
| [L50](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L50) | <code>        check(&quot;Multiprogramming two equal jobs fully utilize CPU&quot;, Multi.run(Arrays.asList(&quot;A 50 2 0&quot;, &quot;B 50 2 0&quot;), &quot;100&quot;, &quot;1&quot;).contains(&quot;CPU utilization: 100.0%&quot;));</code> | Two equal resident jobs keep the CPU busy100% in this no-idle example. |
| [L51](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L51) | <code>        System.out.println(&quot;\n&quot; + checks + &quot;/&quot; + checks + &quot; rubric checks passed&quot;);</code> | Print the passing-check count; any earlier assertion would already have stopped execution. |
| [L52](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L52) | <code>    }</code> | Close the enclosing block/call/array. Match the opening delimiter above; no independent algorithm step is added. |
| [L53](https://github.com/ceias/minions/blob/dba2d321e276383939b0bf661659738741bdd343/MiniOS-Java/RubricTests.java#L53) | <code>}</code> | Close the enclosing block/call/array. Match the opening delimiter above; no independent algorithm step is added. |
