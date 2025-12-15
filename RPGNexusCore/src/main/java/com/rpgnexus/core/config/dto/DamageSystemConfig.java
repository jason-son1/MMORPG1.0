package com.rpgnexus.core.config.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * damage-system.yml 파일에 매핑되는 데미지 공식 설정 객체입니다.
 */
@Data
@NoArgsConstructor
public class DamageSystemConfig {

    private Map<String, String> damageFormulas = new LinkedHashMap<>();

}
