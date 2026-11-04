package org.codeblooded.ftcodesim.driverstation.ui.components.basic;

import org.codeblooded.ftcodesim.driverstation.ui.color.Palette;

import javax.swing.*;
import java.awt.*;

public class RoundedButton extends JButton {

    private Palette colorPalette;

    public RoundedButton(String text, Palette colorPalette) {
        super(text);
        this.colorPalette = colorPalette;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        Color bg = isEnabled()
//                ? (getModel().isPressed() ? .darker() : color)
//                : new Color(0x44, 0x44, 0x44);
        Color bg = this.colorPalette.getPrimaryColor();

        if (isEnabled() && getModel().isPressed()){
            bg = bg.darker();
        } else {
            bg = this.colorPalette.getDisabledColor();
        }

        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
        g2.dispose();
        super.paintComponent(g);
    }

    public Palette getColorPalette() {
        return colorPalette;
    }

    public void setColorPalette(Palette colorPalette) {
        this.colorPalette = colorPalette;
    }
}
