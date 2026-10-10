package org.codeblooded.ftcodesim.hardware.drivetrain;


import org.codeblooded.fit.MotorModel;
import org.codeblooded.ftcodesim.ascope.boundaries.FieldBoundary;
import org.codeblooded.ftcodesim.ascope.boundaries.MotionVector;
import org.codeblooded.ftcodesim.hardware.devices.SimMotor;
import org.psilynx.psikit.core.Logger;

public class SimulatedMecanum extends SimulatedDrivetrain {
    private static final int FL = 0;
    private static final int FR = 1;
    private static final int BL = 2;
    private static final int BR = 3;

    private final double R;
    private final SimMecanumConfig config;

    public SimulatedMecanum(SimMecanumConfig config) {
        super(config, config.frontLeftMotorName, config.frontRightMotorName, config.backLeftMotorName, config.backRightMotorName);

        R = config.wheelbase / 2 + config.trackWidth / 2;
        this.config = config;
    }

    public static double interpolateRadius(double xRadius, double yRadius, double theta) {
        return 1.0 / (Math.abs(Math.cos(theta)) / xRadius + Math.abs(Math.sin(theta)) / yRadius);
    }

    public static double interpolateAcceleration(double xRadius, double yRadius, double theta) {
        double cos = Math.abs(Math.cos(theta));
        double cos3 = cos * cos * cos;
        double sin = Math.abs(Math.sin(theta));
        double sin3 = sin * sin * sin;
        return 1.0 / (Math.abs(cos3) / xRadius + Math.abs(sin3) / yRadius);
    }

