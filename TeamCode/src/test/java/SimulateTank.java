import com.qualcomm.robotcore.hardware.DcMotor;
import org.jjophoven.fakehardware.drivetrain.TankConfig;
import org.jjophoven.simulator.SimulationConfig;
import org.jjophoven.fakehardware.drivetrain.MecanumConfig;
import org.jjophoven.input.Keybinds;
import org.jjophoven.simulator.DriverStationSimulator;
import org.junit.Test;
import java.io.IOException;

public class SimulateTank {
    @Test
    public void test() throws IOException, InterruptedException {
        SimulationConfig simulationConfig = new SimulationConfig();

        TankConfig config = new TankConfig();
        config.frontLeftMotorName = "frontLeft";
        config.frontRightMotorName = "frontRight";
        config.backLeftMotorName = "backLeft";
        config.backRightMotorName = "backRight";
        //config.trackWidth = 4.56693 * 2; // distance from center of backRight wheel to backLeft wheel
        config.trackWidth = 12;
        config.wheelDiameter = 3.77953;
        config.staticVelocityRegion = 2;
        config.staticFriction = 45;
        config.maxAcceleration = 250;
        config.maxVelocity = 70;
        config.naturalDeceleration = 40;
        config.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE;

        simulationConfig.drivetrain = config;
        simulationConfig.gamepad1Keybinds = new Keybinds();
        simulationConfig.gamepad2Keybinds = new Keybinds();

        DriverStationSimulator driverStation = new DriverStationSimulator(simulationConfig);
    }
}
