package org.codeblooded.ftcodesim.driverstation.client.ui;

import org.codeblooded.ftcodesim.driverstation.OpModeState;
import org.codeblooded.ftcodesim.driverstation.client.DSMVController;
import org.codeblooded.ftcodesim.driverstation.client.DSModel;
import org.codeblooded.ftcodesim.driverstation.client.ui.components.OpModeSelector;
import org.codeblooded.ftcodesim.driverstation.client.ui.components.TelemetryOutput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DSView extends JFrame {

    private Runnable onWindowClose = () -> {};
    public DSView() {
        super("FTC Driver Station");
        super.setSize(800, 600);
        super.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        super.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                DSView.this.onWindowClose.run();
            }
        });

        this.initUI();

        super.setVisible(true);
    }


    private OpModeSelector opModeSelector;
    private TelemetryOutput telemetryOutput;
    private void initUI() {
        JPanel contentPanel = new JPanel(new BorderLayout());

        this.opModeSelector = new OpModeSelector();
        this.telemetryOutput = new TelemetryOutput();

        contentPanel.add(this.opModeSelector, BorderLayout.NORTH);
        contentPanel.add(this.telemetryOutput);


        super.getContentPane().setLayout(new BorderLayout());
        super.getContentPane().add(contentPanel, BorderLayout.CENTER);
    }

    public void fetchOpModes(DSModel clientModel) {
        this.opModeSelector.updateAvailableOpModes(clientModel.getAvailableOpModes());
    }

    public void fetchTelemetry(DSModel clientModel) {
        this.telemetryOutput.setDisplayedText(clientModel.getTelemetryText());
    }

    public void updateOpModeState(OpModeState opModeState) {

    }

    public void attachController(DSMVController controller){
        this.onWindowClose = controller::requestClose;
        this.opModeSelector.attachSelectionListener(controller::changeSelectedOpMode);
    }

    public void setConnectionStatus(boolean status) {

    }
}
