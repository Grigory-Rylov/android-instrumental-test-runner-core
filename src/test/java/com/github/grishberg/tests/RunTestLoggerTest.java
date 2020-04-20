package com.github.grishberg.tests;

import com.android.ddmlib.testrunner.TestIdentifier;
import com.github.grishberg.tests.common.RunnerLogger;
import com.github.grishberg.tests.exceptions.ProcessCrashedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by grishberg on 23.03.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class RunTestLoggerTest {
    private static final String TAG = RunTestLogger.class.getSimpleName();
    @Mock
    RunnerLogger runnerLogger;
    private RunTestLogger logger;

    @Before
    public void setUp() throws Exception {
        logger = new RunTestLogger(runnerLogger);
    }

    @Test
    public void error() throws Exception {
        Throwable t = new Throwable();
        logger.error(t, "msg %s", 1);
        verify(runnerLogger).e(TAG, "msg 1", t);
    }

    @Test
    public void warning() throws Exception {
        logger.warning("msg %s %s", 1, "test");
        verify(runnerLogger).w(TAG, "msg 1 test");
    }

    @Test
    public void info() throws Exception {
        logger.info("msg %s %s", 1, "test");
        verify(runnerLogger).i(TAG, "msg 1 test");
    }

    @Test
    public void verbose() throws Exception {
        logger.verbose("msg %s %s", 1, "test");
        verify(runnerLogger).i(TAG, "msg 1 test");
    }

    @Test
    public void error_dontLogWhenFormatIsNull() throws Exception {
        logger.error(new Throwable(), null, 1);
        verify(runnerLogger, never()).e(anyString(), anyString(), any(Throwable.class));
    }

    @Test
    public void warning_dontLogWhenFormatIsNull() throws Exception {
        logger.warning(null, 1, "test");
        verify(runnerLogger, never()).w(anyString(), anyString());
    }

    @Test
    public void warning_dontCrashWhenGivenMessageWithSpecialSymbol() throws Exception {
        logger.warning("test message %s");
        verify(runnerLogger).w(anyString(), anyString());
    }

    @Test
    public void info_dontLogWhenFormatIsNull() throws Exception {
        logger.info(null, 1, "test");
        verify(runnerLogger, never()).i(anyString(), anyString());
    }

    @Test
    public void verbose_dontLogWhenFormatIsNull() throws Exception {
        logger.verbose(null, 1, "test");
        verify(runnerLogger, never()).i(anyString(), anyString());
    }

    @Test(expected = ProcessCrashedException.class)
    public void throwIfProcessCrashed() {
        logger.warning("Failed %s due to %s", "test", "'Process crashed'");
    }

    @Test
    public void testCornerCaseFormatting() {
        String deviceName = "SM-G530H - 5.0.2";
        TestIdentifier test = new TestIdentifier("SomeClass","someTest");
        logger.warning("\n%1$s > %2$s[%3$s] \033[31mFAILED \033[0m",
                test.getClassName(), test.getTestName(), deviceName);
    }
}