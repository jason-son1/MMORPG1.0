package com.rpgnexus.core.network.packet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.nio.charset.StandardCharsets;

@Getter
public abstract class Packet {

    // Jackson ObjectMapper 재사용 (Thread-safe)
    protected static final ObjectMapper mapper = new ObjectMapper();

    private final PacketType type;

    public Packet(PacketType type) {
        this.type = type;
    }

    /**
     * 패킷을 JSON Byte Array로 변환합니다.
     * { "type": "...", "payload": { ... } } 구조를 만듭니다.
     */
    public byte[] toBytes() {
        try {
            return mapper.writeValueAsString(this).getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}
