package com.github.grishberg.tests.planner;

import com.android.ddmlib.MultiLineReceiver;
import com.github.grishberg.tests.common.RunnerLogger;
import com.github.grishberg.tests.exceptions.ProcessCrashedException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Parses am instrument -e log true output and generates.
 */
public class InstrumentTestLogParser extends MultiLineReceiver {
    private static final String TAG = InstrumentTestLogParser.class.getSimpleName();
    private static final String INSTRUMENTATION_STATUS = "INSTRUMENTATION_STATUS: ";
    private static final String INSTRUMENTATION_RESULT = "INSTRUMENTATION_RESULT: ";
    private static final String INSTRUMENTATION_CODE = "INSTRUMENTATION_CODE: ";
    private static final String ID = "id";
    private static final String TEST = "test";
    private static final String CLASS = "class";
    private static final String ERROR = "error";
    private static final String ANNOTATIONS = "annotations";
    private static final String SHORT_MSG = "shortMsg";
    private static final String LONG_MSG = "longMsg";
    private final RunnerLogger logger;
    private final Gson gson = new GsonBuilder().create();
    private final ArrayList<TestPlanElement> testPlanList = new ArrayList<>();
    private State state = new StartNewObject();

    public InstrumentTestLogParser(RunnerLogger logger) {
        this.logger = logger;
    }

    /**
     * @param lines input lines from am instrument.
     * @throws InstrumentTestLogParserException
     */
    @Override
    public void processNewLines(String[] lines) throws InstrumentTestLogParserException {
        for (String word : lines) {
            processLine(word);
        }
        state.storeValuesIfNeeded();
    }

    private void processLine(String word) throws InstrumentTestLogParserException {
        if (logger != null) {
            logger.d(TAG, word);
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

        switch (words[0].toLowerCase()) {
            case ID:
                state.storeValuesIfNeeded();
                state.setTestId(words[1]);
                break;

            case TEST:
                state.setTestMethod(words[1]);
                break;

            case CLASS:
                state.setClassName(words[1]);
                break;

            case ANNOTATIONS:
                state.setAnnotations(parseAnnotations(words[1]));
                break;

            case ERROR:
                throw new InstrumentTestLogParserException(words[1]);

            default:
                logger.w(TAG, "Unknown parameter: {}={}", words[0], words[1]);
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

    @NotNull
    private String[] getSplitArray(String payload) {
        return payload.split("=", 2);
    }

    private List<AnnotationInfo> parseAnnotations(@Nonnull String annotations) {
        return Arrays.asList(gson.fromJson(annotations, AnnotationInfo[].class));
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

        void setAnnotations(List<AnnotationInfo> annotations);

        void setTestId(String testId);

        void setTestMethod(String testMethod);

        void setClassName(String className);

        void setShortMessage(String shortMsg);

        void setLongMessage(String longMsg);

        void setCode(String code);
    }

    private class StartNewObject implements State {
        private String testId;
        private String testMethodName;
        private String testClassName;
        private List<AnnotationInfo> annotations;

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
        public void setAnnotations(List<AnnotationInfo> annotations) {
            this.annotations = annotations;
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
                if (annotations != null) {
                    state.setAnnotations(annotations);
                }
            }
        }
    }

    private class ReadyToStoreObject implements State {
        private final String testId;
        private final String testMethodName;
        private final String testClassName;
        private TestPlanElement testPlan;
        private List<AnnotationInfo> annotations = Collections.emptyList();

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
            testPlan = new TestPlanElement(testId, testMethodName, testClassName, annotations);

            if (!testPlanList.contains(testPlan)) {
                testPlanList.add(testPlan);
            }
        }

        @Override
        public void setAnnotations(List<AnnotationInfo> annotations) {
            if (testPlan != null) {
                testPlan = new TestPlanElement(testId, testMethodName, testClassName, annotations);
                replaceTestPlanInListIfExists(testPlan);
                return;
            }
            this.annotations = annotations;
        }

        private void replaceTestPlanInListIfExists(TestPlanElement testPlan) {
            int pos = testPlanList.indexOf(testPlan); // see TestPlanElement.equals
            if (pos >= 0) {
                testPlanList.remove(pos);
                testPlanList.add(testPlan);
            }
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
        public void setAnnotations(List<AnnotationInfo> annotations) { /* not used */ }

        @Override
        public void setTestId(String testId) { /* not used */ }

        @Override
        public void setTestMethod(String testMethod) { /* not used */ }

        @Override
        public void setClassName(String className) { /* not used */ }

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
}
