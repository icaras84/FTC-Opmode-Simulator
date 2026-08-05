package org.codeblooded.ftcodesim.driverstation.client;

import org.codeblooded.ftcodesim.driverstation.OpModeState;
import org.codeblooded.ftcodesim.driverstation.client.packets.OpModeCommandPacket;
import org.codeblooded.ftcodesim.driverstation.client.packets.OpModesPacket;
import org.codeblooded.ftcodesim.driverstation.client.packets.TelemetryPacket;
import org.codeblooded.ftcodesim.driverstation.client.ui.DSView;

public class DSMVController {

    private final DSClient clientConnection;
    private final String host;
    private final int port;

    private final DSModel clientModel;
    private final DSView clientView;


    public DSMVController(String host, int port) {
        this.host = host;
        this.port = port;

        this.clientModel = new DSModel();
        this.clientView = new DSView();
        this.clientConnection = new DSClient(
                this.clientView::setConnectionStatus,
                this::onTelemetryReceived,
                this::onOpModeListReceived
        );

        this.clientView.attachController(this);
    }

    public DSMVController(int port){
        this("127.0.0.1", port);
    }

    private void onOpModeListReceived(OpModesPacket packet) {
        this.clientModel.makeAvailableOpModes(packet);
        this.clientView.fetchOpModes(this.clientModel);
    }

    private void onTelemetryReceived(TelemetryPacket packet) {
        this.clientModel.setTelemetryText(packet);
        this.clientView.fetchTelemetry(this.clientModel);
    }

    public void changeSelectedOpMode(int idx){
        this.clientModel.setSelectedOpMode(idx);
    }

    public void transitionOpModeState(OpModeState opModeState) {
        this.clientModel.transitionOpModeState(opModeState);
        switch (opModeState) {
            case WAIT_FOR_INIT:
                this.clientConnection.queuePacket(OpModeCommandPacket.STOP);
                break;
            case INITIALIZING:
                this.clientModel.getSelectedOpMode().ifPresent(this.clientConnection::queuePacket);
                break;
            case RUNNING:
                this.clientConnection.queuePacket(OpModeCommandPacket.START);
                break;
        }
        this.clientView.updateOpModeState(opModeState);
    }

    public void requestClose(){
        this.clientConnection.stopThread();
        System.exit(0);
    }
}
