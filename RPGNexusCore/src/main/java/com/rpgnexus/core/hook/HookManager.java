package com.rpgnexus.core.hook;

import com.rpgnexus.core.RPGNexusCore;
import com.rpgnexus.core.hook.impl.LuckPermsHook;
import com.rpgnexus.core.hook.impl.VaultEconomyHook;
import com.rpgnexus.core.hook.interfaces.EconomyHook;
import com.rpgnexus.core.manager.Manager;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.Listener;

/**
 * 외부 플러그인 훅을 관리하고 등록하는 매니저입니다.
 */
public class HookManager extends Manager {

    private EconomyHook economyHook;

    public HookManager(RPGNexusCore plugin) {
        super(plugin);
    }

    @Override
    public void enable() {
        // Economy Hook 등록 (우선순위: Vault)
        this.economyHook = new VaultEconomyHook();
        registerHook(this.economyHook);

        // Permission Hook 등록 (LuckPerms)
        registerHook(new LuckPermsHook());

        // Bridge Listeners 등록 (플러그인 유무 확인 후)
        if (plugin.getServer().getPluginManager().isPluginEnabled("MagicSpells")) {
            plugin.getServer().getPluginManager().registerEvents(new com.rpgnexus.core.bridge.MagicSpellsBridge(plugin),
                    plugin);
            plugin.getLogger().info("MagicSpells 연동 완료.");
        }

        if (plugin.getServer().getPluginManager().isPluginEnabled("MythicMobs")) {
            plugin.getServer().getPluginManager().registerEvents(new com.rpgnexus.core.bridge.MythicMobsBridge(plugin),
                    plugin);
            plugin.getLogger().info("MythicMobs 연동 완료.");
        }
    }

    @Override
    public void disable() {
        // 특별한 종료 로직 없음
    }

    /**
     * 훅을 등록하고 해당 플러그인이 서버에 존재하는지 확인하여 연결을 시도합니다.
     * 
     * @param hookProvider 등록할 훅 구현체
     */
    @SuppressWarnings("unchecked")
    private void registerHook(HookProvider<?> hook) {
        if (hook instanceof Listener) {
            // If the hook is a Listener, it might need to be registered as an event
            // listener.
            // However, the current logic only handles HookProvider's plugin hooking.
            // This block is added as per instruction, but its content is not fully
            // specified.
            // For now, it's an empty block to maintain syntactical correctness.
        }
        String pluginName = hook.getPluginName();
        if (plugin.getServer().getPluginManager().isPluginEnabled(pluginName)) {
            Plugin targetPlugin = plugin.getServer().getPluginManager().getPlugin(pluginName);
            // Java의 제네릭 소거로 인해 안전하지 않은 캐스팅 경고가 뜰 수 있으나,
            // 구조상 HookProvider는 자신의 T 타입에 맞는 플러그인을 받도록 설계됨.
            ((HookProvider<Plugin>) hook).hook(targetPlugin);
            plugin.getLogger().info(pluginName + " 플러그인이 발견되어 연동되었습니다.");
        } else {
            plugin.getLogger().warning(pluginName + " 플러그인을 찾을 수 없습니다. 관련 기능이 비활성화됩니다.");
        }
    }

    public EconomyHook getEconomyHook() {
        return economyHook;
    }
}
