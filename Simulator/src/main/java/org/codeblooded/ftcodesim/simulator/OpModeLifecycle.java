package org.codeblooded.ftcodesim.simulator;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import org.codeblooded.ftcodesim.hardware.SimHardwareMap;
import org.codeblooded.ftcodesim.hardware.devices.SimTelemetry;
import org.psilynx.psikit.core.Logger;
import org.psilynx.psikit.ftc.FtcLoggingSession;

import java.lang.reflect.Field;

public class OpModeLifecycle {
    volatile boolean isStarted = false;
    volatile boolean isStopped = false;
    OpMode opMode;
    SimHardwareMap simHardwareMap;
    SimTelemetry telemetry;
    long loopTimeMs;
    volatile byte[] latestGamepad1Data = new Gamepad().toByteArray();
    volatile byte[] latestGamepad2Data = new Gamepad().toByteArray();

    public OpModeLifecycle(OpMode opMode, SimTelemetry telemetry, SimHardwareMap simHardwareMap, long loopTimeMs) {
        this.opMode = opMode;
        this.simHardwareMap = simHardwareMap;
        this.loopTimeMs = loopTimeMs;
        this.telemetry = telemetry;
    }

    private void internalPreUserCode() {
        opMode.time = opMode.getRuntime();
        opMode.gamepad1.fromByteArray(latestGamepad1Data);
        opMode.gamepad2.fromByteArray(latestGamepad2Data);
    }

    private void internalPostUserCode() {
        //opMode.telemetry.update();
    }

    public void runOpMode() throws InterruptedException {
        opMode.telemetry = telemetry;
        opMode.hardwareMap = simHardwareMap;
        opMode.gamepad1 = new Gamepad();
        opMode.gamepad2 = new Gamepad();

        FtcLoggingSession ftcLog = new FtcLoggingSession();
        ftcLog.start(opMode, 5800, "", true, "sim-logs", null, opMode);

        long start = System.nanoTime();
        Logger.setTimeSource(() -> (System.nanoTime() - start) * 1e-9);

        opMode.init();

        while (!isStarted && !isStopped) {
            wrap(opMode::init_loop, opMode, ftcLog);
        }

        opMode.start();

        while (!isStopped) {
            wrap(opMode::loop, opMode, ftcLog);
        }

        opMode.stop();

        Logger.end();
    }

    public void wrap(Runnable runnable, OpMode opMode, FtcLoggingSession ftcLog) throws InterruptedException {
        internalPreUserCode();

        simHardwareMap.update();

        Logger.periodicBeforeUser();
        ftcLog.logOncePerLoop(opMode);

        runnable.run();

        Logger.periodicAfterUser(0,0);
        Thread.sleep(loopTimeMs);

        internalPostUserCode();
    }

    private void setBooleanFieldIfPresent(Object target, String fieldName, boolean value) {
        Class<?> clazz = target.getClass();

        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.setBoolean(target, value);
                return;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (Throwable e) {
                return;
            }
        }

    }

    public void setStarted(boolean started) {
        setBooleanFieldIfPresent(opMode, "isStarted", started);
        isStarted = started;
    }

    public void setStopped(boolean stopped) {
        setBooleanFieldIfPresent(opMode, "stopRequested", stopped);
        isStopped = stopped;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public boolean isStopped() {
        return isStopped;
    }
}
