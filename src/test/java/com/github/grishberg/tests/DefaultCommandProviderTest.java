package com.github.grishberg.tests;

import com.github.grishberg.tests.commands.ClearCommand;
import com.github.grishberg.tests.commands.DeviceRunnerCommand;
import com.github.grishberg.tests.commands.SetAnimationSpeedCommand;
import com.github.grishberg.tests.commands.SingleInstrumentalTestCommand;
import com.github.grishberg.tests.common.RunnerLogger;
import com.github.grishberg.tests.planner.AnnotationInfo;
import com.github.grishberg.tests.planner.InstrumentalTestPlanProvider;
import com.github.grishberg.tests.planner.TestPlanElement;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by grishberg on 31.03.18.
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultCommandProviderTest {
    private static final HashMap<String, String> ARGS = new HashMap<>();
    private static final AnnotationInfo ANNOTATION = new AnnotationInfo("TestAnnotation");
    private static final String PROJECT_NAME = "test_project";
    @Mock
    InstrumentalExtension extension;
    @Mock
    InstrumentationArgsProvider argsProvider;
    @Mock
    RunnerLogger logger;
    @Mock
    ConnectedDeviceWrapper deviceWrapper;
    @Mock
    InstrumentalTestPlanProvider planProvider;
    @Mock
    Environment environment;
    @Mock
    CommandsForAnnotationProvider commandsForAnnotationProvider;
    @Mock
    TestPlanElement element;
    @Mock
    TestPlanElement elementWithAnnotation;
    @Mock
    TestRunnerContext context;
    private DefaultCommandProvider provider;
    private List<DeviceRunnerCommand> clearCommand = Arrays.asList(new ClearCommand());

    @Before
    public void setUp() throws Exception {
        when(deviceWrapper.getLogger()).thenReturn(logger);
        ArrayList<AnnotationInfo> emptyAnnotations = new ArrayList<>();
        when(element.getAnnotations()).thenReturn(emptyAnnotations);
        List<AnnotationInfo> annotations = Arrays.asList(ANNOTATION);
        when(elementWithAnnotation.getAnnotations()).thenReturn(annotations);

        when(commandsForAnnotationProvider.provideCommand(annotations))
                .thenReturn(clearCommand);
        when(commandsForAnnotationProvider.provideCommand(emptyAnnotations))
                .thenReturn(new ArrayList<>());

        List<TestPlanElement> testPlanElements = Arrays.asList(element);
        when(planProvider.provideTestPlan(deviceWrapper, ARGS)).thenReturn(testPlanElements);
        when(argsProvider.provideInstrumentationArgs(deviceWrapper)).thenReturn(ARGS);
        provider = new DefaultCommandProvider(PROJECT_NAME, argsProvider,
                commandsForAnnotationProvider);
    }

    @Test
    public void provideCommandsForDevice() throws Exception {
        List<DeviceRunnerCommand> commandList = provider.provideCommandsForDevice(deviceWrapper,
                planProvider, environment);
        Assert.assertEquals(3, commandList.size());
        Assert.assertTrue(commandList.get(0) instanceof SetAnimationSpeedCommand);

        Assert.assertTrue(commandList.get(1) instanceof SingleInstrumentalTestCommand);

        Assert.assertTrue(commandList.get(2) instanceof SetAnimationSpeedCommand);
        verify(argsProvider).provideInstrumentationArgs(deviceWrapper);
        verify(commandsForAnnotationProvider).provideCommand(element.getAnnotations());
    }

    @Test
    public void provideCommandsForDeviceWithAnnotations() throws Exception {
        List<AnnotationInfo> annotations = Arrays.asList(ANNOTATION);
        when(element.getAnnotations()).thenReturn(annotations);
        List<DeviceRunnerCommand> clearCommand = Arrays.asList(new ClearCommand());
        when(commandsForAnnotationProvider.provideCommand(annotations))
                .thenReturn(clearCommand);
        List<DeviceRunnerCommand> commandList = provider.provideCommandsForDevice(deviceWrapper,
                planProvider, environment);
        Assert.assertEquals(4, commandList.size());
        Assert.assertTrue(commandList.get(0) instanceof SetAnimationSpeedCommand);

        Assert.assertTrue(commandList.get(1) instanceof ClearCommand);

        Assert.assertTrue(commandList.get(2) instanceof SingleInstrumentalTestCommand);

        Assert.assertTrue(commandList.get(3) instanceof SetAnimationSpeedCommand);
        verify(argsProvider).provideInstrumentationArgs(deviceWrapper);
        verify(commandsForAnnotationProvider).provideCommand(element.getAnnotations());
    }

    @Test
    public void provideCommandsWhenHasAnnotations() throws Exception {
        when(planProvider.provideTestPlan(deviceWrapper, ARGS)).thenReturn(
                Arrays.asList(element, elementWithAnnotation));

        List<DeviceRunnerCommand> commandList = provider.provideCommandsForDevice(deviceWrapper,
                planProvider, environment);
        Assert.assertEquals(5, commandList.size());
        Assert.assertTrue(commandList.get(0) instanceof SetAnimationSpeedCommand);

        Assert.assertTrue(commandList.get(1) instanceof SingleInstrumentalTestCommand);

        Assert.assertTrue(commandList.get(2) instanceof ClearCommand);

        Assert.assertTrue(commandList.get(3) instanceof SingleInstrumentalTestCommand);

        Assert.assertTrue(commandList.get(4) instanceof SetAnimationSpeedCommand);
        verify(argsProvider).provideInstrumentationArgs(deviceWrapper);
        verify(commandsForAnnotationProvider).provideCommand(element.getAnnotations());
    }
}