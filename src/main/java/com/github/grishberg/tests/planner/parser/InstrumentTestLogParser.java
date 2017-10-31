package com.github.grishberg.tests.planner.parser;

import com.android.ddmlib.MultiLineReceiver;

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
    private final HashSet<TestPlan> testInstances = new HashSet<>();
    private TestPlan lastTestInstance;
    private String testId;
    private String testMethodName;
    private String testClassName;

    @Override
    public void processNewLines(String[] lines) {
        for (String word : lines) {
            processLine(word);
        }
    }

    private void processLine(String word) {
        System.out.println(word);

        int startPos = word.indexOf(INSTRUMENTATION_STATUS);
        if (startPos < 0) {
            return;
        }

        String payload = word.substring(startPos + INSTRUMENTATION_STATUS.length());
        String[] words = payload.split("=");

        if (words.length != 2) {
            return;
        }

        if (ID.equals(words[0])) {
            testId = words[1];
            return;
        }

        if (TEST.equals(words[0])) {
            testMethodName = words[1];
            return;
        }

        if (CLASS.equals(words[0])) {
            testClassName = words[1];
        }

        if (testId != null && testMethodName != null && testClassName != null) {
            lastTestInstance = new TestPlan(testId, testMethodName, testClassName);
            if (!testInstances.contains(lastTestInstance)) {
                testInstances.add(lastTestInstance);
            }
            testId = null;
            testMethodName = null;
            testClassName = null;
        }
        if (ANNOTATIONS.equals(words[0])) {
            lastTestInstance.setAnnotations(parseAnnotations(words[1]));
        }
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
        return testInstances;
    }
}
