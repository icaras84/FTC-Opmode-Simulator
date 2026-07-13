package org.codeblooded.ftcodesim.hardware;

import androidx.annotation.Nullable;
import com.qualcomm.robotcore.hardware.*;
import org.codeblooded.ftcodesim.hardware.devices.*;
import org.codeblooded.ftcodesim.physics.FieldBoundary;
import org.codeblooded.ftcodesim.physics.MotionVector;
import org.codeblooded.ftcodesim.physics.RobotGeometry;
import org.codeblooded.ftcodesim.hardware.devices.*;
import org.codeblooded.ftcodesim.hardware.drivetrain.SimulatedDrivetrain;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.psilynx.psikit.core.Logger;

import java.util.List;

public class SimHardwareMap extends HardwareMap {
    private SimulatedDrivetrain drivetrain;
    private long previousTime = System.nanoTime();
    private MotionVector previousLegalPose = new MotionVector(0, 0, 0);
    double deltaTime;
    RobotGeometry robotGeometry;

    public SimulatedDrivetrain getDrivetrain() {
        return drivetrain;
    }

    public void setDrivetrain(SimulatedDrivetrain drivetrain) {
        this.drivetrain = drivetrain;
    }

    public SimHardwareMap(RobotGeometry robotGeometry) {
        super(null, null);
        this.robotGeometry = robotGeometry;

        SimVoltageSensor voltageSensor = new SimVoltageSensor();

        // TODO automatically do this for every device
        this.voltageSensor.put("voltageSensor", voltageSensor);

        put("voltageSensor", voltageSensor);
    }

    public @Nullable <T> T tryGet(Class<? extends T> classOrInterface, String deviceName) {
        synchronized (lock) {
            deviceName = deviceName.trim();
            List<HardwareDevice> list = allDevicesMap.get(deviceName);
            @Nullable T result = null;

            if (list != null) {
                for (HardwareDevice device : list) {
                    if (classOrInterface.isInstance(device)) {
                        rebuildDeviceNamesIfNecessary();
                        result = classOrInterface.cast(device);
                        break;
                    }
                }
            }
            if (result == null) {
                System.out.println("Could not find device " + deviceName + " of type " + classOrInterface.getSimpleName());
            }

            return result;
        }
    }

    public SimMotor motor(SimMotorConfig config) {
        return register(config.name, new SimMotor(config));
    }

    public SimGobildaPinpoint pinpoint(String name) {
        if (drivetrain == null) {
            throw new RuntimeException("Drivetrain not set");
        }
        return register(name, new SimGobildaPinpoint(drivetrain));
    }

    public <T extends HardwareDevice> T register(String name, T device) {
        System.out.println("Registering " + name + " as " + device.getClass().getSimpleName());
        put(name, device);
        return device;
    }

    public void update() {
        updateDeltaTime();

        for (List<HardwareDevice> device : allDevicesMap.values()) {
            for (HardwareDevice d : device) {
                try {
                    ((SimHardwareDevice) d).update(deltaTime);
                } catch (ClassCastException ignored) {
                }
            }
        }
        if (drivetrain != null) {
            drivetrain.step(deltaTime);
        }

        Pose2D pose = getDrivetrain().getActualPose();
        RobotGeometry robot = robotGeometry;
        MotionVector currentPose = MotionVector.fromPose2D(pose);
        boolean isOutOfBounds = FieldBoundary.isOutOfBounds(currentPose, robot);

        if (isOutOfBounds) {
            MotionVector closest = FieldBoundary.closestInBoundsPosition(previousLegalPose, currentPose, robot);

            MotionVector correctionDir = currentPose.minus(closest);

            if (correctionDir.magnitude() > 1e-6) {

                MotionVector normal = correctionDir.unitVector();

                MotionVector velocity = getDrivetrain().velocity;

                double vOut = velocity.dot(normal);

                MotionVector correctedVelocity = velocity.minus(normal.scale(vOut));

                getDrivetrain().setPosition(closest);
                getDrivetrain().setLinearVel(correctedVelocity);
            }
        }
        else {
            previousLegalPose = currentPose;
        }

        Logger.recordOutput("isInBounds", !isOutOfBounds);
        previousLegalPose.log("previousLegalPose");
    }

    private void updateDeltaTime() {
        long currentTime = System.nanoTime();
        deltaTime = (currentTime - previousTime) * 1e-9;
        previousTime = currentTime;
    }
}