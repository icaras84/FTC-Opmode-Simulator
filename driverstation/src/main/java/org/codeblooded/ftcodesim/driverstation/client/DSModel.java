package org.codeblooded.ftcodesim.driverstation.client;

import org.codeblooded.ftcodesim.driverstation.OpModeState;
import org.codeblooded.ftcodesim.driverstation.client.packets.InitOpModePacket;
import org.codeblooded.ftcodesim.driverstation.client.packets.OpModesPacket;
import org.codeblooded.ftcodesim.driverstation.client.packets.Packet;

import java.util.Optional;
import java.util.Vector;
import java.util.function.Consumer;

public class DSModel {
    private final Vector<InitOpModePacket> availableOpModes;
    private int selectedOpMode;
    private OpModeState opModeState;
    private final OpModeTimer opModeTimer;
    private String telemetryText;

    private Consumer<Packet> packetEndpoint;

    public DSModel() {
        this.availableOpModes = new Vector<>();
        this.selectedOpMode = -1;
        this.opModeState = null;
        this.opModeTimer = new OpModeTimer();
        this.telemetryText = "";
        this.packetEndpoint = p -> {};
    }

    public void makeAvailableOpModes(OpModesPacket opModesPacket) {
        this.availableOpModes.clear();
        this.availableOpModes.addAll(opModesPacket.opmodes);
        if (!this.availableOpModes.isEmpty()) this.selectedOpMode = 0;
    }

    public Vector<InitOpModePacket> getAvailableOpModes() {
        return this.availableOpModes;
    }

    public boolean hasSelectedOpMode() {
        return this.selectedOpMode > -1;
    }

    public void setSelectedOpMode(int selectedOpMode) {
        if (selectedOpMode < 0 || selectedOpMode >= this.availableOpModes.size()) {
            throw new IndexOutOfBoundsException("Selected opmode is out of bounds.");
        }
        this.selectedOpMode = selectedOpMode;
        this.transitionOpModeState(OpModeState.WAIT_FOR_INIT);
    }

    public Optional<InitOpModePacket> getSelectedOpMode() {
        if (!hasSelectedOpMode()) return Optional.empty();
        return Optional.ofNullable(this.availableOpModes.get(this.selectedOpMode));
    }

    public Optional<OpModeState> getOpModeState() {
        return Optional.ofNullable(this.opModeState);
    }

    public void transitionOpModeState(OpModeState opModeState) {
        this.opModeState = opModeState;
    }

    public OpModeTimer getOpModeTimer() {
        return this.opModeTimer;
    }

    public void periodicUpdate(){
        this.opModeTimer.update();
    }

    public void setTelemetryText(String telemetryText) {
        this.telemetryText = telemetryText;
    }

    public String getTelemetryText() {
        return this.telemetryText;
    }
}
