package com.github.grishberg.tests.sharding;

import com.github.grishberg.tests.ConnectedDeviceWrapper;
import com.github.grishberg.tests.adb.AdbWrapper;
import com.github.grishberg.tests.common.RunnerLogger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates arguments for sharding.
 * Will be created shards for different device groups. The tests of each groups will not cross different device group.
 *
 * For example: if there is 2 groups: Phones and Tablets, and there is 3 phones and 2 tablets,
 * it will create 3 shards for phones and 2 shards for tablets.
 *
 * Groups are defined in {@link DeviceTypeAdapter}
 */
public class ShardArgumentsImpl implements ShardArguments {
    private static final String TAG = "AbsShardingArguments";
    private static final String NUM_SHARDS_PARAM = "numShards";
    private static final String SHARD_INDEX_PARAM = "shardIndex";
    private final AdbWrapper adbWrapper;
    private final DeviceTypeAdapter deviceTypeAdapter;
    private Map<Integer, List<ConnectedDeviceWrapper>> devicesByTypeMap = new HashMap<>();

    public ShardArgumentsImpl(AdbWrapper adbWrapper,
                              DeviceTypeAdapter deviceTypeAdapter) {

        this.adbWrapper = adbWrapper;
        this.deviceTypeAdapter = deviceTypeAdapter;
    }

    @NotNull
    @Override
    public Map<String, String> createShardArguments(@NotNull ConnectedDeviceWrapper currentDevice) {
        if (devicesByTypeMap.isEmpty()) {
            populateDeviceMap();
        }

        int deviceType = deviceTypeAdapter.provideDeviceType(currentDevice);
        int numShards = devicesByTypeMap.get(deviceType).size();
        int shardIndex = getDeviceIndex(currentDevice, devicesByTypeMap.get(deviceType), currentDevice.getLogger());

        Map<String, String> args = new HashMap<>();
        args.put(NUM_SHARDS_PARAM, "" + numShards);
        args.put(SHARD_INDEX_PARAM, "" + shardIndex);
        return args;
    }

    private int getDeviceIndex(ConnectedDeviceWrapper currentDevice,
                               List<ConnectedDeviceWrapper> deviceList,
                               RunnerLogger logger) {
        for (int i = 0; i < deviceList.size(); i++) {
            if (deviceList.get(i).equals(currentDevice)) {
                return i;
            }
        }

        logger.e(TAG, "device: " + currentDevice + " not found");
        return 0;
    }

    private void populateDeviceMap() {
        List<ConnectedDeviceWrapper> devices = adbWrapper.provideDevices();
        for (ConnectedDeviceWrapper device : devices) {
            int currentDeviceType = deviceTypeAdapter.provideDeviceType(device);
            List<ConnectedDeviceWrapper> currentList = devicesByTypeMap.getOrDefault(currentDeviceType, new ArrayList<>());
            devicesByTypeMap.putIfAbsent(currentDeviceType, currentList);
            currentList.add(device);
        }
    }
}
