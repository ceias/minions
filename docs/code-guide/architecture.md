# Architecture and Java glossary

[Back to guide](README.md)

## Where execution starts

The JavaFX command `mvnw.cmd javafx:run` reads `MiniOS-Java/javafx/pom.xml`, compiles the shared Java engines and JavaFX class, and launches `MiniOSFX.main`. `launch(args)` starts the JavaFX framework, which calls `start(Stage)`. The Stage is the window; its Scene holds the node tree.

The older `java -jar MiniOS.jar` instead starts `MiniOS.main`, which creates a Swing JFrame on Swing's Event Dispatch Thread. These are two interfaces sharing the same algorithms, not JavaFX running inside Swing.

## The path from input to output

```text
Button click or typed command
  → MiniOSFX.open / route
  → an Editor built from Modules.Module metadata
  → Editor.options + Util.lineList snapshot the current controls
  → Editor.run creates a background Task
  → module.run.run(lines, opts)
  → Cpu / Banker / Deadlock / Pages / Memory / Disk / Multi
  → a calculated text report or validation exception
  → JavaFX success/failure handler
  → showResult or a helpful error bubble
```

`module.run.run` looks unusual because the first `run` is the Module field containing a Runner object, while the second `run(...)` is the method invoked on that object. Modules.java supplies each callback. It is not running the calculation twice.

An Editor caches typed input separately from the visible history. Switching modules removes/reattaches cards, not their saved Editor objects. Clear removes messages and cards but leaves the editor map intact. Each run snapshots input/options before submitting work, so later typing does not rewrite the already-submitted calculation.

The background executor has one daemon thread. `Task.call()` performs the calculation there. JavaFX dispatches `setOnSucceeded` and `setOnFailed` handlers back onto its application thread; these are the places that update nodes. `busy` prevents a second simulation from being queued while the current one runs.

## MLQ/MLFQ visibility

`advancedCpu` starts false for every application launch. The Scene accelerator flips it when Ctrl+Shift+M is invoked (Command+Shift+M on macOS). Button `visible` bindings hide drawing; `managed` bindings remove the buttons from layout. A listener updates the CPU dropdown too. MLFQ quantum controls and CPU hints follow the same state. Hiding a selected multilevel algorithm falls back to FCFS without discarding input. Hidden quantum values receive valid defaults in the options snapshot so an irrelevant blank field cannot break a basic algorithm.

This is UI visibility, not removal of the algorithms. Cpu.java still implements both. The Swing UI does not use this JavaFX-only shortcut.

## Java terms used throughout

| Syntax | Meaning in this project |
|---|---|
| `class` | Defines a type with state and methods, such as Cpu or Editor. |
| `public` | Accessible outside its package/type where Java visibility rules permit. |
| `private` | Only accessible inside the declaring class/nested-type access context. |
| No access keyword | Package-private. These project classes use the unnamed/default package. |
| `static` | Belongs to the class, so callers use `Cpu.parse(...)` without constructing Cpu. |
| `final` variable | Cannot be reassigned; a final list reference can still refer to a list whose contents change. |
| `final class` | Cannot be subclassed. |
| Constructor | Same name as its class and no return type; initializes a new object. |
| `this.x = x` | Copy parameter x into this object's field x. |
| `List<T>` | Ordered collection of T; its size can grow with input. |
| `Map<K,V>` | Associates a key with a value, e.g. field label with a UI control. |
| `Set<T>` | Tracks unique values, e.g. duplicate PIDs or a working set of pages. |
| `int[]` / `int[][]` | Array/vector / array of rows used as a matrix. |
| `String...` | Varargs: accepts multiple string arguments, exposed to the method as an array. |
| `null` | No referenced object/value. Some methods deliberately return null for no safe sequence. |
| `new` | Constructs an object or allocates an array. |
| `=` / `==` / `.equals` | Assignment / primitive equality or reference identity / value equality for strings. |
| `&&` / `\|\|` / `!` | Logical AND / OR / NOT with short-circuit evaluation for the first two. |
| `condition ? a : b` | Choose one expression based on the condition. |
| `i++`, `x += y` | Increment / add and assign. |
| `for`, `while` | Repeat a block over items or while a condition holds. |
| `continue`, `break`, `return` | Next loop iteration / exit loop or switch / exit method. |
| `try/catch/finally` | Perform work, handle exceptions, and always perform cleanup. |
| `IllegalArgumentException` | Signals invalid user-provided data; UI handlers display its message. |
| `e -> ...` | Lambda callback; often executes later when the event happens. |
| `this::run` | Method reference passed as a callback; no immediate call at registration. |
| `@Override` | Compiler checks that the method overrides a parent/framework contract. |
| `@Test` | JUnit marks a method as a test. |
| `clone()` | Copies an array; matrices require copying each row to avoid shared inner arrays. |
| `StringBuilder` | Efficiently appends many pieces into a report string. |
| `\n` | Newline inside a Java string; not a separate physical source line. |
| `long`, `double` | Wide whole-number totals; floating-point ratios/percentages. |

## Important variable meanings

- CPU: `a` arrival, `b` burst, `p` priority, `q` MLQ queue; `rem` remaining burst; `ct` completion; `first` first dispatch; `tl` per-tick process timeline; `sl` new-segment flags.
- Banker: `A` Allocation, `N` Need, `avail` Available; `work` resources hypothetically available after completed processes release Allocation.
- Replacement: `F` frames; `fr` contents; `R` referenced, `M` modified; `ld` loaded-at, `lu` last-used-at; `cnt` NFU counter; `hand` CLOCK pointer.
- Paging: `ps` page size, `p` page, `off` offset, `pt` page table.
- Segmentation: each stored pair is `[base, limit]`; valid offsets satisfy `0 <= offset < limit`.
- Disk: `lo` requests below head in descending order; `hi` above head ascending; `loR`/`hiR` reverse copies.
- Multiprogramming: `wait` not yet admitted, `ready` resident RR queue; `mem` occupied capacity; `busy` executed duration; `area` memory×time integral.

Names are local: `p` can mean a CPU process index, page number or process PID depending on its class/method. Read each declaration rather than assuming the same single-letter name always has the same meaning.
