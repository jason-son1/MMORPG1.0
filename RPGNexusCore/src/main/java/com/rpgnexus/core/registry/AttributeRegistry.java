package com.rpgnexus.core.registry;

import com.rpgnexus.core.RPGNexusCore;
import com.rpgnexus.core.config.dto.AttributeConfig;
import java.util.HashMap;
import java.util.Map;

/**
 * 속성(Attribute) 등록소입니다.
 * attributes.yml (AttributeConfig) 데이터를 기반으로 속성을 메모리에 등록합니다.
 * 이제 재귀적으로 파싱하여 임의의 깊이와 구조를 지원합니다.
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

        // Start recursive parsing from root
        parseRecursive(attributeConfig.getPlayerStatusData(), "");

        plugin.getLogger().info("총 " + attributes.size() + "개의 속성이 로드되었습니다.");
    }

    @SuppressWarnings("unchecked")
    private void parseRecursive(Map<String, Object> currentMap, String parentPath) {
        for (Map.Entry<String, Object> entry : currentMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // Construct current path
            String currentPath = parentPath.isEmpty() ? key : parentPath + "." + key;

            if (value instanceof Map) {
                Map<String, Object> childMap = (Map<String, Object>) value;

                // Check if this map is a leaf node (Attribute Definition)
                // We check for specific keys that define an attribute.
                // User said: display-name, default, status-value
                if (isAttributeDefinition(childMap)) {
                    registerAttribute(key, currentPath, childMap);
                } else {
                    // It's a directory/category, recurse deeper
                    parseRecursive(childMap, currentPath);
                }
            } else {
                // Unexpected structure or scalar value where map expected, ignore
            }
        }
    }

    private boolean isAttributeDefinition(Map<String, Object> map) {
        // "default" or "status-value" presence implies it's an attribute
        return map.containsKey("default") || map.containsKey("status-value") || map.containsKey("display-name");
    }

    private void registerAttribute(String key, String path, Map<String, Object> data) {
        String internalKey = key; // Keep case sensitive or not? Usually keys are case-insensitive in lookup

        // For consistency with previous logic, let's lowercase internal keys but keep
        // display names
        // But if user wants arbitrary keys, maybe case sensitivity matters?
        // Let's stick to lowercasing for internal lookup to avoid confusion.
        String lookupKey = internalKey.toLowerCase();

        if (attributes.containsKey(lookupKey)) {
            plugin.getLogger().warning("중복된 속성 키 감지: " + lookupKey + " (" + path + ")");
        }

        String displayName = (String) data.getOrDefault("display-name", key);

        Object defaultObj = data.getOrDefault("default", 0.0);
        double defaultValue = 0.0;
        if (defaultObj instanceof Number) {
            defaultValue = ((Number) defaultObj).doubleValue();
        }

        AttributeDefinition def = new AttributeDefinition(lookupKey, path, displayName, defaultValue);
        attributes.put(lookupKey, def);

        // Debug logging (optional)
        // plugin.debug("속성 등록됨: " + lookupKey + " (" + path + ")");
    }

    public boolean isValid(String key) {
        return attributes.containsKey(key.toLowerCase());
    }

    public AttributeDefinition getAttribute(String key) {
        return attributes.get(key.toLowerCase());
    }

    public Map<String, AttributeDefinition> getAllAttributes() {
        return new HashMap<>(attributes);
    }
}
