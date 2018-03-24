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
    private PackageTreeGenerator generator = new PackageTreeGenerator();

    @Test
    public void makeCompoundClasses() throws Exception {
        ArrayList<TestPlanElement> list = provideTestPlanElements();

        List<TestNodeElement> result = generator.makePackageTree(list);
        TestNodeElement root = result.get(0);
        List<TestNodeElement> compoundElements = root.getCompoundElements();
        Assert.assertTrue(compoundElements.size() == 3);
    }

    @Test
    public void makeCompoundClassesAndMethods() throws Exception {
        ArrayList<TestPlanElement> list = provideTestPlanElements();
        List<TestNodeElement> result = generator.makePackageTree(list);
        TestNodeElement root = result.get(0);

        List<TestNodeElement> testMethods = root.getAllTestMethods();
        testMethods.get(0).exclude();

        List<TestNodeElement> compoundElements = root.getCompoundElements();
        Assert.assertTrue(compoundElements.size() == 4);
    }

    @NotNull
    private ArrayList<TestPlanElement> provideTestPlanElements() {
        ArrayList<TestPlanElement> list = new ArrayList<>();
        list.add(new TestPlanElement("", "test1", "com.pkg1.Test1"));
        list.add(new TestPlanElement("", "test2", "com.pkg1.Test1"));
        list.add(new TestPlanElement("", "test3", "com.pkg1.Test1"));

        list.add(new TestPlanElement("", "test4", "com.pkg1.Test2"));
        list.add(new TestPlanElement("", "test5", "com.pkg1.Test2"));

        list.add(new TestPlanElement("", "test6", "com.pkg2.Test3"));
        list.add(new TestPlanElement("", "test7", "com.pkg2.Test3"));
        return list;
    }
}