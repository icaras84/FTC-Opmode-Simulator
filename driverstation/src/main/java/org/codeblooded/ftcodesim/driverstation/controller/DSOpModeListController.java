package org.codeblooded.ftcodesim.driverstation.controller;

import org.codeblooded.ftcodesim.driverstation.connection.packets.InitOpModePacket;
import org.codeblooded.ftcodesim.driverstation.connection.packets.OpModesPacket;
import org.codeblooded.ftcodesim.driverstation.misc.Publisher;
import org.codeblooded.ftcodesim.driverstation.model.DSOpModeListManager;

import java.util.Optional;
import java.util.Vector;
import java.util.function.Consumer;

public class DSOpModeListController {

    public interface UIMethods{
        void hookController(DSOpModeListController controller);
        void onListPopulation(Vector<InitOpModePacket> availableOpModes);
        void onOpModeSelect(InitOpModePacket opMode);
        void onOpModeDeselect();
    }

    private final DSOpModeListManager model;
    private final UIMethods ui;
    private final Publisher<Optional<InitOpModePacket>> selectionListeners;

    public DSOpModeListController(DSOpModeListManager model, UIMethods ui, Consumer<Optional<InitOpModePacket>> selectionListener) {
        this.model = model;
        this.ui = ui;
        this.ui.hookController(this);
        this.selectionListeners = Publisher.empty();
    }

    public void registerOpModes(OpModesPacket packet){
        if (packet == null || packet.opmodes.isEmpty()) return;

        this.model.registerOpModes(packet);
        this.ui.onListPopulation(this.model.getAvailableOpModes());
    }

    public void searchAndSelect(InitOpModePacket requestedOpMode){
        this.model.searchAndServe(requestedOpMode, this::onValidOpMode, this::deselectOpMode);
    }

    public void searchAndSelect(int idx){
        this.model.searchAndServe(idx, this::onValidOpMode, this::deselectOpMode);
    }

    public void searchAndSelect(String group, String name){
        this.model.searchAndServe(group, name, this::onValidOpMode, this::deselectOpMode);
    }

    private void onValidOpMode(InitOpModePacket opmode){
        this.model.setSelectedOpMode(opmode);
        this.ui.onOpModeSelect(opmode);

        this.selectionListeners.updateAll(Optional.of(opmode));
    }

    public void deselectOpMode(){
        this.model.setSelectedOpMode(null);
        this.ui.onOpModeDeselect();

        this.selectionListeners.updateAll(Optional.empty());
    }

    public Publisher<Optional<InitOpModePacket>> getSelectionListeners() {
        return this.selectionListeners;
    }

    public DSOpModeListManager getModel() {
        return this.model;
    }

    public DSOpModeListController.UIMethods getUI(){
        return this.ui;
    }
}
