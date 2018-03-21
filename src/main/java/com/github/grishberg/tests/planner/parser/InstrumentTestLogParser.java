package com.github.grishberg.tests.planner.parser;

import com.android.ddmlib.MultiLineReceiver;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
    private static final String FLAGS = "flags";
    private ParserLogger logger;
    private final ArrayList<TestPlanElement> testPlanList = new ArrayList<>();
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

        if (FLAGS.equals(words[0])) {
            String[] flags = parseFlags(words[1]);
            state.setFlags(flags);
        }

        if (ANNOTATIONS.equals(words[0])) {
            String[] annotations = parseAnnotations(words[1]);
            state.setAnnotations(annotations);
        }
    }

    private String[] parseFlags(String flags) {
        if (flags == null) {
            return new String[]{""};
        }
        if (flags.indexOf(',') < 0) {
            return new String[]{flags};
        }
        return flags.split(",");
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

    public List<TestPlanElement> getTestInstances() {
        return testPlanList;
    }

    private static class State {
        void storeValuesIfNeeded() { /* to be implemented in subclass */ }

        void setAnnotations(String[] annotations) { /* to be implemented in subclass */ }

        void setTestId(String testId) { /* to be implemented in subclass */ }

        void setTestMethod(String testMethod) { /* to be implemented in subclass */ }

        void setClassName(String className) { /* to be implemented in subclass */ }

        void setFeature(String feature) { /* to be implemented in subclass */ }

        void setFlags(String[] flags) { /* to be implemented in subclass */ }
    }

    private class StartNewObject extends State {
        private String testId;
        private String testMethodName;
        private String testClassName;
        private String feature;
        private String[] annotations;
        private String[] flags;

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

        @Override
        void setFlags(String[] flags) {
            this.flags = flags;
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
                if (flags != null) {
                    state.setFlags(flags);
                }
            }
        }
    }

    private class ReadyToStoreObject extends State {
        private final String testId;
        private final String testMethodName;
        private final String testClassName;
        private TestPlanElement testPlan;
        private String[] annotations;
        private String feature;
        private String[] flags;

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
            testPlan = new TestPlanElement(testId, testMethodName, testClassName);

            if (!testPlanList.contains(testPlan)) {
                testPlanList.add(testPlan);

                testPlan.addAnnotations(annotations);
                testPlan.setFeature(feature);
                testPlan.setFlags(flags);
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
                testPlan.setFeature(feature);
                return;
            }
            this.feature = feature;
        }

        @Override
        void setFlags(String[] flags) {
            if (testPlan != null) {
                testPlan.setFlags(flags);
                return;
            }
            this.flags = flags;
        }
    }

    public interface ParserLogger {
        void logLine(String line);
    }
}
