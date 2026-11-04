package org.codeblooded.ftcodesim.driverstation.ui.components.composite;

import org.codeblooded.ftcodesim.driverstation.connection.packets.InitOpModePacket;
import org.codeblooded.ftcodesim.driverstation.controller.DSOpModeController;
import org.codeblooded.ftcodesim.driverstation.model.OpModeState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OpModeControllerUI extends JPanel implements DSOpModeController.UIMethods {

    public static final String UNLOADED_TXT = "OpMode not loaded.";
    private final JLabel selectedOpMode;
    private final JButton mainBtn;
    private final JButton eStopBtn;

    public OpModeControllerUI(){
        super();

        this.selectedOpMode = new JLabel(OpModeControllerUI.UNLOADED_TXT);
        this.mainBtn = new JButton("INIT");
        this.eStopBtn = new JButton("STOP");
        this.onOpModeStateChange(OpModeState.WAIT_FOR_INIT);

        super.setLayout(new GridLayout(1, 3, 5, 0));
        super.add(mainBtn);
        super.add(eStopBtn);
        super.add(selectedOpMode);

        super.setVisible(true);
    }

    @Override
    public void hookController(DSOpModeController controller) {
        this.mainBtn.addActionListener(e -> controller.onMainButton());
        this.eStopBtn.addActionListener(e -> controller.onStopButton());
    }

    @Override
    public void onOpModeLoad(InitOpModePacket loadedOpMode) {
        this.selectedOpMode.setText(loadedOpMode.name);
    }

    @Override
    public void onOpModeUnload() {
        this.selectedOpMode.setText(OpModeControllerUI.UNLOADED_TXT);
    }

    @Override
    public void onOpModeStateChange(OpModeState nState) {
        switch (nState){
            case WAIT_FOR_INIT:
                this.mainBtn.setText("INIT");
                this.mainBtn.setEnabled(true);

                this.eStopBtn.setEnabled(false);
                break;
            case INITIALIZING:
                this.mainBtn.setText("START");
                this.mainBtn.setEnabled(true);

                this.eStopBtn.setEnabled(true);
                break;
            case RUNNING:
                this.mainBtn.setText("RUNNING");
                this.mainBtn.setEnabled(false);

                this.eStopBtn.setEnabled(true);
                break;
        }
    }

    @Override
    public void onOpModeTimerChange(long elapsedNano) {

    }
}
