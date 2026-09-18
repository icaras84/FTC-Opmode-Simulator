package org.codeblooded.ftcodesim.driverstation.connection;

import org.codeblooded.ftcodesim.driverstation.connection.packets.OpModesPacket;
import org.codeblooded.ftcodesim.driverstation.connection.packets.Packet;
import org.codeblooded.ftcodesim.driverstation.connection.packets.TelemetryPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Consumer;

public class DSConnection {

    private DataInputStream input;
    private DataOutputStream output;
    private Socket socket;
    @SuppressWarnings("all")
    private Optional<Consumer<TelemetryPacket>> telemetryCallback;
    @SuppressWarnings("all")

    private Optional<Consumer<Boolean>> connectedCallback;
    @SuppressWarnings("all")

    private Optional<Consumer<OpModesPacket>> opModeListCallback;

    private final LinkedBlockingDeque<Packet> packetBuffer;

    public DSConnection(Consumer<TelemetryPacket> telemetryCallback, Consumer<OpModesPacket> opModesCallback, Consumer<Boolean> connectionCallback){
        this.setTelemetryCallback(telemetryCallback);
        this.setOpModeListCallback(opModesCallback);
        this.setConnectedCallback(connectionCallback);
        this.socket = null;
        this.input = null;
        this.output = null;
        this.packetBuffer = new LinkedBlockingDeque<>();
    }

    public DSConnection(){
        this(null, null, null);
    }

    public void connect(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.input = new DataInputStream(this.socket.getInputStream());
        this.output = new DataOutputStream(this.socket.getOutputStream());
        this.connectedCallback.ifPresent(callback -> callback.accept(this.socket.isConnected()));
    }

    public void periodicUpdate(){
        try {
            byte type = input.readByte();
            switch (type) {
                case Packet.TELEMETRY:
                    TelemetryPacket telemetryPacket = TelemetryPacket.read(input);
                    this.telemetryCallback.ifPresent(callback -> callback.accept(telemetryPacket));
                    break;
                case Packet.INIT_OPMODE:
                    OpModesPacket opModesPacket = OpModesPacket.read(input);
                    this.opModeListCallback.ifPresent(callback -> callback.accept(opModesPacket));
                    break;
            }

            this.packetBuffer.forEach(packet -> {
                try {
                    packet.write(this.output);
                } catch (IOException ignored) {}
            });
            this.output.flush();
        } catch (Exception ignored) {}
    }

    public void queuePacket(Packet packet){
        this.packetBuffer.offer(packet);
    }

    public void closeConnection(){
        try {
            if (this.socket != null)
                this.socket.close();
        } catch (IOException ignored) {}

        this.connectedCallback.ifPresent(callback -> callback.accept(false));
    }

    public void setTelemetryCallback(Consumer<TelemetryPacket> callback){
        this.telemetryCallback = Optional.ofNullable(callback);
    }

    public void setOpModeListCallback(Consumer<OpModesPacket> callback){
        this.opModeListCallback = Optional.ofNullable(callback);
    }

    public void setConnectedCallback(Consumer<Boolean> callback){
        this.connectedCallback = Optional.ofNullable(callback);
    }
}
