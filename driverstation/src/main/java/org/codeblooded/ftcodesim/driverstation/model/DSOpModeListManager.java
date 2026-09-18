package org.codeblooded.ftcodesim.driverstation.model;

import org.codeblooded.ftcodesim.driverstation.connection.packets.InitOpModePacket;
import org.codeblooded.ftcodesim.driverstation.connection.packets.OpModesPacket;

import java.util.Objects;
import java.util.Optional;
import java.util.Vector;

/**
 * This class is a wrapper for a vector of OpModes available to the client from the server
 */
public class DSOpModeListManager {

    private final Vector<InitOpModePacket> availableOpModes;

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

    public Optional<InitOpModePacket> searchAndServe(InitOpModePacket requestedOpMode){
        if (this.availableOpModes.contains(requestedOpMode)) {
            return Optional.of(this.availableOpModes.get(this.availableOpModes.indexOf(requestedOpMode)));
        }

        return Optional.empty();
    }

    public Optional<InitOpModePacket> searchAndServe(int idx){
        if (!(idx < 0 || idx > this.availableOpModes.size() - 1)){
            return Optional.of(this.availableOpModes.elementAt(idx));
        }

        return Optional.empty();
    }

    public Optional<InitOpModePacket> searchAndServe(String group, String name){
        if ((group != null && name != null) && (!group.isEmpty() && !name.isEmpty())) {
            for (InitOpModePacket opmode : this.availableOpModes) {
                if (Objects.equals(opmode.group, group) && Objects.equals(opmode.name, name))
                    return Optional.of(opmode);
            }
        }

        return Optional.empty();
    }

    public Vector<InitOpModePacket> getAvailableOpModes(){
        return this.availableOpModes;
    }
}
