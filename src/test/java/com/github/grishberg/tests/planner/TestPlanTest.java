package com.github.grishberg.tests.planner;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;

/**
 * Created by grishberg on 27.02.18.
 */
@RunWith(JUnit4.class)
public class TestPlanTest {
    private static final String[] NOT_EMPTY_ANNOTATIONS = {"Annotation1", "Annotation2"};

    @Test
    public void whenPutNullAnnotation_annotationsCleared() {
        TestPlanElement plan = new TestPlanElement("", "", "");
        plan.addAnnotations(Arrays.asList(NOT_EMPTY_ANNOTATIONS));
        Assert.assertNotNull(plan.getAnnotations());
    }
}
