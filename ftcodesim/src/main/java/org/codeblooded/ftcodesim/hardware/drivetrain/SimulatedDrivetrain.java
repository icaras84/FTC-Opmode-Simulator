package org.codeblooded.ftcodesim.hardware.drivetrain;

import org.codeblooded.ftcodesim.ascope.boundaries.FieldBoundary;
import org.codeblooded.ftcodesim.ascope.boundaries.MotionVector;
import org.codeblooded.ftcodesim.ascope.boundaries.RobotGeometry;
import org.codeblooded.ftcodesim.hardware.SimHardwareMap;
import org.codeblooded.ftcodesim.hardware.devices.*;
import org.firstinspires.ftc.robotcore.external.navigation.*;
import org.codeblooded.fit.MotorModel;
import org.psilynx.psikit.core.Logger;

import static org.codeblooded.ftcodesim.hardware.drivetrain.SimulatedMecanum.interpolateRadius;

public abstract class SimulatedDrivetrain implements SimHardwareMechanism {
    protected final SimMotor[] motors;

    public MotionVector position = new MotionVector(72, 72, 0);
    public MotionVector velocity = new MotionVector(0, 0, 0);
    public MotionVector acceleration = new MotionVector(0, 0, 0);

    public SimDrivetrainConfig config;
    public String[] motorNames;
    public SimHardwareMap hardwareMap;
    public SimVoltageSensor voltageSensor;

    protected double[] motorAngularVelocities;
    protected double[] motorAngularAccelerations;
    double[] motorPowers;

    public SimulatedDrivetrain(SimDrivetrainConfig config, String... motorNames) {
        this.config = config;
        this.motors = new SimMotor[motorNames.length];
        this.motorNames = motorNames;

        motorAngularVelocities = new double[motors.length];
        motorAngularAccelerations = new double[motors.length];
        motorPowers = new double[motors.length];
    }

    public Pose2D getActualPose() {
        return new Pose2D(DistanceUnit.INCH, position.x(), position.y(), AngleUnit.RADIANS, position.theta());
    }

    public void registerDevices(SimHardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
        this.voltageSensor = (SimVoltageSensor) hardwareMap.voltageSensor.iterator().next();
        for (int i = 0; i < motorNames.length; i++) {
            motors[i] = hardwareMap.motor(SimMotorConfig.empty(motorNames[i]));
        }
        //position.log("Drivetrain/position", config.robotModel); gives null pointer err
    }

    public abstract void update(double deltaTime);

    @Override
    public void reset() {
        velocity = new MotionVector(0, 0, 0);
        position = new MotionVector(0, 0, 0);
    }

    public void setPosition(MotionVector position) {
        this.position = position;
    }

    public void updateWheelRollVelocities() {
        // Accounts for wheels moving from whole robot moving
        motorAngularVelocities = inverseKinematics(velocity.toRobotFrame(position.theta));
        for (int i = 0; i < motors.length; i++) {
            motors[i].setRollVelocity(motorAngularVelocities[i]);
        }
    }

    abstract MotionVector forwardKinematics(double[] motors);
    abstract double[] inverseKinematics(MotionVector motion);
}