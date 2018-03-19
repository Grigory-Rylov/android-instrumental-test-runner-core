package com.github.grishberg.tests.planner;

import com.github.grishberg.tests.planner.parser.TestPlanElement;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by grishberg on 18.03.18.
 */
@RunWith(JUnit4.class)
public class TestPlanSplitterTest {
    private static final String LONG_PKG = "com.some_very_very_very_very_very_very_very_very_very_long_package_for_test.";

    @Test
    public void testSplit() {
        List<TestPlanElement> list = provideTestPlanElements();
        List<TestPlanElement[]> res = TestPlanSplitter.splitByArgumentLimit(list);
        Assert.assertTrue(res.size() == 1);
    }

    @NotNull
    private ArrayList<TestPlanElement> provideTestPlanElements() {
        ArrayList<TestPlanElement> list = new ArrayList<>();
        list.add(new TestPlanElement("", "test1", LONG_PKG + "pkg1.Test1"));
        list.add(new TestPlanElement("", "test2", LONG_PKG + "pkg1.Test1"));
        list.add(new TestPlanElement("", "test3", LONG_PKG + "pkg1.Test1"));

        list.add(new TestPlanElement("", "test4", LONG_PKG + "pkg1.Test2"));
        list.add(new TestPlanElement("", "test5", LONG_PKG + "pkg1.Test2"));

        list.add(new TestPlanElement("", "test6", LONG_PKG + "pkg2.Test3"));
        list.add(new TestPlanElement("", "test7", LONG_PKG + "pkg2.Test3"));
        return list;
    }
}