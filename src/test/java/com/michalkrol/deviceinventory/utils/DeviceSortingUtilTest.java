package com.michalkrol.deviceinventory.utils;

import static com.michalkrol.deviceinventory.model.DeviceType.ACCESS_POINT;
import static com.michalkrol.deviceinventory.model.DeviceType.GATEWAY;
import static com.michalkrol.deviceinventory.model.DeviceType.SWITCH;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.michalkrol.deviceinventory.model.Device;
import com.michalkrol.deviceinventory.model.DeviceType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class DeviceSortingUtilTest {

    private Device createDevice(DeviceType type, String mac) {
        Device device = new Device();
        device.setDeviceType(type);
        device.setMacAddress(mac);
        return device;
    }

    @Test
    void shouldSortByDeviceTypePriority() {
        List<Device> devices = new ArrayList<>(List.of(
                createDevice(ACCESS_POINT, "AA:00:00:00:00:03"),
                createDevice(GATEWAY, "AA:00:00:00:00:01"),
                createDevice(SWITCH, "AA:00:00:00:00:02")
        ));

        DeviceSortingUtil.sortDevices(devices);

        assertEquals(GATEWAY, devices.get(0).getDeviceType());
        assertEquals(SWITCH, devices.get(1).getDeviceType());
        assertEquals(ACCESS_POINT, devices.get(2).getDeviceType());
    }

    @Test
    void shouldSortByMacAddressWhenSameType() {
        List<Device> devices = new ArrayList<>(List.of(
                createDevice(SWITCH, "AA:00:00:00:00:05"),
                createDevice(SWITCH, "AA:00:00:00:00:01"),
                createDevice(SWITCH, "AA:00:00:00:00:03")
        ));

        DeviceSortingUtil.sortDevices(devices);

        assertEquals("AA:00:00:00:00:01", devices.get(0).getMacAddress());
        assertEquals("AA:00:00:00:00:03", devices.get(1).getMacAddress());
        assertEquals("AA:00:00:00:00:05", devices.get(2).getMacAddress());
    }

    @Test
    void shouldSortByTypeThenMacAddress() {
        List<Device> devices = new ArrayList<>(List.of(
                createDevice(SWITCH, "AA:00:00:00:00:05"),
                createDevice(GATEWAY, "AA:00:00:00:00:02"),
                createDevice(SWITCH, "AA:00:00:00:00:01"),
                createDevice(ACCESS_POINT, "AA:00:00:00:00:04")
        ));

        DeviceSortingUtil.sortDevices(devices);

        assertEquals(GATEWAY, devices.get(0).getDeviceType());
        assertEquals(SWITCH, devices.get(1).getDeviceType());
        assertEquals(SWITCH, devices.get(2).getDeviceType());
        assertEquals(ACCESS_POINT, devices.get(3).getDeviceType());

        assertEquals("AA:00:00:00:00:02", devices.get(0).getMacAddress());
        assertEquals("AA:00:00:00:00:01", devices.get(1).getMacAddress());
        assertEquals("AA:00:00:00:00:05", devices.get(2).getMacAddress());
        assertEquals("AA:00:00:00:00:04", devices.get(3).getMacAddress());
    }

    @Test
    void shouldHandleEmptyList() {
        List<Device> devices = new ArrayList<>();

        assertDoesNotThrow(() -> DeviceSortingUtil.sortDevices(devices));

        assertTrue(devices.isEmpty());
    }

    @Test
    void shouldNotFailWhenAlreadySorted() {
        List<Device> devices = new ArrayList<>(List.of(
                createDevice(GATEWAY, "AA:00:00:00:00:01"),
                createDevice(SWITCH, "AA:00:00:00:00:02"),
                createDevice(ACCESS_POINT, "AA:00:00:00:00:03")
        ));

        assertDoesNotThrow(() -> DeviceSortingUtil.sortDevices(devices));

        assertEquals(GATEWAY, devices.get(0).getDeviceType());
        assertEquals(SWITCH, devices.get(1).getDeviceType());
        assertEquals(ACCESS_POINT, devices.get(2).getDeviceType());
    }
}
