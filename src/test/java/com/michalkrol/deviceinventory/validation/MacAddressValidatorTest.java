package com.michalkrol.deviceinventory.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.michalkrol.deviceinventory.exception.DeviceInventoryException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MacAddressValidatorTest {

    @Test
    void shouldNotThrowExceptionForValidMacAddress() {
        assertDoesNotThrow(() -> MacAddressValidator.validateMacAddress("AA:BB:CC:DD:EE:FF"));
    }

    @Test
    void shouldThrowExceptionWhenMacIsNull() {
        DeviceInventoryException exception = assertThrows(DeviceInventoryException.class, () ->
                MacAddressValidator.validateMacAddress(null)
        );
        assertEquals("MAC address cannot be null.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenMacHasInvalidCharacters() {
        DeviceInventoryException exception = assertThrows(DeviceInventoryException.class, () ->
                MacAddressValidator.validateMacAddress("GG:12:34:56:78:9A") // 'GG' nie jest hexem
        );
        assertEquals(
                "MAC address must comply with XX:XX:XX:XX:XX:XX alphanumerical format.",
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowExceptionWhenMacHasWrongFormat_NoColons() {
        DeviceInventoryException exception = assertThrows(DeviceInventoryException.class, () ->
                MacAddressValidator.validateMacAddress("AABBCCDDEEFF")
        );
        assertEquals(
                "MAC address must comply with XX:XX:XX:XX:XX:XX alphanumerical format.",
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowExceptionWhenMacHasTooFewSegments() {
        DeviceInventoryException exception = assertThrows(DeviceInventoryException.class, () ->
                MacAddressValidator.validateMacAddress("AA:BB:CC:DD:EE")
        );
        assertEquals(
                "MAC address must comply with XX:XX:XX:XX:XX:XX alphanumerical format.",
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowExceptionWhenMacHasLowercaseLetters() {
        DeviceInventoryException exception = assertThrows(DeviceInventoryException.class, () ->
                MacAddressValidator.validateMacAddress("aa:bb:cc:dd:ee:ff") // regex dopuszcza tylko wielkie litery
        );
        assertEquals(
                "MAC address must comply with XX:XX:XX:XX:XX:XX alphanumerical format.",
                exception.getMessage()
        );
    }
}
