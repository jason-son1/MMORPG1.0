package com.rpgnexus.core.registry;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 속성(Attribute)의 메타데이터를 정의하는 클래스입니다.
 * Config에서 로드된 각 스탯/속성의 정의를 담습니다.
 */
@Data
@AllArgsConstructor
public class AttributeDefinition {

    private final String key; // 내부 식별 키 (예: combat_ability)
    private final String path; // Config Path (예: Battle-Status.Combat-Ability)
    private final String displayName; // 표시 이름
    private final double defaultValue; // 기본값

}
