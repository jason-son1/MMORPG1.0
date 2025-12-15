package com.rpgnexus.core.registry;

import com.rpgnexus.core.RPGNexusCore;
import com.rpgnexus.core.config.dto.AttributeConfig;
import java.util.HashMap;
import java.util.Map;

/**
 * 속성(Attribute) 등록소입니다.
 * attributes.yml (AttributeConfig) 데이터를 기반으로 속성을 메모리에 등록합니다.
 */
public class AttributeRegistry {

    private final Map<String, AttributeDefinition> attributes = new HashMap<>();
    private final RPGNexusCore plugin;
    private final AttributeConfig attributeConfig;

    public AttributeRegistry(RPGNexusCore plugin, AttributeConfig attributeConfig) {
        this.plugin = plugin;
        this.attributeConfig = attributeConfig;
    }

    /**
     * Config에서 속성 정의를 로드합니다.
     */
    public void loadAttributes() {
        attributes.clear();

        if (attributeConfig == null || attributeConfig.getPlayerStatusData() == null) {
            plugin.getLogger().warning("속성 설정이 비어있습니다.");
            return;
        }

        for (Map.Entry<String, AttributeConfig.AttributeCategory> categoryEntry : attributeConfig.getPlayerStatusData()
                .entrySet()) {
            String categoryName = categoryEntry.getKey();
            AttributeConfig.AttributeCategory category = categoryEntry.getValue();

            if (category.getAttributes() == null)
                continue;

            for (Map.Entry<String, AttributeConfig.AttributeDefinition> attrEntry : category.getAttributes()
                    .entrySet()) {
                String key = attrEntry.getKey();
                AttributeConfig.AttributeDefinition defDto = attrEntry.getValue();

                // Path construction (Category.AttributeName)
                String path = categoryName + "." + key;

                registerAttribute(key, path, defDto);
            }
        }

        plugin.getLogger().info("총 " + attributes.size() + "개의 속성이 로드되었습니다.");
    }

    private void registerAttribute(String key, String path, AttributeConfig.AttributeDefinition dto) {
        String internalKey = key.toLowerCase().replace("-", "_"); // 키 표준화

        // 중복 체크
        if (attributes.containsKey(internalKey)) {
            plugin.getLogger().warning("중복된 속성 키 감지: " + internalKey + " (" + path + ")");
        }

        String displayName = dto.getDisplayName() != null ? dto.getDisplayName() : key;
        double defaultValue = dto.getDefaultValue();

        AttributeDefinition def = new AttributeDefinition(internalKey, path, displayName, defaultValue);
        attributes.put(internalKey, def);

        plugin.debug("속성 등록됨: " + internalKey + " (기본값: " + defaultValue + ")");
    }

    public boolean isValid(String key) {
        return attributes.containsKey(key);
    }

    public AttributeDefinition getAttribute(String key) {
        return attributes.get(key);
    }

    public Map<String, AttributeDefinition> getAllAttributes() {
        return new HashMap<>(attributes);
    }
}
