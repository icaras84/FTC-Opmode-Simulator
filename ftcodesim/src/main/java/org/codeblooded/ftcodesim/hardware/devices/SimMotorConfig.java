package org.codeblooded.ftcodesim.hardware.devices;

import org.codeblooded.fit.MotorModel;

public class SimMotorConfig {
    public String name;
    public MotorModel motorModel;
    public double[] modelCoefficients;
    public double[] zeroPowerBrakeCoefficients;
    public double staticVelocityRegion;
    public double staticFriction;
    public SimVoltageSensor voltageSensor;

    public SimMotorConfig(String name, MotorModel motorModel, double[] modelCoefficients, double[] zeroPowerBrakeCoefficients, double staticVelocityRegion, double staticFriction, SimVoltageSensor voltageSensor) {
        this.name = name;
        this.motorModel = motorModel;
        this.modelCoefficients = modelCoefficients;
        this.zeroPowerBrakeCoefficients = zeroPowerBrakeCoefficients;
        this.staticVelocityRegion = staticVelocityRegion;
        this.staticFriction = staticFriction;
        this.voltageSensor = voltageSensor;
    }

    public static SimMotorConfig empty(String name) {
        return new SimMotorConfig(name, MotorModel.fromString("a=A"), new double[1], new double[1], 0, 0, new SimVoltageSensor());
    }
}
