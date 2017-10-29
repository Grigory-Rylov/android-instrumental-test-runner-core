package com.grishberg.tests.planner.parser;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by grishberg on 29.10.17.
 */
public class TestInstanceTest {
    @Test
    public void testItemsAreEquals() {
        TestPlan item1 = new TestPlan("id1", "test1", "Class1");
        TestPlan item2 = new TestPlan("id1", "test1", "Class1");
        Assert.assertEquals(item1, item2);
    }

    @Test
    public void testItemsAreNotEquals() {
        TestPlan item1 = new TestPlan("id1", "test1", "Class1");
        TestPlan item2 = new TestPlan("id1", "test1", "Class2");
        Assert.assertNotEquals(item1, item2);
    }
}