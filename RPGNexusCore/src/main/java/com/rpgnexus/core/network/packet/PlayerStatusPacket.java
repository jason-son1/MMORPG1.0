package com.rpgnexus.core.network.packet;

import com.rpgnexus.core.data.dto.NexusProfile;
import lombok.Getter;
import java.util.Map;

@Getter
public class PlayerStatusPacket extends Packet {

    private final Payload payload;

    public PlayerStatusPacket(NexusProfile profile) {
        super(PacketType.PLAYER_STATUS);
        this.payload = new Payload(
                profile.getStat("mana"),
                profile.getStat("max_mana"),
                profile.getStat("exp"),
                1000.0, // TODO: Max Exp Calculation formula required
                (int) profile.getStat("level"),
                profile.getStatContainer());
    }

    @Getter
    public static class Payload {
        private final double currentMana;
        private final double maxMana;
        private final double currentExp;
        private final double maxExp;
        private final int level;
        private final Map<String, Double> stats;

        public Payload(double currentMana, double maxMana, double currentExp, double maxExp, int level,
                Map<String, Double> stats) {
            this.currentMana = currentMana;
            this.maxMana = maxMana;
            this.currentExp = currentExp;
            this.maxExp = maxExp;
            this.level = level;
            this.stats = stats;
        }
    }
}
