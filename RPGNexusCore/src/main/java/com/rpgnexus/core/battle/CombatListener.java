package com.rpgnexus.core.battle;

import com.rpgnexus.core.RPGNexusCore;
import com.rpgnexus.core.data.DataManager;
import com.rpgnexus.core.data.dto.NexusProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * 엔티티 간의 전투 이벤트를 처리합니다.
 */
public class CombatListener implements Listener {

    private final RPGNexusCore plugin;
    private final DataManager dataManager;

    public CombatListener(RPGNexusCore plugin) {
        this.plugin = plugin;
        this.dataManager = plugin.getCoreManager().getDataManager();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) {
            return;
        }

        // 공격자가 플레이어인 경우
        NexusProfile attackerProfile = dataManager.getProfile(attacker.getUniqueId());
        if (attackerProfile == null)
            return;

        int str = (int) attackerProfile.getStat("STR");
        int def = 0;

        // 방어자가 플레이어인 경우 방어력 가져오기
        if (event.getEntity() instanceof Player defender) {
            NexusProfile defenderProfile = dataManager.getProfile(defender.getUniqueId());
            if (defenderProfile != null) {
                def = (int) defenderProfile.getStat("DEF");
            }
        }

        // 데미지 재계산
        BattleManager battleManager = plugin.getCoreManager().getBattleManager();
        double finalDamage = battleManager.calculateDamage(event.getDamage(), str, def);

        event.setDamage(finalDamage);
    }
}
