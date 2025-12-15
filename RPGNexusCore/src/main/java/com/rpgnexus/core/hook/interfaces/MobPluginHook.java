package com.rpgnexus.core.hook.interfaces;

import com.rpgnexus.core.hook.HookProvider;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

/**
 * 몹 생성 플러그인(MythicMobs 등) 연동을 위한 인터페이스입니다.
 */
public interface MobPluginHook extends HookProvider<Plugin> {

    /**
     * 특정 위치에 커스텀 몹을 소환합니다.
     * 
     * @param mobId    몹 식별자
     * @param location 소환 위치
     * @return 소환된 엔티티 (실패 시 null)
     */
    Entity spawnMob(String mobId, Location location);

    /**
     * 해당 엔티티가 이 플러그인의 커스텀 몹인지 확인합니다.
     * 
     * @param entity 확인할 엔티티
     * @return true if custom mob
     */
    boolean isCustomMob(Entity entity);
}
