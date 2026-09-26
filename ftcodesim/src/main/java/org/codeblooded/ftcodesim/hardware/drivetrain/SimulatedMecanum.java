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
    private final double wheelRadius;
    private final SimMecanumConfig config;

    private double strafeEfficiency;

    double forwardBrake;
    double strafeBrake;
    double headingBrake;

    BrakingModel forward;
    BrakingModel strafe;
    BrakingModel heading;

    public SimulatedMecanum(SimMecanumConfig config) {
        super(config, config.frontLeftMotorName, config.frontRightMotorName, config.backLeftMotorName, config.backRightMotorName);

        R = config.wheelbase / 2 + config.trackWidth / 2;
        wheelRadius = config.wheelRadius;
        this.config = config;
//
//        forwardBrake = findRegenerativeBrakingCoefficient(config.foresightConfig.quadraticBrakeCoefficients.get().get(0,0), config.foresightConfig.linearBrakeCoefficients.get().get(0,0), config.foresightConfig.naturalForwardDeceleration.get(), config.foresightConfig.maxAchievableForwardVelocity.get());
//        headingBrake = findRegenerativeBrakingCoefficient(config.foresightConfig.headingBrakeCoefficients.get().x(), config.foresightConfig.headingBrakeCoefficients.get().y(), config.turnNaturalDeceleration, config.maxAngularVelocity);
//        strafeBrake = findRegenerativeBrakingCoefficient(config.foresightConfig.quadraticBrakeCoefficients.get().get(1,1), config.foresightConfig.linearBrakeCoefficients.get().get(1,1), config.foresightConfig.naturalStrafeDeceleration.get(), config.foresightConfig.maxAchievableStrafeVelocity.get());

        //strafeEfficiency = config.foresightConfig.maxAchievableStrafeVelocity.get() / config.foresightConfig.maxAchievableForwardVelocity.get();

        forward = fitBrakingModel(config.foresightConfig.quadraticBrakeCoefficients.get().get(0,0), config.foresightConfig.linearBrakeCoefficients.get().get(0,0), config.foresightConfig.maxAchievableForwardVelocity.get());

        strafe = fitBrakingModel(config.foresightConfig.quadraticBrakeCoefficients.get().get(1,1), config.foresightConfig.linearBrakeCoefficients.get().get(1,1), config.foresightConfig.maxAchievableStrafeVelocity.get());

        heading = fitBrakingModel(config.foresightConfig.headingBrakeCoefficients.get().x(), config.foresightConfig.headingBrakeCoefficients.get().y(), config.maxAngularVelocity);
        strafeEfficiency = 1;
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

    double stoppingFriction(double v) {
        if (Math.abs(v) < 1e-6)
            return 0;

        double d = config.quadraticBrakeHeading * v * Math.abs(v) + config.linearBrakeHeading * v;

        // Acceleration opposing wheel velocity
        return -v * Math.abs(v) / (2.0 * d);
    }

    double regenerativeAcceleration(double v) {
        return -regenerativeBraking * v;
    }

//    @Override
//    public void update(double deltaTime) {
//        for (int i = 0; i < motors.length; i++) {
//            SimMotor motor = motors[i];
//            motorAngularAccelerations[i] = motor.getAcceleration();
//
//            Logger.recordOutput("Mecanum/angular vels radians per second/" + motor.deviceName, motor.getVelocity());
//            Logger.recordOutput("Mecanum/powers/" + motor.deviceName, motor.getPower());
//            Logger.recordOutput("Mecanum/angular accelerations radians per second per second/" + motor.deviceName, motor.getAcceleration());
//        }
//
//        MotionVector robotVel = velocity.toRobotFrame(position.theta);
//        //acceleration = forwardAccKinematics(motorAngularAccelerations, Math.atan2(robotVel.y, robotVel.x));
//        acceleration = forwardKinematics(motorAngularAccelerations);
//        //acceleration = forwardAccKinematics2(motorAngularAccelerations, robotVel);
//
//        double naturalDeceleration = interpolateAcceleration(config.forwardNaturalDeceleration, config.strafeNaturalDeceleration, Math.atan2(robotVel.y, robotVel.x));
//        naturalDeceleration = config.forwardNaturalDeceleration;
//
//        boolean isStationary = acceleration.magnitude() < naturalDeceleration && velocity.magnitude() < config.staticVelocityRegion && Math.abs(acceleration.theta) < config.turnNaturalDeceleration && Math.abs(velocity.theta) < config.staticVelocityRegion;
//
//        if (isStationary) {
//            velocity = new MotionVector(0, 0, 0);
//            acceleration = new MotionVector(0, 0, 0);
//        }
//        else {
//            acceleration = acceleration.minus(robotVel.unitVector().scale(naturalDeceleration));
////            acceleration.theta -= config.turnNaturalDeceleration * Math.signum(velocity.theta);
//
//            // accel = Au - Bv
//
//            (-fl + fr - bl + br) / (4.0 * R)
//
//            // theta accel = (-fl + fr - bl + br) / (4.0 * R)
//            //acceleration.theta -= -velocity.theta / (2 * config.quadraticBrakeHeading * velocity.theta + config.linearBrakeHeading);
//        }
//
//        acceleration = acceleration.toFieldFrame(position.theta);
//
//        velocity = velocity.step(acceleration, deltaTime);
//
//        MotionVector legalPosition = position;
//        position = position.step(velocity, deltaTime);
//
//        boolean isOutOfBounds = FieldBoundary.isOutOfBounds(position, config.robotGeometry);
//        if (isOutOfBounds) {
//            MotionVector normal = FieldBoundary.collisionNormal(
//                    position,
//                    config.robotGeometry
//            );
//
//            double vNormal = velocity.dot(normal);
//
//            if (vNormal > 0) {
//                velocity = velocity.minus(normal.scale(vNormal));
//            }
//
//            double aNormal = acceleration.dot(normal);
//
//            if (aNormal > 0) {
//                acceleration = acceleration.minus(normal.scale(aNormal));
//            }
//
//            double correctionSign = Math.signum(legalPosition.theta - position.theta);
//
//            if (Math.signum(velocity.theta) != correctionSign) {
//                velocity.theta = 0;
//            }
//
//            if (Math.signum(acceleration.theta) != correctionSign) {
//                acceleration.theta = 0;
//            }
//
//            position = legalPosition.step(velocity, deltaTime);
//        }
//
//        motorAngularVelocities = inverseKinematics(velocity.toRobotFrame(position.theta));
//        for (int i = 0; i < motors.length; i++) {
//            motors[i].velocity = motorAngularVelocities[i];
//        }
//
//        acceleration.log("Mecanum/acceleration");
//        velocity.log("Mecanum/velocity");
//        position.log("Mecanum/position", config.robotModel);
//        Logger.recordOutput("isInBounds", !isOutOfBounds);
//    }

    public static class BrakingModel {
        public final double A2;
        public final double B2;
        public final double rSquared;

        public BrakingModel(double A2, double B2, double rSquared) {
            this.A2 = A2;
            this.B2 = B2;
            this.rSquared = rSquared;
        }
    }

    /**
     * Fits:
     *
     *     D(v) = A * v^2 + B * v
     *
     * to the integrated acceleration model:
     *
     *     a(v) = A2 * v + B2
     *
     * giving:
     *
     *     D(v) = v/A2
     *          - B2/A2^2 * ln(1 + A2*v/B2)
     *
     * The fit is performed from v = 0 to maxVelocity.
     */
    public static BrakingModel fitBrakingModel(
            double A,
            double B,
            double maxVelocity
    ) {
        final int samples = 200;
        final int maxIterations = 100;

        double B2 = 1.0 / (2.0 * Math.max(A, 1e-12));
        double A2 = 0.001;

        double previousError = totalSquaredError(
                A, B, maxVelocity, A2, B2, samples
        );

        for (int iteration = 0; iteration < maxIterations; iteration++) {
            double j11 = 0.0;
            double j12 = 0.0;
            double j22 = 0.0;

            double g1 = 0.0;
            double g2 = 0.0;

            for (int i = 1; i <= samples; i++) {
                double v = maxVelocity * i / samples;

                double target = A * v * v + B * v;
                double predicted = stoppingDistance(v, A2, B2);
                double error = predicted - target;

                double da = Math.max(Math.abs(A2) * 1e-5, 1e-7);
                double db = Math.max(Math.abs(B2) * 1e-5, 1e-7);

                // Don't allow the lower numerical-derivative sample
                // to cross zero.
                double lowerA = Math.max(A2 - da, A2 * 0.5);
                double upperA = A2 + da;

                double lowerB = Math.max(B2 - db, B2 * 0.5);
                double upperB = B2 + db;

                double dDa =
                        (stoppingDistance(v, upperA, B2)
                                - stoppingDistance(v, lowerA, B2))
                                / (upperA - lowerA);

                double dDb =
                        (stoppingDistance(v, A2, upperB)
                                - stoppingDistance(v, A2, lowerB))
                                / (upperB - lowerB);

                j11 += dDa * dDa;
                j12 += dDa * dDb;
                j22 += dDb * dDb;

                g1 += dDa * error;
                g2 += dDb * error;
            }

            // Levenberg-style damping.
            //
            // Scale it relative to the diagonal rather than adding the
            // same tiny absolute number every time.
            double lambda = 1e-6;

            j11 += lambda * Math.max(j11, 1.0);
            j22 += lambda * Math.max(j22, 1.0);

            double determinant = j11 * j22 - j12 * j12;

            if (!Double.isFinite(determinant) ||
                    Math.abs(determinant) < 1e-20) {
                break;
            }

            double deltaA =
                    (-g1 * j22 + g2 * j12) / determinant;

            double deltaB =
                    (-j11 * g2 + j12 * g1) / determinant;

            if (!Double.isFinite(deltaA) ||
                    !Double.isFinite(deltaB)) {
                break;
            }

            /*
             * Raw Gauss-Newton can take a gigantic step.
             * Reduce the step until it actually improves the fit.
             */
            double scale = 1.0;

            double newA2 = A2;
            double newB2 = B2;
            double newError = previousError;

            boolean accepted = false;

            for (int attempt = 0; attempt < 20; attempt++) {
                double candidateA =
                        Math.max(A2 + deltaA * scale, 1e-10);

                double candidateB =
                        Math.max(B2 + deltaB * scale, 1e-10);

                double candidateError = totalSquaredError(
                        A,
                        B,
                        maxVelocity,
                        candidateA,
                        candidateB,
                        samples
                );

                if (Double.isFinite(candidateError) &&
                        candidateError < previousError) {
                    newA2 = candidateA;
                    newB2 = candidateB;
                    newError = candidateError;
                    accepted = true;
                    break;
                }

                scale *= 0.5;
            }

            if (!accepted) {
                break;
            }

            double actualDeltaA = newA2 - A2;
            double actualDeltaB = newB2 - B2;

            A2 = newA2;
            B2 = newB2;
            previousError = newError;

            if (Math.abs(actualDeltaA) < 1e-10 &&
                    Math.abs(actualDeltaB) < 1e-8) {
                break;
            }
        }

        // R²
        double mean = 0.0;

        for (int i = 1; i <= samples; i++) {
            double v = maxVelocity * i / samples;
            mean += A * v * v + B * v;
        }

        mean /= samples;

        double ssTot = 0.0;
        double ssRes = 0.0;

        for (int i = 1; i <= samples; i++) {
            double v = maxVelocity * i / samples;

            double target = A * v * v + B * v;
            double predicted = stoppingDistance(v, A2, B2);

            double targetError = target - mean;
            double fitError = target - predicted;

            ssTot += targetError * targetError;
            ssRes += fitError * fitError;
        }

        double rSquared = 1.0 - ssRes / ssTot;

        System.out.println(
                "A: " + A +
                        " B: " + B +
                        " maxVel: " + maxVelocity
        );

        System.out.println(
                "A2: " + A2 +
                        " B2: " + B2 +
                        " rSquared: " + rSquared
        );

        return new BrakingModel(A2, B2, rSquared);
    }

    private static double totalSquaredError(
            double A,
            double B,
            double maxVelocity,
            double A2,
            double B2,
            int samples
    ) {
        double error = 0.0;

        for (int i = 1; i <= samples; i++) {
            double v = maxVelocity * i / samples;

            double target = A * v * v + B * v;
            double predicted = stoppingDistance(v, A2, B2);

            double e = predicted - target;
            error += e * e;
        }

        return error;
    }

    /**
     * Stopping distance resulting from:
     *
     *     a(v) = A2*v + B2
     */
    public static double stoppingDistance(
            double v,
            double A2,
            double B2
    ) {
        if (v <= 0) {
            return 0;
        }

        // Handle the A2 -> 0 case separately.
        if (Math.abs(A2) < 1e-12) {
            return v * v / (2.0 * B2);
        }

        double x = A2 * v / B2;

        return v / A2
                - B2 / (A2 * A2)
                * Math.log1p(x);
    }

    private MotionVector saturateVelocity(MotionVector velocity) {
        double maxForward =
                config.foresightConfig.maxAchievableForwardVelocity.get();

        double maxStrafe =
                config.foresightConfig.maxAchievableStrafeVelocity.get();

        double ratio =
                Math.abs(velocity.x) / maxForward
                        + Math.abs(velocity.y) / maxStrafe;

        if (ratio <= 1.0)
            return velocity;

        double scale = 1.0 / ratio;

        velocity.x *= scale;
        velocity.y *= scale;

        return velocity;
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

        double vx = robotVel.x;
        double vy = robotVel.y;

        double xDecel = 0;
        double yDecel = 0;

        double speed = Math.hypot(vx, vy);

        double velocityDotAcceleration =
                robotVel.x * robotAccel.x
                        + robotVel.y * robotAccel.y;

        if (speed > 1e-6 && velocityDotAcceleration >= 0) {
            double cos = Math.abs(vx) / speed;
            double sin = Math.abs(vy) / speed;

            double forwardDecel =
                    config.foresightConfig.naturalForwardDeceleration.get();

            double strafeDecel =
                    config.foresightConfig.naturalStrafeDeceleration.get();

            // Radius of the mecanum deceleration rhombus in this direction
            double decel =
                    1.0 / (cos / forwardDecel + sin / strafeDecel);

            // 2 left, moving the right two wheels should slow down -> +x, 0 = y, -theta

            // Apply that magnitude opposite the current velocity
            xDecel = decel * vx / speed;
            yDecel = decel * vy / speed;
        }

        double kCoulombFriction = config.foresightConfig.naturalForwardDeceleration.get();
        double backEMF = config.maxAcceleration / config.foresightConfig.maxAchievableForwardVelocity.get();
        double kA = (backEMF * (config.foresightConfig.maxAchievableForwardVelocity.get()) + kCoulombFriction) / config.nominalVoltage;
        double[] xCoefficients = new double[]{
                kA, backEMF, 0, 0, 0
        };

        // TODO add lag button to distrupt path or apply forces

        double kCoulombFriction2 = config.foresightConfig.naturalStrafeDeceleration.get();
        double backEMF2 = config.maxAcceleration / config.foresightConfig.maxAchievableStrafeVelocity.get();
        double kA2 = (backEMF2 * (config.foresightConfig.maxAchievableStrafeVelocity.get()) + kCoulombFriction2) / config.nominalVoltage;
        double[] yCoefficients = new double[]{
                kA2, backEMF2, 0, 0, 0
        };

        double kCoulombFriction3 = config.turnNaturalDeceleration;
        double backEMF3 = config.maxTurnAccel / config.maxAngularVelocity;
        double kA3 = (backEMF3 * (config.maxAngularVelocity) + kCoulombFriction3) / config.nominalVoltage;
        double[] headingCoefficients = new double[]{
                kA3, backEMF3, 0, 0, 0
        };

        MotorModel model = new MotorModel(
                (v, d, b) -> d * b,
                (v, d, b) -> Math.signum(v) == Math.signum(d) ? -v * Math.abs(d) : 0, // back-emf
                (v, d, b) -> Math.signum(v) != Math.signum(d) && d != 0 ? -v : 0,  // regenerative braking, not dependent on duty bc max braking is way stronger than max accel
                (v, d, b) -> d == 0 ? -v : 0,  // short circuiting brake mode
                (v, d, b) -> -Math.signum(v)
        );

        Logger.recordOutput("theta motor effort", motorEffort.theta);

        double voltage = voltageSensor.getVoltage();

        robotAccel.x = model.predict(xCoefficients, robotVel.x, motorEffort.x, voltage);
        robotAccel.y = model.predict(yCoefficients, robotVel.y, motorEffort.y, voltage);
        robotAccel.theta = model.predict(headingCoefficients, robotVel.theta, motorEffort.theta, voltage);

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
            robotAccel.x -= config.foresightConfig.naturalForwardDeceleration.get() * Math.signum(robotVel.x);
            robotAccel.y -= config.foresightConfig.naturalStrafeDeceleration.get() * Math.signum(robotVel.y);
            robotAccel.theta -= config.turnNaturalDeceleration * Math.signum(robotVel.theta);

            if (Math.signum(robotVel.x) != Math.signum(robotAccel.x) || robotAccel.x == 0)
                robotAccel.x -= robotVel.x / (2.0 * config.foresightConfig.quadraticBrakeCoefficients.get().get(0,0) * Math.abs(robotVel.x) + config.foresightConfig.linearBrakeCoefficients.get().get(0,0)) - xDecel;

            if (Math.signum(robotVel.y) != Math.signum(robotAccel.y) || robotAccel.y == 0)
                robotAccel.y -= robotVel.y / (2.0 * config.foresightConfig.quadraticBrakeCoefficients.get().get(1,1) * Math.abs(robotVel.y) + config.foresightConfig.linearBrakeCoefficients.get().get(1,1)) - yDecel;

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
        Logger.recordOutput("isStationary", isStationary);

        //acceleration = robotAccel.toFieldFrame(position.theta);

        robotVel = robotVel.step(robotAccel, deltaTime);
        robotVel = saturateVelocity(robotVel);
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

        acceleration.logV("Mecanum/acceleration");
        velocity.logV("Mecanum/velocity");
        position.log("Mecanum/position", config.robotModel);
        Logger.recordOutput("isInBounds", !isOutOfBounds);
    }


    @Override
    MotionVector forwardKinematics(double[] motors) {
        double fl = motors[FL] * wheelRadius;
        double fr = motors[FR] * wheelRadius;
        double bl = motors[BL] * wheelRadius;
        double br = motors[BR] * wheelRadius;

        double x = (fl + fr + bl + br) / 4.0;
        double y = (-fl + fr + bl - br) / 4.0;
        //double theta = (-this.motors[FL].getTurnForce() + this.motors[FR].getTurnForce() - this.motors[BL].getTurnForce() + this.motors[BR].getTurnForce()) / (4.0 * R);
        double theta = (-fl + fr - bl + br) / (4.0 * R);

        return new MotionVector(x, y * strafeEfficiency, theta);
    }

    MotionVector powerKinematics(double[] motors) {
        double fl = motors[FL];
        double fr = motors[FR];
        double bl = motors[BL];
        double br = motors[BR];

        double x = (fl + fr + bl + br) / 4.0;
        double y = (-fl + fr + bl - br) / 4.0;
        //double theta = (-this.motors[FL].getTurnForce() + this.motors[FR].getTurnForce() - this.motors[BL].getTurnForce() + this.motors[BR].getTurnForce()) / (4.0 * R);
        double theta = (-fl + fr - bl + br) / (4.0);

        return new MotionVector(x, y * strafeEfficiency, theta);
    }

    private boolean against(double wheelVelocity, double wheelPower) {
        return wheelVelocity != 0
                && (wheelPower == 0
                || Math.signum(wheelVelocity) != Math.signum(wheelPower));
    }

    @Override
    double[] inverseKinematics(MotionVector motion) {
        double x = motion.x;
        double y = motion.y / strafeEfficiency;

        return new double[]{
                (x - y - motion.theta * R) / wheelRadius, // FL
                (x + y + motion.theta * R) / wheelRadius, // FR
                (x + y - motion.theta * R) / wheelRadius, // BL
                (x - y + motion.theta * R) / wheelRadius  // BR
        };
    }
}