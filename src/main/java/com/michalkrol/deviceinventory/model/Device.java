package com.michalkrol.deviceinventory.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Device {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Schema(hidden = true)
    private Long id;

    private DeviceType deviceType;

    @Schema(
            description = "Device MAC Address",
            example = "00:1A:2B:3C:4D:5E"
    )
    private String macAddress;

    @Schema(
            description = "Uplink Device MAC Address",
            example = "00:1A:2B:3C:4D:5A"
    )
    private String uplinkMacAddress;
}
