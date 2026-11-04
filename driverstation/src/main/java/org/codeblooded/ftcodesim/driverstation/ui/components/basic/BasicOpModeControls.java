package org.codeblooded.ftcodesim.driverstation.ui.components.basic;

import javax.swing.*;
import java.awt.*;

public class BasicOpModeControls extends JPanel {

    private boolean horizontal; // orientation
    private int buttonHGap;
    private int buttonVGap;

    public BasicOpModeControls(){
        super();
        this.buttonHGap = 5;
        this.buttonVGap = 5;

        this.setOrientation(true);
        this.addButtons();
    }

    public void addButtons(){

    }

    public void refreshUI(){
        super.setLayout(new GridLayout(horizontal ? 1 : 2, horizontal ? 2 : 1, this.buttonHGap, this.buttonVGap));
        super.repaint();
    }

    public void setOrientation(boolean horizontal){
        this.horizontal = horizontal;
        this.refreshUI();
    }

    public boolean isHorizontal(){
        return this.horizontal;
    }

    public boolean isVertical(){
        return !this.horizontal;
    }

    public int getButtonHGap() {
        return buttonHGap;
    }

    public void setButtonHGap(int buttonHGap) {
        this.buttonHGap = buttonHGap;
        this.refreshUI();
    }

    public int getButtonVGap() {
        return buttonVGap;
    }

    public void setButtonVGap(int buttonVGap) {
        this.buttonVGap = buttonVGap;
        this.refreshUI();
    }
}
