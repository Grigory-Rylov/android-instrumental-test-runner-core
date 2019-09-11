package com.github.grishberg.tests.planner;

import com.github.grishberg.tests.utils.TextUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Test plan for running single test.
 */
public class TestPlanElement {
    private final String testId;
    private final String methodName;
    private final String className;
    private final List<AnnotationInfo> annotations;
    @Nullable
    private TestPlanElement parent;
    private final NodeType type;
    private final ArrayList<TestPlanElement> children = new ArrayList<>();
    private boolean excluded;
    private boolean hasExcluded;

    public TestPlanElement(String testId, String methodName, String fullClassName,
                           List<AnnotationInfo> annotations) {
        this.testId = testId;
        this.methodName = methodName;
        this.className = fullClassName;
        this.annotations = annotations;
        type = TextUtils.isEmpty(methodName) ? NodeType.CLASS : NodeType.METHOD;
        if (annotations == null) {
            throw new IllegalArgumentException("Passed null annotations list");
        }
    }

    public TestPlanElement(NodeType type, String packageName) {
        this.type = type;
        testId = "";
        methodName = "";
        className = packageName;
        annotations = Collections.emptyList();
    }

    public TestPlanElement(String testId, String methodName, String fullClassName) {
        this(testId, methodName, fullClassName, Collections.emptyList());
    }

    /**
     * Clone instance of srcElement.
     */
    public TestPlanElement(TestPlanElement srcElement) {
        this.testId = srcElement.testId;
        this.methodName = srcElement.methodName;
        this.className = srcElement.className;
        this.annotations = new ArrayList<>(srcElement.annotations);
        if (srcElement.parent != null) {
            this.parent = new TestPlanElement(srcElement.parent);
        }
        this.type = srcElement.type;
        this.excluded = false;
        this.hasExcluded = false;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getClassName() {
        return className;
    }

    public List<AnnotationInfo> getAnnotations() {
        return Collections.unmodifiableList(annotations);
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

    public boolean isPackage() {
        return type == NodeType.PACKAGE;
    }

    /**
     * @return command for am instrument parameter class or package
     */
    public String getAmInstrumentCommand() {
        if (type == NodeType.METHOD) {
            return className + "#" + methodName;
        }
        String prefix = "";
        if (parent != null) {
            prefix = parent.getAmInstrumentCommand() + ".";
        }
        return prefix + className;
    }

    public void exclude() {
        excluded = true;
        if (parent != null) {
            parent.setHasExcluded(true);
        }
    }

    private void setHasExcluded(boolean hasExcluded) {
        this.hasExcluded = hasExcluded;
        if (parent != null) {
            parent.setHasExcluded(hasExcluded);
        }
    }

    void addChild(TestPlanElement child) {
        children.add(child);
        child.parent = this;
    }

    public List<TestPlanElement> getAllTestMethods() {
        if (type == NodeType.CLASS) {
            return children;
        }
        ArrayList<TestPlanElement> methods = new ArrayList<>();
        for (TestPlanElement element : children) {
            methods.addAll(element.getAllTestMethods());
        }
        return methods;
    }

    public NodeType getType() {
        return type;
    }

    /**
     * @return not excluded packages list.
     */
    public List<TestPlanElement> getCompoundElements() {
        ArrayList<TestPlanElement> result = new ArrayList<>();
        if (!hasExcluded && !excluded && type != NodeType.PACKAGE) {
            result.add(this);
            return result;
        }

        for (TestPlanElement child : children) {
            result.addAll(child.getCompoundElements());
        }
        return result;
    }

    @Override
    public String toString() {
        return "TestPlanElement{" +
                "methodName='" + methodName + '\'' +
                ", className='" + className + '\'' +
                ", type=" + type +
                ", annotations=[" + annotationsAsString() + "]" +
                '}';
    }

    private String annotationsAsString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < annotations.size(); i++) {
            AnnotationInfo annotation = annotations.get(i);
            sb.append(annotation.getName());
            if (i < annotations.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}
