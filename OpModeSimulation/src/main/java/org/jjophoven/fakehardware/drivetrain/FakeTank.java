package org.jjophoven.fakehardware.drivetrain;

import org.jjophoven.fakehardware.FakeMotor;

public class FakeTank extends SimulatedDrivetrain {
    private static final int FL = 0;
    private static final int FR = 1;
    private static final int BL = 2;
    private static final int BR = 3;

    private final double trackWidth;
    private final double wheelRadius;

    public FakeTank(FakeMotor[] motors,
                    double trackWidth,
                    double wheelDiameter) {
        super(motors);

        this.trackWidth = trackWidth;
        this.wheelRadius = wheelDiameter / 2;
    }

    @Override
    MotionVector forwardKinematics(double[] motors) {
        double fl = motors[FL] * wheelRadius;
        double fr = motors[FR] * wheelRadius;
        double bl = motors[BL] * wheelRadius;
        double br = motors[BR] * wheelRadius;

        double left = (fl + bl) / 2.0;
        double right = (fr + br) / 2.0;

        return new MotionVector(
                (left + right) / 2.0,
                0.0,
                (left - right) / trackWidth
        );
    }

    @Override
    double[] inverseKinematics(MotionVector motion) {
        double left = motion.x + motion.theta * trackWidth / 2.0;
        double right = motion.x - motion.theta * trackWidth / 2.0;

        return new double[]{
                left / wheelRadius,
                right / wheelRadius,
                left / wheelRadius,
                right / wheelRadius
        };
    }
}