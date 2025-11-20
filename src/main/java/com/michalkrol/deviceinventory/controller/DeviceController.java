package com.michalkrol.deviceinventory.controller;

import com.michalkrol.deviceinventory.model.Device;
import com.michalkrol.deviceinventory.model.DeviceEntry;
import com.michalkrol.deviceinventory.model.DeviceNode;
import com.michalkrol.deviceinventory.service.DeviceService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/device")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping("/{macAddress}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Validation Error", content = @Content)
    })
    public DeviceEntry getDevice(@PathVariable String macAddress) {
        Device device = deviceService.findByMacAddress(macAddress);
        return toDeviceEntry(device);
    }

    @GetMapping
    public List<DeviceEntry> getSortedDevices() {
        return deviceService.findAllSorted().stream()
                .map(DeviceController::toDeviceEntry)
                .toList();
    }

    private static DeviceEntry toDeviceEntry(Device device) {
        return DeviceEntry.builder()
                .deviceType(device.getDeviceType())
                .macAddress(device.getMacAddress())
                .build();
    }

    @GetMapping("/getTopology")
    public List<DeviceNode> getTopology() {
        return deviceService.getTopology();
    }

    @GetMapping("/getSubDeviceTopology/{rootDeviceMacAddress}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Validation Error", content = @Content)
    })
    public DeviceNode getSubDeviceTopology(@PathVariable String rootDeviceMacAddress) {
        return deviceService.getSubDeviceTopology(rootDeviceMacAddress);
    }

    @PostMapping
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Validation Error", content = @Content)
    })
    public Device createDevice(@RequestBody Device device) {
        return deviceService.save(device);
    }
}
