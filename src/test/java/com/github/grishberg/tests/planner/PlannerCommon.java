package com.github.grishberg.tests.planner;

import java.util.ArrayList;

/**
 * Created by grishberg on 27.03.18.
 */
public class PlannerCommon {
    static final String TEST_NAME_1 = "com.pkg1.Test1";
    static final String TEST_NAME_2 = "com.pkg1.Test2";
    static final String TEST_NAME_3 = "com.pkg2.Test3";
    static final String TEST__METHOD_NAME_3 = "test3";

    static ArrayList<TestPlanElement> provideTestPlanElements() {
        ArrayList<TestPlanElement> list = new ArrayList<>();
        list.add(new TestPlanElement("", "test1", TEST_NAME_1));
        list.add(new TestPlanElement("", "test2", TEST_NAME_1));
        list.add(new TestPlanElement("", TEST__METHOD_NAME_3, TEST_NAME_1));

        list.add(new TestPlanElement("", "test4", TEST_NAME_2));
        list.add(new TestPlanElement("", "test5", TEST_NAME_2));

        list.add(new TestPlanElement("", "test6", TEST_NAME_3));
        list.add(new TestPlanElement("", "test7", TEST_NAME_3));
        return list;
    }
}
