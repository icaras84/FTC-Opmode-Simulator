package org.codeblooded.ftcodesim.driverstation.controller;

import org.codeblooded.ftcodesim.driverstation.connection.packets.InitOpModePacket;
import org.codeblooded.ftcodesim.driverstation.model.DSOpModeManager;
import org.codeblooded.ftcodesim.driverstation.model.OpModeState;

public class DSOpModeController {

    public interface UIMethods {
        void hookController(DSOpModeController controller);
        void onOpModeLoad(InitOpModePacket loadedOpMode);
        void onOpModeUnload();
        void onOpModeStateChange(OpModeState nState);
        void onOpModeTimerChange(long elapsedNano);
    }

    private final DSOpModeManager model;
    private final UIMethods ui;

    public DSOpModeController(DSOpModeManager model, UIMethods ui) {
        this.model = model;
        this.ui = ui;
        this.ui.hookController(this);
    }

    public DSOpModeController(UIMethods ui){
        this(new DSOpModeManager(), ui);
    }

    public void attemptOpModeLoad(InitOpModePacket opMode){
        this.model.attemptLoad(opMode, () -> this.ui.onOpModeLoad(opMode), this.ui::onOpModeUnload);
    }

    public void unloadOpMode(){
        this.model.unload();
        this.ui.onOpModeUnload();
    }

    public void onMainButton(){
        this.model.onOpModeLoaded(() -> {
            switch (this.model.getOpModeState()){
                case WAIT_FOR_INIT:
                    this.model.initOpMode();
                    this.ui.onOpModeStateChange(OpModeState.INITIALIZING);
                    break;
                case INITIALIZING:
                    this.model.startOpMode();
                    this.ui.onOpModeStateChange(OpModeState.RUNNING);
                    break;
                case RUNNING:
                    this.model.stopOpMode();
                    this.ui.onOpModeStateChange(OpModeState.WAIT_FOR_INIT);
                    break;
            }
        }, () -> System.out.println("OpMode not loaded."));
    }

    public void onStopButton(){
        this.model.onOpModeLoaded(() -> {
            this.model.stopOpMode();
            this.ui.onOpModeStateChange(OpModeState.WAIT_FOR_INIT);
        }, () -> System.out.println("OpMode not loaded."));
    }

    public void periodicUpdate(){
        this.model.onOpModeLoaded(() -> this.ui.onOpModeTimerChange(this.model.getElapsedTimeNano()), () -> this.ui.onOpModeTimerChange(0));
    }

    public DSOpModeManager getModel() {
        return this.model;
    }

    public DSOpModeController.UIMethods getUI(){
        return this.ui;
    }
}
