package com.michalkrol.deviceinventory.utils;

import com.michalkrol.deviceinventory.exception.DeviceInventoryException;
import com.michalkrol.deviceinventory.model.Device;
import com.michalkrol.deviceinventory.model.DeviceNode;
import com.michalkrol.deviceinventory.model.DeviceType;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TopologyUtil {

    public static void validateNoCycles(List<Device> devices) {

        // Mapowanie macAddress → Device
        Map<String, Device> devicesMap = devices.stream()
                .collect(Collectors.toMap(Device::getMacAddress, device -> device));

        Map<String, State> deviceStates = devices.stream()
                .collect(Collectors.toMap(Device::getMacAddress, device -> State.UNVISITED));

        Deque<String> pathStack = new ArrayDeque<>();

        devices.stream()
                .map(Device::getMacAddress)
                .forEach(macAddress -> dfs(macAddress, devicesMap, deviceStates, pathStack));
    }

    private static void dfs(
            String currentMacAddress,
            Map<String, Device> devicesMap,
            Map<String, State> deviceStates,
            Deque<String> stack) {

        deviceStates.put(currentMacAddress, State.VISITING);
        stack.push(currentMacAddress);

        Device device = devicesMap.get(currentMacAddress);

        if (device != null && device.getUplinkMacAddress() != null) {
            String uplinkMacAddress = device.getUplinkMacAddress();

            if (devicesMap.containsKey(uplinkMacAddress)) {
                if (deviceStates.get(uplinkMacAddress) == State.UNVISITED) {
                    dfs(uplinkMacAddress, devicesMap, deviceStates, stack);
                } else if (deviceStates.get(uplinkMacAddress) == State.VISITING) {
                    throw new IllegalStateException("A cycle has been detected in the topology: "
                            + buildCycleDescription(stack, uplinkMacAddress));
                }
            }
        }

        stack.pop();
        deviceStates.put(currentMacAddress, State.VISITED);
    }

    private static String buildCycleDescription(Deque<String> stack, String start) {
        List<String> cycle = new ArrayList<>();
        for (String s : stack) {
            cycle.add(s);
            if (s.equals(start)) break;
        }
        Collections.reverse(cycle);
        return cycle.toString();
    }

    public static DeviceNode prepareSubDeviceTopology(String rootDeviceMacAddress, List<Device> devices) {
        // 1. Utwórz mapę: macAddress -> DeviceNode
        Map<String, DeviceNode> deviceNodesMap = devices.stream()
                .collect(Collectors.toMap(Device::getMacAddress, device -> new DeviceNode(device.getMacAddress())));

        devices.forEach(device -> {
            String mac = device.getMacAddress();
            String parentMac = device.getUplinkMacAddress();
            if (parentMac == null || parentMac.isEmpty()) {
                // możliwy korzeń bez podanego uplinka
                return;
            }
            DeviceNode parent = deviceNodesMap.get(parentMac);
            DeviceNode child = deviceNodesMap.get(mac);
            if (parent != null) {
                parent.addLinkedDevice(child);
            }
        });

        // 3. Jeśli korzeń nie został ustalony naturalnie — weź go z mapy
        return deviceNodesMap.getOrDefault(rootDeviceMacAddress, null);
    }

    public static List<DeviceNode> prepareDeviceTopology(List<Device> devices) {
        Set<String> hasUplinkDevice = new HashSet<>();
        Map<String, DeviceNode> deviceNodesMap = prepareDeviceNodesMap(devices);

        devices.forEach(device -> {
            String macAddress = device.getMacAddress();
            String uplinkDeviceMacAddress = device.getUplinkMacAddress();
            if (uplinkDeviceMacAddress != null) {
                DeviceNode parentNode = deviceNodesMap.get(uplinkDeviceMacAddress);
                DeviceNode childNode = deviceNodesMap.get(macAddress);
                parentNode.addLinkedDevice(childNode);
                hasUplinkDevice.add(macAddress);
            }
        });

        return prepareRoots(deviceNodesMap, hasUplinkDevice);
    }

    private static List<DeviceNode> prepareRoots(Map<String, DeviceNode> deviceNodesMap, Set<String> hasUplinkDevice) {
        return deviceNodesMap.entrySet().stream()
                .filter(entry -> !hasUplinkDevice.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    private static Map<String, DeviceNode> prepareDeviceNodesMap(List<Device> devices) {
        Map<String, DeviceNode> deviceNodesMap = new HashMap<>();
        devices.forEach(device -> {
            String macAddress = device.getMacAddress();
            String uplinkDeviceMacAddress = device.getUplinkMacAddress();
            deviceNodesMap.putIfAbsent(macAddress, new DeviceNode(macAddress));
            if (uplinkDeviceMacAddress != null) {
                deviceNodesMap.putIfAbsent(uplinkDeviceMacAddress, new DeviceNode(uplinkDeviceMacAddress));
            }
        });
        return deviceNodesMap;
    }

    public static void checkUplinkMacAddressExistence(Device device, List<Device> devices) {
         if (devices.stream()
                .map(Device::getMacAddress)
                .noneMatch(macAddress -> macAddress.equals(device.getUplinkMacAddress()))) {
             throw new DeviceInventoryException("A Device with MAC address " + device.getUplinkMacAddress()
                     + " does not exist.");
         }
    }

    public static void checkMacAddressUniqueness(Device device, List<Device> devices) {
        if (devices.stream()
                .map(Device::getMacAddress)
                .anyMatch(macAddress -> macAddress.equals(device.getMacAddress()))) {
            throw new DeviceInventoryException("A Device with MAC address " + device.getMacAddress()
                    + " already exists.");
        }
    }

    public static void checkUplinkConnection(Device device, List<Device> devices) {
        if (devices.stream()
                .anyMatch(deviceFromTopology ->
                        device.getUplinkMacAddress().equals(deviceFromTopology.getMacAddress())
                                && DeviceType.ACCESS_POINT.equals(deviceFromTopology.getDeviceType()))) {
            throw new DeviceInventoryException("An Access Point is supposed to connect wireless Devices.");
        }
    }

    enum State { UNVISITED, VISITING, VISITED }
}
