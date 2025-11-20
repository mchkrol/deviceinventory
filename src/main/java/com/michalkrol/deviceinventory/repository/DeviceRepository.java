package com.michalkrol.deviceinventory.repository;

import com.michalkrol.deviceinventory.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device,Long> {

    Optional<Device> findByMacAddress(String macAddress);
}
