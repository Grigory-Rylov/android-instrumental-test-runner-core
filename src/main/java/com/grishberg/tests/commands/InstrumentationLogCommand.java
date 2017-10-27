package com.grishberg.tests.commands;

import com.android.ddmlib.MultiLineReceiver;
import com.grishberg.tests.DeviceWrapper;
import com.grishberg.tests.InstrumentationInfo;
import org.gradle.api.Project;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Logs test result.
 */
public class InstrumentationLogCommand implements DeviceCommand {
    private final InstrumentationInfo instrumentationInfo;
    private final Map<String, String> instrumentalArgs;
    private final Project project;

    public InstrumentationLogCommand(Project project,
                                     InstrumentationInfo instrumentationInfo,
                                     Map<String, String> instrumentalArgs) {
        this.project = project;
        this.instrumentationInfo = instrumentationInfo;
        this.instrumentalArgs = new HashMap<>(instrumentalArgs);
        this.instrumentalArgs.put("log", "true");
    }

    @Override
    public DeviceCommandResult execute(DeviceWrapper device) {
        DeviceCommandResult result = new DeviceCommandResult();
        MultiLineReceiver receiver = new MultiLineReceiver() {
            @Override
            public void processNewLines(String[] lines) {
                for (String word : lines) {
                    System.out.println(word);
                }
            }

            @Override
            public boolean isCancelled() {
                return false;
            }
        };
        StringBuilder command = new StringBuilder("am instrument -r -w");

        for (Map.Entry<String, String> arg : instrumentalArgs.entrySet()) {
            command.append(" -e ");
            command.append(arg.getKey());
            command.append(" ");
            command.append(arg.getValue());
        }
        command.append(" ");
        command.append(instrumentationInfo.getInstrumentalPackage());
        command.append("/");
        command.append(instrumentationInfo.getInstrumentalRunner());
        System.out.println(command.toString());
        try {
            device.executeShellCommand(command.toString(), receiver,
                    0, TimeUnit.SECONDS);
        } catch (Exception e) {
            project.getLogger().error("InstrumentationLogCommand.execute error:", e);
        }
        return result;
    }
}