    @Override
    public void update(double deltaTime) {
        for (int i = 0; i < motors.length; i++) {
            SimMotor motor = motors[i];
            motorAngularAccelerations[i] = motor.getAcceleration();
            motorPowers[i] = motor.getPower();

            Logger.recordOutput("Mecanum/wheel/angular vels radians per second/" + motor.deviceName, motor.getVelocity());
            Logger.recordOutput("Mecanum/wheel/powers/" + motor.deviceName, motor.getPower());
            Logger.recordOutput("Mecanum/wheel/angular accelerations radians per second per second/" + motor.deviceName, motor.getAcceleration());
        }

        MotionVector robotVel = velocity.toRobotFrame(position.theta);
        MotionVector robotAccel = forwardKinematics(motorAngularAccelerations);
        MotionVector motorEffort = powerKinematics(motorPowers);

        robotVel.logV("Mecanum/robot/rawVelocity inches per second");
        robotAccel.logV("Mecanum/robot/rawAcceleration inches per second squared");

//        // TODO add lag button to distrupt path or apply forces

        double kCoulombFriction3 = config.turnNaturalDeceleration;
        double backEMF2 = config.maxTurnAccel / config.maxAngularVelocity;
        double kA2 = (backEMF2 * (config.maxAngularVelocity) + kCoulombFriction3) / config.nominalVoltage;

        // does not account for when motors are randomly opposing each-other, does not account for slippage

        double voltage = voltageSensor.getVoltage();

        double direction = Math.atan2(robotVel.y, robotVel.x);
        double maxVelocity = interpolateRadius(config.foresightConfig.maxAchievableForwardVelocity.get(), config.foresightConfig.maxAchievableStrafeVelocity.get(), direction);
        Logger.recordOutput("maxVelocity", maxVelocity);
        double decel = interpolateAcceleration(config.foresightConfig.naturalForwardDeceleration.get(), config.foresightConfig.naturalStrafeDeceleration.get(), direction);
        double backEMF = config.maxAcceleration / maxVelocity;
        double kA = (backEMF * (maxVelocity) + decel) / config.nominalVoltage;

        robotAccel = new MotionVector(
                kA * voltage * motorEffort.x - backEMF * Math.abs(motorEffort.x) * robotVel.x,
                kA * voltage * motorEffort.y - backEMF * Math.abs(motorEffort.y) * robotVel.y,
                kA2 * voltage * motorEffort.theta - backEMF2 * Math.abs(motorEffort.theta) * robotVel.theta
        );

        boolean isStationary =
                Math.abs(robotVel.x) <= config.staticVelocityRegion
                        && Math.abs(robotVel.y) <= config.staticVelocityRegion
                        && Math.abs(robotVel.theta) <= 0.1
                        && Math.abs(robotAccel.x) <= config.foresightConfig.naturalForwardDeceleration.get() * 1.1
                        && Math.abs(robotAccel.y) <= config.foresightConfig.naturalStrafeDeceleration.get() * 1.1
                        && Math.abs(robotAccel.theta) <= config.turnNaturalDeceleration * 1.1;

        if (isStationary) {
            robotVel = new MotionVector(0, 0, 0);
            robotAccel = new MotionVector(0, 0, 0);
        } else {
            robotAccel.x -= decel * Math.signum(robotVel.x);
            robotAccel.y -= decel * Math.signum(robotVel.y);
            robotAccel.theta -= config.turnNaturalDeceleration * Math.signum(robotVel.theta);

            if (Math.signum(robotVel.x) != Math.signum(robotAccel.x) || robotAccel.x == 0)
                robotAccel.x -= robotVel.x / (2.0 * config.foresightConfig.quadraticBrakeCoefficients.get().get(0,0) * Math.abs(robotVel.x) + config.foresightConfig.linearBrakeCoefficients.get().get(0,0)) - config.foresightConfig.naturalForwardDeceleration.get() * Math.signum(robotVel.x);

            if (Math.signum(robotVel.y) != Math.signum(robotAccel.y) || robotAccel.y == 0)
                robotAccel.y -= robotVel.y / (2.0 * config.foresightConfig.quadraticBrakeCoefficients.get().get(1,1) * Math.abs(robotVel.y) + config.foresightConfig.linearBrakeCoefficients.get().get(1,1)) - config.foresightConfig.naturalStrafeDeceleration.get() * Math.signum(robotVel.y);;

            if (Math.signum(robotVel.theta) != Math.signum(robotAccel.theta) || robotAccel.theta == 0) {
                double theta = robotVel.theta;

                double A = config.foresightConfig.headingBrakeCoefficients.get().x();
                double B = config.foresightConfig.headingBrakeCoefficients.get().y();

                double angularAcceleration =
                        -theta / (2.0 * A * Math.abs(theta) + B);
                robotAccel.theta += angularAcceleration + config.turnNaturalDeceleration * Math.signum(robotVel.theta);
            }
        }

        robotVel.logV("Mecanum/robot/velocity inches per second");
        robotAccel.logV("Mecanum/robot/acceleration inches per second squared");
        Logger.recordOutput("Mecanum/isStationary", isStationary);

        robotVel = robotVel.step(robotAccel, deltaTime);

        velocity = robotVel.toFieldFrame(position.theta);

        MotionVector legalPosition = position;
        position = position.step(velocity, deltaTime);

        boolean isOutOfBounds = FieldBoundary.isOutOfBounds(position, config.robotGeometry);
        if (isOutOfBounds) {
            MotionVector normal = FieldBoundary.collisionNormal(
                    position,
                    config.robotGeometry
            );

            double vNormal = velocity.dot(normal);

            if (vNormal > 0) {
                velocity = velocity.minus(normal.scale(vNormal));
            }

            double aNormal = acceleration.dot(normal);

            if (aNormal > 0) {
                acceleration = acceleration.minus(normal.scale(aNormal));
            }

            double correctionSign = Math.signum(legalPosition.theta - position.theta);

            if (Math.signum(velocity.theta) != correctionSign) {
                velocity.theta = 0;
            }

            if (Math.signum(acceleration.theta) != correctionSign) {
                acceleration.theta = 0;
            }

            position = legalPosition.step(velocity, deltaTime);
        }

        motorAngularVelocities = inverseKinematics(velocity.toRobotFrame(position.theta));
        for (int i = 0; i < motors.length; i++) {
            motors[i].velocity = motorAngularVelocities[i];
        }

        robotAccel.toFieldFrame(position.theta).logV("Mecanum/acceleration");
        velocity.logV("Mecanum/velocity");
        position.log("Mecanum/position", config.robotModel);
        Logger.recordOutput("isInBounds", !isOutOfBounds);
    }

    @Override
    MotionVector forwardKinematics(double[] motors) {
        double fl = motors[FL] * config.wheelRadius;
        double fr = motors[FR] * config.wheelRadius;
        double bl = motors[BL] * config.wheelRadius;
        double br = motors[BR] * config.wheelRadius;

        double x = (fl + fr + bl + br) / 4.0;
        double y = (-fl + fr + bl - br) / 4.0;
        double theta = (-fl + fr - bl + br) / (4.0 * R);

        return new MotionVector(x, y, theta);
    }

    MotionVector powerKinematics(double[] motors) {
        double fl = motors[FL];
        double fr = motors[FR];
        double bl = motors[BL];
        double br = motors[BR];

        double x = (fl + fr + bl + br) / 4.0;
        double y = (-fl + fr + bl - br) / 4.0;
        double theta = (-fl + fr - bl + br) / 4.0; // no R bc this is effort

        return new MotionVector(x, y, theta);
    }

    @Override
    double[] inverseKinematics(MotionVector motion) {
        double x = motion.x;
        double y = motion.y;

        return new double[]{
                (x - y - motion.theta * R) / config.wheelRadius, // FL
                (x + y + motion.theta * R) / config.wheelRadius, // FR
                (x + y - motion.theta * R) / config.wheelRadius, // BL
                (x - y + motion.theta * R) / config.wheelRadius  // BR
        };
    }
}