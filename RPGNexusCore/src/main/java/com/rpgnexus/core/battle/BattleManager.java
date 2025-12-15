package com.rpgnexus.core.battle;

import com.rpgnexus.core.RPGNexusCore;
import com.rpgnexus.core.config.dto.BattleConfig;
import com.rpgnexus.core.manager.Manager;

/**
 * 전투 시스템을 관리하는 매니저입니다.
 * 데미지 리스너를 등록하고 데미지 계산 로직을 제공합니다.
 */
public class BattleManager extends Manager {

    private CombatListener combatListener;

    public BattleManager(RPGNexusCore plugin) {
        super(plugin);
    }

    @Override
    public void enable() {
        this.combatListener = new CombatListener(plugin);
        plugin.getServer().getPluginManager().registerEvents(this.combatListener, plugin);
        plugin.getLogger().info("전투 시스템이 활성화되었습니다.");
    }

    @Override
    public void disable() {
        // 리스너는 플러그인 비활성화 시 자동 해제되나 명시적 처리가 필요하면 추가
    }

    /**
     * 최종 데미지를 계산합니다.
     * 
     * @param damage      원본 데미지
     * @param attackerStr 공격자 STR
     * @param defenderDef 방어자 DEF
     * @return 계산된 데미지
     */
    public double calculateDamage(double damage, int attackerStr, int defenderDef) {
        BattleConfig config = plugin.getCoreManager().getConfigManager().getBattleConfig();

        // 공식 예시: (기본뎀 + STR * 2) * (1 - (DEF / (DEF + 100)))
        // 요구사항: (공격자 NexusProfile의 STR * 2) - (방어자 방어력)
        // 음수 방지를 위해 최소값 1 설정

        double strBonus = attackerStr * 2.0;
        double reduction = defenderDef; // 단순 차감식인 경우

        double finalDamage = (damage + strBonus) - reduction;
        return Math.max(1.0, finalDamage);
    }
}
