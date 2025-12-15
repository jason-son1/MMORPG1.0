package com.rpgnexus.core.network.packet;

import lombok.Getter;

@Getter
public class TargetInfoPacket extends Packet {

    private final Payload payload;

    public TargetInfoPacket(int entityId, String name, double health, double maxHealth, boolean isEnemy) {
        super(PacketType.TARGET_INFO);
        this.payload = new Payload(entityId, name, health, maxHealth, isEnemy);
    }

    @Getter
    public static class Payload {
        private final int entityId;
        private final String name;
        private final double health;
        private final double maxHealth;
        private final boolean isEnemy;

        public Payload(int entityId, String name, double health, double maxHealth, boolean isEnemy) {
            this.entityId = entityId;
            this.name = name;
            this.health = health;
            this.maxHealth = maxHealth;
            this.isEnemy = isEnemy;
        }
    }
}
