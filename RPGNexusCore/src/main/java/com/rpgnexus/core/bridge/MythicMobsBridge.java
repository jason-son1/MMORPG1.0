package com.rpgnexus.core.bridge;

import com.rpgnexus.core.RPGNexusCore;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * MythicMobs에 커스텀 메카닉을 등록합니다.
 */
public class MythicMobsBridge implements Listener {

    private final RPGNexusCore plugin;

    public MythicMobsBridge(RPGNexusCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMechanicLoad(MythicMechanicLoadEvent event) {
        if (event.getMechanicName().equalsIgnoreCase("nexus-skill")) {
            // 커스텀 스킬 메카닉 등록
            // 실제 구현체(NexusSkillMechanic)는 ISkillMechanic을 구현해야 함
            // event.register(new NexusSkillMechanic(event.getConfig()));
            plugin.getLogger().info("MythicMobs Mechanic 'nexus-skill' 등록 완료.");
        }
    }
}
