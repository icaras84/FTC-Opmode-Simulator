package org.codeblooded.ftcodesim.driverstation.client;

import org.codeblooded.ftcodesim.driverstation.client.packets.InitOpModePacket;
import org.codeblooded.ftcodesim.driverstation.client.packets.OpModesPacket;
import org.codeblooded.ftcodesim.driverstation.client.packets.Packet;
import org.codeblooded.ftcodesim.driverstation.client.packets.TelemetryPacket;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Optional;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class DSClient {

    // client socket variables
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    // thread management variables
    private Thread clientThread;
    private AtomicBoolean threadRunning;
    private ConcurrentLinkedDeque<Packet> packets;
    private Vector<Runnable> tasks;


    // callbacks
    private Consumer<TelemetryPacket> telemetryPacketConsumer;
    private Consumer<OpModesPacket> opModesPacketConsumer;


    public DSClient(Consumer<TelemetryPacket>  telemetryPacketConsumer, Consumer<OpModesPacket> opModesPacketConsumer) {
        this.socket = new Socket();
        this.input = null;
        this.output = null;

        this.clientThread = new Thread(this::clientLogic);
        this.threadRunning = new AtomicBoolean(false);
        this.packets = new ConcurrentLinkedDeque<>();
        this.tasks = new Vector<>();

        this.telemetryPacketConsumer = telemetryPacketConsumer == null ? p -> {} : telemetryPacketConsumer;
        this.opModesPacketConsumer = opModesPacketConsumer == null ? p -> {} : opModesPacketConsumer;
    }

    public boolean attemptConnection(String host, int port) {
        try {
            if (host == null) {
                this.socket.connect(new InetSocketAddress(InetAddress.getByName(null), port));
            } else {
                this.socket.connect(new InetSocketAddress(host, port));
            }

            this.input = new DataInputStream(this.socket.getInputStream());
            this.output = new DataOutputStream(this.socket.getOutputStream());
            return true;
        } catch (IOException e) {
            System.err.println("Could not connect to the server");
            return false;
        }
    }

    public void startThread() {
        this.threadRunning.set(true);
        this.clientThread.start();
    }

    private void clientLogic(){
        while (this.threadRunning.get()) {
            try {
                if (!this.socket.isClosed() && this.socket.isConnected()) {
                    this.pollPackets();
                }
            } catch (IOException e) {
                System.err.println("Could not read packets from the server");
            }
            this.tasks.forEach(Runnable::run);
            this.writePackets();
        }
    }

    private void pollPackets() throws IOException {
        byte type = this.input.readByte();
        switch (type) {
            case Packet.TELEMETRY:
                TelemetryPacket packet = TelemetryPacket.read(this.input);
                this.telemetryPacketConsumer.accept(packet);
                break;
            case Packet.INIT_OPMODE:
                OpModesPacket opModesPacket = OpModesPacket.read(this.input);
                System.out.print("RECEIVED OPMODES: ");
                for (InitOpModePacket info : opModesPacket.opmodes) {
                    System.out.print(", " + info.name);
                }
                System.out.println();
                this.opModesPacketConsumer.accept(opModesPacket);
                break;
        }
    }

    private void writePackets(){
        this.getOutputStream().ifPresent(server -> {
            DSClient.this.packets.forEach(packet -> {
                try {
                    server.writeByte(packet.getPacketType());
                    packet.write(server);
                } catch (IOException e) {
                    System.err.println("Could not write packet to the server");
                }
            });

            try {
                server.flush();
            } catch (IOException e) {
                System.err.println("Could not flush packets to the server");
            }
        });
    }

    public boolean queuePacket(Packet packet){
        return this.packets.add(packet);
    }

    public Socket getSocket() {
        return this.socket;
    }

    public Optional<DataInputStream> getInputStream() {
        return Optional.ofNullable(this.input);
    }
    public Optional<DataOutputStream> getOutputStream() {
        return Optional.ofNullable(this.output);
    }
}
