package com.github.grishberg.tests.planner;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import static com.github.grishberg.tests.planner.PlannerCommon.*;

/**
 * Created by grishberg on 18.03.18.
 */
@RunWith(JUnit4.class)
public class PackageTreeGeneratorTest {

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
}