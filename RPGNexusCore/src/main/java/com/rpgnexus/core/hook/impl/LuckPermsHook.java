package com.rpgnexus.core.hook.impl;

import com.rpgnexus.core.RPGNexusCore;
import com.rpgnexus.core.hook.interfaces.PermissionHook;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.node.NodeMutateEvent;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * LuckPerms와 연동하여 권한 및 그룹 관리 기능을 제공합니다.
 * 랭크 변경 이벤트를 감지합니다.
 */
public class LuckPermsHook implements PermissionHook {

    private LuckPerms luckPerms;
    private boolean isHooked = false;

    @Override
    public String getPluginName() {
        return "LuckPerms";
    }

    @Override
    public void hook(Plugin plugin) {
        if (plugin == null || !plugin.isEnabled())
            return;

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            this.luckPerms = provider.getProvider();
            this.isHooked = true;
            registerListeners();
        }
    }

    private void registerListeners() {
        EventBus eventBus = luckPerms.getEventBus();
        eventBus.subscribe(RPGNexusCore.getInstance(), NodeMutateEvent.class, this::onNodeMutate);
    }

    // 권한/그룹 변경 감지
    private void onNodeMutate(NodeMutateEvent event) {
        if (event.isUser()) {
            User user = (User) event.getTarget();
            // TODO: 필요한 경우 RPGNexusCore의 DataManager를 통해 데이터 갱신 로직 추가
            // 예: 랭크에 따른 스탯 보너스 재계산 등
            RPGNexusCore.getInstance().getLogger().info("권한 변경 감지: " + user.getUniqueId());
        }
    }

    @Override
    public boolean isHooked() {
        return isHooked;
    }

    @Override
    public boolean hasPermission(Player player, String permission) {
        if (!isHooked)
            return player.hasPermission(permission);
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null)
            return false;
        return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }

    @Override
    public String getGroup(Player player) {
        if (!isHooked)
            return "default";
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null)
            return "default";
        return user.getPrimaryGroup();
    }
}
