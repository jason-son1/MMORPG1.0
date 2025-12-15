package com.rpgnexus.core.manager;

import com.rpgnexus.core.RPGNexusCore;
import com.rpgnexus.core.config.dto.ClassConfig;
import com.rpgnexus.core.data.node.ClassNode;
import java.util.HashMap;
import java.util.Map;

/**
 * 직업(Class) 시스템 관리자.
 * classes.yml (ClassConfig) 데이터를 기반으로 직업 트리 데이터를 로드하고 메모리에 구축합니다.
 */
public class ClassManager extends Manager {

    // 모든 직업 노드를 이름으로 빠르게 조회하기 위한 Map
    private final Map<String, ClassNode> classMap = new HashMap<>();

    // 트리 루트 노드들 (1차 직업들)
    private final Map<String, ClassNode> rootClasses = new HashMap<>();

    private final ClassConfig classConfig;

    public ClassManager(RPGNexusCore plugin, ClassConfig classConfig) {
        super(plugin);
        this.classConfig = classConfig;
    }

    @Override
    public void enable() {
        loadClasses();
    }

    @Override
    public void disable() {
        classMap.clear();
        rootClasses.clear();
    }

    public void loadClasses() {
        classMap.clear();
        rootClasses.clear();

        if (classConfig == null || classConfig.getClasses() == null) {
            plugin.getLogger().warning("직업 설정이 비어있습니다.");
            return;
        }

        // 1. 모든 노드 생성 (연결은 나중에)
        for (Map.Entry<String, ClassConfig.RpgClass> entry : classConfig.getClasses().entrySet()) {
            String key = entry.getKey();
            ClassConfig.RpgClass dto = entry.getValue();

            int tier = dto.getTier();
            ClassNode node = new ClassNode(key, tier);
            node.setDisplayName(dto.getDisplayName() != null ? dto.getDisplayName() : key);

            classMap.put(key, node);
        }

        // 2. 부모-자식 연결
        for (Map.Entry<String, ClassConfig.RpgClass> entry : classConfig.getClasses().entrySet()) {
            String key = entry.getKey();
            ClassConfig.RpgClass dto = entry.getValue();
            String parentName = dto.getParent();

            ClassNode currentNode = classMap.get(key);

            if (parentName != null && !parentName.equalsIgnoreCase("None") && classMap.containsKey(parentName)) {
                ClassNode parentNode = classMap.get(parentName);
                parentNode.addChild(currentNode);
            } else {
                // 부모가 없으면 루트(1차) 직업으로 간주
                if (currentNode.getTier() == 1) {
                    rootClasses.put(key, currentNode);
                } else {
                    plugin.getLogger().warning("고립된 직업 노드 발견 (부모 없음, 1차 아님): " + key);
                }
            }
        }

        plugin.getLogger().info("총 " + classMap.size() + "개의 직업이 로드되었습니다.");
    }

    public ClassNode getClassNode(String className) {
        return classMap.get(className);
    }

    /**
     * 전직 가능 여부 확인
     * 
     * @param currentClass 현재 직업
     * @param targetClass  목표 직업
     */
    public boolean canAdvance(String currentClass, String targetClass) {
        ClassNode current = classMap.get(currentClass);
        ClassNode target = classMap.get(targetClass);

        if (current == null || target == null)
            return false;

        // 현재 직업의 자식 중에 목표 직업이 있어야 함
        return current.hasChild(targetClass);
    }
}
