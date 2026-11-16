/** In-app guide derived from the supplied OS rubric; scores are instructor-assessed. */
final class RubricGuide {
    static final String TEXT =
        "OPERATING SYSTEM SIMULATOR - RUBRIC GUIDE\n\n" +
        "Implementation: 40 points\n" +
        "CPU Scheduling (6): FCFS, SJF, SRTF, Priority, Preemptive Priority, RR, MLQ, MLFQ.\n" +
        "  Enter PID arrival burst [priority] [queue]. Compare waiting and turnaround.\n" +
        "Banker's Algorithm (5): Available line, then PID allocation... maximum...\n" +
        "  Resource count is inferred from Available; process count from the rows.\n" +
        "  Need = Max - Allocation. Safe means a completion sequence exists.\n" +
        "Paging (4): page/frame table plus page size and logical addresses.\n" +
        "Page Replacement (6): FIFO, LRU, Optimal and six additional algorithms.\n" +
        "  Enter any length reference string and a positive frame count.\n" +
        "Segmentation (4): segment/base/limit table and segment:offset addresses.\n" +
        "Fragmentation (4): variable lists of fixed blocks and processes; First/Best/Worst Fit.\n" +
        "Disk Scheduling (6): FCFS, SSTF, SCAN, C-SCAN, LOOK, C-LOOK.\n" +
        "Multiprogramming (5): PID memory burst arrival; admission, RR and utilization.\n\n" +
        "Other assessment areas\n" +
        "GUI/usability (10): navigation, readable output, validation, flexible input.\n" +
        "Accuracy/testing (15): run Tests and RubricTests; save evidence.\n" +
        "Source/submission (10): submit sources, MiniOS.jar, setup guide and test evidence.\n" +
        "Presentation (15): explain and demonstrate within ten minutes.\n" +
        "OS understanding (10): explain calculations and justify algorithm differences.\n\n" +
        "DEMO PLAN (10 minutes)\n" +
        "0-1: Purpose and language. 1-3: GUI, inputs and navigation.\n" +
        "3-7: Demonstrate all eight required modules with Small/Edge/Large samples.\n" +
        "7-9: Explain calculations, compare algorithms and discuss one challenge.\n" +
        "9-10: Questions and conclusion.\n\n" +
        "No fixed item-count cap is imposed. Numeric ranges and available memory/CPU\n" +
        "still apply. Large simulations can take time and produce wide output tables.\n" +
        "Confirm the algorithms covered in your class with your instructor.\n" +
        "Read RUBRIC-COVERAGE.md and ANTIGRAVITY-SETUP.md in the project folder.\n";
    private RubricGuide() {}
}
