package org.jjophoven.fakehardware;

import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.jjophoven.fit.MotorModel;

public class FakeMotor implements DcMotorEx {
    private double power = 0;
    public double velocity = 0;
    private double acceleration = 0;

    MotorModel motorModel;
    double[] zeroPowerBrakeCoefficients;
    double[] modelCoefficients;
    double staticVelocityRegion;
    double staticFriction;
    public String deviceName;
    public ZeroPowerBehavior zeroPowerBehavior;
    public FakeHardwareMap fakeHardwareMap;

    public FakeMotor(FakeHardwareMap fakeHardwareMap, String name, MotorModel motorModel, double[] modelCoefficients, double[] zeroPowerBrakeCoefficients, double staticVelocityRegion, double staticFriction) {
        this.fakeHardwareMap = fakeHardwareMap;
        this.deviceName = name;
        this.motorModel = motorModel;
        this.modelCoefficients = modelCoefficients;
        this.zeroPowerBrakeCoefficients = zeroPowerBrakeCoefficients;
        this.staticVelocityRegion = staticVelocityRegion;
        this.staticFriction = staticFriction;

        fakeHardwareMap.put(name, this);
    }

    @Override
    public void setDirection(Direction direction) {

    }

    public void step(double deltaTime) {
        double voltage = fakeHardwareMap.voltageSensor.iterator().next().getVoltage();
        if (zeroPowerBehavior == ZeroPowerBehavior.BRAKE && power == 0) {
            acceleration = motorModel.predict(zeroPowerBrakeCoefficients, velocity, power, voltage);
        }
        else {
            acceleration = motorModel.predict(modelCoefficients, velocity, power, voltage);
        }
        if (Math.abs(velocity) < staticVelocityRegion && Math.abs(acceleration) < staticFriction) {
            velocity = 0;
            acceleration = 0;
            return;
        }
        velocity += acceleration * deltaTime;
    }

    public void setRollVelocity(double velocity) {
        this.velocity = velocity;
    }

    public boolean isStationary() {
        return acceleration == 0 && velocity == 0;
    }

    @Override
    public Direction getDirection() {
        return null;
    }

    public void setPower(double power) {
        if (power > 1) power = 1;
        if (power < -1) power = -1;
        this.power = power;
    }

    public double getPower() {
        return power;
    }

    public double getAcceleration() {
        return acceleration;
    }

    @Override
    public void setMotorEnable() {

    }

    @Override
    public void setMotorDisable() {
        power = 0;
    }

    @Override
    public boolean isMotorEnabled() {
        return false;
    }

    @Override
    public void setVelocity(double angularRate) {

    }

    @Override
    public void setVelocity(double angularRate, AngleUnit unit) {

    }

    @Override
    public double getVelocity() {
        return velocity;
    }

    @Override
    public double getVelocity(AngleUnit unit) {
        return AngleUnit.RADIANS.fromUnit(unit, velocity);
    }

    @Override
    public void setPIDCoefficients(RunMode mode, PIDCoefficients pidCoefficients) {

    }

    @Override
    public void setPIDFCoefficients(RunMode mode, PIDFCoefficients pidfCoefficients) throws UnsupportedOperationException {

    }

    @Override
    public void setVelocityPIDFCoefficients(double p, double i, double d, double f) {

    }

    @Override
    public void setPositionPIDFCoefficients(double p) {

    }

    @Override
    public PIDCoefficients getPIDCoefficients(RunMode mode) {
        return null;
    }

    @Override
    public PIDFCoefficients getPIDFCoefficients(RunMode mode) {
        return null;
    }

    @Override
    public void setTargetPositionTolerance(int tolerance) {

    }

    @Override
    public int getTargetPositionTolerance() {
        return 0;
    }

    @Override
    public double getCurrent(CurrentUnit unit) {
        return 0;
    }

    @Override
    public double getCurrentAlert(CurrentUnit unit) {
        return 0;
    }

    @Override
    public void setCurrentAlert(double current, CurrentUnit unit) {

    }

    @Override
    public boolean isOverCurrent() {
        return false;
    }

    @Override
    public MotorConfigurationType getMotorType() {
        return null;
    }

    @Override
    public void setMotorType(MotorConfigurationType motorType) {

    }

    @Override
    public DcMotorController getController() {
        return null;
    }

    @Override
    public int getPortNumber() {
        return 0;
    }

    @Override
    public void setZeroPowerBehavior(ZeroPowerBehavior zeroPowerBehavior) {
        this.zeroPowerBehavior = zeroPowerBehavior;
    }

    @Override
    public ZeroPowerBehavior getZeroPowerBehavior() {
        return this.zeroPowerBehavior;
    }

    @Override
    public void setPowerFloat() {

    }

    @Override
    public boolean getPowerFloat() {
        return false;
    }

    @Override
    public void setTargetPosition(int position) {

    }

    @Override
    public int getTargetPosition() {
        return 0;
    }

    @Override
    public boolean isBusy() {
        return false;
    }

    @Override
    public int getCurrentPosition() {
        return 0;
    }

    @Override
    public void setMode(RunMode mode) {

    }

    @Override
    public RunMode getMode() {
        return null;
    }

    @Override
    public Manufacturer getManufacturer() {
        return null;
    }

    @Override
    public String getDeviceName() {
        return "";
    }

    @Override
    public String getConnectionInfo() {
        return "";
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public void resetDeviceConfigurationForOpMode() {

    }

    @Override
    public void close() {

    }
}
