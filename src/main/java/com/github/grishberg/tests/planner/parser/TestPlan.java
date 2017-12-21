package com.github.grishberg.tests.planner.parser;

import java.util.Arrays;

/**
 * Test plan for running single test.
 */
public class TestPlan {
    private final String testId;
    private final String name;
    private final String className;
    private String[] annotations;

    public TestPlan(String testId, String name, String className) {
        this.testId = testId;
        this.name = name;
        this.className = className;
        this.annotations = new String[0];
    }

    public void setAnnotations(String[] annotations) {
        this.annotations = annotations.clone();
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    public String[] getAnnotations() {
        return annotations.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TestPlan that = (TestPlan) o;

        if (testId != null ? !testId.equals(that.testId) : that.testId != null) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        return className != null ? className.equals(that.className) : that.className == null;
    }

    @Override
    public int hashCode() {
        int result = testId != null ? testId.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (className != null ? className.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(annotations);
        return result;
    }

    @Override
    public String toString() {
        return "TestPlan{" +
                "name='" + name + '\'' +
                ", className='" + className + '\'' +
                ", testId='" + testId + '\'' +
                '}';
    }
}
