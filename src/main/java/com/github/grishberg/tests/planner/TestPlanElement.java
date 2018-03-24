package com.github.grishberg.tests.planner;

import java.util.ArrayList;
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
    private List<String> flags = new ArrayList<>();

    public TestPlanElement(String testId, String methodName, String fullClassName) {
        this.testId = testId;
        this.methodName = methodName;
        this.className = fullClassName;
        this.annotations = new ArrayList<>();
    }

    public void addAnnotations(List<String> annotations) {
        if (annotations == null) {
            return;
        }
        this.annotations.addAll(annotations);
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

    void setFeature(String feature) {
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

    public List<String> getFlags() {
        return new ArrayList<>(flags);
    }

    void setFlags(List<String> flags) {
        if (flags == null) {
            this.flags.clear();
            return;
        }
        this.flags = new ArrayList<>(flags);
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
