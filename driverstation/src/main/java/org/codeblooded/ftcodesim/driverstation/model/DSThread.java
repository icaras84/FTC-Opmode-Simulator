package org.codeblooded.ftcodesim.driverstation.model;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class DSThread {

    private final Thread thread;
    private final AtomicBoolean running;
    private final Vector<Runnable> initialTasks;
    private final Vector<Runnable> periodicTasks;
    private final Vector<Runnable> closingTasks;

    public DSThread(){
        this.thread = new Thread(this::loop, "DriverStationMain");
        this.initialTasks = new Vector<>();
        this.periodicTasks = new Vector<>();
        this.closingTasks = new Vector<>();

        this.running = new AtomicBoolean(false);
    }

    private void loop(){
        this.initialTasks.forEach(Runnable::run);

        while (this.running.get()) {
            this.periodicTasks.forEach(Runnable::run);
        }

        this.closingTasks.forEach(Runnable::run);
    }

    public void start(){
        this.running.set(true);
        this.thread.start();
    }

    public void stop(){
        this.running.set(false);
        try {
            this.thread.join();
        } catch (InterruptedException ignored) {}
    }

    public boolean isRunning() {
        return this.running.get();
    }

    public Vector<Runnable> getInitialTasks() {
        return this.initialTasks;
    }

    public Vector<Runnable> getPeriodicTasks() {
        return this.periodicTasks;
    }

    public Vector<Runnable> getClosingTasks() {
        return this.closingTasks;
    }
}
