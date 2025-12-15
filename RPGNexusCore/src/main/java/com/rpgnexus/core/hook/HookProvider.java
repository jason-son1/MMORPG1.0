package com.rpgnexus.core.hook;

import org.bukkit.plugin.Plugin;

/**
 * 모든 훅(Hook) 클래스가 구현해야 하는 기본 인터페이스입니다.
 * 외부 플러그인과의 연동을 추상화합니다.
 */
public interface HookProvider<T extends Plugin> {

    /**
     * 해당 훅이 의존하는 플러그인의 이름을 반환합니다.
     * 
     * @return Plugin Name
     */
    String getPluginName();

    /**
     * 훅을 활성화하고 플러그인 인스턴스를 설정합니다.
     * 
     * @param plugin 찾은 플러그인 인스턴스
     */
    void hook(T plugin);

    /**
     * 훅이 유효한지(플러그인이 로드되었는지) 확인합니다.
     * 
     * @return true if hooked
     */
    boolean isHooked();
}
