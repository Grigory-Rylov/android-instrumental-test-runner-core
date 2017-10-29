package com.grishberg.tests.commands;

import com.android.ddmlib.MultiLineReceiver;
import com.grishberg.tests.DeviceWrapper;
import com.grishberg.tests.InstrumentationInfo;
import org.gradle.api.Project;

import java.util.concurrent.TimeUnit;

/**
 * Created by grishberg on 29.10.17.
 */
public class ClearCommand implements DeviceCommand {
    private final Project project;
    private final InstrumentationInfo instrumentationInfo;

    public ClearCommand(Project project,
                        InstrumentationInfo instrumentalInfo) {
        this.project = project;
        this.instrumentationInfo = instrumentalInfo;
    }

    @Override
    public DeviceCommandResult execute(DeviceWrapper device) {
        MultiLineReceiver receiver = new MultiLineReceiver() {
            @Override
            public void processNewLines(String[] lines) {
                for (String line : lines) {
                    project.getLogger().info(line);
                }
            }

            @Override
            public boolean isCancelled() {
                return false;
            }
        };
        StringBuilder command = new StringBuilder("pm clear ");
        command.append(instrumentationInfo.getApplicationId());

        try {
            device.executeShellCommand(command.toString(), receiver,
                    0, TimeUnit.SECONDS);
        } catch (Exception e) {
            project.getLogger().error("ClearCommand.execute error:", e);
        }
        return new DeviceCommandResult();
    }
}
