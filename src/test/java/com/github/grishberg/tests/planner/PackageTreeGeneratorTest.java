package com.github.grishberg.tests.planner;

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
public class PackageTreeGeneratorTest {
    private static final String TEST_NAME_1 = "com.pkg1.Test1";
    private static final String TEST_NAME_2 = "com.pkg1.Test2";
    private static final String TEST_NAME_3 = "com.pkg2.Test3";
    private static final String TEST__METHOD_NAME_3 = "test3";
    private PackageTreeGenerator generator = new PackageTreeGenerator();

    @Test
    public void makeCompoundClasses() throws Exception {
        ArrayList<TestPlanElement> list = provideTestPlanElements();

        List<TestPlanElement> result = generator.makePackageTree(list);
        TestPlanElement root = result.get(0);
        List<TestPlanElement> compoundElements = root.getCompoundElements();
        Assert.assertTrue(compoundElements.size() == 3);
        TestPlanElement testPlanElement1 = compoundElements.get(0);
        TestPlanElement testPlanElement2 = compoundElements.get(1);
        TestPlanElement testPlanElement3 = compoundElements.get(2);
        Assert.assertEquals(TEST_NAME_1, testPlanElement1.getAmInstrumentCommand());
        Assert.assertEquals(TEST_NAME_2, testPlanElement2.getAmInstrumentCommand());
        Assert.assertEquals(TEST_NAME_3, testPlanElement3.getAmInstrumentCommand());
    }

    @Test
    public void makeCompoundClassesAndMethods() throws Exception {
        ArrayList<TestPlanElement> list = provideTestPlanElements();
        List<TestPlanElement> result = generator.makePackageTree(list);
        TestPlanElement root = result.get(0);

        List<TestPlanElement> testMethods = root.getAllTestMethods();
        testMethods.get(0).exclude();

        List<TestPlanElement> compoundElements = root.getCompoundElements();
        Assert.assertTrue(compoundElements.size() == 4);
        TestPlanElement testPlanElement1 = compoundElements.get(1);
        TestPlanElement testPlanElement2 = compoundElements.get(2);
        TestPlanElement testPlanElement3 = compoundElements.get(3);
        Assert.assertEquals(TEST_NAME_1 + "#" + TEST__METHOD_NAME_3, testPlanElement1.getAmInstrumentCommand());
        Assert.assertEquals(TEST_NAME_2, testPlanElement2.getAmInstrumentCommand());
        Assert.assertEquals(TEST_NAME_3, testPlanElement3.getAmInstrumentCommand());
    }

    @NotNull
    private ArrayList<TestPlanElement> provideTestPlanElements() {
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