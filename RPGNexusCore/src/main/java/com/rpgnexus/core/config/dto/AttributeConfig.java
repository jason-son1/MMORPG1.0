package com.rpgnexus.core.config.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * attributes.yml 파일에 매핑되는 스탯/속성 설정 객체입니다.
 * 이제 임의의 중첩 구조를 지원하기 위해 Map<String, Object>를 사용합니다.
 */
@Data
@NoArgsConstructor
public class AttributeConfig {

    private Map<String, Object> playerStatusData = new LinkedHashMap<>();

    // AttributeDefinition is still useful for deserializing leaf nodes if we want
    // to try-cast,
    // but ConfigManager might populate it as LinkedHashMap.
    // We will handle parsing in AttributeRegistry.

    @Data
    @NoArgsConstructor
    public static class AttributeDefinition {
        private String displayName;
        private double defaultValue;
        private int statusValue; // Added status-value support
        // Jackson mapping might fail if we try to map a Map<String, Object> to this
        // directly without help,
        // so we rely on ObjectMapper mapping to Map and manual parsing in Registry.
    }
}
