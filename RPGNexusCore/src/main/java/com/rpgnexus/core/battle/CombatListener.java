package com.rpgnexus.core.battle;

import com.rpgnexus.core.RPGNexusCore;
import com.rpgnexus.core.data.DataManager;
import com.rpgnexus.core.data.dto.NexusProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.Map;

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
        // Only handle Player vs Entity or Player vs Player for now
        if (!(event.getDamager() instanceof Player attacker)) {
            return;
        }

        NexusProfile attackerProfile = dataManager.getProfile(attacker.getUniqueId());
        if (attackerProfile == null)
            return;

        NexusProfile defenderProfile = null;
        if (event.getEntity() instanceof Player defender) {
            defenderProfile = dataManager.getProfile(defender.getUniqueId());
        }

        // Build Context Tags dynamically
        // In the future this can come from Metadata, Item Tags, or Skill API
        Map<String, String> tags = new HashMap<>();

        // Default mappings matching the example config
        tags.put("Damage-Kind", "melee"); // Default to melee
        tags.put("Damage-Sort", "physical"); // Default to physical
        tags.put("Damage-Stem", "normal"); // Default to normal attack

        // Example: Check if attacker is holding a specific item to change 'Damage-Kind'
        // to 'ranged'
        // This logic can be expanded later.

        // 데미지 재계산
        BattleManager battleManager = plugin.getCoreManager().getBattleManager();
        double finalDamage = battleManager.calculateDamage(attackerProfile, defenderProfile, event.getDamage(), tags);

        event.setDamage(finalDamage);
    }
}
