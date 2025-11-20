package com.michalkrol.deviceinventory.utils;

import static com.michalkrol.deviceinventory.model.DeviceType.ACCESS_POINT;
import static com.michalkrol.deviceinventory.model.DeviceType.GATEWAY;
import static com.michalkrol.deviceinventory.model.DeviceType.SWITCH;

import com.michalkrol.deviceinventory.model.Device;
import com.michalkrol.deviceinventory.model.DeviceType;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class DeviceSortingUtils {

    private static final Map<DeviceType, Integer> DEVICE_TYPE_PRIORITY = Map.of(
            GATEWAY, 1,
            SWITCH, 2,
            ACCESS_POINT, 3
    );

    public static void sortDevices(List<Device> devices) {
        devices.sort(
                Comparator.comparingInt((Device device) -> DEVICE_TYPE_PRIORITY.get(device.getDeviceType()))
                        .thenComparing(Device::getMacAddress)
        );
    }
}
