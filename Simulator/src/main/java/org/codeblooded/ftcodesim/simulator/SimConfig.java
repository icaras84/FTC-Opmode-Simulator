package org.codeblooded.ftcodesim.simulator;

import org.codeblooded.ftcodesim.physics.RobotGeometry;
import org.codeblooded.ftcodesim.input.DefaultKeybinds;
import org.codeblooded.ftcodesim.hardware.SimHardwareMap;
import org.codeblooded.ftcodesim.input.Keybinds;
import org.codeblooded.ftcodesim.physics.SeasonField;


public class SimConfig {
    public SimHardwareMap simHardwareMap;

    public long loopTimeMs = 20;

    public Keybinds gamepad1Keybinds = new DefaultKeybinds();
    public Keybinds gamepad2Keybinds = new DefaultKeybinds();

    public SeasonField field;
}