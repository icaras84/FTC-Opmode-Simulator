package org.codeblooded.ftcodesim.driverstation;

import org.codeblooded.ftcodesim.driverstation.connection.packets.InitOpModePacket;
import org.codeblooded.ftcodesim.driverstation.controller.DSOpModeController;
import org.codeblooded.ftcodesim.driverstation.ui.components.composite.OpModeControllerUI;

import javax.swing.*;

public class DSClient {
    public static void main(String[] args) {
        windowTest();
    }

    public static void windowTest(){
        DSOpModeController opModeController = new DSOpModeController(new OpModeControllerUI());
        opModeController.attemptOpModeLoad(new InitOpModePacket(InitOpModePacket.Type.AUTO, "TEST_OP", "root"));
        opModeController.getStateListener().registerSubscriber(System.out::println);

        JFrame frame = new JFrame("Test OpMode control.");
        frame.getContentPane().add((JPanel) opModeController.getUI());
        frame.pack();

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
