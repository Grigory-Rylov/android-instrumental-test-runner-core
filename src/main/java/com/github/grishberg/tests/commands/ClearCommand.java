package com.github.grishberg.tests.commands;

import com.android.ddmlib.MultiLineReceiver;
import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.InstrumentalPluginExtension;
import org.gradle.api.logging.Logger;

import java.util.concurrent.TimeUnit;

/**
 * Cleans app data.
 */
public class ClearCommand implements DeviceRunnerCommand {
    private final Logger logger;
    private final InstrumentalPluginExtension instrumentationInfo;

    public ClearCommand(Logger logger,
                        InstrumentalPluginExtension instrumentalInfo) {
        this.logger = logger;
        this.instrumentationInfo = instrumentalInfo;
    }

    @Override
    public DeviceCommandResult execute(ConnectedDeviceWrapper device) throws ExecuteCommandException {
        logger.info("ClearCommand for package {}", instrumentationInfo.getApplicationId());
        MultiLineReceiver receiver = new MultiLineReceiver() {
            @Override
            public void processNewLines(String[] lines) {
                for (String line : lines) {
                    logger.info(line);
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
            logger.error("ClearCommand.execute error:", e);
        }
        return new DeviceCommandResult();
    }
}
