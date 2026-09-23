package org.codeblooded.ftcodesim.driverstation.controller;

import org.codeblooded.ftcodesim.driverstation.connection.packets.InitOpModePacket;
import org.codeblooded.ftcodesim.driverstation.model.DSOpModeListManager;

import java.util.Vector;

public class DSOpModeListController {

    public interface UIMethods{
        void hookController(DSOpModeListController controller);
        void onListPopulation(Vector<InitOpModePacket> availableOpModes);
        void onOpModeSelect(InitOpModePacket opMode);
        void onOpModeDeselect();
    }

    private DSOpModeListManager model;
    private UIMethods ui;



    public DSOpModeListManager getModel() {
        return model;
    }

    public DSOpModeListController.UIMethods getUI(){
        return this.ui;
    }
}
