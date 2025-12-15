package com.rpgnexus.core.bridge;

import com.nisovin.magicspells.events.SpellCastEvent;
import com.rpgnexus.core.RPGNexusCore;
import com.rpgnexus.core.data.DataManager;
import com.rpgnexus.core.data.dto.NexusProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * MagicSpells 이벤트를 감지하여 Nexus 마나 시스템과 연동합니다.
 */
public class MagicSpellsBridge implements Listener {

    private final RPGNexusCore plugin;
    private final DataManager dataManager;

    public MagicSpellsBridge(RPGNexusCore plugin) {
        this.plugin = plugin;
        this.dataManager = plugin.getCoreManager().getDataManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpellCast(SpellCastEvent event) {
        if (event.isCancelled())
            return;

        // MagicSpells의 기본 마나/시약 소모 시스템은 설정에서 비활성화했다고 가정하거나,
        // 여기서 추가적인 커스텀 마나 로직을 적용.

        if (!(event.getCaster() instanceof Player)) {
            return;
        }

        Player caster = (Player) event.getCaster();
        NexusProfile profile = dataManager.getProfile(caster.getUniqueId());

        if (profile == null)
            return;

        // 예시 로직: 모든 스펠 시전 시 INT에 따라 데미지 보정 (Power 수정)
        // MagicSpells API 버전에 따라 event.setPower() 메서드가 다를 수 있음.
        // float power = event.getPower();
        // int intelligence = profile.getStat("INT");
        // event.setPower(power * (1 + (intelligence * 0.01f)));

        // 예시 로직: 커스텀 마나 확인 (여기서 false 반환 시 스펠 취소)
        // double manaCost = 10.0; // 스펠마다 다르게 설정 필요 (예: 스펠 변수 읽기)
        // if (profile.getCurrentMana() < manaCost) {
        // event.setCancelled(true);
        // caster.sendMessage("마나가 부족합니다.");
        // } else {
        // profile.setCurrentMana(profile.getCurrentMana() - manaCost);
        // }
    }
}
