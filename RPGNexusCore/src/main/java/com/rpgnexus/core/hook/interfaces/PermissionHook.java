package com.rpgnexus.core.hook.interfaces;

import com.rpgnexus.core.hook.HookProvider;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * 권한 플러그인(Permission) 연동을 위한 인터페이스입니다.
 */
public interface PermissionHook extends HookProvider<Plugin> {

    boolean hasPermission(Player player, String permission);

    String getGroup(Player player);
}
