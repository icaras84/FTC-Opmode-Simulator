package org.codeblooded.ftcodesim.driverstation.model;

import org.codeblooded.ftcodesim.driverstation.connection.packets.InitOpModePacket;

import java.util.Optional;

/**
 * This class contains the logistics of managing a specific OpMode with the appropriate states contained in the object.
 * <br/>
 * The states stored are as follows:
 * <ul>
 *     <li>is the OpMode loaded</li>
 *     <li>status/phase of the OpMode</li>
 *     <li>start time</li>
 *     <li>elapsed time</li>
 * </ul>
 */
public class DSOpModeManager {
    private boolean opModeLoaded;
    private InitOpModePacket loadedOpMode;
    private OpModeState opModeState;
    private boolean opModeStopped;

    // timer
    private long startTimeNano;
    private long elapsedTimeNano;

    public DSOpModeManager(InitOpModePacket opMode){
        this.attemptLoad(opMode, () -> {}, this::unload);
    }

    public DSOpModeManager() {
        this.unload();
    }

    public void attemptLoad(InitOpModePacket opMode, Runnable loadSuccess, Runnable loadFail){
        if (opMode == null) {
            this.unload();
            loadFail.run();
        }

        this.loadedOpMode = opMode;
        this.opModeState = OpModeState.WAIT_FOR_INIT;
        this.opModeStopped = true;
        this.startTimeNano = 0;
        this.elapsedTimeNano = 0;

        loadSuccess.run();
    }

    public void unload(){
        this.opModeLoaded = false;
        this.loadedOpMode = null;
        this.opModeState = null;
        this.opModeStopped = true;
        this.startTimeNano = -1; // for debugging
        this.elapsedTimeNano = 0;
    }

    public void initOpMode(){
        if (!this.opModeLoaded) return; // abort early if opmode is not loaded

        // OpMode runs and exits initialization stage
        this.opModeStopped = false;
        this.opModeState = OpModeState.INITIALIZING;

        // reset stored times
        this.startTimeNano = 0;
        this.elapsedTimeNano = 0;
    }

    public void startOpMode(){
        if (!this.opModeLoaded) return; // same spiel as initOpMode()

        // OpMode starts and exits start stage, signaling that the OpMode loop is entered
        this.opModeStopped = false;
        this.opModeState = OpModeState.RUNNING;
    }

    public void stopOpMode(){
        if (!this.opModeLoaded) return; //...

        // OpMode stops, thus transitioning into waiting for the cue to be reinitialized (if needed)
        this.opModeStopped = true;
        this.opModeState = OpModeState.WAIT_FOR_INIT;
    }

    public void periodicUpdate(){
        // guard the timer updates
        if (this.opModeStopped || !this.opModeLoaded) return;

        this.elapsedTimeNano = System.nanoTime() - this.startTimeNano;
    }

    public void onOpModeLoaded(Runnable isLoaded, Runnable onElse){
        if (this.opModeLoaded){
            isLoaded.run();
        } else {
            if (onElse != null) onElse.run();
        }
    }

    public boolean isOpModeLoaded() {
        return this.opModeLoaded;
    }

    public InitOpModePacket getLoadedOpMode() {
        return this.loadedOpMode;
    }

    public OpModeState getOpModeState() {
        return this.opModeState;
    }

    public boolean isOpModeStopped() {
        return this.opModeStopped;
    }

    public long getStartTimeNano() {
        return this.startTimeNano;
    }

    public long getElapsedTimeNano() {
        return this.elapsedTimeNano;
    }

    @Override
    public String toString() {
        return "DSOpModeManager{" + "opModeLoaded=" + opModeLoaded +
                ", loadedOpMode=" + loadedOpMode +
                ", opModeState=" + opModeState +
                ", opModeStopped=" + opModeStopped +
                ", startTimeNano=" + startTimeNano +
                ", elapsedTimeNano=" + elapsedTimeNano +
                '}';
    }
}
