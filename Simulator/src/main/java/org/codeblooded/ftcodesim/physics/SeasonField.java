package org.codeblooded.ftcodesim.physics;

public enum SeasonField {
    DECODE("FTC:2025-2026 Field", FieldBoundary.DECODE_FIELD);

    public final String ascopeName;
    public final MotionVector[] boundaries;

    SeasonField(String ascopeName, MotionVector[] boundaries) {
        this.ascopeName = ascopeName;
        this.boundaries = boundaries;
    }
}
