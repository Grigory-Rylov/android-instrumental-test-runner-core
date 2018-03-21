package com.github.grishberg.tests.planner;

import com.github.grishberg.tests.planner.parser.TestPlanElement;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Generates tree of classes and packages.
 */
public class PackageTreeGenerator {
    /**
     * @param planList list of TestPlanElement generated from am instrument log.
     * @return root element of test classes tree.
     */
    List<TestNodeElement> makePackageTree(List<TestPlanElement> planList) {
        HashMap<String, TestNodeElement> nodes = new HashMap<>();
        ArrayList<TestNodeElement> roots = new ArrayList<>();

        for (TestPlanElement currentTestPlan : planList) {

            String methodName = currentTestPlan.getMethodName();
            String[] leftPart = currentTestPlan.getClassName().split("\\.");

            // process packages and Class name
            TestNodeElement parent = parsePackageElement(nodes, roots, leftPart);

            // process method name
            TestNodeElement methodNodeElement = new TestNodeElement(NodeType.METHOD, methodName);
            methodNodeElement.addAnnotations(currentTestPlan.getAnnotations());
            methodNodeElement.setTestPlan(currentTestPlan);
            if (parent != null) {
                parent.addChild(methodNodeElement);
            }
        }

        return roots;
    }

    @Nullable
    private TestNodeElement parsePackageElement(HashMap<String, TestNodeElement> nodes, ArrayList<TestNodeElement> roots, String[] leftPart) {
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
                    roots.add(currentElement);
                }
                nodes.put(sbPath.toString(), currentElement);
            }
            parent = currentElement;
        }
        return parent;
    }
}
