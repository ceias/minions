# Guide verification

[Back to guide](README.md)

- Source snapshot: dba2d321e276383939b0bf661659738741bdd343.
- All 17 Java files are covered: 2,209 physical source lines.
- Including CSS, Maven and launch/build/bootstrap files: 28 walkthroughs and 2,712 lines.
- Every walkthrough row was compared with its source: exact original text, sequential line numbers, nonempty explanation, and SHA-256 recorded in source-manifest.json.
- Local Markdown navigation links were checked for existing targets.
- The self-contained HTML reader was exercised in headless Chromium through a local test server: 28 file choices; 330 initial JavaFX rows; code search; empty search results; file switching; and navigation to CPU line 183 with its matching timing explanation. No JavaScript page errors occurred.
- The reader was visually inspected at a desktop viewport. It includes file-by-file printing; the explanatory Markdown chapters can also be read in VS Code preview.
- File-URL navigation is blocked by this cloud browser's administrator policy, so browser automation used a local HTTP server for testing. The generated HTML has inline data, styling and script, and requires no external service to display its walkthroughs. On a normal local desktop it can be opened directly; GitHub source links require Internet.
- Only documentation and the repository README were changed. No algorithm/GUI/test/build implementation was modified or application test suite rerun for this documentation-only update.
