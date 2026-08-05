package org.codeblooded.ftcodesim.driverstation.client;

import org.codeblooded.ftcodesim.driverstation.OpModeState;
import org.codeblooded.ftcodesim.driverstation.client.packets.InitOpModePacket;
import org.codeblooded.ftcodesim.driverstation.client.packets.OpModesPacket;
import org.codeblooded.ftcodesim.driverstation.client.packets.Packet;
import org.codeblooded.ftcodesim.driverstation.client.packets.TelemetryPacket;

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
        this.selectedOpMode = selectedOpMode;
        this.transitionOpModeState(OpModeState.WAIT_FOR_INIT);
    }

    public Optional<InitOpModePacket> getSelectedOpMode() {
        if (!hasSelectedOpMode()) return Optional.empty();
        return Optional.of(this.availableOpModes.get(this.selectedOpMode));
    }

    public OpModeState getOpModeState() {
        return this.opModeState;
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

    public void setTelemetryText(TelemetryPacket packet) {
        this.telemetryText = packet.telemetry;
    }

    public String getTelemetryText() {
        return this.telemetryText;
    }
}
