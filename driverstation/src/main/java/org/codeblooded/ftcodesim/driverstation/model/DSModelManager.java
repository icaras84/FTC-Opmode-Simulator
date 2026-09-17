package org.codeblooded.ftcodesim.driverstation.model;

import org.codeblooded.ftcodesim.driverstation.packets.OpModesPacket;

public class DSModelManager {
    private final DSOpModeListManager opModeListManager;
    private final DSOpModeManager opModeManager;

    public DSModelManager(){
        this.opModeListManager = new DSOpModeListManager();
        this.opModeManager = new DSOpModeManager();
    }

    public void periodicUpdate(){
        this.opModeManager.periodicUpdate();
    }

    public void populateOpModeList(OpModesPacket packet){

    }
}
