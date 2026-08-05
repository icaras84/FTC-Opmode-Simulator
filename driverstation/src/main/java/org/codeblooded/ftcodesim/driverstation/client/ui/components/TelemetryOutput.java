package org.codeblooded.ftcodesim.driverstation.client.ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TelemetryOutput extends JPanel {

    public static class Style {

        // label bar
        public Font labelTextFont;
        public Color labelTextColor;
        public Color labelBarColor;

        // output text
        public Font outputTextFont;
        public Color outputTextColor;

        // output background
        public Color outputBackgroundColor;

        public Style() {
            this.labelTextFont = new Font("Dialog", Font.BOLD, 12);
            this.labelTextColor = Color.WHITE;
            this.labelBarColor = new Color(0x1f1f1f);

            this.outputTextFont = new Font("Monospaced", Font.PLAIN, 12);
            this.outputTextColor = Color.GREEN;

            this.outputBackgroundColor = new Color(0x0f0f0f);
        }

        public void apply(TelemetryOutput telemetryOutput) {
            telemetryOutput.header.setOpaque(true);
            telemetryOutput.header.setBackground(this.labelBarColor);

            telemetryOutput.label.setFont(this.labelTextFont);
            telemetryOutput.label.setForeground(this.labelTextColor);

            telemetryOutput.textArea.setFont(this.outputTextFont);
            telemetryOutput.textArea.setForeground(this.outputTextColor);
            telemetryOutput.textArea.setCaretColor(this.outputTextColor);

            telemetryOutput.textArea.setBackground(this.outputBackgroundColor);
            telemetryOutput.scrollPane.setBackground(this.outputBackgroundColor);
        }
    }

    private JPanel header;
    private JLabel label;
    private JScrollPane scrollPane;
    private JTextArea textArea;

    public TelemetryOutput(TelemetryOutput.Style style) {
        super(new BorderLayout());
        super.setOpaque(false);

        this.header = new JPanel();
        this.header.setLayout(new BoxLayout(this.header, BoxLayout.X_AXIS));
        this.header.setBorder(new EmptyBorder(5, 5, 5, 5));

        this.label = new JLabel("Telemetry");
        this.header.add(this.label);
        this.header.add(Box.createHorizontalGlue());

        this.textArea = new JTextArea();
        this.textArea.setBorder(new EmptyBorder(5, 5, 5, 5));

        this.scrollPane = new JScrollPane(this.textArea);

        style.apply(this);
        super.add(this.header, BorderLayout.NORTH);
        super.add(this.scrollPane, BorderLayout.CENTER);
    }

    public TelemetryOutput(){
        this(new TelemetryOutput.Style());
    }

    public void setDisplayedText(String text) {
        this.textArea.setText(text);
    }
}
