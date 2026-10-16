package org.codeblooded.ftcodesim.driverstation.model;

import org.codeblooded.ftcodesim.driverstation.connection.packets.InitOpModePacket;
import org.codeblooded.ftcodesim.driverstation.connection.packets.OpModesPacket;
import org.codeblooded.ftcodesim.driverstation.misc.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.Vector;
import java.util.function.Consumer;

/**
 * This class is a wrapper for a vector of OpModes available to the client from the server
 */
public class DSOpModeListManager {

    private final Vector<InitOpModePacket> availableOpModes;
    private InitOpModePacket selectedOpMode;

    public DSOpModeListManager(){
        this.availableOpModes = new Vector<>();
    }

    public void registerOpModes(OpModesPacket groupedPacket){
        // simple loop to register multiple opmodes that come in a batch packet
        for (InitOpModePacket member : groupedPacket.opmodes) {
            this.registerOpMode(member);
        }
    }

    public void registerOpMode(InitOpModePacket packet){
        // register unique opmodes to prevent potential selection problem
        if (!this.availableOpModes.contains(packet)) {
            this.availableOpModes.add(packet);
        }
    }

    public void clearAllOpModes(){
        this.availableOpModes.clear();
    }

    public Nullable<InitOpModePacket> searchAndServe(InitOpModePacket requestedOpMode){
        if (this.availableOpModes.contains(requestedOpMode)) {
            return Nullable.of(this.availableOpModes.get(this.availableOpModes.indexOf(requestedOpMode)));
        }
        return Nullable.empty();
    }

    public Nullable<InitOpModePacket> searchAndServe(int idx){
        if (!(idx < 0 || idx > this.availableOpModes.size() - 1)){
            return Nullable.of(this.availableOpModes.elementAt(idx));
        }
        return Nullable.empty();
    }

    public Nullable<InitOpModePacket> searchAndServe(String group, String name){
        if ((group != null && name != null) && (!group.isEmpty() && !name.isEmpty())) {
            for (InitOpModePacket opmode : this.availableOpModes) {
                if (Objects.equals(opmode.group, group) && Objects.equals(opmode.name, name)) {
                    return Nullable.of(opmode);
                }
            }
        }

        return Nullable.empty();
    }

    public Vector<InitOpModePacket> getAvailableOpModes(){
        return this.availableOpModes;
    }

    public Optional<InitOpModePacket> getSelectedOpMode() {
        return Optional.ofNullable(this.selectedOpMode);
    }

    public void setSelectedOpMode(InitOpModePacket selectedOpMode) {
        this.selectedOpMode = selectedOpMode;
    }
}
