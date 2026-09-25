package org.codeblooded.ftcodesim.driverstation.controller;

import org.codeblooded.ftcodesim.driverstation.connection.packets.InitOpModePacket;
import org.codeblooded.ftcodesim.driverstation.connection.packets.OpModesPacket;
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
    private InitOpModePacket opmode;
    @SuppressWarnings("All") // misusing optionals as fields, but helpful for one-liners
    private Optional<Consumer<Optional<InitOpModePacket>>> selectionListener;

    public DSOpModeListController(DSOpModeListManager model, UIMethods ui, Consumer<Optional<InitOpModePacket>> selectionListener) {
        this.model = model;
        this.ui = ui;
        this.ui.hookController(this);
        this.opmode = null;
        this.selectionListener = Optional.ofNullable(selectionListener);
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
        this.ui.onOpModeSelect(opmode);
        this.opmode = opmode;

        this.selectionListener.ifPresent(listener -> listener.accept(this.getSelectedOpMode()));
    }

    public void deselectOpMode(){
        this.ui.onOpModeDeselect();
        this.opmode = null;

        this.selectionListener.ifPresent(listener -> listener.accept(this.getSelectedOpMode()));
    }

    public Optional<InitOpModePacket> getSelectedOpMode(){
        return Optional.ofNullable(this.opmode);
    }

    public void setSelectionListener(Consumer<Optional<InitOpModePacket>> nListener){
        this.selectionListener = Optional.ofNullable(nListener);
    }

    public Optional<Consumer<Optional<InitOpModePacket>>> getSelectionListener() {
        return selectionListener;
    }

    public DSOpModeListManager getModel() {
        return this.model;
    }

    public DSOpModeListController.UIMethods getUI(){
        return this.ui;
    }
}
