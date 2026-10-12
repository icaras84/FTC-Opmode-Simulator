package org.codeblooded.ftcodesim.driverstation.ui.color;

import java.awt.*;

public class Palette {
    private final Color primary;
    private final Color secondary;
    private final Color accent;
    private final Color disabled;

    public Palette(Color primary, Color secondary, Color accent, Color disabled) {
        this.primary = primary;
        this.secondary = secondary;
        this.accent = accent;
        this.disabled = disabled;
    }

    public Color getPrimary() {
        return primary;
    }

    public Color getSecondary() {
        return secondary;
    }

    public Color getAccent() {
        return accent;
    }

    public Color getDisabled() {
        return disabled;
    }
}
