package com.github.grishberg.tests.planner.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Test plan for running single test.
 */
public class TestPlanElement {
    private final String testId;
    private final String methodName;
    private final String className;
    private List<String> annotations;
    private String feature;
    private String flags[] = new String[0];

    public TestPlanElement(String testId, String methodName, String fullClassName) {
        this.testId = testId;
        this.methodName = methodName;
        this.className = fullClassName;
        this.annotations = new ArrayList<>();
    }

    public void addAnnotations(String[] annotations) {
        if (annotations == null) {
            return;
        }
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

        TestPlanElement that = (TestPlanElement) o;

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

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public boolean isClass() {
        return methodName == null || methodName.length() == 0;
    }

    public boolean isPackage() {
        return false;
    }

    /**
     * @return command for am instrument parameter class or package
     */
    public String getAmInstrumentCommand() {
        if (isClass()) {
            return className;
        }
        return className + "#" + methodName;
    }

    public String[] getFlags() {
        return flags.clone();
    }

    public void setFlags(String[] flags) {
        if (flags == null) {
            this.flags = new String[0];
            return;
        }
        this.flags = flags.clone();
    }

    @Override
    public String toString() {
        return "TestPlanElement{" +
                "methodName='" + methodName + '\'' +
                ", className='" + className + '\'' +
                ", testId='" + testId + '\'' +
                '}';
    }
}
