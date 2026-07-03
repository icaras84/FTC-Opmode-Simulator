package org.jjophoven.simulator;

import org.jjophoven.fakehardware.FakeHardwareMap;
import org.jjophoven.fakehardware.drivetrain.DrivetrainConfig;
import org.jjophoven.fakehardware.drivetrain.SimulatedDrivetrain;
import org.jjophoven.input.Keybinds;


public class SimulationConfig {
    public FakeHardwareMap fakeHardwareMap;

    public SimulatedDrivetrain drivetrain;

    public Keybinds gamepad1Keybinds;
    public Keybinds gamepad2Keybinds;
}