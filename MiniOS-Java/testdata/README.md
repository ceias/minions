# Reference-output provenance

`cases.txt` originally captured the prior Python simulator's outputs. It is a compatibility reference, not an independent correctness oracle.

The six `disk: edge <algorithm> Down` cases use head0, requests0/99/50 and disk size100. Their comparison tables were corrected for the equal-head bug: cylinder0 is served immediately, before moving down/wrapping. C-SCAN and C-LOOK therefore travel0→99→50, totaling99+49=148, instead of returning to0 for198. The selected C-SCAN/C-LOOK path and total were corrected as well; repeated boundary/request waypoints in the printed SCAN paths have zero movement.

`AuditRegressionTests` independently checks these totals, checks LOOK with head50/requests50,40 produces movement10, and verifies duplicates at the head in both directions. Expected values were calculated manually; the fixture was not regenerated wholesale from implementation output.
