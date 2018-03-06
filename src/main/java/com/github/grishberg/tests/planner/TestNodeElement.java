package com.github.grishberg.tests.planner;

import java.util.ArrayList;
import java.util.List;

/**
 * Element of classes and packages tree.
 */
public class TestNodeElement {
    private String amInstrumentPath;
    private final NodeType type;
    private TestNodeElement parent;
    private final ArrayList<TestNodeElement> children = new ArrayList<>();
    private final String name;
    private boolean excluded;
    private boolean hasExcluded;
    private final ArrayList<String> annotations = new ArrayList<>();

    public TestNodeElement(NodeType type, String name) {
        this.type = type;
        this.name = name;
        amInstrumentPath = name;
    }

    ArrayList<TestNodeElement> getChildren() {
        return children;
    }

    String getName() {
        return name;
    }

    void addChild(TestNodeElement child) {
        children.add(child);
        child.setParent(this);
    }

    public void excluded() {
        this.excluded = true;
        parent.setHasExcluded(true);
    }

    public List<TestNodeElement> getAllTestMethods() {
        if (type == NodeType.CLASS) {
            return getChildren();
        }
        ArrayList<TestNodeElement> methods = new ArrayList<>();
        for (TestNodeElement element : children) {
            methods.addAll(element.getAllTestMethods());
        }
        return methods;
    }

    void addAnnotations(List<String> annotations) {
        annotations.addAll(annotations);
    }

    void setHasExcluded(boolean hasExcluded) {
        this.hasExcluded = hasExcluded;
        if (parent != null) {
            parent.setHasExcluded(hasExcluded);
        }
    }

    TestNodeElement getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return "TestNodeElement{" +
                "type=" + type +
                ", name='" + name + '\'' +
                '}';
    }

    private void setParent(TestNodeElement parent) {
        this.parent = parent;
        if (type == NodeType.METHOD) {
            amInstrumentPath = parent.amInstrumentPath + "#" + amInstrumentPath;
        } else {
            amInstrumentPath = parent.amInstrumentPath + '.' + amInstrumentPath;
        }
    }

    /**
     * @return full path (package/class name/ test name) for am instrument.
     */
    public String getAmInstrumentPath() {
        return amInstrumentPath;
    }

    public ArrayList<String> getAnnotations() {
        return annotations;
    }

    /**
     * @return not excluded packages list.
     */
    public List<TestNodeElement> getCommandsForAmInstrument() {
        ArrayList<TestNodeElement> result = new ArrayList<>();
        if (!hasExcluded) {
            result.add(this);
            return result;
        }

        for (TestNodeElement child : children) {
            result.addAll(child.getCommandsForAmInstrument());
        }
        return result;
    }
}