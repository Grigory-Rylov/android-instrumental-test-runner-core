package com.github.grishberg.tests.planner;

import com.github.grishberg.tests.planner.parser.TestPlanElement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Split list of TestPlanElement items
 */
public class TestPlanSplitter {
    private static final int STRING_LIMIT = 3500;

    public static List<TestPlanElement[]> splitByArgumentLimit(List<TestPlanElement> src) {
        ArrayList<TestPlanElement[]> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        ArrayList<TestPlanElement> currentRange = new ArrayList<>();

        Iterator<TestPlanElement> iterator = src.iterator();
        while (iterator.hasNext()) {
            TestPlanElement testPlan = iterator.next();

            String testName = testPlan.getAmInstrumentCommand();
            if (sb.length() + testName.length() + 1 > STRING_LIMIT) {
                // add current range to result
                result.add(currentRange.toArray(new TestPlanElement[currentRange.size()]));

                sb = new StringBuilder();
                currentRange = new ArrayList<>();
            }

            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(testPlan.getAmInstrumentCommand());
            currentRange.add(testPlan);
        }

        if (currentRange.size() > 0) {
            result.add(currentRange.toArray(new TestPlanElement[currentRange.size()]));
        }
        return result;
    }
}
