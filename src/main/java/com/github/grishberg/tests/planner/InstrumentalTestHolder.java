package com.github.grishberg.tests.planner;

import java.util.Iterator;
import java.util.List;

public interface InstrumentalTestHolder {
    /**
     * @return iterator with all test methods in project.
     */
    Iterator<TestPlanElement> provideTestNodeElementsIterator();

    List<TestPlanElement> provideCompoundTestPlan();
}
