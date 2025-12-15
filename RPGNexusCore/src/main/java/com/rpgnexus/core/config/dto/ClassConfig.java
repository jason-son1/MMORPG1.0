package com.rpgnexus.core.config.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * class.yml 파일에 매핑되는 직업 관련 설정 객체입니다.
 */
@Data
@NoArgsConstructor
public class ClassConfig {

    private Map<String, RpgClass> classes = new LinkedHashMap<>();

    @Data
    @NoArgsConstructor
    public static class RpgClass {
        private String displayName;
        private int tier;
        private String parent;
    }
}
