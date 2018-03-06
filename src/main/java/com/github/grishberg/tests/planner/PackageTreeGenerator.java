package com.github.grishberg.tests.planner;

import com.github.grishberg.tests.planner.parser.TestPlan;

import java.util.HashMap;
import java.util.Set;

/**
 * Generates tree of classes and packages.
 */
public class PackageTreeGenerator {
    /**
     * @param planList list of TestPlan generated from am instrument log.
     * @return root element of test classes tree.
     */
    public TestNodeElement makePackageTree(Set<TestPlan> planList) {
        HashMap<String, TestNodeElement> nodes = new HashMap<>();
        TestNodeElement root = null;

        for (TestPlan currentTestPlan : planList) {

            String methodName = currentTestPlan.getMethodName();
            String[] leftPart = currentTestPlan.getClassName().split("\\.");

            // process packages and Class name
            StringBuilder sbPath = new StringBuilder();
            TestNodeElement parent = null;
            for (int pos = 0; pos < leftPart.length; pos++) {
                if (sbPath.length() > 0) {
                    sbPath.append(".");
                }
                String pathElement = leftPart[pos];
                sbPath.append(pathElement);
                TestNodeElement currentElement = nodes.get(sbPath.toString());
                NodeType nodeType = pos < leftPart.length - 1 ? NodeType.PACKAGE : NodeType.CLASS;
                if (currentElement == null) {
                    currentElement = new TestNodeElement(nodeType, pathElement);
                    if (parent != null) {
                        parent.addChild(currentElement);
                    } else {
                        root = currentElement;
                    }
                    nodes.put(sbPath.toString(), currentElement);
                }
                parent = currentElement;
            }

            // process method name
            TestNodeElement methodNodeElement = new TestNodeElement(NodeType.METHOD, methodName);
            methodNodeElement.addAnnotations(currentTestPlan.getAnnotations());
            parent.addChild(methodNodeElement);
        }

        return root;
    }
}
