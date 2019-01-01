package com.github.grishberg.tests.planner;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.github.grishberg.tests.planner.PlannerCommon.provideTestPlanElements;

/**
 * Created by grishberg on 27.03.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class InstrumentalTestHolderTest {
    private InstrumentalTestHolderImpl holder;
    private PackageTreeGenerator generator = new PackageTreeGenerator();
    private ArrayList<TestPlanElement> list = provideTestPlanElements();

    @Before
    public void setUp() throws Exception {

        holder = new InstrumentalTestHolderImpl(list, generator);
    }

    @Test
    public void provideTestNodeElementsIterator() throws Exception {
        Iterator<TestPlanElement> iterator = holder.provideTestNodeElementsIterator();
        for (TestPlanElement element : list) {
            Assert.assertTrue(iterator.hasNext());
            TestPlanElement iteratorElement = iterator.next();
            Assert.assertEquals(element, iteratorElement);
        }
    }

    @Test
    public void provideCompoundTestPlan() throws Exception {
        List<TestPlanElement> elements = holder.provideCompoundTestPlan();
        Assert.assertEquals(3, elements.size());
    }
}