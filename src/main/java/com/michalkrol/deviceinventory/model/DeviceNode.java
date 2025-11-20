package com.michalkrol.deviceinventory.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DeviceNode {

    @Schema(
            description = "Uplink Device MAC Address",
            example = "00:1A:2B:3C:4D:5A"
    )
    private String macAddress;
    private List<DeviceNode> linkedDevices =  new ArrayList<>();

    public DeviceNode(String macAddress) {
        this.macAddress = macAddress;
    }

    public void addLinkedDevice(DeviceNode deviceNode) {
        linkedDevices.add(deviceNode);
    }
}
