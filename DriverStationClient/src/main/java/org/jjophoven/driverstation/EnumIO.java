package org.jjophoven.driverstation;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public final class EnumIO {
    public static <E extends Enum<E>> void writeEnum(DataOutput out, E value) {
        try {
            out.writeByte(value.ordinal());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <E extends Enum<E>> E readEnum(DataInput in, E[] values)  {
        try {
            return values[in.readByte() & 0xFF];
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}