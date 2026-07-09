package org.jjophoven.driverstation.ui.components;

import org.jjophoven.driverstation.packets.OpModePacket;
import org.jjophoven.driverstation.ui.DSClientModel;
import org.jjophoven.driverstation.ui.DSClientView;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.util.Vector;

public class OpModeSelector extends JPanel {

    private final DefaultComboBoxModel<OpModePacket> comboBoxModel;
    private final JComboBox<OpModePacket> opModeComboBox;

    public OpModeSelector() {
        super(new BorderLayout());

        this.comboBoxModel = new DefaultComboBoxModel<>();
        this.opModeComboBox = new JComboBox<>(comboBoxModel);

        super.setMinimumSize(new Dimension(100, 20));

        super.add(this.opModeComboBox, BorderLayout.CENTER);
    }

    public void acceptClient(DSClientModel client) {
        this.opModeComboBox.addActionListener(evt -> client.setSelectedOpMode(OpModeSelector.this.opModeComboBox.getSelectedIndex()));
    }

    public void updateAvailableOpModes(Vector<OpModePacket> availableOpModes){
        this.comboBoxModel.removeAllElements();
        availableOpModes.forEach(this.comboBoxModel::addElement);
        this.comboBoxModel.setSelectedItem(this.comboBoxModel.getElementAt(0));
    }

    public JComboBox<OpModePacket> getOpModeComboBox() {
        return opModeComboBox;
    }
}
