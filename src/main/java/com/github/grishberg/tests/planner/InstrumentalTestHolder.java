package com.github.grishberg.tests.planner;

import com.github.grishberg.tests.planner.parser.TestPlan;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Holder for instrumental test.
 */
public class InstrumentalTestHolder {
    private List<TestPlan> planList;
    private final PackageTreeGenerator packageTreeGenerator;
    private ArrayList<TestNodeElement> prevRoots = new ArrayList<>();

    InstrumentalTestHolder(List<TestPlan> planList, PackageTreeGenerator packageTreeGenerator) {
        this.planList = planList;

        this.packageTreeGenerator = packageTreeGenerator;
    }

    /**
     * @return iterator with all test methods in project.
     */
    public Iterator<TestNodeElement> provideTestNodeElementsIterator() {
        prevRoots.clear();
        prevRoots.addAll(packageTreeGenerator.makePackageTree(planList));
        return new FlatIterator(prevRoots);
    }

    public List<TestPlan> provideCompoundTestPlan() {
        ArrayList<TestPlan> compoundPlans = new ArrayList<>();
        for (TestNodeElement rootElement : prevRoots) {
            List<TestNodeElement> compoundElements = rootElement.getCompoundElements();
            for (TestNodeElement currentCompoundElement : compoundElements) {
                compoundPlans.add(currentCompoundElement.getTestPlan());
            }
        }
        return compoundPlans;
    }

    /**
     * Returns tree-items in flat list.
     */
    private static class FlatIterator implements Iterator<TestNodeElement> {
        private final Iterator<TestNodeElement> iterator;

        FlatIterator(ArrayList<TestNodeElement> roots) {
            ArrayList<TestNodeElement> flatList = new ArrayList<>();
            for (TestNodeElement rootElement : roots) {
                flatList.addAll(rootElement.getAllTestMethods());
            }
            iterator = flatList.iterator();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public TestNodeElement next() {
            return iterator.next();
        }
    }
}
