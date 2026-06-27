package org.jjophoven.driverstation;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class OpModeInfo {
    public Type type;
    public String group;
    public String name;

    public OpModeInfo(Type type, String name, String group) {
        this.type = type;
        this.group = group;
        this.name = name;
    }

    @Override
    public String toString() {
        if (group == null || group.isEmpty()) return name;
        return group + " / " + name;
    }

    public enum Type {
        TELEOP, AUTO;

        public static Type read(DataInput in) {
            return EnumIO.readEnum(in, values());
        }

        public static void write(DataOutput out, Type type) {
            EnumIO.writeEnum(out, type);
        }
    }

    public void write(DataOutput output) {
        try {
            Type.write(output, type);
            output.writeUTF(name);
            output.writeUTF(group);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static OpModeInfo read(DataInput input) {
        try {
            return new OpModeInfo(
                    Type.read(input),
                    input.readUTF(),
                    input.readUTF()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}