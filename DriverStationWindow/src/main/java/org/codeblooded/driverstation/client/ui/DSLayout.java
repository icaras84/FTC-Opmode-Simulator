package org.codeblooded.driverstation.client.ui;


import org.codeblooded.driverstation.client.ui.components.*;

import javax.swing.*;

public interface DSLayout {
    void mainLayout(JPanel contentPanel,
                    ConnectionLabel connectionLabel,
                    OpModeSelector opModeSelector,
                    OpModeControlButtons opModeControlButtons,
                    TimerLabel timerLabel,
                    TelemetryArea telemetryArea);
}
