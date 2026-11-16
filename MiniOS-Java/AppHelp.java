/** User-facing operating instructions shared by the Swing and JavaFX interfaces. */
final class AppHelp {
    static final String TEXT =
        "MINI OS - HOW TO USE\n\n" +
        "Choose a module or algorithm. Edit its input and settings, then Run.\n" +
        "Small, Large and Edge load editable examples. Copy or Save preserves results.\n\n" +
        "CPU Scheduling: one process per line: PID arrival burst [priority] [queue].\n" +
        "  Compare scheduling order, waiting time, turnaround and response time.\n" +
        "Banker's Algorithm: enter Available, then PID allocation... maximum...\n" +
        "  Resource count follows Available; each process needs matching vectors.\n" +
        "  An optional request tests whether granting it preserves safety.\n" +
        "Deadlock Detection: Available, then PID allocation... request...\n" +
        "Paging: enter page/frame pairs, page size and logical addresses.\n" +
        "Page Replacement: enter page references, frame count and an algorithm.\n" +
        "Segmentation: enter segment/base/limit rows and segment:offset addresses.\n" +
        "Fragmentation: enter block sizes on line 1 and process sizes on line 2.\n" +
        "Disk Scheduling: enter requests, head position, disk size and direction.\n" +
        "Multiprogramming: one job per line: PID memory burst arrival.\n\n" +
        "Blank or inconsistent input produces an error message; correct it and retry.\n" +
        "Inputs support variable numbers of entries. Very large simulations use more\n" +
        "memory and computation. For each module, read the input hint for its format.\n";
    private AppHelp() {}
}
