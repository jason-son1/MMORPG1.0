package com.rpgnexus.core.util;

import com.nisovin.magicspells.MagicSpells;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * 플러그인 간 변수 동기화 유틸리티입니다.
 */
public class VariableBridge {

    /**
     * MythicMobs, MagicSpells 변수 동기화 시도.
     * 
     * @param caster 대상 엔티티
     * @param key    변수 키
     * @param value  값
     */
    public static void sync(Entity caster, String key, double value) {
        // 1. MythicMobs 변수 설정 (Global or Scope 관련 API 사용 필요)
        if (MythicBukkit.inst() != null) {
            // MythicMobs Variable API 사용 (예시)
            // MythicBukkit.inst().getVariableManager().putGlobalVariable(key, value);
        }

        // 2. MagicSpells 변수 설정
        if (caster instanceof Player && MagicSpells.getInstance() != null) {
            Player player = (Player) caster;
            MagicSpells.getVariableManager().set(key, player, value);
        }
    }
}
