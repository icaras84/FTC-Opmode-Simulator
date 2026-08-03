package org.codeblooded.ftcodesim.driverstation.client;

public class OpModeTimer {
    private final static double NANO_TO_MILLISECONDS = 1e-6;

    private long startTime;

    private long elapsedMs;
    private boolean stopped;

    private long displayMs;
    private long displaySeconds;
    private long displayMinutes;

    public OpModeTimer() {
        this.stopped = true;
        this.startTime = 0;
        this.elapsedMs = 0;
        this.displayMs = 0;
        this.displaySeconds = 0;
        this.displayMinutes = 0;
    }

    public void start(){
        this.stopped = false;
        this.startTime = System.nanoTime();
    }

    public void update(){
        if (this.stopped) return;
        long deltaNano = System.nanoTime() - this.startTime;

        this.elapsedMs = (long) (deltaNano * NANO_TO_MILLISECONDS);
        this.displayMs = this.elapsedMs % 1000;
        this.displaySeconds = (this.elapsedMs / 1000) % 60;
        this.displayMinutes = (this.elapsedMs / 1000) / 60;
    }

    public void stop(){
        this.stopped = true;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getElapsedMs() {
        return elapsedMs;
    }

    public long getDisplayMs() {
        return displayMs;
    }

    public long getDisplaySeconds() {
        return displaySeconds;
    }

    public long getDisplayMinutes() {
        return displayMinutes;
    }
}
