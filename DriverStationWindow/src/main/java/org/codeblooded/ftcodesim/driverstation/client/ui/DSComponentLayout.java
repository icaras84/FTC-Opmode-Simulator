package org.codeblooded.ftcodesim.driverstation.client.ui;


import org.codeblooded.driverstation.client.ui.components.*;
import org.codeblooded.ftcodesim.driverstation.client.ui.components.*;

import javax.swing.*;
import java.awt.*;

public class DSComponentLayout {
    public void mainLayout(DSComponentStyle componentStyle,
                           JPanel contentPanel,
                           ConnectionLabel connectionLabel,
                           OpModeSelector opModeSelector,
                           OpModeStateLabel opModeStateLabel,
                           OpModeControlButtons opModeControlButtons,
                           TimerLabel timerLabel,
                           TelemetryArea telemetryArea){


        JPanel controlPanel = new JPanel(new BorderLayout(5, 5));

        JPanel topBar = buildTopBar(componentStyle, connectionLabel);
        JPanel mainPanel = buildOpModePanel(componentStyle, opModeSelector, opModeStateLabel, timerLabel, opModeControlButtons);

        controlPanel.add(topBar, BorderLayout.NORTH);
        controlPanel.add(mainPanel, BorderLayout.CENTER);

        contentPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));

        contentPanel.add(controlPanel);
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(telemetryArea);
    }

    private JPanel buildTopBar(DSComponentStyle componentStyle, ConnectionLabel connectionLabel){
        JPanel output = new JPanel();
        componentStyle.stylePanel(output);

        JLabel titleLabel = new JLabel("FTC Driver Station");
        titleLabel.setForeground(new Color(0x88, 0x88, 0x88));

        output.setLayout(new BoxLayout(output, BoxLayout.X_AXIS));
        output.add(titleLabel);
        output.add(Box.createHorizontalGlue());
        output.add(connectionLabel);
        return output;
    }

    private JPanel buildOpModePanel(DSComponentStyle componentStyle, OpModeSelector opModeSelector, OpModeStateLabel opModeStateLabel, TimerLabel timerLabel, OpModeControlButtons opModeControlButtons){
        JPanel output = new JPanel();
        componentStyle.stylePanel(output);
        output.setLayout(new BoxLayout(output, BoxLayout.Y_AXIS));

        // add selector to the output panel
        output.add(opModeSelector);

        // build a status panel first to house state label and timer
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));

        statusPanel.add(opModeStateLabel);
        statusPanel.add(Box.createHorizontalGlue());
        statusPanel.add(timerLabel);

        output.add(statusPanel);

        // finally, buttons
        output.add(opModeControlButtons);
        output.add(Box.createVerticalGlue());

        return output;
    }
}
