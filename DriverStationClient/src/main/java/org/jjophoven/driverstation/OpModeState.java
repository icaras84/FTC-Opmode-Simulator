package org.jjophoven.driverstation;

import java.io.DataInput;
import java.io.DataOutput;

public enum OpModeState {
    WAIT_FOR_INIT,
    INITIALIZING,
    RUNNING,
    STOPPED;

    public static OpModeState read(DataInput in) {
        return EnumIO.readEnum(in, values());
    }

    public static void write(DataOutput out, OpModeState state) {
        EnumIO.writeEnum(out, state);
    }
}

