package com.rpgnexus.core.manager;

import com.rpgnexus.core.RPGNexusCore;
import com.rpgnexus.core.battle.BattleManager;
import com.rpgnexus.core.config.ConfigManager;
import com.rpgnexus.core.data.DataManager;
import com.rpgnexus.core.hook.HookManager;
import java.util.ArrayList;
import java.util.List;

/**
 * 플러그인의 핵심 매니저들을 관리하는 CoreManager입니다.
 * 하위 매니저들의 등록, 초기화, 종료 순서를 제어합니다.
 */
public class CoreManager extends Manager {

    private final List<Manager> managers = new ArrayList<>();

    // Sub-managers
    private ConfigManager configManager;
    private HookManager hookManager;
    private DataManager dataManager;
    private BattleManager battleManager;
    private com.rpgnexus.core.network.NetworkManager networkManager;

    // Changing ScriptManager to ScriptEngine
    private com.rpgnexus.core.script.ScriptEngine scriptEngine;

    // New Systems
    private com.rpgnexus.core.registry.AttributeRegistry attributeRegistry;
    private com.rpgnexus.core.manager.ClassManager classManager;
    private com.rpgnexus.core.manager.NormalSystemManager normalSystemManager;

    public CoreManager(RPGNexusCore plugin) {
        super(plugin);
    }

    @Override
    public void enable() {
        // 1. ConfigManager 초기화 (가장 먼저 설정 로드)
        this.configManager = new ConfigManager(plugin);
        registerManager(configManager);
        // ConfigManager must be enabled first to load configs
        this.configManager.enable();

        // 1.5 AttributeRegistry 초기화 (데이터 로드 전 필수)
        this.attributeRegistry = new com.rpgnexus.core.registry.AttributeRegistry(plugin,
                configManager.getAttributeConfig());
        this.attributeRegistry.loadAttributes();

        // 2. DataManager 초기화 (데이터 로딩 준비)
        this.dataManager = new DataManager(plugin);
        registerManager(dataManager);

        // 3. BattleManager 초기화
        this.battleManager = new BattleManager(plugin);
        registerManager(battleManager);

        // 3.5 ClassManager 초기화
        this.classManager = new com.rpgnexus.core.manager.ClassManager(plugin, configManager.getClassConfig());
        registerManager(classManager);

        // 3.6 NormalSystemManager 초기화
        this.normalSystemManager = new com.rpgnexus.core.manager.NormalSystemManager(plugin);
        registerManager(normalSystemManager);

        // 4. NetworkManager 초기화 (통신 채널)
        this.networkManager = new com.rpgnexus.core.network.NetworkManager(plugin);
        registerManager(networkManager);

        // 5. HookManager 초기화 (설정 로드 후 외부 플러그인 연결)
        this.hookManager = new HookManager(plugin);
        registerManager(hookManager);

        // 6. ScriptEngine 초기화
        this.scriptEngine = new com.rpgnexus.core.script.ScriptEngine(plugin);
        registerManager(scriptEngine);

        // 등록된 모든 매니저 활성화
        for (Manager manager : managers) {
            // ConfigManager is already enabled manually above
            if (manager instanceof ConfigManager)
                continue;

            try {
                manager.enable();
            } catch (Exception e) {
                plugin.getLogger().severe("매니저 활성화 중 오류 발생: " + manager.getClass().getSimpleName());
                e.printStackTrace();
            }
        }

        // PAPI 등록 (HookManager 이후)
        if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new com.rpgnexus.core.papi.NexusPapiExpansion(plugin).register();
            plugin.getLogger().info("PlaceholderAPI 확장이 등록되었습니다.");
        }
    }

    @Override
    public void disable() {
        for (int i = managers.size() - 1; i >= 0; i--) {
            try {
                managers.get(i).disable();
            } catch (Exception e) {
                plugin.getLogger().severe("매니저 비활성화 중 오류 발생: " + managers.get(i).getClass().getSimpleName());
                e.printStackTrace();
            }
        }
        managers.clear();
    }

    @Override
    public void reload() {
        plugin.reloadConfig();
        if (attributeRegistry != null) {
            attributeRegistry.loadAttributes();
        }

        for (Manager manager : managers) {
            manager.reload();
        }
    }

    private void registerManager(Manager manager) {
        managers.add(manager);
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public com.rpgnexus.core.battle.BattleManager getBattleManager() {
        return battleManager;
    }

    public com.rpgnexus.core.network.NetworkManager getNetworkManager() {
        return networkManager;
    }

    // Updated getter
    public com.rpgnexus.core.script.ScriptEngine getScriptEngine() {
        return scriptEngine;
    }

    public HookManager getHookManager() {
        return hookManager;
    }

    public com.rpgnexus.core.registry.AttributeRegistry getAttributeRegistry() {
        return attributeRegistry;
    }

    public com.rpgnexus.core.manager.ClassManager getClassManager() {
        return classManager;
    }

    public com.rpgnexus.core.manager.NormalSystemManager getNormalSystemManager() {
        return normalSystemManager;
    }
}
