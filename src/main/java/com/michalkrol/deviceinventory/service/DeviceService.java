package com.michalkrol.deviceinventory.service;

import com.michalkrol.deviceinventory.exception.DeviceInventoryException;
import com.michalkrol.deviceinventory.model.Device;
import com.michalkrol.deviceinventory.model.DeviceNode;
import com.michalkrol.deviceinventory.repository.DeviceRepository;
import com.michalkrol.deviceinventory.utils.DeviceSortingUtils;
import com.michalkrol.deviceinventory.utils.TopologyUtils;
import com.michalkrol.deviceinventory.validation.MacAddressValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public Device save(Device device) {
        List<Device> devices = deviceRepository.findAll();
        if (device.getMacAddress().equals(device.getUplinkMacAddress())) {
            throw new DeviceInventoryException("MAC address must be different from the uplink MAC address");
        }
        MacAddressValidator.validateMacAddress(device.getMacAddress());
        MacAddressValidator.validateMacAddress(device.getUplinkMacAddress());
        TopologyUtils.checkUplinkMacAddressExistence(device, devices);
        TopologyUtils.checkMacAddressUniqueness(device, devices);
        TopologyUtils.checkUplinkConnection(device, devices);
        return deviceRepository.save(device);
    }

    public Device findByMacAddress(String macAddress) {
        MacAddressValidator.validateMacAddress(macAddress);
        return deviceRepository.findByMacAddress(macAddress)
                .orElseThrow(() -> new DeviceInventoryException("A Device with MAC Address " + macAddress
                        + " not found."));
    }

    public List<Device> findAllSorted() {
        List<Device> devices = deviceRepository.findAll();
        DeviceSortingUtils.sortDevices(devices);
        return devices;
    }

    public List<DeviceNode> getTopology() {
        List<Device> devices = deviceRepository.findAll();
        TopologyUtils.validateNoCycles(devices);
        return TopologyUtils.prepareDeviceTopology(devices);
    }

    public DeviceNode getSubDeviceTopology(String rootDeviceMacAddress) {
        MacAddressValidator.validateMacAddress(rootDeviceMacAddress);
        List<Device> devices = deviceRepository.findAll();
        TopologyUtils.validateNoCycles(devices);
        deviceRepository.findByMacAddress(rootDeviceMacAddress)
                .orElseThrow(() -> new DeviceInventoryException("A Device with MAC Address " + rootDeviceMacAddress
                        + " not found."));
        return TopologyUtils.prepareSubDeviceTopology(rootDeviceMacAddress, devices);
    }
}
