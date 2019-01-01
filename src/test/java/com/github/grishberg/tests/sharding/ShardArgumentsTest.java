package com.github.grishberg.tests.sharding;

import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.adb.AdbWrapper;
import com.github.grishberg.tests.common.RunnerLogger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ShardArgumentsTest {
    @Mock
    ConnectedDeviceWrapper phone1;
    @Mock
    ConnectedDeviceWrapper phone2;
    @Mock
    ConnectedDeviceWrapper tablet1;
    @Mock
    ConnectedDeviceWrapper tablet2;
    @Mock
    AdbWrapper adbWrapper;
    @Mock
    RunnerLogger logger;
    private ShardArguments sharding;
    private ArrayList<ConnectedDeviceWrapper> deviceList = new ArrayList<>();

    @Before
    public void setUp() {
        deviceList.add(phone1);
        deviceList.add(phone2);
        deviceList.add(tablet1);
        deviceList.add(tablet2);

        when(phone1.getWidthInDp()).thenReturn(500L);
        when(phone2.getWidthInDp()).thenReturn(500L);
        when(tablet1.getWidthInDp()).thenReturn(600L);
        when(tablet2.getWidthInDp()).thenReturn(600L);
        when(adbWrapper.provideDevices()).thenReturn(deviceList);
        sharding = new ShardArgumentsImpl(adbWrapper, logger,
                new TabletsAndPhoneDeviceTypeAdapter(600));
    }

    @Test
    public void shardIndex0WhenFirstPhone() {
        Map<String, String> args = sharding.createShardArguments(phone1);

        assertEquals("2", args.get("numShards"));
        assertEquals("0", args.get("shardIndex"));
    }

    @Test
    public void  shardIndex1WhenSecondPhone() {
        Map<String, String> args = sharding.createShardArguments(phone2);

        assertEquals("2", args.get("numShards"));
        assertEquals("1", args.get("shardIndex"));
    }

    @Test
    public void  shardIndex0WhenFirstTablet() {
        Map<String, String> args = sharding.createShardArguments(phone1);

        assertEquals("2", args.get("numShards"));
        assertEquals("0", args.get("shardIndex"));
    }

    @Test
    public void  shardIndex1WhenSecondTablet() {
        Map<String, String> args = sharding.createShardArguments(phone2);

        assertEquals("2", args.get("numShards"));
        assertEquals("1", args.get("shardIndex"));
    }
}
