package com.rpgnexus.core;

import com.rpgnexus.core.manager.CoreManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * RPGNexusCore 메인 클래스.
 * 플러그인의 진입점이며 CoreManager를 통해 전체 시스템을 관리합니다.
 */
public class RPGNexusCore extends JavaPlugin {

    private static RPGNexusCore instance;
    private CoreManager coreManager;

    @Override
    public void onEnable() {
        instance = this;

        // 데이터 폴더 생성
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // CoreManager 초기화 및 시작
        this.coreManager = new CoreManager(this);
        this.coreManager.enable();

        // 명령어 등록
        getCommand("nexus").setExecutor(new com.rpgnexus.core.command.NexusCommand(this));

        getLogger().info("RPGNexusCore가 활성화되었습니다.");
    }

    @Override
    public void onDisable() {
        // CoreManager 종료 로직
        if (this.coreManager != null) {
            this.coreManager.disable();
        }

        getLogger().info("RPGNexusCore가 비활성화되었습니다.");
    }

    /**
     * 플러그인 인스턴스를 반환합니다.
     * 
     * @return RPGNexusCore instance
     */
    public static RPGNexusCore getInstance() {
        return instance;
    }

    public void debug(String message) {
        if (coreManager.getConfigManager().getGeneralConfig().isDebugMode()) {
            getLogger().info("[DEBUG] " + message);
        }
    }

    /**
     * CoreManager 인스턴스를 반환합니다.
     * 
     * @return CoreManager instance
     */
    public CoreManager getCoreManager() {
        return coreManager;
    }
}
