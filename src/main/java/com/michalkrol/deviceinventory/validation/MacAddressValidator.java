package com.michalkrol.deviceinventory.validation;

import com.michalkrol.deviceinventory.exception.DeviceInventoryException;

public class MacAddressValidator {

    public static void validateMacAddress(String macAddress) {
        if (macAddress == null) {
            throw new DeviceInventoryException("MAC address cannot be null.");
        }

        String regex = "^([0-9A-F]{2}:){5}[0-9A-F]{2}$";

        if (!macAddress.matches(regex)) {
            throw new DeviceInventoryException("MAC address must comply with XX:XX:XX:XX:XX:XX alphanumerical format.");
        }
    }
}
