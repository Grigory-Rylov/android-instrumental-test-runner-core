package com.github.grishberg.tests.planner;

import com.android.ddmlib.MultiLineReceiver;
import com.github.grishberg.tests.exceptions.ProcessCrashedException;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Parses am instrument -e log true output and generates.
 */
public class InstrumentTestLogParser extends MultiLineReceiver {
    private static final String INSTRUMENTATION_STATUS = "INSTRUMENTATION_STATUS: ";
    private static final String INSTRUMENTATION_RESULT = "INSTRUMENTATION_RESULT: ";
    private static final String INSTRUMENTATION_CODE = "INSTRUMENTATION_CODE: ";
    private static final String ID = "id";
    private static final String TEST = "test";
    private static final String CLASS = "class";
    private static final String ANNOTATIONS = "annotations";
    private static final String FEATURE = "feature";
    private static final String FLAGS = "flags";
    private static final String SHORT_MSG = "shortMsg";
    private static final String LONG_MSG = "longMsg";
    private static final String PROCESS_CRASHED = "Process crashed.";
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
        int startPos = word.indexOf(INSTRUMENTATION_RESULT);
        if (startPos >= 0) {
            parseInstrumentationResult(word.substring(startPos + INSTRUMENTATION_STATUS.length()));
            return;
        }

        startPos = word.indexOf(INSTRUMENTATION_CODE);
        if (startPos >= 0) {
            state.setCode(word.substring(startPos + INSTRUMENTATION_CODE.length()));
            return;
        }

        startPos = word.indexOf(INSTRUMENTATION_STATUS);
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
            List<String> flags = parseFlags(words[1]);
            state.setFlags(flags);
        }

        if (ANNOTATIONS.equals(words[0])) {
            List<String> annotations = parseAnnotations(words[1]);
            state.setAnnotations(annotations);
        }
    }

    private void parseInstrumentationResult(String payload) {
        if (state instanceof StartNewObject) {
            state = new ProcessCrashedState();
        }
        String[] words = getSplitArray(payload);

        if (SHORT_MSG.equals(words[0])) {
            state.setShortMessage(words[1]);
        }

        if (LONG_MSG.equals(words[0])) {
            state.setLongMessage(words[1]);
        }
    }

    private List<String> parseFlags(@Nonnull String flags) {
        ArrayList<String> result = new ArrayList<>();
        if (flags.indexOf(',') < 0) {
            result.add(flags);
            return result;
        }
        return Arrays.asList(flags.split(","));
    }

    @NotNull
    private String[] getSplitArray(String payload) {
        return payload.split("=", 2);
    }

    private List<String> parseAnnotations(@Nonnull String annotations) {
        ArrayList<String> result = new ArrayList<>();
        if (annotations.indexOf(',') < 0) {
            result.add(annotations);
            return result;
        }
        return Arrays.asList(annotations.split(","));
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    public List<TestPlanElement> getTestInstances() {
        return testPlanList;
    }

    private interface State {
        void storeValuesIfNeeded();

        void setAnnotations(List<String> annotations);

        void setTestId(String testId);

        void setTestMethod(String testMethod);

        void setClassName(String className);

        void setFeature(@Nonnull String feature);

        void setFlags(List<String> flags);

        void setShortMessage(String shortMsg);

        void setLongMessage(String longMsg);

        void setCode(String code);
    }

    private class StartNewObject implements State {
        private String testId;
        private String testMethodName;
        private String testClassName;
        private String feature = "";
        private List<String> annotations;
        private List<String> flags;

        @Override
        public void setTestId(String id) {
            testId = id;
        }

        @Override
        public void setTestMethod(String testMethod) {
            testMethodName = testMethod;
            changeStateIfNeeded();
        }

        @Override
        public void setClassName(String className) {
            testClassName = className;
            changeStateIfNeeded();
        }

        @Override
        public void setFeature(@Nonnull String feature) {
            this.feature = feature;
        }

        @Override
        public void setAnnotations(List<String> annotations) {
            this.annotations = annotations;
        }

        @Override
        public void setFlags(List<String> flags) {
            this.flags = flags;
        }

        @Override
        public void storeValuesIfNeeded() { /* not used */ }

        @Override
        public void setShortMessage(String shortMsg) { /* not used */ }

        @Override
        public void setLongMessage(String longMsg) { /* not used */ }

        @Override
        public void setCode(String code) { /* not used */ }

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

    private class ReadyToStoreObject implements State {
        private final String testId;
        private final String testMethodName;
        private final String testClassName;
        private TestPlanElement testPlan;
        private List<String> annotations;
        private String feature = "";
        private List<String> flags;

        private ReadyToStoreObject(String testId, String testMethodName, String testClassName) {
            this.testId = testId;
            this.testMethodName = testMethodName;
            this.testClassName = testClassName;
        }

        @Override
        public void setTestId(String testId) {
            storeValuesIfNeeded();
            state = new StartNewObject();
            state.setTestId(testId);
        }

        @Override
        public void storeValuesIfNeeded() {
            testPlan = new TestPlanElement(testId, testMethodName, testClassName);

            if (!testPlanList.contains(testPlan)) {
                testPlanList.add(testPlan);

                testPlan.addAnnotations(annotations);
                testPlan.setFeature(feature);
                testPlan.setFlags(flags);
            }
        }

        @Override
        public void setAnnotations(List<String> annotations) {
            if (testPlan != null) {
                testPlan.addAnnotations(annotations);
                return;
            }
            this.annotations = annotations;
        }

        @Override
        public void setFeature(@Nonnull String feature) {
            if (testPlan != null) {
                testPlan.setFeature(feature);
                return;
            }
            this.feature = feature;
        }

        @Override
        public void setFlags(List<String> flags) {
            if (testPlan != null) {
                testPlan.setFlags(flags);
                return;
            }
            this.flags = flags;
        }

        @Override
        public void setTestMethod(String testMethod) {/* not used */}

        @Override
        public void setClassName(String className) {/* not used */}

        @Override
        public void setShortMessage(String shortMsg) { /* not used */ }

        @Override
        public void setLongMessage(String longMsg) { /* not used */ }

        @Override
        public void setCode(String code) { /* not used */ }
    }

    private class ProcessCrashedState implements State {
        private String shortMessage = "";
        private String longMessage;

        @Override
        public void storeValuesIfNeeded() { /* not used */ }

        @Override
        public void setAnnotations(List<String> annotations) { /* not used */ }

        @Override
        public void setTestId(String testId) { /* not used */ }

        @Override
        public void setTestMethod(String testMethod) { /* not used */ }

        @Override
        public void setClassName(String className) { /* not used */ }

        @Override
        public void setFeature(@Nonnull String feature) { /* not used */ }

        @Override
        public void setFlags(List<String> flags) { /* not used */ }

        @Override
        public void setShortMessage(String shortMsg) {
            shortMessage = shortMsg;
        }

        @Override
        public void setLongMessage(String longMsg) {
            longMessage = longMsg;
        }

        @Override
        public void setCode(String code) {
            if ("0".equals(code)) {
                throw new ProcessCrashedException(
                        longMessage != null ? longMessage : shortMessage
                );
            }
        }
    }

    public interface ParserLogger {
        void logLine(String line);
    }
}
