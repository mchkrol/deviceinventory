package com.michalkrol.deviceinventory.config;

import com.michalkrol.deviceinventory.model.Device;
import com.michalkrol.deviceinventory.repository.DeviceRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeviceDataLoader implements ApplicationRunner {

    private final DeviceProperties deviceProperties;
    private final DeviceRepository deviceRepository;

    public DeviceDataLoader(DeviceProperties deviceProperties, DeviceRepository deviceRepository) {
        this.deviceProperties = deviceProperties;
        this.deviceRepository = deviceRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (deviceRepository.count() == 0) {
            List<Device> devices = deviceProperties.getInitial().stream()
                    .map(cfg -> {
                        Device d = new Device();
                        d.setDeviceType(cfg.getDeviceType());
                        d.setMacAddress(cfg.getMacAddress());
                        d.setUplinkMacAddress(cfg.getUplinkMacAddress());
                        return d;
                    })
                    .toList();

            deviceRepository.saveAll(devices);

            System.out.println(">>> Start Devices uploaded: " + devices.size());
        }
    }
}

