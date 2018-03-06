package com.github.grishberg.tests.planner;

import com.github.grishberg.tests.planner.parser.TestPlan;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by grishberg on 06.03.18.
 */
@RunWith(JUnit4.class)
public class PackageTreeGeneratorTest {
    @Test
    public void testTreeGenerating() {
        Set<TestPlan> planList = getTestPlans();
        PackageTreeGenerator generator = new PackageTreeGenerator();
        TestNodeElement root = generator.makePackageTree(planList);
        assertNotNull(root.getAnnotations());
        assertEquals("com", root.getName());
        assertEquals(1, root.getChildren().size());
        assertEquals("test", root.getChildren().get(0).getName());
        assertEquals(2, root.getChildren().get(0).getChildren().size());
        assertEquals(4, root.getChildren().get(0).getChildren().get(0).getChildren().size());
        assertEquals(4, root.getChildren().get(0).getChildren().get(1).getChildren().size());
    }

    @NotNull
    private HashSet<TestPlan> getTestPlans() {
        HashSet<TestPlan> testPlans = new HashSet<>();
        testPlans.add(new TestPlan("ID", "test11", "com.test.TestClass1"));
        testPlans.add(new TestPlan("ID", "test12", "com.test.TestClass1"));
        testPlans.add(new TestPlan("ID", "test13", "com.test.TestClass1"));
        testPlans.add(new TestPlan("ID", "test14", "com.test.TestClass1"));
        testPlans.add(new TestPlan("ID", "test21", "com.test.TestClass2"));
        testPlans.add(new TestPlan("ID", "test22", "com.test.TestClass2"));
        testPlans.add(new TestPlan("ID", "test23", "com.test.TestClass2"));
        testPlans.add(new TestPlan("ID", "test24", "com.test.TestClass2"));

        return testPlans;
    }
}