package com.michalkrol.deviceinventory.config;

import com.michalkrol.deviceinventory.model.DeviceType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "devices")
public class DeviceProperties {

    private List<DeviceConfig> initial = new ArrayList<>();

    public List<DeviceConfig> getInitial() {
        return initial;
    }

    public void setInitial(List<DeviceConfig> initial) {
        this.initial = initial;
    }

    public static class DeviceConfig {

        private DeviceType deviceType;
        private String macAddress;
        private String uplinkMacAddress;

        public DeviceType getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(DeviceType deviceType) {
            this.deviceType = deviceType;
        }

        public String getMacAddress() {
            return macAddress;
        }

        public void setMacAddress(String macAddress) {
            this.macAddress = macAddress;
        }

        public String getUplinkMacAddress() {
            return uplinkMacAddress;
        }

        public void setUplinkMacAddress(String uplinkMacAddress) {
            this.uplinkMacAddress = uplinkMacAddress;
        }
    }
}

