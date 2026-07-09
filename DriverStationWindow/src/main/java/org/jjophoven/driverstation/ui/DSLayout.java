package org.jjophoven.driverstation.ui;

import org.jjophoven.driverstation.ui.components.*;

import javax.swing.*;

public interface DSLayout {
    void mainLayout(JPanel contentPanel,
                    ConnectionLabel connectionLabel,
                    OpModeSelector opModeSelector,
                    OpModeControlButtons opModeControlButtons,
                    TimerLabel timerLabel,
                    TelemetryArea telemetryArea);
}
