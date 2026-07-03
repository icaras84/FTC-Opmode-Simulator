package org.jjophoven.fakehardware;

import androidx.annotation.Nullable;
import com.qualcomm.robotcore.hardware.*;

import java.util.List;

public class FakeHardwareMap extends HardwareMap {
    public FakeHardwareMap() { // TODO remove simulation config dependency
        super(null, null);
        pinpoint = new FakeGobildaPinpoint();
        voltageSensor2 = new FakeVoltageSensor();

        voltageSensor.put("voltageSensor", voltageSensor2);

        put("voltageSensor", voltageSensor2);
        put("pinpoint", pinpoint);
    }

    FakeVoltageSensor voltageSensor2;
    public FakeGobildaPinpoint pinpoint;

    public @Nullable <T> T tryGet(Class<? extends T> classOrInterface, String deviceName) {
        synchronized (lock) {
            deviceName = deviceName.trim();
            List<HardwareDevice> list = allDevicesMap.get(deviceName);
            @Nullable T result = null;

            if (list != null) {
                for (HardwareDevice device : list) {
                    if (classOrInterface.isInstance(device)) {
                        rebuildDeviceNamesIfNecessary();
                        result = classOrInterface.cast(device);
                        break;
                    }
                }
            }
            return result;
        }
    }
}