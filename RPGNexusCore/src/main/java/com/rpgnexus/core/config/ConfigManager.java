package com.rpgnexus.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.rpgnexus.core.RPGNexusCore;
import com.rpgnexus.core.config.dto.AttributeConfig;
import com.rpgnexus.core.config.dto.BattleConfig;
import com.rpgnexus.core.config.dto.ClassConfig;
import com.rpgnexus.core.config.dto.DamageSystemConfig;
import com.rpgnexus.core.config.dto.GeneralConfig;
import com.rpgnexus.core.config.dto.NormalSystemConfig;
import com.rpgnexus.core.manager.Manager;

import java.io.File;
import java.io.IOException;

/**
 * 설정 파일을 로드하고 저장하며 DTO 객체로 관리하는 매니저입니다.
 * Jackson Dataformat YAML을 사용하여 DTO <-> YAML 변환을 처리합니다.
 */
public class ConfigManager extends Manager {

    private final ObjectMapper mapper;

    private GeneralConfig generalConfig;
    private BattleConfig battleConfig;
    private ClassConfig classConfig;
    private AttributeConfig attributeConfig;
    private DamageSystemConfig damageSystemConfig;
    private NormalSystemConfig normalSystemConfig;

    private File generalFile;
    private File battleFile;
    private File classFile;
    private File attributeFile;
    private File damageSystemFile;
    private File normalSystemFile;

    public ConfigManager(RPGNexusCore plugin) {
        super(plugin);
        this.mapper = new ObjectMapper(new YAMLFactory());
        // 필요시 mapper 설정 추가 (예: 알 수 없는 속성 무시 등)
        // this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
        // false);
    }

    @Override
    public void enable() {
        this.generalFile = new File(plugin.getDataFolder(), "general.yml");
        this.battleFile = new File(plugin.getDataFolder(), "battle.yml");
        this.classFile = new File(plugin.getDataFolder(), "classes.yml");
        this.attributeFile = new File(plugin.getDataFolder(), "attributes.yml");
        this.damageSystemFile = new File(plugin.getDataFolder(), "damage-system.yml");
        this.normalSystemFile = new File(plugin.getDataFolder(), "normal-system.yml");

        reload();
    }

    @Override
    public void disable() {
        // 종료 시 저장 필요하다면 수행 (일반적으로는 로드만 함)
        // saveAll();
    }

    @Override
    public void reload() {
        this.generalConfig = loadConfig(generalFile, GeneralConfig.class, "general.yml");
        this.battleConfig = loadConfig(battleFile, BattleConfig.class, "battle.yml");
        this.classConfig = loadConfig(classFile, ClassConfig.class, "classes.yml");
        this.attributeConfig = loadConfig(attributeFile, AttributeConfig.class, "attributes.yml");
        this.damageSystemConfig = loadConfig(damageSystemFile, DamageSystemConfig.class, "damage-system.yml");
        this.normalSystemConfig = loadConfig(normalSystemFile, NormalSystemConfig.class, "normal-system.yml");

        plugin.getLogger().info("모든 설정 파일이 로드되었습니다.");
    }

    private <T> T loadConfig(File file, Class<T> clazz, String resourceName) {
        // 1. 파일이 없으면
        if (!file.exists()) {
            // 리소스가 존재하면 복사
            try {
                if (plugin.getResource(resourceName) != null) {
                    plugin.saveResource(resourceName, false);
                } else {
                    // 리소스도 없으면 기본 생성
                    T defaultConfig = clazz.getDeclaredConstructor().newInstance();
                    saveConfig(file, defaultConfig);
                    return defaultConfig;
                }
            } catch (Exception e) {
                plugin.getLogger().severe("리소스 저장/생성 실패: " + resourceName);
                e.printStackTrace();
            }
        }

        // 2. 파일 로드 (복사 후 혹은 이미 존재할 때)
        try {
            return mapper.readValue(file, clazz);
        } catch (IOException e) {
            plugin.getLogger().severe("설정 로드 실패: " + file.getName());
            e.printStackTrace();
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception ex) {
                return null;
            }
        }
    }

    public void saveConfig(File file, Object configObj) {
        try {
            mapper.writeValue(file, configObj);
        } catch (IOException e) {
            plugin.getLogger().severe("설정 저장 실패: " + file.getName());
            e.printStackTrace();
        }
    }

    // Getters
    public GeneralConfig getGeneralConfig() {
        return generalConfig;
    }

    public BattleConfig getBattleConfig() {
        return battleConfig;
    }

    public ClassConfig getClassConfig() {
        return classConfig;
    }

    public AttributeConfig getAttributeConfig() {
        return attributeConfig;
    }

    public DamageSystemConfig getDamageSystemConfig() {
        return damageSystemConfig;
    }

    public NormalSystemConfig getNormalSystemConfig() {
        return normalSystemConfig;
    }
}
