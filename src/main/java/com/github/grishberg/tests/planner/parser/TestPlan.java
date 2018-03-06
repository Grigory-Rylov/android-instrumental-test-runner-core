package com.github.grishberg.tests.planner.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Test plan for running single test.
 */
public class TestPlan {
    private final String testId;
    private final String methodName;
    private final String className;
    private List<String> annotations;
    private String featureParameter;

    public TestPlan(String testId, String methodName, String fullClassName) {
        this.testId = testId;
        this.methodName = methodName;
        this.className = fullClassName;
        this.annotations = new ArrayList<>();
    }

    public void addAnnotations(String[] annotations) {
        this.annotations.addAll(Arrays.asList(annotations));
    }

    public String getMethodName() {
        return methodName;
    }

    public String getClassName() {
        return className;
    }

    public List<String> getAnnotations() {
        return new ArrayList<>(annotations);
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
        if (methodName != null ? !methodName.equals(that.methodName) : that.methodName != null) {
            return false;
        }
        return className != null ? className.equals(that.className) : that.className == null;
    }

    @Override
    public int hashCode() {
        int result = testId.hashCode();
        result = 31 * result + methodName.hashCode();
        result = 31 * result + className.hashCode();
        result = 31 * result + annotations.hashCode();
        return result;
    }

    public String getFeatureParameter() {
        return featureParameter;
    }

    public void setFeatureParameter(String featureParameter) {
        this.featureParameter = featureParameter;
    }

    @Override
    public String toString() {
        return "TestPlan{" +
                "methodName='" + methodName + '\'' +
                ", className='" + className + '\'' +
                ", testId='" + testId + '\'' +
                '}';
    }
}
