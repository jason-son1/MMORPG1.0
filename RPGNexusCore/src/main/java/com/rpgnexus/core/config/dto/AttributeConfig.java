package com.rpgnexus.core.config.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * attributes.yml 파일에 매핑되는 스탯/속성 설정 객체입니다.
 */
@Data
@NoArgsConstructor
public class AttributeConfig {

    private Map<String, AttributeCategory> playerStatusData = new LinkedHashMap<>();

    @Data
    @NoArgsConstructor
    public static class AttributeCategory {
        private Map<String, AttributeDefinition> attributes = new LinkedHashMap<>();
    }

    @Data
    @NoArgsConstructor
    public static class AttributeDefinition {
        private String displayName;
        private double defaultValue;
    }
}
