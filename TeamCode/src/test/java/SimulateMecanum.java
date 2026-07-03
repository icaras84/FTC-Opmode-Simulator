import org.jjophoven.simhardware.SimHardwareMap;
import org.jjophoven.simhardware.drivetrain.SimulatedMecanum;
import org.jjophoven.simulator.SimulationConfig;
import org.jjophoven.simhardware.drivetrain.SimulatedMecanumConfig;
import org.jjophoven.input.DefaultKeybinds;
import org.jjophoven.simulator.DriverStationSimulator;
import org.junit.Test;
import java.io.IOException;

public class SimulateMecanum {
    @Test
    public void test() throws IOException, InterruptedException {
        SimulationConfig simulationConfig = new SimulationConfig();
        SimHardwareMap simHardwareMap = new SimHardwareMap();

        SimulatedMecanumConfig mecanumConfig = new SimulatedMecanumConfig();
        mecanumConfig.frontLeftMotorName = "frontLeft";
        mecanumConfig.frontRightMotorName = "frontRight";
        mecanumConfig.backLeftMotorName = "backLeft";
        mecanumConfig.backRightMotorName = "backRight";
        mecanumConfig.wheelbase = 4.68504 * 2; // distance from center of frontLeft wheel to backLeft wheel
        mecanumConfig.trackWidth = 4.56693 * 2; // distance from center of backRight wheel to backLeft wheel
        mecanumConfig.wheelRadius = 3.77953 / 2;
        mecanumConfig.staticVelocityRegion = 2;
        mecanumConfig.staticFriction = 45;
        mecanumConfig.maxAcceleration = 250;
        mecanumConfig.maxVelocity = 70;
        mecanumConfig.naturalDeceleration = 40;
        mecanumConfig.strafeEfficiency = 0.90;
        mecanumConfig.simHardwareMap = simHardwareMap;

        simHardwareMap.setDrivetrain(new SimulatedMecanum(mecanumConfig));

        simulationConfig.gamepad1Keybinds = new DefaultKeybinds();
        simulationConfig.gamepad2Keybinds = new DefaultKeybinds();
        simulationConfig.simHardwareMap = simHardwareMap;

        simHardwareMap.pinpoint("pinpoint");

        DriverStationSimulator driverStation = new DriverStationSimulator(simulationConfig);
    }
}
