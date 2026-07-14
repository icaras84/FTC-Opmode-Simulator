package org.codeblooded.driverstation.client.ui;

import org.codeblooded.driverstation.client.ui.components.*;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class DSClientView extends JPanel {

    public static final Color MAIN_BG = new Color(0x1A, 0x1A, 0x1A);
    public static final Color PANEL_BG = new Color(0x2A, 0x2A, 0x2A);
    public static final Color BORDER_BG = new Color(0x3A, 0x3A, 0x3A);

    private final DSClientModel clientModel;

    private ConnectionLabel connectionLabel;
    private OpModeSelector opModeSelector;
    private OpModeStateLabel opModeStateLabel;
    private OpModeControlButtons opModeControlButtons;
    private TelemetryArea telemetryArea;
    private TimerLabel timerLabel;

    private final Consumer<Boolean> connectionCallback;

    public DSClientView(int port, Consumer<Boolean> connectionCallback) {
        super();
        this.connectionCallback = connectionCallback;

        initUIComponents();

        this.clientModel = new DSClientModel(port,
                this::onConnection,
                this.opModeSelector::updateAvailableOpModes,
                (state) -> {
                    this.opModeStateLabel.changeIndicatorState(state);
                    this.opModeControlButtons.onOpModeStateChanged(state);
                    },
                this.telemetryArea::updateTelemetry,
                this.timerLabel::updateTime,
                (i) -> {},
                (i) -> {}
        );

        this.opModeSelector.acceptClient(this.clientModel);
        this.opModeControlButtons.acceptClient(this.clientModel);

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this.clientModel);
    }

    private void initUIComponents(){
        this.connectionLabel = new ConnectionLabel();
        this.opModeSelector = new OpModeSelector();
        this.opModeStateLabel = new OpModeStateLabel();
        this.opModeControlButtons = new OpModeControlButtons();
        this.telemetryArea = new TelemetryArea();
        this.timerLabel = new TimerLabel();

        super.setBackground(DSClientView.MAIN_BG);
        super.setLayout(new BorderLayout());

        this.connectionLabel.setPreferredSize(new Dimension(100,25));

        toolbarLayout();
    }

    private void toolbarLayout(){
        JPanel topPanel = new JPanel();

        topPanel.setBackground(DSClientView.PANEL_BG);
        topPanel.setLayout(new GridBagLayout());


        JPanel leftCluster = new JPanel(new BorderLayout(5, 0));

        leftCluster.add(this.connectionLabel, BorderLayout.WEST);
        leftCluster.add(this.opModeSelector, BorderLayout.CENTER);
        leftCluster.add(this.opModeStateLabel, BorderLayout.EAST);

        topPanel.add(leftCluster, createGBC(0));

        JPanel spacer = new JPanel();
        GridBagConstraints spacerConstraint = createGBC(1);
        spacerConstraint.weightx = 1;
        topPanel.add(spacer, spacerConstraint);

        topPanel.add(this.opModeControlButtons, createGBC(2));

        for (Component component : topPanel.getComponents()) {
            component.setBackground(DSClientView.PANEL_BG);
        }

        this.connectionLabel.setBackground(DSClientView.PANEL_BG);
        this.opModeSelector.setBackground(DSClientView.PANEL_BG);

        super.add(topPanel, BorderLayout.NORTH);
        super.add(this.telemetryArea, BorderLayout.CENTER);
    }

    private GridBagConstraints createGBC(int x){
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.gridx = x;
        gbc.gridy = 0;
        return gbc;
    }

    private void onConnection(boolean isConnected){
        this.connectionLabel.onConnectionStateChange(isConnected);
        this.connectionCallback.accept(isConnected);
    }

    public DSClientModel getClientModel() {
        return clientModel;
    }
}
