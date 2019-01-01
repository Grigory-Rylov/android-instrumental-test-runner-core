package com.github.grishberg.tests.planner;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by grishberg on 30.03.18.
 */
public class TestPlanElementTest {
    private static final String TEST_METHOD_NAME = "testMethodName";

    @Test
    public void classTypeWhenMethodIsEmpty() {
        TestPlanElement element = new TestPlanElement("", "", "com.test.TestClass");
        Assert.assertEquals(NodeType.CLASS, element.getType());
    }

    @Test
    public void methodTypeWhenMethodIsEmpty() {
        TestPlanElement element = new TestPlanElement("", TEST_METHOD_NAME, "com.test.TestClass");
        Assert.assertEquals(NodeType.METHOD, element.getType());
    }

    @Test
    public void addNullAnnotations() {
        TestPlanElement element = new TestPlanElement("", TEST_METHOD_NAME, "com.test.TestClass");

        element.addAnnotations(null);

        List<String> annotations = element.getAnnotations();
        Assert.assertNotNull(annotations);
        Assert.assertEquals(0, annotations.size());
    }

    @Test
    public void returnMethodNameAfterInit() {
        TestPlanElement element = new TestPlanElement("", TEST_METHOD_NAME, "com.test.TestClass");
        Assert.assertEquals(TEST_METHOD_NAME, element.getMethodName());
    }

    @Test
    public void testEquals() {
        TestPlanElement element1 = new TestPlanElement("id1", "test1", "com.test.TestClass1");
        TestPlanElement element2 = new TestPlanElement("id1", "test2", "com.test.TestClass1");
        Assert.assertFalse(element1.equals(element2));
        Assert.assertFalse(element1.equals(null));
        Assert.assertFalse(new TestPlanElement("id1", "test1", "com.test.TestClass1")
                .equals(new TestPlanElement("id2", "test1", "com.test.TestClass1")));
        Assert.assertFalse(new TestPlanElement("id1", "test1", "com.test.TestClass1")
                .equals(new TestPlanElement("id2", "test1", "com.test.TestClass1")));
    }

    @Test
    public void testCopyConstructor() {
        TestPlanElement element1 = new TestPlanElement("id1", "test1", "com.test.TestClass1");
        TestPlanElement element2 = new TestPlanElement(element1);
        Assert.assertTrue(element1.equals(element2));
    }
}