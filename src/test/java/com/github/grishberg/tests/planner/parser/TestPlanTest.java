package com.github.grishberg.tests.planner.parser;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Created by grishberg on 27.02.18.
 */
@RunWith(JUnit4.class)
public class TestPlanTest {
    private static final String[] NOT_EMPTY_ANNOTATIONS = {"Annotation1", "Annotation2"};

    @Test
    public void whenPutNullAnnotation_annotationsCleared() {
        TestPlan plan = new TestPlan("", "", "");
        plan.addAnnotations(NOT_EMPTY_ANNOTATIONS);
        Assert.assertNotNull(plan.getAnnotations());
    }
}
