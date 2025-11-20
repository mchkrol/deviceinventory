package com.michalkrol.deviceinventory.utils;

import static com.michalkrol.deviceinventory.model.DeviceType.GATEWAY;
import static com.michalkrol.deviceinventory.model.DeviceType.SWITCH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.michalkrol.deviceinventory.exception.DeviceInventoryException;
import com.michalkrol.deviceinventory.model.Device;
import com.michalkrol.deviceinventory.model.DeviceNode;
import com.michalkrol.deviceinventory.model.DeviceType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class TopologyUtilTest {

    private Device createDevice(DeviceType deviceType, String macAddress, String uplinkMacAddress) {
        Device device = new Device();
        device.setMacAddress(macAddress);
        device.setUplinkMacAddress(uplinkMacAddress);
        device.setDeviceType(deviceType);
        return device;
    }

    private Device createDevice(String macAddress, String uplinkMacAddress) {
        Device device = new Device();
        device.setMacAddress(macAddress);
        device.setUplinkMacAddress(uplinkMacAddress);
        return device;
    }

    @Test
    void prepareSubDeviceTopology_shouldReturnTreeWithGivenRoot() {
        // given
        Device device1 = createDevice("AA:BB:CC:DD:EE:01", null);                        // root
        Device device2 = createDevice("AA:BB:CC:DD:EE:02", "AA:BB:CC:DD:EE:01");
        Device device3 = createDevice("AA:BB:CC:DD:EE:03", "AA:BB:CC:DD:EE:01");
        Device device4 = createDevice("AA:BB:CC:DD:EE:04", "AA:BB:CC:DD:EE:02");

        List<Device> devices = List.of(device1, device2, device3, device4);

        // when
        DeviceNode root = TopologyUtil.prepareSubDeviceTopology("AA:BB:CC:DD:EE:01", devices);

        // then
        assertThat(root.getMacAddress()).isEqualTo("AA:BB:CC:DD:EE:01");
        assertThat(root.getLinkedDevices()).hasSize(2);

        DeviceNode node02 = root.getLinkedDevices().stream()
                .filter(deviceNode -> deviceNode.getMacAddress().equals("AA:BB:CC:DD:EE:02"))
                .findFirst().orElseThrow();

        DeviceNode node03 = root.getLinkedDevices().stream()
                .filter(deviceNode -> deviceNode.getMacAddress().equals("AA:BB:CC:DD:EE:03"))
                .findFirst().orElseThrow();

        assertThat(node02.getLinkedDevices()).hasSize(1);
        assertThat(node02.getLinkedDevices().getFirst().getMacAddress()).isEqualTo("AA:BB:CC:DD:EE:04");
        assertThat(node03.getLinkedDevices()).isEmpty();
    }

    @Test
    void prepareSubDeviceTopology_shouldReturnRootEvenIfItHasNoChildren() {
        // given
        Device device1 = createDevice("AA:BB:CC:DD:EE:01", null);
        Device device2 = createDevice("AA:BB:CC:DD:EE:02", "AA:BB:CC:DD:EE:01");

        List<Device> devices = List.of(device1, device2);

        // when
        DeviceNode root = TopologyUtil.prepareSubDeviceTopology("AA:BB:CC:DD:EE:02", devices);

        // then
        assertThat(root.getMacAddress()).isEqualTo("AA:BB:CC:DD:EE:02");
        assertThat(root.getLinkedDevices()).isEmpty();
    }

    @Test
    void prepareDeviceTopology_shouldReturnMultipleIndependentTrees() {
        // given
        Device device1 = createDevice("AA:BB:CC:DD:EE:01", null);           // root 1
        Device device2 = createDevice("AA:BB:CC:DD:EE:02", "AA:BB:CC:DD:EE:01");
        Device device3 = createDevice("AA:BB:CC:DD:EE:03", null);           // root 2
        Device device4 = createDevice("AA:BB:CC:DD:EE:04", "AA:BB:CC:DD:EE:03");
        Device device5 = createDevice("AA:BB:CC:DD:EE:05", "AA:BB:CC:DD:EE:03");

        List<Device> devices = List.of(device1, device2, device3, device4, device5);

        // when
        List<DeviceNode> roots = TopologyUtil.prepareDeviceTopology(devices);

        // then
        assertThat(roots).hasSize(2);
        assertThat(roots).extracting(DeviceNode::getMacAddress)
                .containsExactlyInAnyOrder("AA:BB:CC:DD:EE:01", "AA:BB:CC:DD:EE:03");

        DeviceNode root01 = roots.stream()
                .filter(r -> r.getMacAddress().equals("AA:BB:CC:DD:EE:01"))
                .findFirst()
                .orElseThrow();
        DeviceNode root03 = roots.stream()
                .filter(r -> r.getMacAddress().equals("AA:BB:CC:DD:EE:03"))
                .findFirst()
                .orElseThrow();

        assertThat(root01.getLinkedDevices())
                .extracting(DeviceNode::getMacAddress)
                .containsExactly("AA:BB:CC:DD:EE:02");

        assertThat(root03.getLinkedDevices())
                .extracting(DeviceNode::getMacAddress)
                .containsExactlyInAnyOrder("AA:BB:CC:DD:EE:04", "AA:BB:CC:DD:EE:05");
    }

    @Test
    void prepareDeviceTopology_shouldReturnAllDevicesAsRootsIfNoUplinks() {
        // given
        List<Device> devices = List.of(
                createDevice("AA:BB:CC:DD:EE:01", null),
                createDevice("AA:BB:CC:DD:EE:02", null),
                createDevice("AA:BB:CC:DD:EE:03", null)
        );

        // when
        List<DeviceNode> roots = TopologyUtil.prepareDeviceTopology(devices);

        // then
        assertThat(roots).hasSize(3);
        assertThat(roots).extracting(DeviceNode::getMacAddress)
                .containsExactlyInAnyOrder(
                        "AA:BB:CC:DD:EE:01",
                        "AA:BB:CC:DD:EE:02",
                        "AA:BB:CC:DD:EE:03"
                );
    }

    @Test
    void prepareSubDeviceTopology_shouldReturnNullIfListEmpty() {
        // when
        DeviceNode root = TopologyUtil.prepareSubDeviceTopology("AA:BB:CC:DD:EE:01", List.of());

        // then
        assertThat(root).isNull();
    }

    @Test
    void prepareDeviceTopology_shouldReturnEmptyListIfInputEmpty() {
        // when
        List<DeviceNode> roots = TopologyUtil.prepareDeviceTopology(List.of());

        // then
        assertThat(roots).isEmpty();
    }

    @Test
    void prepareSubDeviceTopology_shouldReturnNullIfRootNotPresent() {
        // given
        Device device = createDevice("AA:BB:CC:DD:EE:02", null);

        List<Device> devices = List.of(device);

        // when
        DeviceNode root = TopologyUtil.prepareSubDeviceTopology("AA:BB:CC:DD:EE:99", devices);

        // then
        assertThat(root).isNull();
    }

    @Test
    void checkUplinkMacAddressExistence_shouldNotThrowWhenUplinkMacExists() {
        Device root = createDevice(GATEWAY, "AA:BB:CC:DD:EE:01", null);
        Device newDevice = createDevice(SWITCH, "AA:BB:CC:DD:EE:02", "AA:BB:CC:DD:EE:01");

        assertDoesNotThrow(() -> TopologyUtil.checkUplinkMacAddressExistence(newDevice, List.of(root)));
    }

    @Test
    void checkUplinkMacAddressExistence_shouldThrowWhenUplinkMacCheckNotExistence() {
        Device newDevice = createDevice(SWITCH, "AA:BB:CC:DD:EE:02", "AA:BB:CC:DD:EE:03");

        DeviceInventoryException ex = assertThrows(DeviceInventoryException.class, () ->
                TopologyUtil.checkUplinkMacAddressExistence(newDevice, List.of())
        );

        assertEquals("A Device with MAC address AA:BB:CC:DD:EE:03 does not exist.", ex.getMessage());
    }

    @Test
    void checkMacAddressUniqueness_shouldNotThrowWhenMacCheckUniqueness() {
        Device existing = createDevice(GATEWAY, "AA:BB:CC:DD:EE:01", null);
        Device newDevice = createDevice(SWITCH, "AA:BB:CC:DD:EE:02", "AA:BB:CC:DD:EE:01");

        assertDoesNotThrow(() -> TopologyUtil.checkMacAddressUniqueness(newDevice, List.of(existing)));
    }

    @Test
    void checkMacAddressUniqueness_shouldThrowWhenMacAlreadyExists() {
        Device existing = createDevice(GATEWAY, "AA:BB:CC:DD:EE:01", null);
        Device newDevice = createDevice(SWITCH, "AA:BB:CC:DD:EE:01", "AA:BB:CC:DD:EE:02");

        DeviceInventoryException ex = assertThrows(DeviceInventoryException.class, () ->
                TopologyUtil.checkMacAddressUniqueness(newDevice, List.of(existing))
        );

        assertEquals("A Device with MAC address AA:BB:CC:DD:EE:01 already exists.", ex.getMessage());
    }

    @Test
    void validateNoCycles_shouldNotThrowWhenTopologyIsAcyclic() {
        List<Device> devices = List.of(
                createDevice("AA:BB:CC:00:00:01", "AA:BB:CC:00:00:02"),
                createDevice("AA:BB:CC:00:00:02", "AA:BB:CC:00:00:03"),
                createDevice("AA:BB:CC:00:00:03", null)   // brak cyklu
        );

        assertDoesNotThrow(() -> TopologyUtil.validateNoCycles(devices));
    }

    @Test
    void validateNoCycles_shouldThrowWhenSimpleCycleExists() {
        List<Device> devices = List.of(
                createDevice("AA:BB:CC:00:00:01", "AA:BB:CC:00:00:02"),
                createDevice("AA:BB:CC:00:00:02", "AA:BB:CC:00:00:01")   // cykl 2-elementowy
        );

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> TopologyUtil.validateNoCycles(devices)
        );

        assertTrue(ex.getMessage().contains("AA:BB:CC:00:00:01"));
        assertTrue(ex.getMessage().contains("AA:BB:CC:00:00:02"));
    }

    @Test
    void validateNoCycles_shouldThrowWhenLongerCycleExists() {
        List<Device> devices = List.of(
                createDevice("AA:BB:CC:00:00:01", "AA:BB:CC:00:00:02"),
                createDevice("AA:BB:CC:00:00:02", "AA:BB:CC:00:00:03"),
                createDevice("AA:BB:CC:00:00:03", "AA:BB:CC:00:00:01")   // cykl 3-elementowy
        );

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> TopologyUtil.validateNoCycles(devices)
        );

        assertTrue(ex.getMessage().contains("AA:BB:CC:00:00:01"));
        assertTrue(ex.getMessage().contains("AA:BB:CC:00:00:02"));
        assertTrue(ex.getMessage().contains("AA:BB:CC:00:00:03"));
    }

    @Test
    void validateNoCycles_shouldNotThrowWhenMultipleRoots() {
        List<Device> devices = List.of(
                createDevice("AA:BB:CC:00:00:01", null),
                createDevice("AA:BB:CC:00:00:02", null),
                createDevice("AA:BB:CC:00:00:03", "AA:BB:CC:00:00:01"),
                createDevice("AA:BB:CC:00:00:04", "AA:BB:CC:00:00:02")
        );

        assertDoesNotThrow(() -> TopologyUtil.validateNoCycles(devices));
    }

    @Test
    void validateNoCycles_shouldIgnoreUnknownUplinkDevices() {
        List<Device> devices = List.of(
                createDevice("AA:BB:CC:00:00:01", "FF:EE:DD:CC:BB:AA"), // nieistniejący uplink
                createDevice("AA:BB:CC:00:00:02", "AA:BB:CC:00:00:01")
        );

        assertDoesNotThrow(() -> TopologyUtil.validateNoCycles(devices));
    }

    @Test
    void validateNoCycles_shouldThrowOnSelfLoop() {
        List<Device> devices = List.of(
                createDevice("AA:BB:CC:00:00:01", "AA:BB:CC:00:00:01")  // self-loop
        );

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> TopologyUtil.validateNoCycles(devices)
        );

        assertTrue(ex.getMessage().contains("AA:BB:CC:00:00:01"));
    }

    @Test
    void validateNoCycles_shouldNotThrowOnEmptyList() {
        List<Device> devices = List.of();

        assertDoesNotThrow(() ->
                TopologyUtil.validateNoCycles(devices)
        );
    }

    @Test
    void checkUplinkConnection_ThrowsException_WhenUplinkIsAccessPoint() {
        // given
        Device accessPoint = createDevice(DeviceType.ACCESS_POINT, "AA:BB:CC:DD:EE:01", null);
        Device device = createDevice(DeviceType.SWITCH, "AA:BB:CC:DD:EE:02", "AA:BB:CC:DD:EE:01");

        List<Device> devices = List.of(accessPoint);

        // when & then
        DeviceInventoryException exception = assertThrows(DeviceInventoryException.class, () ->
                TopologyUtil.checkUplinkConnection(device, devices));

        assertEquals("An Access Point is supposed to connect wireless Devices.", exception.getMessage());
    }

    @Test
    void checkUplinkConnection_DoesNotThrow_WhenUplinkIsNotAccessPoint() {
        // given
        Device switchDevice = createDevice(DeviceType.SWITCH, "AA:BB:CC:DD:EE:03", null);
        Device device = createDevice(DeviceType.SWITCH, "AA:BB:CC:DD:EE:04", "AA:BB:CC:DD:EE:03");

        List<Device> devices = List.of(switchDevice);

        // when & then
        assertDoesNotThrow(() -> TopologyUtil.checkUplinkConnection(device, devices));
    }

    @Test
    void checkUplinkConnection_DoesNotThrow_WhenNoMatchingUplink() {
        // given
        Device accessPoint = createDevice(DeviceType.ACCESS_POINT, "AA:BB:CC:DD:EE:05", null);
        Device device = createDevice(DeviceType.SWITCH, "AA:BB:CC:DD:EE:06", "AA:BB:CC:DD:EE:FF");

        List<Device> devices = List.of(accessPoint);

        // when & then
        assertDoesNotThrow(() -> TopologyUtil.checkUplinkConnection(device, devices));
    }
}
