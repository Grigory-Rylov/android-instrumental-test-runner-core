package com.github.grishberg.tests.commands;

import com.android.ddmlib.MultiLineReceiver;
import com.github.grishberg.tests.DeviceWrapper;
import com.github.grishberg.tests.InstrumentationInfo;
import org.gradle.api.logging.Logger;

import java.util.concurrent.TimeUnit;

/**
 * Cleans app data.
 */
public class ClearCommand implements DeviceCommand {
    private final Logger logger;
    private final InstrumentationInfo instrumentationInfo;

    public ClearCommand(Logger logger,
                        InstrumentationInfo instrumentalInfo) {
        this.logger = logger;
        this.instrumentationInfo = instrumentalInfo;
    }

    @Override
    public DeviceCommandResult execute(DeviceWrapper device) {
        logger.info("ClearCommand for package " + instrumentationInfo.getApplicationId());
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
