package com.github.grishberg.tests.planner;

import java.util.ArrayList;
import java.util.List;

/**
 * Split list of TestPlanElement items
 */
public class TestPlanSplitter {
    private static final int STRING_LIMIT = 3500;

    private TestPlanSplitter() {/* not used */}

    public static List<List<TestPlanElement>> splitByArgumentLimit(List<TestPlanElement> src) {
        ArrayList<List<TestPlanElement>> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        ArrayList<TestPlanElement> currentRange = new ArrayList<>();

        for (TestPlanElement testPlan : src) {
            String testName = testPlan.getAmInstrumentCommand();
            if (sb.length() + testName.length() + 1 > STRING_LIMIT) {
                // add current range to result
                result.add(currentRange);

                sb = new StringBuilder();
                currentRange = new ArrayList<>();
            }

            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(testPlan.getAmInstrumentCommand());
            currentRange.add(testPlan);
        }

        if (!currentRange.isEmpty()) {
            result.add(currentRange);
        }
        return result;
    }
}
