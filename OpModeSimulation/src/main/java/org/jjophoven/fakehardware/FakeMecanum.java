package org.jjophoven.fakehardware;

import org.psilynx.psikit.core.Logger;
import org.psilynx.psikit.core.wpi.math.Pose2d;
import org.psilynx.psikit.core.wpi.math.Rotation2d;
import org.jjophoven.fit.MotorModel;

public class FakeMecanum {
    private final FakeMotor[] motors;

    private static final int FL = 0;
    private static final int BL = 1;
    private static final int FR = 2;
    private static final int BR = 3;

    private enum Wheel {
        FL, BL, FR, BR
    }

    public FakeMecanum(FakeMotor[] motors) {
        this.motors = motors;
    }

    double fieldX = 0;
    double fieldY = 0;
    public double heading = 0;

    double fieldXVel = 0;
    double fieldYVel = 0;
    double headingVel = 0;

    double[] motorPowers = new double[4];
    double[] motorAccelerations = new double[4];
    double[] motorVelocities = new double[4];

    public void update(double deltaTime) {
        MotorModel model = MotorModel.fromString("a=Au-Bv*abs(d)-Cv-Dsgn(v)");

        // maxVel, maxAccel, coast decel, static friction

        double accelK = 0.7;
        double dutyBackEMF = 1.5;
        double viscousFriction = 0.7;
        double coulombFriction = 3; // rolling friction?
        double staticFriction = 4.5; // accel in/s^2 must be > coulombFriction

        double staticVelocityRegion = 0.05;

        double[] coefficients = new double[]{
                accelK,
                dutyBackEMF,
                viscousFriction,
                coulombFriction
        };

        boolean allWheelsStationary = true;
        for (int i = 0; i < 4; i++) {
            double power = motors[i].getPower();
            double vel = motorVelocities[i];
            double accel = model.predict(coefficients, vel, power, 13);
            // TODO get voltage from sensor

            if (Math.abs(vel) < staticVelocityRegion && Math.abs(accel) < staticFriction) {
                accel = 0;
                vel = 0;
            } else {
                allWheelsStationary = false;
                accel = model.predict(coefficients, vel, power, 13);
                vel += accel * deltaTime;
            }

            motorPowers[i] = power;
            motorVelocities[i] = vel;
            motorAccelerations[i] = accel;

            String wheel = String.valueOf(Wheel.values()[i]);
            Logger.recordOutput("Mecanum/powers/" + wheel, power);
            Logger.recordOutput("Mecanum/vels/" + wheel, vel);
            Logger.recordOutput("Mecanum/accels/" + wheel, accel);
        }

        //    L = half the wheelbase (center to front/back wheel)
        //    W = half the track width (center to left/right wheel)
        // R = L + W
        double R = 0.2286 + 0.2286;

        double flAccel = motorAccelerations[FL];
        double frAccel = motorAccelerations[FR];
        double blAccel = motorAccelerations[BL];
        double brAccel = motorAccelerations[BR];

        double forwardAccel = (frAccel + flAccel + brAccel + blAccel) / 4;
        double strafeAccel = (flAccel - frAccel + brAccel - blAccel) / 4;
        double headingAccel = -(brAccel - blAccel + frAccel - flAccel) / (4 * R);

        double cos = Math.cos(heading);
        double sin = Math.sin(heading);
        double xAccel = forwardAccel * cos - strafeAccel * sin;
        double yAccel = forwardAccel * sin + strafeAccel * cos;

        fieldXVel += xAccel * deltaTime;
        fieldYVel += yAccel * deltaTime;
        headingVel += headingAccel * deltaTime;

        if (allWheelsStationary) {
            fieldXVel = 0;
            fieldYVel = 0;
            headingVel = 0;
        }

        fieldX += fieldXVel * deltaTime;
        fieldY += fieldYVel * deltaTime;
        heading += headingVel * deltaTime;

        double cos2 = Math.cos(-heading);
        double sin2 = Math.sin(-heading);
        double forwardVel = fieldXVel * cos2 - fieldYVel * sin2;
        double strafeVel = fieldXVel * sin2 + fieldYVel * cos2;

        motorVelocities[FL] = forwardVel + strafeVel + headingVel * R;
        motorVelocities[BL] = forwardVel - strafeVel + headingVel * R;
        motorVelocities[FR] = forwardVel - strafeVel - headingVel * R;
        motorVelocities[BR] = forwardVel + strafeVel - headingVel * R;

        Logger.recordOutput("Mecanum/fieldXVel", fieldXVel);
        Logger.recordOutput("Mecanum/fieldYVel", fieldYVel);
        Logger.recordOutput("Mecanum/headingVel", headingVel);

        Logger.recordOutput("Mecanum/pose inches",
                new Pose2d(fieldX, fieldY, new Rotation2d(heading)));
    }
}