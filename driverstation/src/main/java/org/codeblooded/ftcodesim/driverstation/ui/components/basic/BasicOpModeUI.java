package org.codeblooded.ftcodesim.driverstation.ui.components.basic;

import org.codeblooded.ftcodesim.driverstation.connection.packets.InitOpModePacket;
import org.codeblooded.ftcodesim.driverstation.controller.DSOpModeController;
import org.codeblooded.ftcodesim.driverstation.model.OpModeState;

import javax.swing.*;

public class BasicOpModeUI extends JPanel implements DSOpModeController.UIMethods {

    private JLabel opModeSelected;
    private JButton mainButton, stopButton;

    public BasicOpModeUI() {
        super();

        this.opModeSelected = new JLabel("OpMode Not Selected");
    }

    @Override
    public void hookController(DSOpModeController controller) {

    }

    @Override
    public void onOpModeLoad(InitOpModePacket loadedOpMode) {
        this.opModeSelected.setText(loadedOpMode.name);
    }

    @Override
    public void onOpModeUnload() {
        this.opModeSelected.setText("OpMode Not Selected");
    }

    @Override
    public void onOpModeStateChange(OpModeState nState) {

    }

    @Override
    public void onOpModeTimerChange(long elapsedNano) {

    }
}
