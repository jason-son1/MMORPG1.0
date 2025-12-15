package com.rpgnexus.core.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rpgnexus.core.RPGNexusCore;
import com.rpgnexus.core.registry.AttributeRegistry;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 플레이어의 통합 데이터를 담는 DTO 클래스입니다.
 * Dynamic Attribute System이 적용되어, 고정 필드 대신 Map을 사용합니다.
 */
@Data
@NoArgsConstructor
public class NexusProfile {

    private String uuid; // UUID.toString()
    private String playerName;

    // Stat / Attribute Container (e.g. STR, DEX, HP, MP, Level, Exp)
    // 모든 수치형 데이터는 Double로 관리합니다.
    private Map<String, Double> statContainer = new HashMap<>();

    // Metadata Container (e.g. Class Name, Quest Progress, non-numeric data)
    private Map<String, Object> metaContainer = new HashMap<>();

    public NexusProfile(UUID uuid, String playerName) {
        this.uuid = uuid.toString();
        this.playerName = playerName;
    }

    /**
     * 스탯 값을 가져옵니다.
     * 
     * @param key AttributeRegistry에 등록된 키
     * @return 스탯 값 (없으면 기본값 0.0)
     */
    public double getStat(String key) {
        if (!isValidAttribute(key)) {
            // 경고를 띄울지, 조용히 처리할지 결정. 여기서는 기본값 리턴.
            return 0.0;
        }
        return statContainer.getOrDefault(key, 0.0);
    }

    /**
     * 스탯 값을 설정합니다.
     * 
     * @param key   AttributeRegistry에 등록된 키
     * @param value 설정할 값
     */
    public void setStat(String key, double value) {
        if (!isValidAttribute(key)) {
            // 유효하지 않은 키는 저장하지 않음 (Log or Ignore)
            // System.out.println("[Warning] Invalid Attribute Key: " + key);
            return;
        }
        statContainer.put(key, value);
    }

    /**
     * 메타 데이터를 가져옵니다.
     */
    @SuppressWarnings("unchecked")
    public <T> T getMeta(String key) {
        return (T) metaContainer.get(key);
    }

    public void setMeta(String key, Object value) {
        metaContainer.put(key, value);
    }

    // --- Helper Methods ---

    @JsonIgnore
    private boolean isValidAttribute(String key) {
        // CoreManager -> AttributeRegistry 접근
        // 주의: DTO에서 로직 접근은 최소화해야 하나, 요구사항(검증)을 위해 접근.
        try {
            AttributeRegistry registry = RPGNexusCore.getInstance().getCoreManager().getAttributeRegistry();
            if (registry == null)
                return true; // 레지스트리 로드 전이라면 Pass (안전장치)
            return registry.isValid(key);
        } catch (Exception e) {
            return true; // 플러그인 초기화 중일 수 있음
        }
    }

    // 편의상 Level 등은 메타나 스탯 중 하나로 약속해서 사용.
    // 여기서는 getter 없앰 (Map 접근 권장)
}
