package com.rpgnexus.core.config.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.ArrayList;

/**
 * battle.yml 파일에 매핑되는 전투 관련 설정 객체입니다.
 */
@Data
@NoArgsConstructor
public class BattleConfig {

    private double baseCriticalDamage = 1.5;
    private double maxDefenseReduction = 0.8;
    private List<String> disabledWorlds = new ArrayList<>();

    // 예시: PVP 설정
    private PvpSettings pvpSettings = new PvpSettings();

    @Data
    @NoArgsConstructor
    public static class PvpSettings {
        private boolean enabled = true;
        private int combatTagDuration = 15;
    }
}
