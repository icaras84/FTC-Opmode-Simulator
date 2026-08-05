package org.codeblooded.ftcodesim.driverstation.client.ui.components;

import org.codeblooded.ftcodesim.driverstation.client.DSModel;
import org.codeblooded.ftcodesim.driverstation.client.packets.InitOpModePacket;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;
import java.util.function.Consumer;

public class OpModeSelector extends JPanel {

    private final DefaultComboBoxModel<InitOpModePacket> opModeModel;
    private final JComboBox<InitOpModePacket> opModeComboBox;
    private Consumer<Integer> selectionListener;

    public OpModeSelector() {
        super(new BorderLayout());

        this.opModeModel = new DefaultComboBoxModel<>();
        this.opModeComboBox = new JComboBox<>(this.opModeModel);
        this.selectionListener = idx -> {};

        this.opModeComboBox.addActionListener(e -> OpModeSelector.this.selectionListener.accept(OpModeSelector.this.opModeComboBox.getSelectedIndex()));

        super.add(this.opModeComboBox, BorderLayout.CENTER);
    }

    public void updateAvailableOpModes(Vector<InitOpModePacket> opModes) {
        this.opModeModel.removeAllElements();
        opModes.forEach(this.opModeModel::addElement);
        this.opModeModel.setSelectedItem(0);
    }

    public void attachSelectionListener(Consumer<Integer> selectionListener) {
        this.selectionListener = selectionListener;
    }
}
