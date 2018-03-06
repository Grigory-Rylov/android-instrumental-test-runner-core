package com.github.grishberg.tests.planner.parser;

import com.android.ddmlib.MultiLineReceiver;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Parses am instrument -e log true output and generates.
 */
public class InstrumentTestLogParser extends MultiLineReceiver {
    private static final String INSTRUMENTATION_STATUS = "INSTRUMENTATION_STATUS: ";
    private static final String ID = "id";
    private static final String TEST = "test";
    private static final String CLASS = "class";
    private static final String ANNOTATIONS = "annotations";
    private static final String FEATURE = "feature";
    private ParserLogger logger;
    private final HashSet<TestPlan> testPlanList = new HashSet<>();
    private State state = new StartNewObject();

    public void setLogger(ParserLogger logger) {
        this.logger = logger;
    }

    @Override
    public void processNewLines(String[] lines) {
        for (String word : lines) {
            processLine(word);
        }
        state.storeValuesIfNeeded();
    }

    private void processLine(String word) {
        if (logger != null) {
            logger.logLine(word);
        }

        int startPos = word.indexOf(INSTRUMENTATION_STATUS);
        if (startPos < 0) {
            return;
        }

        String payload = word.substring(startPos + INSTRUMENTATION_STATUS.length());
        String[] words = getSplitArray(payload);

        if (words.length != 2) {
            return;
        }

        if (ID.equals(words[0])) {
            state.storeValuesIfNeeded();
            state.setTestId(words[1]);
            return;
        }

        if (TEST.equals(words[0])) {
            state.setTestMethod(words[1]);
            return;
        }

        if (CLASS.equals(words[0])) {
            state.setClassName(words[1]);
            return;
        }

        if (FEATURE.equals(words[0])) {
            state.setFeature(words[1]);
            return;
        }

        if (ANNOTATIONS.equals(words[0])) {
            String[] annotations = parseAnnotations(words[1]);
            state.setAnnotations(annotations);
        }
    }

    @NotNull
    private String[] getSplitArray(String payload) {
        return payload.split("=", 2);
    }

    private String[] parseAnnotations(String annotations) {
        if (annotations.indexOf(',') < 0) {
            return new String[]{annotations};
        }
        return annotations.split(",");
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    public Set<TestPlan> getTestInstances() {
        return testPlanList;
    }

    private static class State {
        void storeValuesIfNeeded() {
        }

        void setAnnotations(String[] annotations) {
        }

        void setTestId(String testId) {
        }

        void setTestMethod(String testMethod) {
        }

        void setClassName(String className) {
        }

        void setFeature(String feature) {
        }
    }

    private class StartNewObject extends State {
        private String testId;
        private String testMethodName;
        private String testClassName;
        private String feature;
        private String[] annotations;

        StartNewObject() {
        }

        @Override
        void setTestId(String id) {
            testId = id;
        }

        @Override
        void setTestMethod(String testMethod) {
            testMethodName = testMethod;
            changeStateIfNeeded();
        }

        @Override
        void setClassName(String className) {
            testClassName = className;
            changeStateIfNeeded();
        }

        @Override
        void setFeature(String feature) {
            this.feature = feature;
        }

        @Override
        void setAnnotations(String[] annotations) {
            this.annotations = annotations;
        }

        private void changeStateIfNeeded() {
            if (testId != null && testMethodName != null && testClassName != null) {
                state = new ReadyToStoreObject(testId, testMethodName, testClassName);
                if (feature != null) {
                    state.setFeature(feature);
                }
                if (annotations != null) {
                    state.setAnnotations(annotations);
                }
            }
        }
    }

    private class ReadyToStoreObject extends State {
        private final String testId;
        private final String testMethodName;
        private final String testClassName;
        private TestPlan testPlan;
        private String[] annotations;
        private String feature;

        private ReadyToStoreObject(String testId, String testMethodName, String testClassName) {
            this.testId = testId;
            this.testMethodName = testMethodName;
            this.testClassName = testClassName;
        }

        @Override
        void setTestId(String testId) {
            storeValuesIfNeeded();
            state = new StartNewObject();
            state.setTestId(testId);
        }

        @Override
        void storeValuesIfNeeded() {
            testPlan = new TestPlan(testId, testMethodName, testClassName);

            if (!testPlanList.contains(testPlan)) {
                testPlanList.add(testPlan);

                testPlan.addAnnotations(annotations);
                testPlan.setFeatureParameter(feature);
            }
        }

        @Override
        void setAnnotations(String[] annotations) {
            if (testPlan != null) {
                testPlan.addAnnotations(annotations);
                return;
            }
            this.annotations = annotations;
        }

        @Override
        void setFeature(String feature) {
            if (testPlan != null) {
                testPlan.setFeatureParameter(feature);
                return;
            }
            this.feature = feature;
        }
    }

    public interface ParserLogger {
        void logLine(String line);
    }
}
