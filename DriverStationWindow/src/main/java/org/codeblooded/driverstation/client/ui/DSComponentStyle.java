package org.codeblooded.driverstation.client.ui;

import org.codeblooded.driverstation.client.ui.components.*;

import javax.swing.*;
import java.awt.*;

public interface DSComponentStyle {
    default void stylePanel(JPanel panel){
        panel.setBackground(new Color(0x2A, 0x2A, 0x2A));
    }
    default void styleWindow(JPanel windowContentPanel){
        windowContentPanel.setBackground(new Color(0x1A, 0x1A, 0x1A));
    }
    void styleConnectionLabel(ConnectionLabel connectionLabel);
    void styleOpModeSelector(OpModeSelector opModeSelector);
    void styleOpModeControlButtons(OpModeControlButtons opModeControlButtons);
    void styleTimerLabel(TimerLabel timerLabel);
    void styleTelemetryArea(TelemetryArea telemetryArea);
}
