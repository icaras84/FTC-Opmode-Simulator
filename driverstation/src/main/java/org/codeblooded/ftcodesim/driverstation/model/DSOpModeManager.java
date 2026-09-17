package org.codeblooded.ftcodesim.driverstation.model;

import org.codeblooded.ftcodesim.driverstation.OpModeState;
import org.codeblooded.ftcodesim.driverstation.packets.InitOpModePacket;

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

    // this isn't what optionals are made for (they should be returned from methods, not stored as fields),
    // but enables shorter one-liners in methods that use these callbacks
    // convenient for getters too
    @SuppressWarnings("all")
    private Optional<Runnable> initOpModeCallback;
    @SuppressWarnings("all")

    private Optional<Runnable> startOpModeCallback;
    @SuppressWarnings("all")
    private Optional<Runnable> stopOpModeCallback;

    /**
     * Instances a manager object with no loaded OpMode
     * @param initOpModeCallback callback to run at OpMode initialization
     * @param startOpModeCallback callback to run at OpMode start
     * @param stopOpModeCallback callback to run if the OpMode is stopped
     */
    public DSOpModeManager(Runnable initOpModeCallback, Runnable startOpModeCallback, Runnable stopOpModeCallback) {
        this.setInitOpModeCallback(initOpModeCallback);
        this.setStartOpModeCallback(startOpModeCallback);
        this.setStopOpModeCallback(stopOpModeCallback);
        this.unload();
    }

    public DSOpModeManager(){
        // no callbacks
        this(null, null, null);
    }

    public boolean load(InitOpModePacket opMode){
        if (opMode == null)
            return this.opModeLoaded = false;

        this.loadedOpMode = opMode;
        this.opModeState = OpModeState.WAIT_FOR_INIT;
        this.opModeStopped = true;
        this.startTimeNano = 0;
        this.elapsedTimeNano = 0;

        return this.opModeLoaded = true;
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

        this.initOpModeCallback.ifPresent(Runnable::run);
    }

    public void startOpMode(){
        if (!this.opModeLoaded) return; // same spiel as initOpMode()

        // OpMode starts and exits start stage, signaling that the OpMode loop is entered
        this.opModeStopped = false;
        this.opModeState = OpModeState.RUNNING;

        this.startOpModeCallback.ifPresent(Runnable::run);
    }

    public void stopOpMode(){
        if (!this.opModeLoaded) return; //...

        // OpMode stops, thus transitioning into waiting for the cue to be reinitialized (if needed)
        this.opModeStopped = true;
        this.opModeState = OpModeState.WAIT_FOR_INIT;

        this.stopOpModeCallback.ifPresent(Runnable::run);
    }

    public void periodicUpdate(){
        // guard the timer updates
        if (this.opModeStopped || !this.opModeLoaded) return;

        this.elapsedTimeNano = System.nanoTime();
    }

    public Optional<Runnable> getInitOpModeCallback() {
        return this.initOpModeCallback;
    }

    public Optional<Runnable> getStartOpModeCallback() {
        return this.startOpModeCallback;
    }

    public Optional<Runnable> getStopOpModeCallback() {
        return this.stopOpModeCallback;
    }

    public void setInitOpModeCallback(Runnable initOpModeCallback){
        this.initOpModeCallback = Optional.ofNullable(initOpModeCallback);
    }

    public void setStartOpModeCallback(Runnable startOpModeCallback){
        this.startOpModeCallback = Optional.ofNullable(startOpModeCallback);
    }

    public void setStopOpModeCallback(Runnable stopOpModeCallback){
        this.stopOpModeCallback = Optional.ofNullable(stopOpModeCallback);
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
                ", initOpModeCallback=" + initOpModeCallback +
                ", startOpModeCallback=" + startOpModeCallback +
                ", stopOpModeCallback=" + stopOpModeCallback +
                '}';
    }
}
