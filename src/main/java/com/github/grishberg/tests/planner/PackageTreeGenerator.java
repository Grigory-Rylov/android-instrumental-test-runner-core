package com.github.grishberg.tests.planner;

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
    List<TestPlanElement> makePackageTree(List<TestPlanElement> planList) {
        HashMap<String, TestPlanElement> nodes = new HashMap<>();
        ArrayList<TestPlanElement> roots = new ArrayList<>();

        for (TestPlanElement currentTestPlan : planList) {

            String[] leftPart = currentTestPlan.getClassName().split("\\.");

            // process packages and Class name
            TestPlanElement parent = parsePackageElement(nodes, roots, leftPart);

            // process method name
            if (parent != null) {
                parent.addChild(currentTestPlan);
            }
        }

        return roots;
    }

    @Nullable
    private TestPlanElement parsePackageElement(HashMap<String, TestPlanElement> nodes,
                                                ArrayList<TestPlanElement> roots, String[] leftPart) {
        StringBuilder sbPath = new StringBuilder();
        TestPlanElement parent = null;
        for (int pos = 0; pos < leftPart.length; pos++) {
            if (sbPath.length() > 0) {
                sbPath.append(".");
            }
            String pathElement = leftPart[pos];
            sbPath.append(pathElement);
            TestPlanElement currentElement = nodes.get(sbPath.toString());
            NodeType nodeType = pos < leftPart.length - 1 ? NodeType.PACKAGE : NodeType.CLASS;
            if (currentElement == null) {
                currentElement = new TestPlanElement(nodeType, pathElement);
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
