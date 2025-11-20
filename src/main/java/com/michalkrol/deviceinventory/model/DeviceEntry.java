package com.michalkrol.deviceinventory.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DeviceEntry {

    private DeviceType deviceType;

    @Schema(
            description = "Device MAC Address",
            example = "00:1A:2B:3C:4D:5A"
    )
    private String macAddress;
}
