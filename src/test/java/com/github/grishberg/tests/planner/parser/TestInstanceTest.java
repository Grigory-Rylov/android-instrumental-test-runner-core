package com.github.grishberg.tests.planner.parser;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by grishberg on 29.10.17.
 */
public class TestInstanceTest {
    @Test
    public void testItemsAreEquals() {
        TestPlanElement item1 = new TestPlanElement("id1", "test1", "Class1");
        TestPlanElement item2 = new TestPlanElement("id1", "test1", "Class1");
        Assert.assertEquals(item1, item2);
    }

    @Test
    public void testItemsAreNotEquals() {
        TestPlanElement item1 = new TestPlanElement("id1", "test1", "Class1");
        TestPlanElement item2 = new TestPlanElement("id1", "test1", "Class2");
        Assert.assertNotEquals(item1, item2);
    }
}